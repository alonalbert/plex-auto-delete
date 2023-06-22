package com.alonalbert.pad.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

suspend fun <T> Context.getSetting(key: Preferences.Key<T>): T? = dataStore.data.first()[key]

val SERVER = stringPreferencesKey("server")
val USERNAME = stringPreferencesKey("username")
val PASSWORD = stringPreferencesKey("password")
