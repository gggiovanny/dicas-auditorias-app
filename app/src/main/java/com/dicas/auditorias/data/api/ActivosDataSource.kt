package com.dicas.auditorias.data.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicas.auditorias.data.model.Activo
import com.dicas.auditorias.data.model.ApiResponse
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class ActivosDataSource {

    companion object {
        private const val TAG = "ActivosDataSource"
    }

    private val _activos = MutableLiveData<List<Activo>>()
    val activos: LiveData<List<Activo>> = _activos

    private val _response = MutableLiveData<ApiResponse>()
    val response: LiveData<ApiResponse> = _response

    fun callActivosAPI(
        apiKey: String,
        auditoriaActual: String,
        empresa: String? = null,
        departamento: String? = null,
        clasificacion: String? = null
    ) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable = apiService.getActivos(
            auditoria_actual = auditoriaActual,
            empresa = empresa ?: "",
            departamento = departamento ?: "",
            clasificacion = clasificacion ?: ""
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ responseJson: JsonObject ->
                val responseObject = ApiResponse(
                    status = responseJson.get("status").asString,
                    description = responseJson.get("description").asString
                )
                _response.value = responseObject
                Log.d(TAG, "callActivosAPI: status=${responseObject.status}")
                Log.d(TAG, "callActivosAPI: description=[${responseObject.description}]")

                if (responseObject.isOk) {
                    val listJson = responseJson.get("list").asJsonArray

                    val activosList = ArrayList<Activo>()

                    listJson?.forEach {
                        val activoJson = it.asJsonObject

                        val activo = Activo(
                            id = activoJson.get("idActivoFijo").asString,
                            descripcion = activoJson.get("descripcion").asString,
                            existencia_guardada = activoJson.get("existencia_guardada")?.asString,
                            existencia_actual = activoJson.get("existencia_actual")?.asString,
                            fecha_existencia = activoJson.get("fecha_existencia")?.asString,
                            id_auditoria_existencia = activoJson.get("id_auditoria_existencia")?.asString,
                            auditoria_autor = activoJson.get("auditoria_autor")?.asString,
                            idClasificacion = activoJson.get("idClasificacion").asString,
                            idDepartamento = activoJson.get("idDepartamento")?.asString,
                            idEmpresa = activoJson.get("idEmpresa")?.asString,
                            ultimo_movimiento = activoJson.get("ultimo_movimiento")?.asString
                        )
                        activosList.add(activo)
                    }
                    _activos.value = activosList
                }
            }, this::ErrorHandler)
    }

    private fun ErrorHandler(error: Throwable) {
        throw IOException("Error getting token from API", error)
    }

    fun setActivoExistenciaActualAPI(
        apiKey: String,
        idAuditoria: Int,
        idActivo: Int,
        existencia: Boolean
    ) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable = apiService.setActivoExistenciaActual(
            id_auditoria = idAuditoria,
            id_activo = idActivo,
            existencia = existencia
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ responseJson: JsonObject ->
                val responseObject = ApiResponse(
                    status = responseJson.get("status").asString,
                    description = responseJson.get("description").asString
                )
                /** La respuesta de la API se manda como objeto de respuesta directamente */
                Log.d(TAG, "setActivoExistenciaActual: status=${responseObject.status}")
                Log.d(TAG, "setActivoExistenciaActual: description=[${responseObject.description}]")
                _response.value = responseObject
            }, {
                it.printStackTrace()
                _response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo crear la nueva auditoria"
                )

            })
    }

}