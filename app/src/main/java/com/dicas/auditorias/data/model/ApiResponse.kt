package com.dicas.auditorias.data.model

data class ApiResponse(
    val status: String? = null,
    val description: String? = null,
    var token: String? = null,
    var username: String? = null
) {
    val isOk: Boolean
        get() = status == "ok"

    val hasToken: Boolean
        get() = token != null
}

