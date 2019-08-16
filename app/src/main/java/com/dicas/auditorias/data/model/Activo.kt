package com.dicas.auditorias.data.model

import android.util.Log
import com.google.gson.JsonObject
import java.io.Serializable

class Activo(jsonActivo: JsonObject) : Serializable {
    lateinit var id: String
    lateinit var descripcion: String
    lateinit var conteo_guardado: String
    lateinit var conteo_actual: String
    lateinit var fecha_conteo: String
    lateinit var id_auditoria_conteo: String
    lateinit var id_clasificacion: String
    lateinit var id_departamento: String
    lateinit var id_empresa: String
    lateinit var ultimo_movimiento: String

    init {
        Log.d(TAG, "Trying to fill object")
        try {
            id = jsonActivo!!.get("idActivoFijo").asString
            descripcion = jsonActivo!!.get("descripcion").asString
            conteo_guardado = jsonActivo!!.get("conteo_guardado").asString
            conteo_actual = jsonActivo!!.get("conteo_actual").asString
            fecha_conteo = jsonActivo!!.get("fecha_conteo").asString
            id_auditoria_conteo = jsonActivo!!.get("id_auditoria_conteo").asString
            id_clasificacion = jsonActivo!!.get("idClasificacion").asString
            id_departamento = jsonActivo!!.get("idDepartamento").asString
            id_empresa = jsonActivo!!.get("idEmpresa").asString
            ultimo_movimiento = jsonActivo!!.get("ultimo_movimiento").asString
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "Activo"
    }
}