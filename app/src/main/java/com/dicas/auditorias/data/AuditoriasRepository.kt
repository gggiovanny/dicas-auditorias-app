package com.dicas.auditorias.data

import androidx.lifecycle.MutableLiveData
import com.dicas.auditorias.data.api.AuditoriasDataSource
import com.dicas.auditorias.data.model.*

class AuditoriasRepository(val dataSource: AuditoriasDataSource) {

    val auditorias: MutableLiveData<ArrayList<Auditoria>> = dataSource.auditorias
    val empresas: MutableLiveData<ArrayList<Empresa>> = dataSource.empresas
    val departamentos: MutableLiveData<ArrayList<Departamento>> = dataSource.departamentos
    val clasificaciones: MutableLiveData<ArrayList<Clasificacion>> = dataSource.clasificaciones
    val response: MutableLiveData<ApiResponse> = dataSource.response

    fun callAuditoriasAPI(apikey: String, user: String =  "", status: String = "") {
        dataSource.callAuditoriasAPI(apikey, user, status)
    }

    fun createAuditoriaAPI(
        apiKey: String,
        descripcion: String = "",
        empresa: String = "",
        departamento: String = "",
        clasificacion: String = ""
    ) {
        dataSource.createAuditoriaAPI(apiKey, descripcion, empresa, departamento, clasificacion)
    }

    fun callEmpresasApi(apikey: String) {
        dataSource.callEmpresasAPI(apikey)
    }

    fun callDepartamentoApi(apikey: String, empresaID: Int) {
        dataSource.callDepartamentosAPI(apikey, empresaID)
    }

    fun callClasificacionesAPI(apiKey: String) {
        dataSource.callClasificacionesAPI(apiKey)
    }
}
