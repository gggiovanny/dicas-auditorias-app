package com.dicas.auditorias.data

import android.util.Log
import com.dicas.auditorias.data.api.ApiAdapter
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.LoggedInUser
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource{

    companion object {
        private const val TAG = "LoginDataSource"
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        var token: String? = null

        try {
            callTokenApi(username, password, object: ResponseCallback<String> {
                override fun onSucess(data: String) {
                    token = data
                }

                override fun onError(exception: Exception) {
                    throw exception
                }

            })
            Log.d(TAG, "login: token from callback=$token")
            val user = LoggedInUser(token!!)
            return Result.Success(user)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    private fun callTokenApi(username: String, password: String, responseCallback: ResponseCallback<String>?) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getClientService()
        val call = apiService.getToken(username, password)

        call.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("ERROR: ", t.message)
                t.stackTrace
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var apiResponse = ApiResponse()

                try {
                    apiResponse = ApiResponse(
                        status = (response.body() ?: return).get("status").asString,
                        description = (response.body() ?: return).get("description").asString
                    )
                    Log.d(TAG, "callToken.onResponse: Can not get the token!. status=${apiResponse.status}")
                    Log.d(TAG, "callToken.onResponse: description=${apiResponse.description}")
                } catch (e: Exception) {
                    responseCallback?.onError(IOException("Error getting token from API", e))
                }

                if(apiResponse.statusOk) {
                    try {
                        apiResponse.token = (response.body() ?: return).get("token").asString
                        Log.d(TAG, "callToken.onResponse: token=${apiResponse.token}")

                        responseCallback?.onSucess(apiResponse.token!!)
                    } catch (e: Throwable) {
                        responseCallback?.onError(IOException("Error getting token from API", e))
                    }
                }
            }
        })
    }
}

