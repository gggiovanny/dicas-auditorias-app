package com.dicas.auditorias.models

import android.util.Log
import com.google.gson.JsonObject
import java.io.Serializable

class Activo(jsonActivo: JsonObject) : Serializable
{
    lateinit var id                  : String
    lateinit var descripcion         : String
    lateinit var conteo_guardado     : String
    lateinit var conteo_actual       : String
    lateinit var fecha_conteo        : String
    lateinit var id_auditoria_conteo : String
    lateinit var id_clasificacion    : String
    lateinit var id_departamento     : String
    lateinit var id_empresa          : String
    lateinit var ultimo_movimiento   : String


    init {
        Log.d("Activo", "Trying to fill object")
        try {
            id                  = jsonActivo!!.get(ID                 ).asString
            descripcion         = jsonActivo!!.get(DESCRIPCION        ).asString
            conteo_guardado     = jsonActivo!!.get(CONTEO_GUARDADO    ).asString
            conteo_actual       = jsonActivo!!.get(CONTEO_ACTUAL      ).asString
            fecha_conteo        = jsonActivo!!.get(FECHA_CONTEO       ).asString
            id_auditoria_conteo = jsonActivo!!.get(ID_AUDITORIA_CONTEO).asString
            id_clasificacion    = jsonActivo!!.get(ID_CLASIFICACION   ).asString
            id_departamento     = jsonActivo!!.get(ID_DEPARTAMENTO    ).asString
            id_empresa          = jsonActivo!!.get(ID_EMPRESA         ).asString
            ultimo_movimiento   = jsonActivo!!.get(ULTIMO_MOVIMIENTO  ).asString
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val ID                    = "idActivoFijo"
        private const val DESCRIPCION           = "descripcion"
        private const val CONTEO_GUARDADO       = "conteo_guardado"
        private const val CONTEO_ACTUAL         = "conteo_actual"
        private const val FECHA_CONTEO          = "fecha_conteo"
        private const val ID_AUDITORIA_CONTEO   = "id_auditoria_conteo"
        private const val ID_CLASIFICACION      = "idClasificacion"
        private const val ID_DEPARTAMENTO       = "idDepartamento"
        private const val ID_EMPRESA            = "idEmpresa"
        private const val ULTIMO_MOVIMIENTO     = "ultimo_movimiento"
    }
}