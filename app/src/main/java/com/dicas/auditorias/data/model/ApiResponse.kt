package com.dicas.auditorias.data.model

data class ApiResponse(
    val status: String,
    val description: String,
    val tipo: String,
    var token: String? = null,
    var username: String? = null,
    var idAuditoria: String? = null
) {
    val isOk: Boolean
        get() = status == "ok"

    val hasToken: Boolean
        get() = token != null
}

