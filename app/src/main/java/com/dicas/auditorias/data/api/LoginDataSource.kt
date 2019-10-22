package com.dicas.auditorias.data.api

import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

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
        throw IOException("Error getting token from API", error)
    }
}

