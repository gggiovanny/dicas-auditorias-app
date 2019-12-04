package com.dicas.auditorias.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.LoggedInUser
import com.dicas.auditorias.ui.common.afterTextChanged
import com.dicas.auditorias.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "LoginActivity"
    }

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        val loginFailed = intent.getBooleanExtra("login_failed", false)
        checkForSavedToken(loginFailed)

        loginViewModel.loginState.removeObservers(this)
        loginViewModel.loginState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            button_login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username_layout.error = getString(loginState.usernameError)
            } else {
                username_layout.error = null
            }

            if (loginState.passwordError != null) {
                password_layout.error = getString(loginState.passwordError)
            } else {
                password_layout.error = null
            }
        })

        loginViewModel.loginResult.removeObservers(this)
        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                if(loginResult.error != R.string.no_local_token) {
                    showLoginFailed(loginResult.error, loginResult.description)
                } else {
                    username.visibility = View.VISIBLE
                    password.visibility = View.VISIBLE
                    button_login.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                }
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                //Complete and destroy login activity once successful
                finish()
            }
            setResult(Activity.RESULT_OK)
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        if(loginViewModel.loginState.value?.isDataValid == true) {
                            loading.visibility = View.VISIBLE
                            loginViewModel.login(
                                username.text.toString(),
                                password.text.toString()
                            )
                        }
                }
                false
            }

            button_login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun updateUiWithUser(userData: LoggedInUser) {
        Log.d(TAG, "updateUiWithUser: Sucessful loged in!")

        if (!userData.fromMemory) {
            Toast.makeText(applicationContext, "${getString(R.string.welcome)} ${userData.name}", Toast.LENGTH_LONG)
                .show()
        }

        /** Creación de intent para abrir MainActivity si el login fue correcto
         * Se le envian los datos del usuario en el intent */
        val main = Intent(this, MainActivity::class.java).apply {
            putExtra("user_data", userData)
        }
        startActivity(main)
        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int, errorDescription: String? = null) {
        if (errorDescription == null)
            Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(applicationContext, "$errorDescription", Toast.LENGTH_SHORT).show()
    }

    private fun checkForSavedToken(loginFailed: Boolean) {
        if (!loginFailed) {
            username.visibility = View.GONE
            password.visibility = View.GONE
            button_login.visibility = View.GONE
            loading.visibility = View.VISIBLE
            val result = loginViewModel.checkLocalToken()
        } else {
            loginViewModel.deleteLocalToken()
        }
    }
}

