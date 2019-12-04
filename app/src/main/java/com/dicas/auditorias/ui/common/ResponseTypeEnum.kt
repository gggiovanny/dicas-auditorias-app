package com.dicas.auditorias.ui.common

enum class ResponseTypeEnum(text: String) {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    WARNING("WARNING"),
    ERROR("ERROR"),
    INTERNAL_ERROR("INTERNAL_ERROR"),
    LOGIN_FAILED("LOGIN_FAILED"),
    DO_NOT_SHOW("DO_NOT_SHOW")
    ;

    val text = text
    override fun toString(): String = text
}
