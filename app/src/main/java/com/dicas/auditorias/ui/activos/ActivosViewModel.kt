package com.dicas.auditorias.ui.activos

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import com.dicas.auditorias.data.ActivosRepository
import com.dicas.auditorias.data.model.Activo
import com.dicas.auditorias.data.model.ApiResponse

class ActivosViewModel(private val repository: ActivosRepository) : ViewModel() {

    val activos: LiveData<List<Activo>> = repository.activos
    val response: LiveData<ApiResponse> = repository.response

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

    fun getActivoAt(position: Int): Activo? = activos.value?.get(position)

    fun setActivosInRecyclerAdapter(activos: List<Activo>) {
        recyclerActivosAdapter.setActivosList(activos)
        recyclerActivosAdapter.notifyDataSetChanged()
    }
}
