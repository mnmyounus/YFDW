package com.mnmyounus.yfdw.data.integrity

import java.security.MessageDigest

/**
 * Wraps a MessageDigest so chunks can be fed into it as they're written to
 * disk — no need to re-read the finished file just to compute its hash.
 */
class StreamingHasher(algorithm: String = "SHA-256") {
    private val digest = MessageDigest.getInstance(algorithm)

    fun update(buffer: ByteArray, length: Int) {
        digest.update(buffer, 0, length)
    }

    fun finishHex(): String = digest.digest().joinToString("") { "%02x".format(it) }
}
