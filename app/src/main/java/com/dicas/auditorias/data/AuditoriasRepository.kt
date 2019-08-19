package com.dicas.auditorias.data

import androidx.lifecycle.LiveData
import com.dicas.auditorias.data.api.AuditoriasDataSource
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria

class AuditoriasRepository(val dataSource: AuditoriasDataSource) {

    val auditorias: LiveData<List<Auditoria>> = dataSource.auditorias
    val response: LiveData<ApiResponse> = dataSource.response


    fun callAuditoriasAPI(apikey: String, user: String =  "", status: String = "") {
        dataSource.callAuditoriaAPI(apikey, user, status)
    }
}
