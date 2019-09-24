package com.dicas.auditorias.ui.auditorias


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Departamento
import com.dicas.auditorias.data.model.Empresa
import com.dicas.auditorias.data.model.LoggedInUser
import com.dicas.auditorias.ui.login.LoginActivity
import com.dicas.auditorias.ui.utils.OnItemSelectedListener
import kotlinx.android.synthetic.main.fragment_nueva_auditoria.*


class NuevaAuditoriaFragment : Fragment() {

    companion object {
        private const val TAG = "NuevaAuditoriaFragment"
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
            userData = arguments?.getParcelable<LoggedInUser>("user_data")!!
        } catch (ex: Throwable) {
            throw Exception("$TAG: No se recibieron los datos del usuario desde el login!", ex)
        }

        /** Inicializando view model */
        viewModel = ViewModelProviders.of(this, AuditoriasViewModelFactory())
            .get(AuditoriaViewModel::class.java)
        setupLoginIfExpiredToken()


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nueva_auditoria, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSpinners()
    }

    private fun createAuditorias() {
        /*
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
         */
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
        })
        /** Los departamentos se actualizan cuando se cambia la empresa elegida para traer los de la misma*/
        viewModel.departamento.observe(this, Observer { departamentos: List<Departamento> ->
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
                        e.printStackTrace()
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
}
