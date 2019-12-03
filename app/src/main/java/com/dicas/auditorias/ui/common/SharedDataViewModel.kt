package com.dicas.auditorias.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.LoggedInUser
import com.dicas.auditorias.ui.login.LoginActivity

class SharedDataViewModel : ViewModel() {
    var userDataSource: MutableLiveData<LoggedInUser> = MutableLiveData()

    val token: String
        get() = userDataSource.value?.token!!

    val username: String
        get() = userDataSource.value?.name!!

    val isDataFromMemory: Boolean
        get() = userDataSource.value?.fromMemory!!

    var firstError = true
    var firstSucess = true

    fun handleGlobalResponse(context: Context, activity: Activity, response: ApiResponse) {
        Log.d(TAG, "handleGlobalResponse: ${response.status}: ${response.description}")

        when (response.isOk) {
            true -> {
                if (firstSucess && isDataFromMemory) {
                    firstSucess = false
                    Toast.makeText(
                        context,
                        "${context.getString(R.string.welcome)} ${username}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            false -> {
                // Cuando la respuesta no es satisfactoria, mostrar indistintamente el mensaje de la API
                Toast.makeText(context, response.description, Toast.LENGTH_LONG).show()


                if (firstError && response.tipo == ResponseTypeEnum.LOGIN_FAILED.toString()) {
                    firstError = false
                    val login = Intent(context, LoginActivity::class.java).apply {
                        putExtra("login_failed", true)
                    }
                    startActivity(context, login, null)
                    activity.finish()
                }
            }

        }
    }


    companion object {
        private const val TAG = "SharedDataViewModel"
    }

}