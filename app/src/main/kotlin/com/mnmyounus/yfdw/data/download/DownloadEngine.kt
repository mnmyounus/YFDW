package com.mnmyounus.yfdw.data.download

import com.mnmyounus.yfdw.data.integrity.MalwareSignatureChecker
import com.mnmyounus.yfdw.data.integrity.StreamingHasher
import com.mnmyounus.yfdw.data.security.TorManager
import com.mnmyounus.yfdw.domain.model.OperatingMode
import com.mnmyounus.yfdw.domain.repository.OperatingModeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

/**
 * Coordinates one full download: probe -> plan up to 32 ranged chunks ->
 * parallel fetch -> merge in order -> stream-hash -> local malware-blocklist
 * check -> finalize.
 */
class DownloadEngine @Inject constructor(
    private val baseClient: OkHttpClient,
    private val chunkDownloader: ChunkDownloader,
    private val torManager: TorManager,
    private val operatingModeRepository: OperatingModeRepository,
    private val malwareChecker: MalwareSignatureChecker
) {
    data class Result(val finalFile: File, val sha256: String, val blocked: Boolean)

    suspend fun run(
        url: String,
        workDir: File,
        finalFile: File,
        onProgress: (downloadedBytes: Long, totalBytes: Long) -> Unit
    ): Result = coroutineScope {
        val client = clientForCurrentMode()
        val (totalBytes, supportsRanges) = try {
            probe(client, url)
        } catch (e: Exception) {
            throw IOException("Failed to probe $url: ${e.message}", e)
        }

        if (totalBytes <= 0) {
            throw IOException("Server returned invalid content-length: $totalBytes")
        }

        val ranges = DownloadChunkPlanner.plan(totalBytes, supportsRanges)
        workDir.mkdirs()
        val partFiles = ranges.map { File(workDir, "part_${it.index}.tmp") }
        val totalDownloaded = AtomicLong(0L)

        val jobs = ranges.mapIndexed { idx, range ->
            async(Dispatchers.IO) {
                repeat(3) { attempt ->
                    try {
                        chunkDownloader.download(
                            url = url,
                            range = range,
                            partFile = partFiles[idx],
                            alreadyWrittenBytes = partFiles[idx].takeIf { it.exists() }?.length() ?: 0L
                        ) { bytes ->
                            val newTotal = totalDownloaded.addAndGet(bytes.toLong())
                            onProgress(newTotal, totalBytes)
                        }
                        return@repeat // success, exit retry loop
                    } catch (e: Exception) {
                        if (attempt >= 2) throw IOException("Chunk ${range.index} failed after 3 retries: ${e.message}", e)
                        kotlinx.coroutines.delay(1000L * (attempt + 1)) // exponential backoff
                    }
                }
            }
        }
        jobs.awaitAll()

        val hasher = StreamingHasher()
        finalFile.parentFile?.mkdirs()
        FileOutputStream(finalFile).use { out ->
            for (part in partFiles) {
                if (!part.exists()) throw IOException("Part file missing: ${part.path}")
                FileInputStream(part).use { input ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        out.write(buffer, 0, read)
                        hasher.update(buffer, read)
                    }
                }
            }
        }
        partFiles.forEach { it.delete() }

        val sha256 = hasher.finishHex()
        val blocked = malwareChecker.isFlagged(sha256)
        if (blocked) finalFile.delete()

        Result(finalFile, sha256, blocked)
    }

    private suspend fun clientForCurrentMode(): OkHttpClient {
        val mode = operatingModeRepository.mode.first()
        val proxy = if (mode == OperatingMode.PRIVACY_ANONYMITY) torManager.socksProxyOrNull() else null
        return if (proxy != null) baseClient.newBuilder().proxy(proxy).build() else baseClient
    }

    private fun probe(client: OkHttpClient, url: String): Pair<Long, Boolean> {
        val request = Request.Builder().url(url).head().build()
        client.newCall(request).execute().use { response ->
            val length = response.header("Content-Length")?.toLongOrNull() ?: -1L
            val supportsRanges = response.header("Accept-Ranges")?.contains("bytes", ignoreCase = true) == true
            return length to supportsRanges
        }
    }
}
