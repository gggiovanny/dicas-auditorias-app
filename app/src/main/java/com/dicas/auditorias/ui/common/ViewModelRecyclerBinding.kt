package com.dicas.auditorias.ui.common

interface ViewModelRecyclerBinding<T> {
    fun getObjectAt(position: Int): T?
}