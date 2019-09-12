package com.dicas.auditorias.ui.auditorias

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.*
import com.dicas.auditorias.ui.login.LoginActivity
import com.dicas.auditorias.ui.utils.OnItemSelectedListener
import com.dicas.auditorias.ui.utils.setupAppBarScrollFade
import kotlinx.android.synthetic.main.fragment_auditoria.*
import kotlinx.android.synthetic.main.layout_nueva_auditoria.*


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
        setupLoginIfExpiredToken()

        return inflater.inflate(R.layout.fragment_auditoria, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = Navigation.findNavController(view!!)


        loading.visibility = View.VISIBLE
        setupSpinners()
        setupRecyclerView()
        setupAppBarScrollFade(app_bar_layout, ArrayList<View>().apply {
            add(toolbar_spinners)
        })
        setupNuevaAuditoriaButton()
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

    private fun setupSpinners() {
        /** Creando los adapters */
        val adapterEmpresas =
            ArrayAdapter<Empresa>(context ?: return, android.R.layout.simple_spinner_item)
        with(adapterEmpresas) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            add(Empresa("-1", getString(R.string.elija_empresa_spinner)))
        }

        val adapterDeptos =
            ArrayAdapter<Departamento>(context ?: return, android.R.layout.simple_spinner_item)
        with(adapterDeptos) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            add(Departamento("-1", getString(R.string.elija_departamento_spinner)))
        }

        /** Llamando al API para obtener los datos y creando el observer para añadir la informacion cuando llegue */
        viewModel.callEmpresas(apikey = userData.token)
        viewModel.empresas.observe(this, Observer { empresas: List<Empresa> ->
            for (empresa in empresas)
                adapterEmpresas.add(empresa)
            Log.d(TAG, "setupSpinners: Observer empresas done!")
            loading.visibility = View.GONE
        })
        /** Los departamentos se actualizan cuando se cambia la empresa elegida para traer los de la misma*/
        viewModel.departamento.observe(this, Observer { departamentos: List<Departamento> ->
            loading.visibility = View.GONE
            with(adapterDeptos) {
                clear()
                add(Departamento("-1", getString(R.string.elija_departamento_spinner)))
            }
            departamento_spinner.setSelection(0)

            departamentos.ifEmpty {
                Toast.makeText(
                    context,
                    "Esta empresa no tiene departamentos dados de alta",
                    Toast.LENGTH_LONG
                ).show()
            }
            for (depto in departamentos)
                adapterDeptos.add(depto)
            Log.d(TAG, "setupSpinners: Observer departamentos done! ")
        })

        /** Seteando adapters y creando listeners */
        with(empresa_spinner) {
            adapter = adapterEmpresas
            OnItemSelectedListener { parent, position ->
                if (position > 0) {
                    loading.visibility = View.VISIBLE
                    val empresa: Empresa? = parent.getItemAtPosition(position) as? Empresa
                    Log.d(
                        TAG,
                        "setupSpinners: Selected: id=${empresa?.id}, nombre=${empresa?.nombre}"
                    )
                    /** Cuando Se elija una empresa, se cargan los departamentos de la misma */
                    Log.d(TAG, "setupSpinners: $position")

                    try {
                        viewModel.callDepartamentos(
                            apikey = userData.token,
                            empresaID = empresa!!.id.toInt()
                        )
                    } catch (e: Throwable) {
                        Exception(
                            "No se pudo consultar el spinner de departamentos debido a que la empresa es null",
                            e
                        )
                    }
                }
            }
        }
        with(departamento_spinner) {
            adapter = adapterDeptos
        }
    }

    private fun setupLoginIfExpiredToken() {
        viewModel.response.observe(this, Observer {
            val response: ApiResponse = it ?: return@Observer
            loading.visibility = View.GONE
            Log.d(TAG, "setupLoginIfExpiredToken: ${response.status}: ${response.description}")
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
                if (firstError && !(response.status ?: return@Observer).contains("app")) {
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

    private fun setupNuevaAuditoriaButton() {
        fab_nueva_auditoria.setOnClickListener {
            if (empresa_spinner.selectedItemId > 0) {
                val empresa = empresa_spinner.selectedItem as? Empresa
                Log.d(
                    TAG,
                    "Empresa_nombre: ${empresa_spinner.selectedItem} Empresa_id: ${empresa?.id} Empresa_pos: ${empresa_spinner.selectedItemPosition}}"
                )


                //TODO("Falta poner texto de descripcion de auditoria")
/*
                val nuevaAuditoria = Auditoria(
                    descripcion = "",
                    idEmpresa =
                )

                openActivos()*/
            }

        }
    }

    private fun newAuditoria(nuevaAuditoria: Auditoria): Boolean {
        val response: ApiResponse = ApiResponse("ok", "xd")
        return response.isOk
        //TODO("Crear metodo post para crear la auditoria")
    }

    private fun openActivos(auditoriaActiva: Auditoria) {
        val bundle = bundleOf("user_data" to userData, "auditoria_activa" to auditoriaActiva)
        navController.navigate(R.id.action_auditoriasFragment_to_activosFragment, bundle)
    }

}



