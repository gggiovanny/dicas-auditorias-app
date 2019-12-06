package com.dicas.auditorias.data.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dicas.auditorias.data.model.*
import com.dicas.auditorias.ui.common.ResponseTypeEnum
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AuditoriasDataSource {

    companion object {
        private const val TAG = "AuditoriasDataSource"
    }

    val auditorias: MutableLiveData<ArrayList<Auditoria>> = MutableLiveData()
    val empresas: MutableLiveData<ArrayList<Empresa>> = MutableLiveData()
    val departamentos: MutableLiveData<ArrayList<Departamento>> = MutableLiveData()
    val clasificaciones: MutableLiveData<ArrayList<Clasificacion>> = MutableLiveData()
    val response: MutableLiveData<ApiResponse> = MutableLiveData()

    fun callAuditoriasAPI(apiKey: String, user: String = "", status: String = "") {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable = apiService.getAuditorias(user, status)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ responseJson: JsonObject ->
                val responseObject = ApiResponse(
                    status = responseJson.get("status").asString,
                    description = responseJson.get("description").asString,
                    tipo = responseJson.get("tipo").asString
                )
                response.value = responseObject
                Log.d(TAG, "AuditoriasResponseHandler: status=${responseObject.status}")
                Log.d(TAG, "AuditoriasResponseHandler: description=[${responseObject.description}]")

                if (responseObject.isOk) {
                    val listJson = responseJson.get("list").asJsonArray

                    val auditoriasList = ArrayList<Auditoria>()

                    listJson?.forEach {
                        val auditoriaJson = it.asJsonObject

                        val auditoria = Auditoria(
                            id = auditoriaJson.get("id").asString,
                            fechaCreacion = auditoriaJson.get("fechaCreacion").asString,
                            status = auditoriaJson.get("status").asString,
                            descripcion = auditoriaJson.get("descripcion")?.asString,
                            username = auditoriaJson.get("username").asString,
                            idEmpresa = auditoriaJson.get("idEmpresa")?.asString,
                            empresa = auditoriaJson.get("empresa")?.asString,
                            idDepartamento = auditoriaJson.get("idDepartamento")?.asString,
                            departamento = auditoriaJson.get("departamento")?.asString,
                            idClasificacion = auditoriaJson.get("idClasificacion")?.asString,
                            clasificacion = auditoriaJson.get("clasificacion")?.asString,
                            terminada = auditoriaJson.get("terminada").asString,
                            fechaGuardada = auditoriaJson.get("fechaGuardada")?.asString
                        )
                        auditoriasList.add(auditoria)
                    }
                    auditorias.value = auditoriasList
                }
            }, {
                it.printStackTrace()
                response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo consultar la auditoria",
                    tipo = ResponseTypeEnum.INTERNAL_ERROR.toString()
                )
            })
    }

    fun createAuditoriaAPI(
        apiKey: String,
        descripcion: String = "",
        empresa: String = "",
        departamento: String = "",
        clasificacion: String = ""
    ) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable = apiService.createAuditoria(
            descripcion = descripcion,
            empresa = empresa,
            departamento = departamento,
            clasificacion = clasificacion
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ responseJson: JsonObject ->
                val responseObject = ApiResponse(
                    status = responseJson.get("status").asString,
                    description = responseJson.get("description").asString,
                    tipo = responseJson.get("tipo").asString
                )

                Log.d(TAG, "createAuditoriaAPI: status=${responseObject.status}")
                Log.d(TAG, "createAuditoriaAPI: description=[${responseObject.description}]")
                Log.d(TAG, "createAuditoriaAPI: tipo=[${responseObject.tipo}]")

                if (responseObject.isOk) {
                    responseObject.payload = responseJson.get("id").asString
                    response.value = responseObject
                    Log.d(TAG, "createAuditoriaAPI: idAuditoria=[${responseObject.payload}]")
                } else {
                    response.value = responseObject
                }
            }, {

                it.printStackTrace()
                Log.d(TAG, "createAuditoriaAPI: anomaxd")
                response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo crear la nueva auditoria. Error en el servidor.",
                    tipo = ResponseTypeEnum.INTERNAL_ERROR.toString()
                )

            })
    }

    fun callEmpresasAPI(apiKey: String) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable = apiService.getEmpresas()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ responseJson: JsonObject ->
                val responseObject = ApiResponse(
                    status = responseJson.get("status").asString,
                    description = responseJson.get("description").asString,
                    tipo = responseJson.get("tipo").asString
                )
                response.value = responseObject
                Log.d(TAG, "EmpresasResponseHandler: status=${responseObject.status}")
                Log.d(TAG, "EmpresasResponseHandler: description=[${responseObject.description}]")

                if (responseObject.isOk) {
                    val listJson = responseJson.get("list").asJsonArray

                    val empresasList = ArrayList<Empresa>()

                    listJson?.forEach {
                        val empresaJson = it.asJsonObject

                        val empresa = Empresa(
                            empresaJson.get("idEmpresa").asString,
                            empresaJson.get("nombre").asString
                        )
                        empresasList.add(empresa)
                    }
                    empresas.value = empresasList
                }
            }, {
                it.printStackTrace()
                response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo consultar la auditoria",
                    tipo = ResponseTypeEnum.INTERNAL_ERROR.toString()
                )

            })
    }

    fun callDepartamentosAPI(apiKey: String, idEmpresa: Int) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable = apiService.getDepartamentos(idEmpresa.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ responseJson: JsonObject ->
                val responseObject = ApiResponse(
                    status = responseJson.get("status").asString,
                    description = responseJson.get("description").asString,
                    tipo = responseJson.get("tipo").asString
                )
                response.value = responseObject
                Log.d(TAG, "DepartamentosResponseHandler: status=${responseObject.status}")
                Log.d(
                    TAG,
                    "DepartamentosResponseHandler: description=[${responseObject.description}]"
                )

                if (responseObject.isOk) {
                    val listJson = responseJson.get("list").asJsonArray

                    val deptosList = ArrayList<Departamento>()

                    listJson?.forEach {
                        val empresaJson = it.asJsonObject

                        val departamento = Departamento(
                            empresaJson.get("idDepartamento").asString,
                            empresaJson.get("nombre").asString
                        )
                        deptosList.add(departamento)
                    }
                    departamentos.value = deptosList
                }
            }, {
                it.printStackTrace()
                response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo consultar la auditoria",
                    tipo = ResponseTypeEnum.INTERNAL_ERROR.toString()
                )
            })
    }

    fun callClasificacionesAPI(apiKey: String) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable = apiService.getClasificaciones()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ responseJson: JsonObject ->
                val responseObject = ApiResponse(
                    status = responseJson.get("status").asString,
                    description = responseJson.get("description").asString,
                    tipo = responseJson.get("tipo").asString
                )
                response.value = responseObject
                Log.d(TAG, "DepartamentosResponseHandler: status=${responseObject.status}")
                Log.d(
                    TAG,
                    "DepartamentosResponseHandler: description=[${responseObject.description}]"
                )

                if (responseObject.isOk) {
                    val listJson = responseJson.get("list").asJsonArray

                    val clasificacionesList = ArrayList<Clasificacion>()

                    listJson?.forEach {
                        val clasificacionJson = it.asJsonObject

                        val clasificacion = Clasificacion(
                            clasificacionJson.get("idClasificacion").asString,
                            clasificacionJson.get("nombre").asString
                        )
                        clasificacionesList.add(clasificacion)
                    }
                    clasificaciones.value = clasificacionesList
                }
            }, {
                it.printStackTrace()
                response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo consultar la auditoria",
                    tipo = ResponseTypeEnum.INTERNAL_ERROR.toString()
                )
            })
    }

    fun updateAuditoriaTerminadaStatus(
        apiKey: String,
        idAuditoria: Int,
        terminada: Boolean,
        onResponse: (responseJson: JsonObject) -> Unit
    ) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable = apiService.updateAuditoriaTerminadaStatus(
            id = idAuditoria,
            terminada = terminada
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(onResponse, {
                it.printStackTrace()
                response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo actualizar el estatus de la auditoria!",
                    tipo = ResponseTypeEnum.INTERNAL_ERROR.toString()
                )
            })
    }

    fun saveAuditoria(
        apiKey: String,
        idAuditoria: Int,
        onResponse: (responseJson: JsonObject) -> Unit
    ) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable = apiService.saveAuditoria(
            id = idAuditoria
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(onResponse, {
                it.printStackTrace()
                response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo finalizar y guardar la auditoria!",
                    tipo = ResponseTypeEnum.INTERNAL_ERROR.toString()
                )
            })
    }




}