package com.dicas.auditorias.ui.auditorias

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.dicas.auditorias.R
import kotlinx.android.synthetic.main.fragment_auditoria.*
import kotlinx.android.synthetic.main.layout_nueva_auditoria.*
import android.widget.Toast
import com.dicas.auditorias.data.model.*
import com.dicas.auditorias.ui.login.LoginActivity


class AuditoriasFragment : Fragment() {

    companion object {
        fun newInstance() = AuditoriasFragment()
        private const val TAG = "AuditoriasFragment"

    }


    private lateinit var viewModel: AuditoriaViewModel
    lateinit var userData: LoggedInUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /** Obteniendo los datos de usuario que vienen del login */
        try {
            userData = arguments?.getParcelable<LoggedInUser>("user_data")!!
        } catch (e: Throwable) {
            throw Exception("$TAG: No se recibieron los datos del usuario desde el login!", e)
        }

        val view = inflater.inflate(R.layout.fragment_auditoria, container, false)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Inicializando view model
        viewModel = ViewModelProviders.of(this, AuditoriasViewModelFactory())
            .get(com.dicas.auditorias.ui.auditorias.AuditoriaViewModel::class.java)

        setupSpinners()
        setupRecyclerView()
        setupLoginIfExpiredToken()
    }


    private fun setupRecyclerView() {
        rvAuditorias.adapter = viewModel.recyclerAuditoriasAdapter

        viewModel.callAuditorias(apikey = userData.token)
        viewModel.auditorias.observe(this, Observer { auditorias: List<Auditoria> ->
            viewModel.setAuditoriasInRecyclerAdapter(auditorias)
            Log.d(TAG, "setupRecyclerView: observe done!")
        })
    }

    private fun setupSpinners() {
        /** Creando los adapters */
        val adapterEmpresas = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item)
        with(adapterEmpresas) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            add("Selecicione una empresa")
        }

        val adapterDeptos = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item)
        with(adapterDeptos) {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            add("Seleccione un departamento")
        }

        /** Llamando al API para obtener los datos y creando el observer para añadir la informacion cuando llegue */
        viewModel.callEmpresas(apikey = userData.token)
        viewModel.empresas.observe(this, Observer { empresas: List<Empresa> ->
            for (empresa in empresas)
                adapterEmpresas.add(empresa.nombre)
            Log.d(TAG, "setupSpinners: Observer empresas done!")
        })
        /** Los departamentos se actualizan cuando se cambia la empresa elegida para traer los de la misma*/
        viewModel.departamento.observe(this, Observer { departamentos: List<Departamento> ->
            with(adapterDeptos) {
                clear()
                add("Seleccione un departamento")
            }
            departamento_spinner.setSelection(0)
            for (depto in departamentos)
                adapterDeptos.add(depto.nombre)
            Log.d(TAG, "setupSpinners: Observer departamentos done! ")
        })

        /** Seteando adapters y creando listeners */
        with(empresa_spinner) {
            adapter = adapterEmpresas
            OnItemSelectedListener { parent, position ->
                if (position > 0) {
                    val item = parent.getItemAtPosition(position).toString()
                    Toast.makeText(parent.context, "Selected: $item", Toast.LENGTH_LONG).show()
                    /** Cuando Se elija una empresa, se cargan los departamentos de la misma */
                    Log.d(TAG, "setupSpinners: $position")
                    viewModel.callDepartamentos(apikey = userData.token, empresaID = position)
                }
            }
        }

        with(departamento_spinner) {
            adapter = adapterDeptos
        }

    }

    private fun setupLoginIfExpiredToken() {
        viewModel.response.observe(this, Observer { response: ApiResponse ->
            Log.d(TAG, "setupLoginIfExpiredToken: ${response.status}: ${response.description}")
            if (!response.statusOk) {
                showCallApiFailed(response)
                val login = Intent(context, LoginActivity::class.java)
                startActivity(login)
                this.activity?.finish()
            }

        })
        Log.d(TAG, "setupLoginIfExpiredToken: created observer done!")
    }

    private fun showCallApiFailed(response: ApiResponse) {
        Toast.makeText(context, "${response.status}: ${response.description}", Toast.LENGTH_SHORT).show()
    }

}

/**
 * Extension function to simplify setting an OnItemSelectedListener action to EditText components.
 */
fun AdapterView<*>.OnItemSelectedListener(lamdaListener: (parent: AdapterView<*>, position: Int) -> Unit) {
    setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            lamdaListener.invoke(parent, position)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    })
}

