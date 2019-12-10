package com.dicas.auditorias.data.api

import android.util.Log
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Class that handles authentication w/ login credentials and retrieves userDataSource information.
 */
class LoginDataSource {

    fun login(username: String, password: String, responseHandler: (jsonResponse: JsonObject) -> Unit) {
            callTokenApi(username, password, responseHandler)
    }

    private fun callTokenApi(username: String, password: String, responseHandler: (jsonResponse: JsonObject) -> Unit) {

        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService()
        val request: Disposable = apiService.getToken(username, password)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(responseHandler, this::ErrorHandler)
    }

    private fun ErrorHandler(error: Throwable) {
        Log.e(TAG, "ErrorHandler: Error getting token from API")
        error.printStackTrace()
    }

    companion object {
        private const val TAG = "LoginDataSource"
    }
}

