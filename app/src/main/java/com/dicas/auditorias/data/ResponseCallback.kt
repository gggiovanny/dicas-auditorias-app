package com.dicas.auditorias.data

import java.lang.Exception

interface ResponseCallback<T : Any> {
    fun onSucess(data: T)
    fun onError(exception: Exception)
}