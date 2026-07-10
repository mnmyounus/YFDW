package com.mnmyounus.yfdw.data.download

data class ChunkRange(val index: Int, val start: Long, val end: Long)

object DownloadChunkPlanner {
    const val MAX_CHUNKS = 32
    private const val MIN_CHUNK_SIZE = 2L * 1024 * 1024 // don't split below ~2MB per chunk

    fun plan(totalBytes: Long, supportsRanges: Boolean): List<ChunkRange> {
        if (!supportsRanges || totalBytes <= 0) {
            return listOf(ChunkRange(0, 0, if (totalBytes > 0) totalBytes - 1 else -1))
        }
        val maxByPayload = (totalBytes / MIN_CHUNK_SIZE).toInt().coerceAtLeast(1)
        val chunkCount = maxByPayload.coerceAtMost(MAX_CHUNKS)
        val baseSize = totalBytes / chunkCount

        val ranges = mutableListOf<ChunkRange>()
        var start = 0L
        for (i in 0 until chunkCount) {
            val end = if (i == chunkCount - 1) totalBytes - 1 else start + baseSize - 1
            ranges += ChunkRange(i, start, end)
            start = end + 1
        }
        return ranges
    }
}
