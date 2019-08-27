package com.dicas.auditorias.data.model

data class Auditoria(
    val id: String,
    val fechaCreacion: String,
    val status: String,
    val descripcion: String? = null
)