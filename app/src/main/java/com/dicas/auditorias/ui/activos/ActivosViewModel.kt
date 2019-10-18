package com.dicas.auditorias.ui.activos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import com.dicas.auditorias.data.ActivosRepository
import com.dicas.auditorias.data.model.Activo
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.ui.utils.ViewModelRecyclerBinding

class ActivosViewModel(private val repository: ActivosRepository) : ViewModel(),
    ViewModelRecyclerBinding<Activo> {
    companion object {
        private const val TAG = "ActivosViewModel"
    }

    val activos: MutableLiveData<ArrayList<Activo>> = repository.activos
    val response: MutableLiveData<ApiResponse> = repository.response


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

    /** Esto actualiza el valor del livedata y ya que este esta siendo observado,
     * hace que se refleje el cambio en la UI */
    fun setActivoExistente(idActivo: Int) {
        val activosList = activos.value
        /** Se busca la id del activo proporcionada en los elementos de la lista del livedata  */
        val activoUpdating: Activo? = activosList?.find { activo -> activo.id.toInt() == idActivo }

        /** Si se encuentra, se actualiza su valor */
        if (activoUpdating != null) {
            val indexForUpdate = activosList.indexOf(activoUpdating)
            activoUpdating.existencia_actual = "1"
            activosList[indexForUpdate] = activoUpdating
            activos.value = activosList
            recyclerActivosAdapter.setActivosList(activosList)
            recyclerActivosAdapter.notifyItemChanged(indexForUpdate)
        } else {
            response.value = ApiResponse(
                status = "alert_show",
                description = "¡El activo escaneado no se encuentra en esta auditoria!"

            )
        }


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
