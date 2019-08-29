package com.dicas.auditorias.ui.activos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.dicas.auditorias.R

class ActivosFragment : Fragment() {

    companion object {
        fun newInstance() = ActivosFragment()
    }

    private lateinit var viewModel: ActivosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ActivosViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
