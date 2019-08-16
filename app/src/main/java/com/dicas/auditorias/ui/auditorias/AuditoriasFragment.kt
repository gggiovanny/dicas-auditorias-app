package com.dicas.auditorias.ui.auditorias

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicas.auditorias.R


class AuditoriasFragment : Fragment() {

    companion object {
        fun newInstance() = AuditoriasFragment()
    }

    private lateinit var viewModel: AuditoriaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auditoria, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AuditoriaViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
