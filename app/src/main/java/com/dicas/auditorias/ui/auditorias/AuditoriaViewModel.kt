package com.dicas.auditorias.ui.auditorias

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel;
import com.dicas.auditorias.R
import com.dicas.auditorias.data.AuditoriasRepository
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.data.model.Departamento
import com.dicas.auditorias.data.model.Empresa

class AuditoriaViewModel(private val repository: AuditoriasRepository) : ViewModel() {

    val auditorias: LiveData<List<Auditoria>> = repository.auditorias
    val empresas: LiveData<List<Empresa>> = repository.empresas
    val departamento: LiveData<List<Departamento>> = repository.departamento
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

    fun callEmpresas(apikey: String) {
        repository.callEmpresasApi(apikey)
    }

    fun callDepartamentos(apikey: String, empresaID: Int) {
        repository.callDepartamentoApi(apikey, empresaID)
    }



}
