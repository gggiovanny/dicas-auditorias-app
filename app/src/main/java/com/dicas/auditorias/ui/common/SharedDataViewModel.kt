package com.dicas.auditorias.ui.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.data.model.LoggedInUser

class SharedDataViewModel : ViewModel() {
    var userDataSource: MutableLiveData<LoggedInUser> = MutableLiveData()

    val token: String
        get() = userDataSource.value?.token!!

    val username: String
        get() = userDataSource.value?.name!!

    val isDataFromMemory: Boolean
        get() = userDataSource.value?.fromMemory!!

    var auditoriaActiva: Auditoria? = null

}