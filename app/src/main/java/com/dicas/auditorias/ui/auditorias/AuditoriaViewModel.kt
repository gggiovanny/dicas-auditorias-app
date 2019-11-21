package com.dicas.auditorias.ui.auditorias

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import com.dicas.auditorias.data.AuditoriasRepository
import com.dicas.auditorias.data.model.*

class AuditoriaViewModel(private val repository: AuditoriasRepository) : ViewModel() {

    val auditorias: MutableLiveData<ArrayList<Auditoria>> = repository.auditorias
    val empresas: MutableLiveData<ArrayList<Empresa>> = repository.empresas
    val departamentos: MutableLiveData<ArrayList<Departamento>> = repository.departamentos
    val clasificaciones: MutableLiveData<ArrayList<Clasificacion>> = repository.clasificaciones
    val response: MutableLiveData<ApiResponse> = repository.response

    val recyclerAuditoriasAdapter = RecyclerAuditoriasAdapter(this, R.layout.layout_auditoria_item)

    fun callAuditorias(apikey: String, user: String = "", status: String = "") {
        repository.callAuditoriasAPI(apikey, user, status)
    }

    fun createAuditoria(
        apiKey: String,
        descripcion: String = "",
        empresa: String = "",
        departamento: String = "",
        clasificacion: String = ""
    ) {
        repository.createAuditoriaAPI(apiKey, descripcion, empresa, departamento, clasificacion)
    }

    fun getAuditoriaAt(index: Int): Auditoria? = auditorias.value?.get(index)

    fun setRecyclerClickListener(listener: (index: Int) -> Unit) {
        recyclerAuditoriasAdapter.clickListener = listener
        recyclerAuditoriasAdapter.notifyDataSetChanged()
    }

    fun setRecyclerStatusChipClickListener(listener: (index: Int) -> Unit) {
        recyclerAuditoriasAdapter.statusChipClickListener = listener
        recyclerAuditoriasAdapter.notifyDataSetChanged()
    }

    fun callEmpresas(apikey: String) {
        repository.callEmpresasApi(apikey)
    }

    fun callDepartamentos(apikey: String, empresaID: Int) {
        repository.callDepartamentoApi(apikey, empresaID)
    }

    fun callClasificaciones(apiKey: String) {
        repository.callClasificacionesAPI(apiKey)
    }


}
