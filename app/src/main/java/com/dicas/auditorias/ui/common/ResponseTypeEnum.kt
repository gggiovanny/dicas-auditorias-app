package com.dicas.auditorias.ui.common

enum class ResponseTypeEnum(text: String) {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    WARNING("WARNING"),
    ERROR("ERROR"),
    INTERNAL_ERROR("INTERNAL_ERROR")
    ;

    val text = text
    override fun toString(): String = text
}