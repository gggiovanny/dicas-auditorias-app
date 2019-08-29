package com.dicas.auditorias.data.model

data class Activo(
    val id: String,
    val descripcion: String,
    val existencia_guardada: String? = null,
    val existencia_actual: String? = null,
    val fecha_existencia: String? = null,
    val id_auditoria_existencia: String? = null,
    val auditoria_autor: String? = null,
    val idClasificacion: String,
    val idDepartamento: String? = null,
    val idEmpresa: String? = null,
    val ultimo_movimiento: String? = null
)