package com.dicas.auditorias.ui.common

enum class AuditoriaStatusEnum(text: String) {
    EN_CURSO("En curso"),
    TERMINADA("Terminada"),
    GUARDADA("Guardada");

    val text = text
    override fun toString(): String = this.text
}