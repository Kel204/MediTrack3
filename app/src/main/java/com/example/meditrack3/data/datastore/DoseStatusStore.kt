package com.example.meditrack3.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "dose_status")

object DoseStatusStore {

    suspend fun save(
        context: Context,
        key: String,
        taken: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(key)] = taken
        }
    }

    suspend fun loadAll(context: Context): Map<String, Boolean> {
        val prefs = context.dataStore.data.first()

        return prefs.asMap()
            .mapNotNull { entry ->
                val key = entry.key.name
                val value = entry.value as? Boolean
                value?.let { key to it }
            }
            .toMap()
    }
}
