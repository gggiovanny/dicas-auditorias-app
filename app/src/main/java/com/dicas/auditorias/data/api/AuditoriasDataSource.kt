package com.dicas.auditorias.data.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class AuditoriasDataSource {

    companion object {
        private const val TAG = "AuditoriasDataSource"
    }

    private val _auditorias = MutableLiveData<List<Auditoria>>()
    val auditorias: LiveData<List<Auditoria>> = _auditorias

    private val _response = MutableLiveData<ApiResponse>()
    val response: LiveData<ApiResponse> = _response


    fun callAuditoriaAPI(apiKey: String, user: String =  "", status: String = "") {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable  = apiService.getAuditorias(user, status)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(this::AuditoriasResponseHandler, this::ErrorHandler)
    }

    private fun AuditoriasResponseHandler(responseJson: JsonObject) {

        val responseObject = ApiResponse (
            status = responseJson.get("status").asString,
            description = responseJson.get("description").asString
        )
        Log.d(TAG, "AuditoriasResponseHandler: status=${responseObject.status}")
        Log.d(TAG, "AuditoriasResponseHandler: description=[${responseObject.description}]")

        if(responseObject.statusOk) {
            val listJson = responseJson.get("list").asJsonArray

            val auditoriasList = ArrayList<Auditoria>()

            listJson?.forEach {
                val auditoriaJson = it.asJsonObject

                val auditoria = Auditoria(
                    auditoriaJson.get("id").asString,
                    auditoriaJson.get("fechaCreacion").asString,
                    auditoriaJson.get("status").asString,
                    auditoriaJson.get("descripcion").asString
                )
                auditoriasList.add(auditoria)
            }
            _auditorias.value = auditoriasList
            _response.value = responseObject
        }
    }

    private fun ErrorHandler(error: Throwable) {
        throw IOException("Error getting Auditorias from API", error)
    }
}