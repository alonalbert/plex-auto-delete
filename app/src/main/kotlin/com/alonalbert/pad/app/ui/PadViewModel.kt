package com.alonalbert.pad.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alonalbert.pad.app.database.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch

class PadViewModel : ViewModel() {
    init {
    }

    fun getUsers(): LiveData<List<User>> {
        return object : LiveData<List<User>>() {
            init {
                viewModelScope.launch {
                    HttpClient(Android).use { client ->
                        val json = client.get("http://10.0.0.191:8080/api/users").bodyAsText()
                        postValue(Gson().fromJson(json, object : TypeToken<List<User>>() {}))
                    }
                }
            }
        }
    }
}