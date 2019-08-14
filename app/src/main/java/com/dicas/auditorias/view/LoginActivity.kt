package com.dicas.auditorias.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dicas.auditorias.R
import kotlinx.android.synthetic.main.activity_login.*
import com.dicas.auditorias.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var model: LoginViewModel = LoginViewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        button_login.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        var user = username.text.toString()
        val passwd = password.text.toString()

        model.getToken(user, passwd)
    }
}
