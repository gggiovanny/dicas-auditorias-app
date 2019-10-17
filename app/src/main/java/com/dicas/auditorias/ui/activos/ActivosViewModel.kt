package com.dicas.auditorias.ui.activos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import com.dicas.auditorias.data.ActivosRepository
import com.dicas.auditorias.data.model.Activo
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.ui.utils.ViewModelRecyclerBinding

class ActivosViewModel(private val repository: ActivosRepository) : ViewModel(),
    ViewModelRecyclerBinding<Activo> {

    val activos: LiveData<List<Activo>> = repository.activos
    val response: LiveData<ApiResponse> = repository.response

    private val _idActivoExistente: MutableLiveData<Int> = MutableLiveData()
    val idActivoExistente: LiveData<Int> = _idActivoExistente

    val recyclerActivosAdapter = RecyclerActivosAdapter(this, R.layout.layout_activo_item)

    fun callActivosAPI(
        apiKey: String,
        auditoriaActual: String,
        empresa: String? = null,
        departamento: String? = null,
        clasificacion: String? = null
    ) {
        repository.callActivosAPI(apiKey, auditoriaActual, empresa, departamento, clasificacion)
    }

    override fun getObjectAt(position: Int) = activos.value?.get(position)

    override fun setupRecyclerAdapter(activos: List<Activo>) {
        recyclerActivosAdapter.setActivosList(activos)
        recyclerActivosAdapter.notifyDataSetChanged()
    }

    fun getExistenciaGuardada(position: Int) {
        activos.value?.get(position)?.existencia_guardada == "1"
    }

    fun setActivoExistente(idActivo: Int) {
        _idActivoExistente.value = idActivo
        /** Esto actualiza el valor del livedata y hace que se refleje el cambio en la UI */
        // TODO("Handle cuando de marque como existente")
    }

    private fun setActivoNoEncontrado(idActivo: Int) {
        //TODO()
    }


    fun setActivoExistenciaActualAPI(
        apiKey: String,
        idAuditoria: Int,
        idActivo: Int,
        existencia: Boolean
    ) {
        //TODO("Borrar y llamar directamente del repositorio si no es necesario llamar a este metodo desde fuera")
        repository.setActivoExistenciaActualAPI(apiKey, idAuditoria, idActivo, existencia)
    }
}
