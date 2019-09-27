package com.dicas.auditorias.data

import androidx.lifecycle.LiveData
import com.dicas.auditorias.data.api.AuditoriasDataSource
import com.dicas.auditorias.data.model.*

class AuditoriasRepository(val dataSource: AuditoriasDataSource) {

    val auditorias: LiveData<List<Auditoria>> = dataSource.auditorias
    val empresas: LiveData<List<Empresa>> = dataSource.empresas
    val departamentos: LiveData<List<Departamento>> = dataSource.departamentos
    val clasificaciones: LiveData<List<Clasificacion>> = dataSource.clasificaciones
    val response: LiveData<ApiResponse> = dataSource.response

    fun callAuditoriasAPI(apikey: String, user: String =  "", status: String = "") {
        dataSource.callAuditoriasAPI(apikey, user, status)
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
