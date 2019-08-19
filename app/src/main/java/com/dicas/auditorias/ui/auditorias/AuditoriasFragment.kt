package com.dicas.auditorias.ui.auditorias

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.data.model.LoggedInUser
import com.dicas.auditorias.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_auditoria.*
import kotlinx.android.synthetic.main.layout_nueva_auditoria.view.*


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
        setupBindings()

        return inflater.inflate(R.layout.fragment_auditoria, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSpinners()

    }

    fun setupBindings() {

        val activityMainBinding: com.dicas.auditorias.databinding.FragmentAuditoriaBinding =
            DataBindingUtil.setContentView(this.requireActivity(), R.layout.fragment_auditoria)

        viewModel = ViewModelProviders.of(this, AuditoriasViewModelFactory())
            .get(com.dicas.auditorias.ui.auditorias.AuditoriaViewModel::class.java)


        activityMainBinding.modelFragment = viewModel
        setupListUpdate()
    }

    fun setupListUpdate() {
        viewModel.callAuditorias(apikey = userData.token)
        viewModel.auditorias.observe(this, Observer { auditorias: List<Auditoria> ->
            viewModel.setAuditoriasInRecyclerAdapter(auditorias)
        })
    }

    fun setupSpinners() {
        val adapter = ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.auditoria_status_options, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        empresa_spinner.adapter = adapter
        departamento_spinner.adapter = adapter

        Log.d(TAG, "setupSpinners: data seted!")


    }

}
