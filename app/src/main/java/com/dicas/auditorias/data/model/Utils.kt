package com.dicas.auditorias.data.model

import com.google.gson.JsonObject

fun responseWrapper(responseJson: JsonObject): ApiResponse {
    if (responseJson.get("payload") == null)
        return ApiResponse(
            status = responseJson.get("status").asString,
            description = responseJson.get("description").asString,
            tipo = responseJson.get("tipo").asString
        )
    else
        return ApiResponse(
            status = responseJson.get("status").asString,
            description = responseJson.get("description").asString,
            tipo = responseJson.get("tipo").asString,
            payload = responseJson.get("payload").asString ?: ""
        )
}

