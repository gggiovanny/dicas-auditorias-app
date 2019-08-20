package com.dicas.auditorias.ui.auditorias

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.data.model.LoggedInUser
import kotlinx.android.synthetic.main.fragment_auditoria.*


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
    }



    fun setupRecyclerView() {
        rvAuditorias.adapter = viewModel.recyclerAuditoriasAdapter

        viewModel.callAuditorias(apikey = userData.token)
        viewModel.auditorias.observe(this, Observer { auditorias: List<Auditoria> ->
            viewModel.setAuditoriasInRecyclerAdapter(auditorias)
            Log.d(TAG, "setupRecyclerView: observe done!")
        })
    }

    fun setupSpinners() {
        val adapter = ArrayAdapter.createFromResource(
            context,
            R.array.auditoria_status_options, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        empresa_spinner.adapter = adapter
        departamento_spinner.adapter = adapter

        Log.d(TAG, "setupSpinners: done!")


    }

}
