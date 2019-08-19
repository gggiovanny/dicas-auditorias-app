package com.dicas.auditorias.ui.auditorias

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel;
import com.dicas.auditorias.R
import com.dicas.auditorias.data.AuditoriasRepository
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria

class AuditoriaViewModel(private val repository: AuditoriasRepository) : ViewModel() {

    val auditorias: LiveData<List<Auditoria>> = repository.auditorias
    val response: LiveData<ApiResponse> = repository.response

    val recyclerAuditoriasAdapter = RecyclerAuditoriasAdapter(this, R.layout.layout_auditoria_item)

    fun callAuditorias(apikey: String, user: String =  "", status: String = "")
    {
        repository.callAuditoriasAPI(apikey, user, status)
    }

    fun getAuditoriaAt(index: Int): Auditoria? = auditorias.value?.get(index)

    fun setAuditoriasInRecyclerAdapter(auditorias: List<Auditoria>) {
        recyclerAuditoriasAdapter.setAuditoriasList(auditorias)
        recyclerAuditoriasAdapter.notifyDataSetChanged()
    }

}
