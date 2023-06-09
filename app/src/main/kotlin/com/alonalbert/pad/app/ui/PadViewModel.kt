package com.alonalbert.pad.app.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.database.PadDatabase
import com.alonalbert.pad.app.database.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PadViewModel @Inject constructor(private val database: PadDatabase) : ViewModel() {
    fun getUsers(): LiveData<List<User>> {
        val userDao = database.userDao()
        viewModelScope.launch(Dispatchers.IO) {
            HttpClient(Android).use { client ->
                try {
                    val json = client.get("http://10.0.0.74:8080/api/users").bodyAsText()
                    val users = Gson().fromJson(json, object : TypeToken<List<User>>() {})
                    userDao.insertAll(users)
                } catch (e: IOException) {
                    Log.e("PAD", "Error loading users", e)
                }
            }
        }

        return userDao.getAll()
    }
}
