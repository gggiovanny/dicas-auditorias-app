package com.dicas.auditorias.ui.activos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dicas.auditorias.BR
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.Activo
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.data.model.LoggedInUser
import com.dicas.auditorias.ui.login.LoginActivity
import com.google.android.material.appbar.AppBarLayout
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

        val binding: ViewDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_activos, container, false)
        bindAuditoria(binding)

        return binding.root //view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ActivosViewModel::class.java)

        setupRecyclerView()


        setupScrollFade(ArrayList<View>().apply {
            add(chipDepartamento)
            add(chipEmpresa)
        })
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

    private fun bindAuditoria(binding: ViewDataBinding) {
        binding.setVariable(BR.auditoriaAct, auditoriaActiva)
        binding.executePendingBindings()
    }

    private fun setupScrollFade(widgets: List<View>) {
        app_bar_layout.addOnOffsetChangedListener(object :
            AppBarLayout.BaseOnOffsetChangedListener<AppBarLayout> {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                try {
                    val alpha =
                        (appBarLayout!!.totalScrollRange + verticalOffset).toFloat() / appBarLayout.totalScrollRange
                    widgets.forEach { it.alpha = alpha }

                    if (alpha > 0)
                        widgets.forEach { it.visibility = View.VISIBLE }
                    else
                        widgets.forEach { it.visibility = View.INVISIBLE }

                } catch (e: Throwable) {
                    Exception(
                        "$TAG:setupScrollFade: No se pudo configurar el fade del los widgets al hacer scroll",
                        e
                    )
                }
            }

        })
    }

}
