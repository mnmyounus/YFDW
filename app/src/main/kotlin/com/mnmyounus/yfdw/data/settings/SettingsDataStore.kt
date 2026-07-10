package com.mnmyounus.yfdw.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "yfdw_settings")

class SettingsDataStore(private val context: Context) {

    private val modeKey = stringPreferencesKey("operating_mode")

    val mode: Flow<String?> = context.dataStore.data.map { it[modeKey] }

    suspend fun setMode(value: String) {
        context.dataStore.edit { it[modeKey] = value }
    }
}
