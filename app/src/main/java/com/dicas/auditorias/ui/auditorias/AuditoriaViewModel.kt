package com.dicas.auditorias.ui.auditorias

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import com.dicas.auditorias.data.AuditoriasRepository
import com.dicas.auditorias.data.model.*
import com.dicas.auditorias.ui.common.AuditoriaStatus
import com.google.gson.JsonObject

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

    fun updateAuditoriaTerminadaStatus(
        apiKey: String,
        idAuditoria: Int,
        datamodelIndex: Int,
        onResponse: (responseJson: JsonObject) -> Unit
    ) {
        // Se inicializa al estatus contrario del que esta actualmente para que se alterne su valor cada vez que se llame a este metodo
        val auditoriaTerminada = when (auditorias.value!![datamodelIndex].terminada) {
            "1" -> false
            else -> true
        }


        repository.updateAuditoriaTerminadaStatus(
            apiKey = apiKey,
            idAuditoria = idAuditoria,
            terminada = auditoriaTerminada,
            onResponse = {
                onResponse(it)
                when (auditoriaTerminada) {
                    true -> {
                        auditorias.value!![datamodelIndex].terminada = "1"
                        auditorias.value!![datamodelIndex].status =
                            AuditoriaStatus.TERMINADA.toString()
                        Log.d(
                            TAG,
                            "updateAuditoriaTerminadaStatus: Status actualizado a ${AuditoriaStatus.TERMINADA}"
                        )
                    }
                    false -> {
                        auditorias.value!![datamodelIndex].terminada = "0"
                        auditorias.value!![datamodelIndex].status =
                            AuditoriaStatus.EN_CURSO.toString()
                        Log.d(
                            TAG,
                            "updateAuditoriaTerminadaStatus: Status actualizado a ${AuditoriaStatus.EN_CURSO}"
                        )
                    }
                }
                recyclerAuditoriasAdapter.notifyItemChanged(datamodelIndex)
            }
        )


    }

    fun saveAuditoria(
        apiKey: String,
        idAuditoria: Int,
        datamodelIndex: Int,
        onResponse: (responseJson: JsonObject) -> Unit
    ) {
        repository.saveAuditoria(apiKey = apiKey,
            idAuditoria = idAuditoria, onResponse = {
                onResponse(it)
                auditorias.value!![datamodelIndex].status = AuditoriaStatus.GUARDADA.toString()
                Log.d(
                    TAG,
                    "updateAuditoriaTerminadaStatus: Status actualizado a ${AuditoriaStatus.GUARDADA}"
                )
                recyclerAuditoriasAdapter.notifyItemChanged(datamodelIndex)
            }
        )

    }

}
