package com.dicas.auditorias.ui.auditorias

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.data.model.LoggedInUser
import com.dicas.auditorias.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_auditoria.*
import kotlinx.android.synthetic.main.layout_toolbar_general.*


class AuditoriasFragment : Fragment() {

    companion object {
        fun newInstance() = AuditoriasFragment()
        private const val TAG = "AuditoriasFragment"

    }

    private lateinit var viewModel: AuditoriaViewModel
    private lateinit var userData: LoggedInUser
    private lateinit var navController: NavController

    private var firstError = true
    private var firstSucess = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /** Obteniendo los datos de usuario que vienen del login */
        try {
            userData = arguments?.getParcelable("user_data")!!
        } catch (ex: Throwable) {
            throw Exception("$TAG: No se recibieron los datos del usuario desde el login!", ex)
        }

        /** Inicializando view model */
        viewModel = ViewModelProviders.of(this, AuditoriasViewModelFactory())
            .get(AuditoriaViewModel::class.java)
        setupResponseHandler()

        return inflater.inflate(R.layout.fragment_auditoria, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = Navigation.findNavController(view ?: return)


        loading.visibility = View.VISIBLE
        setupRecyclerView()
        setupNuevaAuditoriaButton()
        setupScannerButton()
    }


    private fun setupRecyclerView() {
        rvAuditorias.adapter = viewModel.recyclerAuditoriasAdapter

        viewModel.callAuditorias(apikey = userData.token)
        viewModel.auditorias.observe(this, Observer { auditorias: List<Auditoria> ->
            viewModel.setAuditoriasInRecyclerAdapter(auditorias)
            Log.d(TAG, "setupRecyclerView: observe done!")
            loading.visibility = View.GONE
        })

        /** clickListener */
        viewModel.setRecyclerClickListener { index ->

            val auditoriaActiva = viewModel.auditorias.value?.get(index)
            Log.d(TAG, "setupRecyclerView: Auditoria seleccionada: ${auditoriaActiva?.id}")
            openActivos(auditoriaActiva ?: return@setRecyclerClickListener)

        }

    }

    private fun setupResponseHandler() {
        viewModel.response.observe(this, Observer {
            val response: ApiResponse = it ?: return@Observer
            if (response.status == null) return@Observer

            loading.visibility = View.GONE
            Log.d(TAG, "setupResponseHandler: ${response.status}: ${response.description}")

            /*
            if(response.status.contains("show")) {
                Toast.makeText(
                    context, response.description,
                    Toast.LENGTH_LONG
                ).show()
            }

             */

            if (response.isOk) {
                if (firstSucess && userData.fromMemory) {
                    firstSucess = false
                    Toast.makeText(
                        context,
                        "${getString(R.string.welcome)} ${userData.name}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                if (firstError && !response.status.contains("app")) {
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
        Log.d(TAG, "setupResponseHandler: created observer done!")
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

    private fun setupNuevaAuditoriaButton() {
        fab_nueva_auditoria.setOnClickListener {
            val bundle = bundleOf("user_data" to userData)
            navController.navigate(R.id.action_auditoriasFragment_to_nuevaAuditoria, bundle)
        }
    }

    private fun openActivos(auditoriaActiva: Auditoria) {
        val bundle = bundleOf("user_data" to userData, "auditoria_activa" to auditoriaActiva)
        navController.navigate(R.id.action_auditoriasFragment_to_activosFragment, bundle)
    }

    private fun setupScannerButton() {
        img_scanner.setOnClickListener {
            navController.navigate(R.id.action_auditoriasFragment_to_scannerFragment)
        }
    }
}



