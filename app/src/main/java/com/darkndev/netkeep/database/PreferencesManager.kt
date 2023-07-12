package com.darkndev.netkeep.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "netKeep_preferences")
    private val dataStore = context.dataStore

    suspend fun updateToken(token: String) {
        dataStore.edit { preferences ->
            preferences[Keys.TOKEN_KEY] = token
        }
    }

    val token = dataStore.data.map { preferences ->
        preferences[Keys.TOKEN_KEY]
    }

    private object Keys {
        val TOKEN_KEY = stringPreferencesKey("com.darkndev.netkeep.database.TOKEN_KEY")
    }
}