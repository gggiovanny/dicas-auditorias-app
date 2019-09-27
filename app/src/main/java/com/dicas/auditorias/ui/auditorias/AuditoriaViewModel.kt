package com.dicas.auditorias.ui.auditorias

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import com.dicas.auditorias.data.AuditoriasRepository
import com.dicas.auditorias.data.model.*

class AuditoriaViewModel(private val repository: AuditoriasRepository) : ViewModel() {

    val auditorias: LiveData<List<Auditoria>> = repository.auditorias
    val empresas: LiveData<List<Empresa>> = repository.empresas
    val departamentos: LiveData<List<Departamento>> = repository.departamentos
    val clasificaciones: LiveData<List<Clasificacion>> = repository.clasificaciones
    val response: LiveData<ApiResponse> = repository.response

    val recyclerAuditoriasAdapter = RecyclerAuditoriasAdapter(this, R.layout.layout_auditoria_item)

    fun callAuditorias(apikey: String, user: String =  "", status: String = "") {
        repository.callAuditoriasAPI(apikey, user, status)
    }

    fun getAuditoriaAt(index: Int): Auditoria? = auditorias.value?.get(index)

    fun setAuditoriasInRecyclerAdapter(auditorias: List<Auditoria>) {
        recyclerAuditoriasAdapter.setAuditoriasList(auditorias)
        recyclerAuditoriasAdapter.notifyDataSetChanged()
    }

    fun setRecyclerClickListener(listener: (index: Int) -> Unit) {
        recyclerAuditoriasAdapter.setOnClickListenner(listener)
        recyclerAuditoriasAdapter.notifyDataSetChanged()
    }



    fun callEmpresas(apikey: String) {
        repository.callEmpresasApi(apikey)
    }

    fun callDepartamentos(apikey: String, empresaID: Int) {
        repository.callDepartamentoApi(apikey, empresaID)
    }

    fun callClasificacionesAPI(apiKey: String) {
        repository.callClasificacionesAPI(apiKey)
    }



}
