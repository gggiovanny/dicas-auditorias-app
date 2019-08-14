package com.dicas.auditorias.viewmodel

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dicas.auditorias.R
import com.dicas.auditorias.models.Repository

class LoginService(view: AppCompatActivity) {

    private val repository = Repository()
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

    private fun storeTokenLocal(token: String) {
        val sharedPref = view?.getSharedPreferences(getPreferenceName(), Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(getTokenKey(), token)
            apply()
        }
        Log.d(TAG, "storeTokenLocal: Saved!: $token")
    }

    private fun getTokenLocal() {
        val sharedPref = view?.getSharedPreferences(getPreferenceName(), Context.MODE_PRIVATE) ?: return
        token.value = sharedPref.getString(getTokenKey(), "") ?: ""
        Log.d(TAG, "getTokenLocal: $token")
    }

    private fun getTokenKey() = view.getString(R.string.saved_token_key)
    private fun getPreferenceName() = view.getString(R.string.preference_token_key)

}