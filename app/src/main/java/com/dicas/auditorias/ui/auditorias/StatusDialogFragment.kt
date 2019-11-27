package com.dicas.auditorias.ui.auditorias

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicas.auditorias.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_status_dialog.*


/**
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 */
const val ARG_AUDITORIA_ID = "auditoria_id"

class StatusDialogFragment : BottomSheetDialogFragment() {
    private lateinit var onEnCursoListener: (idAuditoria: String) -> Unit
    private lateinit var onTerminadaListener: (idAuditoria: String) -> Unit
    private lateinit var onGuardadaListener: (idAuditoria: String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_status_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val idAuditoria: String = arguments?.getString(ARG_AUDITORIA_ID)!!
        txt_id_auditoria.text = getString(R.string.auditoria_no).plus(idAuditoria)

        btn_terminada.setOnClickListener {
            onTerminadaListener(idAuditoria)
            Log.d(TAG, "OnTerminadaClicked: Intentando marcar como terminada #$idAuditoria...")
        }

        btn_guardada.setOnClickListener {
            onGuardadaListener(idAuditoria)
            Log.d(TAG, "OnGuardadaClicked: Intentando marcar como guardada #$idAuditoria...")
        }

        btn_en_curso.setOnClickListener {
            onEnCursoListener(idAuditoria)
            Log.d(TAG, "OnEnCursoClicked: Intentando marcar como en curso...#$idAuditoria...")
        }
    }

    fun setOnTerminadaListener(listener: (idAuditoria: String) -> Unit) {
        onTerminadaListener = listener
    }

    fun setOnGuardadaListener(listener: (idAuditoria: String) -> Unit) {
        onGuardadaListener = listener
    }

    fun setOnEnCursoListener(listener: (idAuditoria: String) -> Unit) {
        onEnCursoListener = listener
    }

    fun test() {
        this
        //TODO("FUNCION PARA CERRAR LA ACTIVIDAD")
    }

    companion object {
        fun newInstance(idAuditoria: String): StatusDialogFragment =
            StatusDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_AUDITORIA_ID, idAuditoria)
                }
            }

        private const val TAG = "StatusDialogFragment"
    }
}
