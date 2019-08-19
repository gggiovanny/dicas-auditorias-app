package com.dicas.auditorias.data.model

data class Auditoria(
    val id: String,
    val fechaCreacion: String,
    val status: String,
    val descripcion: String,
    val username: String,
    val terminada: String,
    val fechaGuardada: String
) {
    companion object {
        val NAME = "Auditoria"
    }
}