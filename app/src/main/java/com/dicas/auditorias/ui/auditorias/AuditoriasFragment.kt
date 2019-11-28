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
import androidx.recyclerview.widget.ItemTouchHelper
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.ui.common.SharedDataViewModel
import com.dicas.auditorias.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_auditoria.*
import kotlinx.android.synthetic.main.layout_toolbar_general.*


class AuditoriasFragment : Fragment() {

    companion object {
        fun newInstance() = AuditoriasFragment()
        private const val TAG = "AuditoriasFragment"

    }

    private lateinit var viewModel: AuditoriaViewModel
    private lateinit var sharedData: SharedDataViewModel
    private lateinit var navController: NavController

    private var firstError = true
    private var firstSucess = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /** Obteniendo los datos de usuario que vienen del login */
        sharedData = activity?.run {
            ViewModelProviders.of(this)[SharedDataViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        try {
            sharedData.userDataSource.value = arguments?.getParcelable("user_data")!!
        } catch (ex: Throwable) {
            throw Exception("$TAG: No se recibieron los datos del usuario desde el login!", ex)
        }

        /** Inicializando view model */
        viewModel = activity.run {
            ViewModelProviders.of(
                this ?: throw Exception("Invalid fragment activity"),
                AuditoriasViewModelFactory()
            ).get(AuditoriaViewModel::class.java)
        }



        setupResponseHandler()

        return inflater.inflate(R.layout.fragment_auditoria, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = Navigation.findNavController(view ?: return)

        setupRecyclerView()
        setupNuevaAuditoriaButton()
        setupScannerButton()
    }

    override fun onResume() {
        super.onResume()
        setLoading(true)
    }

    private fun callAPI() {
        viewModel.callAuditorias(apikey = sharedData.token)
    }

    private fun setupRecyclerView() {
        rv_auditorias.adapter = viewModel.recyclerAuditoriasAdapter

        val itemTouchHelper = ItemTouchHelper(SwipeHandler()).apply {
            attachToRecyclerView(rv_auditorias)
        }


        refresh_layout_auditoria.setOnRefreshListener {
            callAPI()
        }

        if (viewModel.auditorias.value == null) {
            callAPI()
        }


        viewModel.auditorias.observe(this, Observer { auditorias: List<Auditoria> ->
            viewModel.recyclerAuditoriasAdapter.notifyDataSetChanged()
            setLoading(false)
            Log.d(TAG, "auditorias observer: RecyclerView updated!")
        })

        /** clickListener */
        viewModel.setRecyclerClickListener { index ->

            val auditoriaActiva = viewModel.auditorias.value?.get(index)
            Log.d(TAG, "setupRecyclerView: Auditoria seleccionada: ${auditoriaActiva?.id}")
            openActivos(auditoriaActiva ?: return@setRecyclerClickListener)

        }

        /** statusChipClickListener */
        viewModel.setRecyclerStatusChipClickListener { index ->
            val idAuditoria: String =
                viewModel.getAuditoriaAt(index)?.id ?: return@setRecyclerStatusChipClickListener

            StatusDialogFragment.newInstance(idAuditoria).apply {

                setOnTerminadaListener { idAuditoria ->
                    viewModel.updateAuditoriaTerminadaStatus(
                        apiKey = sharedData.token,
                        idAuditoria = idAuditoria.toInt(),
                        datamodelIndex = index,
                        terminada = true,
                        onResponse = {
                            Toast.makeText(context, "#$idAuditoria terminada!", Toast.LENGTH_SHORT)
                                .show()
                            close()
                        }
                    )
                }

                setOnEnCursoListener { idAuditoria ->
                    viewModel.updateAuditoriaTerminadaStatus(
                        apiKey = sharedData.token,
                        idAuditoria = idAuditoria.toInt(),
                        datamodelIndex = index,
                        terminada = false,
                        onResponse = {
                            Toast.makeText(context, "#$idAuditoria en curso!", Toast.LENGTH_SHORT)
                                .show()
                            close()
                        }
                    )
                }

                setOnGuardadaListener { idAuditoria ->
                    viewModel.saveAuditoria(
                        apiKey = sharedData.token,
                        idAuditoria = idAuditoria.toInt(),
                        datamodelIndex = index,
                        onResponse = {
                            Toast.makeText(context, "#$idAuditoria guardada!", Toast.LENGTH_SHORT)
                                .show()
                            close()
                        }
                    )
                }

            }.show(requireFragmentManager(), "dialog")

        }

    }

    private fun setupResponseHandler() {
        viewModel.response.observe(this, Observer {
            val response: ApiResponse = it ?: return@Observer
            if (response.status == null) return@Observer

            Log.d(TAG, "setupResponseHandler: ${response.status}: ${response.description}")


            if(response.status.contains("show")) {
                Log.d(TAG, "setupResponseHandler: showing: ${response.description}")
                Toast.makeText(
                    context, response.description,
                    Toast.LENGTH_LONG
                ).show()
            }



            if (response.isOk) {
                if (firstSucess && sharedData.isDataFromMemory) {
                    firstSucess = false
                    Toast.makeText(
                        context,
                        "${getString(R.string.welcome)} ${sharedData.username}",
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
            navController.navigate(R.id.action_auditoriasFragment_to_nuevaAuditoria)
        }
    }

    private fun openActivos(auditoriaActiva: Auditoria) {
        val bundle = bundleOf("auditoria_activa" to auditoriaActiva)
        navController.navigate(R.id.action_auditoriasFragment_to_activosFragment, bundle)
    }

    private fun setupScannerButton() {
        img_scanner.setOnClickListener {
            navController.navigate(R.id.action_auditoriasFragment_to_scannerFragment)
        }
    }

    private fun setLoading(loading: Boolean) {


        when (loading) {
            true -> {
                if (viewModel.auditorias.value != null)
                    return

                rv_auditorias.visibility = View.GONE
                text_auditoria_activos_empty.visibility = View.GONE
                refresh_layout_auditoria.isRefreshing = true
            }

            false -> {
                if (viewModel.auditorias.value.isNullOrEmpty()) {
                    rv_auditorias.visibility = View.GONE
                    text_auditoria_activos_empty.visibility = View.VISIBLE
                } else {
                    rv_auditorias.visibility = View.VISIBLE
                    text_auditoria_activos_empty.visibility = View.GONE
                }

                refresh_layout_auditoria.isRefreshing = false
            }
        }


    }
}



