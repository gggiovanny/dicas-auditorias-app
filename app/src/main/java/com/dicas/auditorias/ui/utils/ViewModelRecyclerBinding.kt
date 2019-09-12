package com.dicas.auditorias.ui.utils

interface ViewModelRecyclerBinding<T> {
    fun getObjectAt(position: Int): T?
    fun setupRecyclerAdapter(dataObject: List<T>)
}