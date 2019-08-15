package com.dicas.auditorias.viewmodel

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dicas.auditorias.R

class LoginService(view: AppCompatActivity) {

    private val view: AppCompatActivity
    private var loginStatus = false
    private var token = MutableLiveData<String>()

    init {
        this.view = view
    }

    companion object {
        private const val TAG = "LoginService"
    }

    fun checkLoginStatus() {
        TODO()
    }

    fun getTokenAPI() = token

    fun callTokenAPI(username: String, password: String) {
        repository.callToken(username, password)
        repository.getToken().observe(view as LifecycleOwner, Observer {
            token.value = it
            storeTokenLocal(it)
        })
    }



}