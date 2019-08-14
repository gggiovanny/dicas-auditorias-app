package com.dicas.auditorias.viewmodel

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import com.dicas.auditorias.models.Repository
import kotlinx.android.synthetic.main.activity_login.*

class LoginViewModel(view: AppCompatActivity): ViewModel() {

    private val repository = Repository()
    private val preferenceName by lazy { Resources.getSystem().getString(R.string.preference_token_key) }
    private val tokenKey by lazy { Resources.getSystem().getString(R.string.saved_token_key) }
    private var token = ""
    lateinit var view: AppCompatActivity

    init {
        this.view = view
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }

    fun getTokenAPI(username: String, password: String) {
        repository.callToken(username, password)
        repository.getToken().observe(view, Observer {
            token = it
            storeTokenLocal()
            Log.d(TAG, "getToken: $it")
        })
    }

    fun storeTokenLocal() {
        val sharedPref = view?.getSharedPreferences(preferenceName, Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(tokenKey, token)
            apply()
        }
        Log.d(TAG, "storeTokenLocal: Saved!: $token")
    }

    fun getTokenLocal()
    {
        val sharedPref = view?.getSharedPreferences(preferenceName, Context.MODE_PRIVATE) ?: return
        token = sharedPref.getString(tokenKey, "") ?: ""
        Log.d(TAG, "getTokenLocal: $token")
    }

    fun getToken(username: String, password: String): String {
        if(token.isEmpty()) {
            getTokenLocal()

            if(token.isEmpty()) {
                getTokenAPI(username, password)

                if(token.isEmpty()) {
                    view.username.setText(R.string.login_wrong_user)
                }
            }

        }
        return token
    }
}