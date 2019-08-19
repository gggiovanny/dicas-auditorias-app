package com.dicas.auditorias.ui.auditorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicas.auditorias.data.AuditoriasRepository
import com.dicas.auditorias.data.LoginRepository
import com.dicas.auditorias.data.api.AuditoriasDataSource
import com.dicas.auditorias.data.api.LoginDataSource
import com.dicas.auditorias.ui.login.LoginViewModel

class AuditoriasViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuditoriaViewModel::class.java)) {
            return AuditoriaViewModel(
                repository = AuditoriasRepository(
                    dataSource = AuditoriasDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}