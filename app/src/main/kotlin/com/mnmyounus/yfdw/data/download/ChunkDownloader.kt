package com.mnmyounus.yfdw.data.download

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

class ChunkDownloader(private val client: OkHttpClient) {

    /**
     * Downloads [range] of [url] into [partFile], starting from
     * [alreadyWrittenBytes] so a previously-interrupted chunk resumes
     * instead of restarting from zero.
     */
    fun download(
        url: String,
        range: ChunkRange,
        partFile: File,
        alreadyWrittenBytes: Long,
        onBytes: (Int) -> Unit
    ) {
        val effectiveStart = range.start + alreadyWrittenBytes
        if (range.end >= 0 && effectiveStart > range.end) return // chunk already complete

        val rangeHeader = if (range.end >= 0) "bytes=$effectiveStart-${range.end}" else "bytes=$effectiveStart-"
        val request = Request.Builder().url(url).header("Range", rangeHeader).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code} for range $rangeHeader")
            val body = response.body ?: throw IOException("Empty body for range $rangeHeader")

            RandomAccessFile(partFile, "rw").use { raf ->
                raf.seek(alreadyWrittenBytes)
                body.byteStream().use { input ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        raf.write(buffer, 0, read)
                        onBytes(read)
                    }
                }
            }
        }
    }
}
