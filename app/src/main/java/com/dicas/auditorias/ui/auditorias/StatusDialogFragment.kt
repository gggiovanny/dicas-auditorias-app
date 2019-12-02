package com.dicas.auditorias.ui.auditorias

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.dicas.auditorias.R
import com.dicas.auditorias.ui.common.AuditoriaStatusEnum
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_status_dialog.*


/**
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 */
const val ARG_AUDITORIA_ID = "auditoria_id"
const val ARG_AUDITORIA_STATUS = "auditoria_status"

class StatusDialogFragment : BottomSheetDialogFragment() {
    private lateinit var onAlternarTerminadaListener: (idAuditoria: String) -> Unit
    private lateinit var onGuardadaListener: (idAuditoria: String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_status_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val idAuditoria: String = arguments?.getString(ARG_AUDITORIA_ID)!!
        val statusAuditoria: String = arguments?.getString(ARG_AUDITORIA_STATUS)!!

        txt_id_auditoria.text = getString(R.string.auditoria_no).plus(idAuditoria)

        // Se ponen los botones al estado contrario que el actual
        when (statusAuditoria) {
            AuditoriaStatusEnum.EN_CURSO.toString() -> setButtonToTerminada()
            AuditoriaStatusEnum.TERMINADA.toString() -> setButtonToEnCurso()
            AuditoriaStatusEnum.GUARDADA.toString() -> close()
        }

        btn_guardada.setOnClickListener {
            onGuardadaListener(idAuditoria)
            Log.d(TAG, "OnGuardadaClicked: Intentando marcar como guardada #$idAuditoria...")
        }

        btn_alternar_terminada.setOnClickListener {
            onAlternarTerminadaListener(idAuditoria)
            Log.d(TAG, "OnEnCursoClicked: Intentando marcar como en curso...#$idAuditoria...")
        }
    }

    fun setOnGuardadaListener(listener: (idAuditoria: String) -> Unit) {
        onGuardadaListener = {
            val idAuditoria = it

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

            builder.setTitle(getString(R.string.dialog_title))
            builder.setMessage(getString(R.string.dialog_message))

            builder.setPositiveButton("SI") { _, _ ->
                // Ejecutar la accion bindeada
                listener(idAuditoria)
            }

            builder.setNegativeButton("NO") { dialog, _ ->
                // No hacer nada
                dialog.dismiss()
                close()
            }

            val alert: AlertDialog = builder.create()
            alert.show()


        }
    }

    fun setOnAlternarTerminadaListener(listener: (idAuditoria: String) -> Unit) {
        onAlternarTerminadaListener = {
            listener(it)
        }
    }

    fun close() {
        this.dismiss()
    }

    fun setButtonToEnCurso() {
        btn_alternar_terminada.setBackgroundColor(
            ContextCompat.getColor(
                context!!,
                R.color.sucess_green
            )
        )
        btn_alternar_terminada.setText(R.string.marcar_auditoria_encurso)
    }

    fun setButtonToTerminada() {
        btn_alternar_terminada.setBackgroundColor(
            ContextCompat.getColor(
                context!!,
                R.color.yellow_pastel
            )
        )
        btn_alternar_terminada.setText(R.string.marcar_auditoria_terminada)
        btn_guardada.isEnabled = false
    }

    companion object {
        fun newInstance(idAuditoria: String, statusAuditoria: String): StatusDialogFragment =
            StatusDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_AUDITORIA_ID, idAuditoria)
                    putString(ARG_AUDITORIA_STATUS, statusAuditoria)
                }
            }

        private const val TAG = "StatusDialogFragment"
    }
}
