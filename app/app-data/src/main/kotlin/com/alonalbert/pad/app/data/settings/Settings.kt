package com.alonalbert.pad.app.data.settings

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

suspend fun <T> Context.getSetting(key: Preferences.Key<T>): T? = dataStore.data.first()[key]

val SERVER = stringPreferencesKey("server")
val USERNAME = stringPreferencesKey("username")
val PASSWORD = stringPreferencesKey("password")
val LOGGED_IN = booleanPreferencesKey("loggedIn")

context(CoroutineScope)
fun Application.updateSettings(block: suspend MutablePreferences.() -> Unit) {
  launch {
    dataStore.updateData {
      it.toMutablePreferences().apply {
        block()
      }
    }
  }
}
