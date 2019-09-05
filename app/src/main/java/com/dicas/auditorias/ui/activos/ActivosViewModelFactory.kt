package com.dicas.auditorias.ui.activos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicas.auditorias.data.ActivosRepository
import com.dicas.auditorias.data.api.ActivosDataSource

class ActivosViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivosViewModel::class.java)) {
            return ActivosViewModel(
                repository = ActivosRepository(
                    dataSource = ActivosDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}