package com.dicas.auditorias.viewmodel


import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginViewModel(view: AppCompatActivity): ViewModel() {

    private val view: AppCompatActivity
    val loginService: LoginService

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    init {
        this.view = view
        loginService = LoginService(view)
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }



    fun getTokenAPI(username: String, password: String)
    {
        loginService.callTokenAPI(username, password)
        loginService.getTokenAPI().observe(view, Observer {
            if (it.isEmpty()) {
                view.login_status.setText(R.string.login_status_wrong_user)
                view.login_status.setTextColor(ContextCompat.getColor(view, R.color.error_red))
                view.login_status.visibility = View.VISIBLE
            } else {
                Log.d(TAG, "getTokenAPI: $it")
                view.login_status.setText(R.string.login_status_sucessful)
                view.login_status.setTextColor(ContextCompat.getColor(view, R.color.sucess_green))
                view.login_status.visibility = View.VISIBLE
            }
        })


    }

}