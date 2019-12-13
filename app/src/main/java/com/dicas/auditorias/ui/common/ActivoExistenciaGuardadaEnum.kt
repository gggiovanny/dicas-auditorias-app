package com.dicas.auditorias.ui.common

enum class ActivoExistenciaGuardadaEnum(text: String) {
    FALSE("Existencia no registrada"),
    TRUE("Existencia registrada"),
    GUARDADA("Existencia desconocida");

    val text = text
    override fun toString(): String = this.text
}
/*
$existencia_guardada_enum = [
            "0"     => 'Existencia no registrada',
            "1"     => 'Existencia registrada',
            "null"  => 'Existencia desconocida'
        ];


 */