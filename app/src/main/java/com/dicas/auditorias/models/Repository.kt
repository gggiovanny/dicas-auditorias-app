package com.dicas.auditorias.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class Repository {
    companion object {
        private const val TAG = "Repository"
    }

    private var token = MutableLiveData<String>()

    fun getToken() = token

    fun callToken(username: String, password: String) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getClientService()
        val call = apiService.getToken(username, password)

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("ERROR: ", t.message)
                t.stackTrace
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var status = ""
                var description = ""
                try {
                    status = (response.body() ?: return).get("status").asString
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if(status == "ok") {
                    try {
                        token.value = (response.body() ?: return).get("token").asString ?: ""
                        Log.d(TAG, "callToken.onResponse: token=${token.value}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    description = (response.body() ?: return).get("description").asString
                    Log.d(TAG, "callToken.onResponse: Can not get the token!. status=$status")
                    Log.d(TAG, "callToken.onResponse: description=$description")
                    token.value=""
                }
            }
        })
    }
}