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
                try {
                    val status = (response.body() ?: return).get("status").asString
                    val description = (response.body() ?: return).get("description").asString
                    token.value = (response.body() ?: return).get("token").asString ?: ""

                    Log.d(TAG, "onResponse: status=$status")
                    Log.d(TAG, "onResponse: description=$description")
                    Log.d(TAG, "onResponse: etoken=${token.value}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }



            }
        })
    }
}