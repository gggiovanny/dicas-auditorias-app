package com.dicas.auditorias.ui.common

enum class AuditoriaStatus(text: String) {
    EN_CURSO("En curso"),
    TERMINADA("Terminada"),
    GUARDADA("Guardada");

    val text = text
    override fun toString(): String = this.text
}