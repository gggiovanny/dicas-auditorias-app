package com.dicas.auditorias.ui.activos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.Activo
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.data.model.LoggedInUser
import com.dicas.auditorias.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_activos.*

class ActivosFragment : Fragment() {

    companion object {
        fun newInstance() = ActivosFragment()
        private const val TAG = "ActivosFragment"

    }

    private lateinit var viewModel: ActivosViewModel
    private lateinit var userData: LoggedInUser
    private lateinit var auditoriaActiva: Auditoria

    private var firstError = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /** Obteniendo los datos de usuario que vienen del login */
        try {
            userData = arguments?.getParcelable<LoggedInUser>("user_data")!!
        } catch (ex: Throwable) {
            throw Exception("$TAG: No se recibieron los datos del usuario desde el login!", ex)
        }

        /** Obteniendo auditoria activa */
        try {
            auditoriaActiva = arguments?.getParcelable<Auditoria>("auditoria_activa")!!
        } catch (ex: Throwable) {
            throw Exception("$TAG: No se recibió la auditoria activa!", ex)
        }

        /** Inicializando view model */
        viewModel = ViewModelProviders.of(this, ActivosViewModelFactory())
            .get(ActivosViewModel::class.java)
        setupLoginIfExpiredToken()

        return inflater.inflate(R.layout.fragment_activos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ActivosViewModel::class.java)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rv_activos.adapter = viewModel.recyclerActivosAdapter

        viewModel.callActivosAPI(
            apiKey = userData.token,
            auditoriaActual = auditoriaActiva.id
        )

        viewModel.activos.observe(this, Observer { auditorias: List<Activo> ->
            viewModel.setActivosInRecyclerAdapter(auditorias)
            Log.d(TAG, "setupRecyclerView: observe done!")
        })
    }

    private fun setupLoginIfExpiredToken() {
        viewModel.response.observe(this, Observer {
            val response: ApiResponse = it ?: return@Observer
            Log.d(TAG, "setupLoginIfExpiredToken: ${response.status}: ${response.description}")
            if (!response.isOk && firstError) {
                if (!(response.status ?: return@Observer).contains("app")) {
                    firstError = false
                    showSesionCaducada(response)
                    val login = Intent(context, LoginActivity::class.java).apply {
                        putExtra("login_failed", true)
                    }
                    startActivity(login)
                    this.activity?.finish()
                } else {
                    Toast.makeText(
                        context, R.string.error_api,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        Log.d(TAG, "setupLoginIfExpiredToken: created observer done!")
    }

    private fun showSesionCaducada(response: ApiResponse) {
        if (response.description == "Expired token") {
            Log.d(TAG, "showSesionCaducada: ${response.status}: ${response.description}")
            Toast.makeText(context, R.string.invalid_token, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "${response.status}: ${response.description}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
