package com.dicas.auditorias.data.model

import com.google.gson.JsonObject

fun ResponseWrapper(responseJson: JsonObject) = ApiResponse(
    status = responseJson.get("status").asString,
    description = responseJson.get("description").asString,
    tipo = responseJson.get("tipo").asString
)