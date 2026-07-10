package com.mnmyounus.yfdw.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.inject.Inject

/**
 * Generates a random 256-bit SQLCipher passphrase on first run and stores
 * it inside EncryptedSharedPreferences, whose own key lives in the Android
 * Keystore (StrongBox-backed on devices that support it). The raw
 * passphrase never touches disk in cleartext.
 */
class DatabaseKeyProvider @Inject constructor(
    private val context: Context
) {
    fun getOrCreatePassphrase(): ByteArray {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            context,
            "yfdw_db_key_store",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        prefs.getString(KEY_ALIAS, null)?.let { return it.decodeHex() }

        val newKey = ByteArray(32).also { SecureRandom().nextBytes(it) }
        prefs.edit().putString(KEY_ALIAS, newKey.toHex()).apply()
        return newKey
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
    private fun String.decodeHex(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    companion object {
        private const val KEY_ALIAS = "sqlcipher_passphrase"
    }
}
