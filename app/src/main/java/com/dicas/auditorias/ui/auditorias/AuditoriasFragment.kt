package com.dicas.auditorias.ui.auditorias

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.*
import com.dicas.auditorias.ui.login.LoginActivity
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_auditoria.*
import kotlinx.android.synthetic.main.layout_nueva_auditoria.*


class AuditoriasFragment : Fragment() {

    companion object {
        fun newInstance() = AuditoriasFragment()
        private const val TAG = "AuditoriasFragment"

    }

    private lateinit var viewModel: AuditoriaViewModel
    lateinit var userData: LoggedInUser

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

        val view = inflater.inflate(R.layout.fragment_auditoria, container, false)

        // Inicializando view model
        viewModel = ViewModelProviders.of(this, AuditoriasViewModelFactory())
            .get(com.dicas.auditorias.ui.auditorias.AuditoriaViewModel::class.java)
        setupLoginIfExpiredToken()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loading.visibility = View.VISIBLE
        setupSpinners()
        setupRecyclerView()
        setupScrollFade()
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
            if (response.statusOk) {
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

    private fun setupScrollFade() {
        app_bar_layout.addOnOffsetChangedListener(object :
            AppBarLayout.BaseOnOffsetChangedListener<AppBarLayout> {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                try {
                    val alpha =
                        (appBarLayout!!.totalScrollRange + verticalOffset).toFloat() / appBarLayout.totalScrollRange
                    toolbar_spinners.alpha = alpha

                    if (alpha > 0)
                        toolbar_spinners.visibility = View.VISIBLE
                    else
                        toolbar_spinners.visibility = View.INVISIBLE


                } catch (e: Throwable) {
                    Exception(
                        "$TAG:setupScrollFade: No se pudo configurar el fade del los spinners al hacer scroll",
                        e
                    )
                }
            }

        })
    }

    private fun setupNuevaAuditoriaButton() {
        nueva_auditoria.setOnClickListener {
            Log.d(
                TAG,
                "Empresa: ${empresa_spinner.selectedItem}, Depto: ${departamento_spinner.selectedItem}"
            )


        }
    }

}



/**
 * Extension function to simplify setting an OnItemSelectedListener action to EditText components.
 */
fun AdapterView<*>.OnItemSelectedListener(lamdaListener: (parent: AdapterView<*>, position: Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            lamdaListener.invoke(parent, position)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}

