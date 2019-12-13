package com.dicas.auditorias.ui.common

enum class ActivoExistenciaActualEnum(text: String) {
    FALSE("No encontrado"),
    TRUE("Encontrado"),
    NULL("Sin definir");

    val text = text
    override fun toString(): String = this.text
}

/*

        $existencia_actual_enum = [
            "0"     => 'No encontrado',
            "1"     => 'Encontrado',
            "null"  => 'Sin definir'
        ];

 */