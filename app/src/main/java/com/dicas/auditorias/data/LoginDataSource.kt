package com.dicas.auditorias.data

import android.util.Log
import com.dicas.auditorias.data.api.ApiAdapter
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.LoggedInUser
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
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

    fun login(username: String, password: String, responseHandler: (jsonResponse: JsonObject) -> Unit) {
            callTokenApiObservable(username, password, responseHandler)
    }

    private fun callTokenApiObservable(username: String, password: String, responseHandler: (jsonResponse: JsonObject) -> Unit) {

        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getRequestInterface()
        val request = apiService.getToken(username, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(responseHandler, { e ->
                throw IOException("Error getting token from API", e)
            })
    }
}

