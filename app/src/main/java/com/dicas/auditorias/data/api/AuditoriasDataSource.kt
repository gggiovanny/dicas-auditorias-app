package com.dicas.auditorias.data.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.data.model.Departamento
import com.dicas.auditorias.data.model.Empresa
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AuditoriasDataSource {

    companion object {
        private const val TAG = "AuditoriasDataSource"
    }

    private val _auditorias = MutableLiveData<List<Auditoria>>()
    val auditorias: LiveData<List<Auditoria>> = _auditorias

    private val _empresas = MutableLiveData<List<Empresa>>()
    val empresas: LiveData<List<Empresa>> = _empresas

    private val _departamentos= MutableLiveData<List<Departamento>>()
    val departamento: LiveData<List<Departamento>> = _departamentos

    private val _response = MutableLiveData<ApiResponse>()
    val response: LiveData<ApiResponse> = _response


    fun callAuditoriasAPI(apiKey: String, user: String =  "", status: String = "") {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable  = apiService.getAuditorias(user, status)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({responseJson: JsonObject ->
                val responseObject = ApiResponse (
                    status = responseJson.get("status").asString,
                    description = responseJson.get("description").asString
                )
                _response.value = responseObject
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
                            terminada = auditoriaJson.get("terminada").asString
                        )
                        auditoriasList.add(auditoria)
                    }
                    _auditorias.value = auditoriasList
                }
            }, {
                it.printStackTrace()
                _response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo consultar la auditoria"
                )
            })
    }

    fun callEmpresasAPI(apiKey: String) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable  = apiService.getEmpresas()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({responseJson: JsonObject ->
                val responseObject = ApiResponse (
                    status = responseJson.get("status").asString,
                    description = responseJson.get("description").asString
                )
                _response.value = responseObject
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
                    _empresas.value = empresasList
                }
            }, {
                it.printStackTrace()
                _response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo consultar la auditoria"
                )

            })
    }

    fun callDepartamentosAPI(apiKey: String, idEmpresa: Int) {
        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getApiService(apiKey)
        val request: Disposable  = apiService.getDepartamentos(idEmpresa.toString())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({responseJson: JsonObject ->
                val responseObject = ApiResponse (
                    status = responseJson.get("status").asString,
                    description = responseJson.get("description").asString
                )
                _response.value = responseObject
                Log.d(TAG, "DepartamentosResponseHandler: status=${responseObject.status}")
                Log.d(TAG, "DepartamentosResponseHandler: description=[${responseObject.description}]")

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
                    _departamentos.value = deptosList
                }
            }, {
                it.printStackTrace()
                _response.value = ApiResponse(
                    status = "error_app",
                    description = "No se pudo consultar la auditoria"
                )
            })
    }


}