package com.dicas.auditorias.data

import androidx.lifecycle.LiveData
import com.dicas.auditorias.data.api.ActivosDataSource
import com.dicas.auditorias.data.model.Activo
import com.dicas.auditorias.data.model.ApiResponse

class ActivosRepository(val dataSource: ActivosDataSource) {

    val activos: LiveData<List<Activo>> = dataSource.activos
    val response: LiveData<ApiResponse> = dataSource.response

    fun callActivosAPI(
        apiKey: String,
        auditoriaActual: String,
        empresa: String? = null,
        departamento: String? = null,
        clasificacion: String? = null
    ) {
        dataSource.callActivosAPI(apiKey, auditoriaActual, empresa, departamento, clasificacion)
    }

    fun setActivoExistenciaActualAPI(
        apiKey: String,
        idAuditoria: Int,
        idActivo: Int,
        existencia: Boolean
    ) {
        dataSource.setActivoExistenciaActualAPI(apiKey, idAuditoria, idActivo, existencia)
    }

}