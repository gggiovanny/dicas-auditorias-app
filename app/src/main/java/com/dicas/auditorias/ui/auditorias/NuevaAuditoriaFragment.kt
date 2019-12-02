package com.dicas.auditorias.ui.auditorias


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.*
import com.dicas.auditorias.ui.common.OnItemSelectedListener
import com.dicas.auditorias.ui.common.ResponseTypeEnum
import com.dicas.auditorias.ui.common.SharedDataViewModel
import com.dicas.auditorias.ui.common.afterTextChanged
import com.dicas.auditorias.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_nueva_auditoria.*


class NuevaAuditoriaFragment : Fragment() {

    companion object {
        private const val TAG = "NuevaAuditoriaFragment"
    }

    private lateinit var viewModel: AuditoriaViewModel
    private lateinit var navController: NavController
    private lateinit var sharedData: SharedDataViewModel


    private var firstError = true
    private var firstSucess = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /** Obteniendo los datos de usuario compartidos */
        sharedData = activity?.run {
            ViewModelProviders.of(this)[SharedDataViewModel::class.java]
        } ?: throw Exception("No se recibieron los datos del usuario desde el login!")

        /** Inicializando view model */
        viewModel = activity.run {
            ViewModelProviders.of(
                this ?: throw Exception("Invalid fragment activity"),
                AuditoriasViewModelFactory()
            ).get(AuditoriaViewModel::class.java)
        }

        setupResponseHandler()


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nueva_auditoria, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = Navigation.findNavController(view ?: return)

        setupSpinners()
        setupButtonNuevaAuditoria()
        setupReloadWhenBackButton()
    }

    private fun setupButtonNuevaAuditoria() {
        button_nueva_auditoria.setOnClickListener {
            try {
                viewModel.createAuditoria(
                    apiKey = sharedData.token,
                    empresa = (empresa_spinner.selectedItem as? Empresa)!!.id,
                    departamento = (departamento_spinner.selectedItem as? Departamento)!!.id,
                    clasificacion = (clasificacion_spinner.selectedItem as? Clasificacion)!!.id,
                    descripcion = text_descripcion.text.toString()
                )

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        /** Creando observer para la respuesta del metodo post
        viewModel.response.observe(this, Observer {
        val apiResponse = it ?: return@Observer

        Toast.makeText(
        context,
        "${apiResponse.status}: ${apiResponse.description}",
        Toast.LENGTH_SHORT
        ).show()

        })
         */


    }

    private fun isDataValid(): Boolean {
        return clasificacion_spinner.selectedItemPosition > 0
                && empresa_spinner.selectedItemPosition > 0
                && departamento_spinner.selectedItemPosition > 0
                && (text_descripcion.text ?: return false).length <= 255
                && (text_descripcion.text ?: return false).length >= 3

    }

    private fun openActivos(idNuevaAuditoria: String) {
        val auditoriaActiva = Auditoria(
            id = idNuevaAuditoria,
            idEmpresa = (empresa_spinner.selectedItem as? Empresa)!!.id,
            idDepartamento = (departamento_spinner.selectedItem as? Departamento)!!.id,
            idClasificacion = (clasificacion_spinner.selectedItem as? Clasificacion)!!.id,
            descripcion = text_descripcion.text.toString()
        )

        val bundle = bundleOf("auditoria_activa" to auditoriaActiva)
        navController.navigate(R.id.action_nuevaAuditoria_to_activosFragment, bundle)
    }

    private fun setupSpinners() {

        /** Creando los adapters */
        val adapterEmpresas =
            SpinnerAuditoriasAdapter<Empresa>(context ?: return)
        with(adapterEmpresas) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            add(Empresa("-1", ""))
        }

        val adapterDeptos =
            SpinnerAuditoriasAdapter<Departamento>(context ?: return)
        with(adapterDeptos) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            add(Departamento("-1", ""))
        }

        val adapterClasif =
            SpinnerAuditoriasAdapter<Clasificacion>(context ?: return)
        with(adapterClasif) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            add(Clasificacion("-1", ""))
        }

        /** Llamando al API para obtener los datos y creando el observer de empresas*/
        viewModel.callEmpresas(apikey = sharedData.token)
        viewModel.empresas.observe(this, Observer { empresas: List<Empresa> ->
            for (empresa in empresas)
                adapterEmpresas.add(empresa)
            Log.d(TAG, "setupSpinners: Observer empresas done!")
        })

        /** Observer de departamentos */
        /** Los departamentos se actualizan cuando se cambia la empresa elegida para traer los de la misma*/
        viewModel.departamentos.observe(this, Observer { departamentos: List<Departamento> ->
            with(adapterDeptos) {
                clear()
                add(Departamento("-1", ""))
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

        /** Observer de clasificaciones */
        viewModel.callClasificaciones(apiKey = sharedData.token)
        viewModel.clasificaciones.observe(this, Observer { clasificaciones: List<Clasificacion> ->
            for (clasif in clasificaciones)
                adapterClasif.add(clasif)
            Log.d(TAG, "setupSpinners: Observer clasificaciones done! ")
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
                            apikey = sharedData.token,
                            empresaID = empresa!!.id.toInt()
                        )
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }

                button_nueva_auditoria.isEnabled =
                    isDataValid() // Para habilitar o desabilitar el boton de creacion de auditoria
            }
        }
        departamento_spinner.adapter = adapterDeptos
        clasificacion_spinner.adapter = adapterClasif

        /** Listeners para habilitar o desabilitar el boton de creacion de auditoria */
        departamento_spinner.OnItemSelectedListener { parent, position ->
            button_nueva_auditoria.isEnabled = isDataValid()
        }
        clasificacion_spinner.OnItemSelectedListener { parent, position ->
            button_nueva_auditoria.isEnabled = isDataValid()
        }
        text_descripcion.afterTextChanged { button_nueva_auditoria.isEnabled = isDataValid() }
    }

    private fun setupResponseHandler() {
        viewModel.response.observe(this, Observer {
            val response: ApiResponse = it ?: return@Observer
            Log.d(TAG, "setupResponseHandler: ${response.status}: ${response.description}")
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

            if (response.isOk && response.description.contains("Entry sucessfuly created")) {

                try {
                    openActivos(response.idAuditoria!!)
                } catch (e: Exception) {
                    viewModel.response.value = ApiResponse(
                        status = "error_show",
                        description = "¡Error! No se obtuvo de la base de datos el ID de la nueva auditoria creada.",
                        tipo = ResponseTypeEnum.INTERNAL_ERROR.toString()
                    )
                    return@Observer
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

    private fun setupReloadWhenBackButton() {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            navController.popBackStack()
        }
    }
}
