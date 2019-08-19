package com.dicas.auditorias.ui.auditorias

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicas.auditorias.R
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

        return inflater.inflate(R.layout.fragment_auditoria, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(AuditoriaViewModel::class.java)
        // TODO: Use the ViewModel

        textTest.text = userData.name
    }

}
