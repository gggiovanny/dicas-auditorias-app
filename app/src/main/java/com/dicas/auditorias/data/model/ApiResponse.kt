package com.dicas.auditorias.data.model

data class ApiResponse(
    val status: String? = null,
    val description: String? = null,
    var token: String? = null,
    var list: String? = null
) {
    val statusOk: Boolean
        get() = status == "ok"

    val hasToken: Boolean
        get() = token != null

    val hasList: Boolean
        get() = list != null
}

