package com.dicas.auditorias.data.model

data class Clasificacion(
    val id: String,
    val nombre: String
) {
    override fun toString(): String {
        return nombre
    }
}