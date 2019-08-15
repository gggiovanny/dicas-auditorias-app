package com.dicas.auditorias.data

import android.content.Context
import android.util.Log
import com.dicas.auditorias.App
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.LoggedInUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {
    companion object {
        private const val TAG = "LoginRepository"
    }
    // in-memory cache of the token
    var token: String? = null
        private set

    val isLoggedIn: Boolean
        get() = token != null

    fun logout() {
        token = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data.token)
        }

        return result
    }

    private fun setLoggedInUser(token: String) {
        this.token = token
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        storeTokenLocal(token)
    }

    private fun storeTokenLocal(token: String) {
        val sharedPref = App.sApplication.applicationContext.getSharedPreferences(getPreferenceName(), Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(getTokenKey(), token)
            apply()
        }
        Log.d(TAG, "storeTokenLocal: Saved!: $token")
    }

    private fun getTokenLocal(): String {
        val token: String
        val sharedPref = App.sApplication.applicationContext.getSharedPreferences(getPreferenceName(), Context.MODE_PRIVATE) ?: return ""
        token = sharedPref.getString(getTokenKey(), "") ?: ""
        Log.d(TAG, "getTokenLocal: $token")
        return token
    }

    private fun getTokenKey() = App.sApplication.applicationContext.getString(R.string.saved_token_key)
    private fun getPreferenceName() = App.sApplication.applicationContext.getString(R.string.preference_token_key)

}
