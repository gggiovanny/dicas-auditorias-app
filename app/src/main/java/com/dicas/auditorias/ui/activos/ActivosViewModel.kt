package com.dicas.auditorias.ui.activos

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import com.dicas.auditorias.data.ActivosRepository
import com.dicas.auditorias.data.model.Activo
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.ui.common.ViewModelRecyclerBinding
import com.google.gson.JsonObject

class ActivosViewModel(private val repository: ActivosRepository) : ViewModel(),
    ViewModelRecyclerBinding<Activo> {
    companion object {
        private const val TAG = "ActivosViewModel"
    }

    val activos: MutableLiveData<ArrayList<Activo>> = repository.activos
    val response: MutableLiveData<ApiResponse> = repository.response

    var ultimaAuditoriaConsultada: String = "-1"

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

    /**
     * Esto actualiza el valor del livedata y ya que este esta siendo observado,
     * hace que se refleje el cambio en la UI
     * */
    fun setActivoExistencia(apiKey: String, idAuditoria: Int, idActivo: Int, existe: Boolean) {
        // Se busca la id del activo proporcionada en los elementos de la lista del livedata
        val activoUpdating: Activo? =
            activos.value?.find { activo -> activo.id.toInt() == idActivo }

        // Si se encuentra, se actualiza su valor en la interfaz y en la UI
        if (activoUpdating != null) {

            val indexForUpdate = activos.value?.indexOf(activoUpdating)

            try {
                // Actualizando la existencia en la API
                repository.setActivoExistenciaActualAPI(
                    apiKey = apiKey,
                    idAuditoria = idAuditoria,
                    idActivo = idActivo,
                    existencia = true,
                    onResponse = { responseJson: JsonObject ->
                        val responseObject = ApiResponse(
                            status = responseJson.get("status").asString,
                            description = responseJson.get("description").asString
                        )

                        Log.d(TAG, "setActivoExistenciaActual: status=${responseObject.status}")
                        Log.d(
                            TAG,
                            "setActivoExistenciaActual: description=[${responseObject.description}]"
                        )

                        if (existe)
                            activos.value!![indexForUpdate!!].existencia_actual = "1"
                        else
                            activos.value!![indexForUpdate!!].existencia_actual = "0"

                        recyclerActivosAdapter.notifyDataSetChanged()
                    }
                )



            } catch (e: Throwable) {
                response.value = ApiResponse(
                    status = "error_show",
                    description = "¡El activo escaneado no se encuentra en esta auditoria!"
                )
            }

        } else {
            response.value = ApiResponse(
                status = "alert_show",
                description = "¡El activo escaneado no se encuentra en esta auditoria!"

            )
        }
    }


}
