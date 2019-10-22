package com.dicas.auditorias.data

import android.content.Context
import android.util.Log
import com.dicas.auditorias.App
import com.dicas.auditorias.R
import com.dicas.auditorias.data.api.LoginDataSource
import com.dicas.auditorias.data.model.LoggedInUser
import com.google.gson.JsonObject

/**
 * Class that requests authentication and userDataSource information from the remote data source and
 * maintains an in-memory cache of login status and userDataSource credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {
    companion object {
        private const val TAG = "LoginRepository"
    }
    // in-memory cache of the token
    var token: String? = null
        private set
    var username: String? = null
        private set

    val isLoggedIn: Boolean
        get() = token != null

    fun logout() {
        token = null
    }

    fun login(username: String, password: String, responseHandler: (jsonResponse: JsonObject) -> Unit) {
        // handle login
        dataSource.login(username, password, responseHandler)
    }

    fun setLoggedInUser(token: String, username: String) {
        this.token = token
        // If userDataSource credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        storeTokenLocal(token, username)
    }

    private fun storeTokenLocal(token: String, username: String) {
        val sharedPref = App.sApplication.applicationContext.getSharedPreferences(getPreferenceName(), Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(getTokenKey(), token)
            putString(getUsernameKey(), username)
            apply()
        }
        Log.d(TAG, "storeTokenLocal: Saved!: $token")
    }

    fun getTokenLocal(): Result<LoggedInUser> {
        val sharedPref = App.sApplication.applicationContext.getSharedPreferences(getPreferenceName(), Context.MODE_PRIVATE)
        token = sharedPref.getString(getTokenKey(), null)
        username = sharedPref.getString(getUsernameKey(), null)
        Log.d(TAG, "getTokenLocal: $token")

        if(token != null) {
            return Result.Success(LoggedInUser(token!!, username!!, fromMemory = true))
        } else
            return Result.Error(Exception("$TAG: No local token!"))
    }

    fun deleteTokenLocal() {
        val sharedPref = App.sApplication.applicationContext.getSharedPreferences(getPreferenceName(), Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            clear()
            apply()
        }
        Log.d(TAG, "deleteTokenLocal: Token deleted!")
    }

    private fun getTokenKey() = App.sApplication.applicationContext.getString(R.string.saved_token_key)
    private fun getUsernameKey() = App.sApplication.applicationContext.getString(R.string.saved_username_key)
    private fun getPreferenceName() = App.sApplication.applicationContext.getString(R.string.preference_token_key)

}
