package com.dicas.auditorias.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicas.auditorias.R
import com.dicas.auditorias.data.LoginRepository
import com.dicas.auditorias.data.Result
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.LoggedInUser

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }
    private val _loginState = MutableLiveData<LoginFormState>()
    val loginState: LiveData<LoginFormState> = _loginState

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        loginRepository.login(username, password) {responseJson ->

            var apiResponse = ApiResponse(
                status = responseJson.get("status").asString,
                description = responseJson.get("description").asString
            )

            if (apiResponse.isOk) {
                apiResponse.token = responseJson.get("token").asString
                apiResponse.username = responseJson.get("username").asString
                Log.d(TAG, "callToken.onResponse: token=${apiResponse.token}")

                try {
                    loginRepository.setLoggedInUser(apiResponse.token!!, apiResponse.username!!)
                    _loginResult.value = LoginResult(success = LoggedInUser(token = apiResponse.token!!, name = apiResponse.username))
                } catch (e: Throwable) {
                    _loginResult.value = LoginResult(error = R.string.login_failed, description = apiResponse.description)
                    throw Exception("$TAG: No token on response object!", e)
                }
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed, description = apiResponse.description)
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginState.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginState.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginState.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean = password.isNotEmpty()

    fun checkLocalToken() {
        val result = loginRepository.getTokenLocal()

        if (result is Result.Success)
        {
            _loginResult.value = LoginResult(success = result.data)
        }
        else
        {
            _loginResult.value = LoginResult(error = R.string.no_local_token)
        }
    }

    fun deleteLocalToken() {
        loginRepository.deleteTokenLocal()
    }
}
