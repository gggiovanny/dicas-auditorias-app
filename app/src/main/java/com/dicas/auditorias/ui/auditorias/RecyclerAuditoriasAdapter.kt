package com.dicas.auditorias.ui.auditorias

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dicas.auditorias.BR
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.ui.common.AuditoriaStatusEnum
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.layout_auditoria_item.view.*
import java.util.*


class RecyclerAuditoriasAdapter(
    var auditoriaViewModel: AuditoriaViewModel,
    var id_layout_item: Int
) : RecyclerView.Adapter<RecyclerAuditoriasAdapter.AuditoriaCardHolder>() {

    lateinit var clickListener: (index: Int) -> Unit
    lateinit var statusChipClickListener: (index: Int) -> Unit

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AuditoriaCardHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(p0.context)
        val binding: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, p1, p0, false)
        return AuditoriaCardHolder(binding)
    }

    override fun getItemCount(): Int {
        return auditoriaViewModel.auditorias.value?.count() ?: 0
    }

    override fun onBindViewHolder(holder: AuditoriaCardHolder, position: Int) {
        holder.setDataCard(
            auditoriaViewModel,
            position,
            clickListener,
            statusChipClickListener
        )
    }

    override fun getItemViewType(index: Int): Int {
        return id_layout_item
    }

    class AuditoriaCardHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            private const val TAG = "AuditoriaCardHolder"
        }

        private var binding: ViewDataBinding? = null

        init {
            this.binding = binding
        }

        fun setDataCard(
            auditoriaViewModel: AuditoriaViewModel,
            index: Int,
            clickListener: (index: Int) -> Unit,
            statusChipClickListener: (index: Int) -> Unit
        ) {

            /** Configurando onClickListener del cardholder*/
            itemView.setOnClickListener {
                Log.d(TAG, "setDataCard: clicked: $index")
                clickListener(index)
            }

            /** Configurando onClickListener del chip de estatus (para cambiar el estatus) */
            itemView.chip_status_auditoria.setOnClickListener {
                Log.d(
                    TAG,
                    "setDataCard: PRESSED CHIP $index ${auditoriaViewModel.getAuditoriaAt(index)?.id}"
                )

                statusChipClickListener(index)
            }

            /** Configurando */


            /** Configurando color e icono de chip de status */
            when (auditoriaViewModel.getAuditoriaAt(index)?.status) {
                AuditoriaStatusEnum.EN_CURSO.toString() -> {
                    itemView.chip_status_auditoria.setChipBackgroundColorResource(R.color.sucess_green)
                    itemView.chip_status_auditoria.setChipIconResource(R.drawable.ic_in_progres_white_24dp)
                }
                AuditoriaStatusEnum.TERMINADA.toString() -> {
                    itemView.chip_status_auditoria.setChipBackgroundColorResource(R.color.yellow_pastel)
                    itemView.chip_status_auditoria.setChipIconResource(R.drawable.ic_finished_white_24dp)
                }
                AuditoriaStatusEnum.GUARDADA.toString() -> {
                    itemView.chip_status_auditoria.setChipBackgroundColorResource(R.color.error_red)
                    itemView.chip_status_auditoria.setChipIconResource(R.drawable.ic_saved_white_24dp)
                }
            }

            /** Agregar en el item chips para indicar de que empresa, departamentos y categoria es. */
            addDescriptionChipsInToolbar(auditoriaViewModel.getAuditoriaAt(index)!!)

            if (auditoriaViewModel.getAuditoriaAt(index)?.descripcion.isNullOrEmpty())
                itemView.description_auditoria.visibility = View.GONE
            else
                itemView.description_auditoria.visibility = View.VISIBLE

            /** Se bindean las variables del layouts */
            binding?.setVariable(BR.model, auditoriaViewModel)
            binding?.setVariable(BR.index, index)
            binding?.executePendingBindings()
        }

        private fun addDescriptionChipsInToolbar(auditoriaActiva: Auditoria) {
            itemView.chip_group.removeAllViews()

            val textColor =
                getColorStateList(itemView.context ?: return, R.color.text_secondary_dark)

            if (!auditoriaActiva.empresa.isNullOrEmpty()) {
                itemView.chip_group.addView(Chip(itemView.chip_group.context).apply {
                    text = auditoriaActiva.empresa.toLowerCase(Locale.ENGLISH).capitalize()
                    chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.colorEmpresa_dark
                            )
                        )
                    setTextColor(textColor)
                    setChipIconResource(R.drawable.ic_empresa_black_24dp)
                    chipIconTint = textColor
                })
            }

            if (!auditoriaActiva.departamento.isNullOrEmpty()) {
                itemView.chip_group.addView(Chip(itemView.chip_group.context).apply {
                    text = auditoriaActiva.departamento.toLowerCase(Locale.ENGLISH)
                        .capitalize()
                    chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.colorDepartamento_dark
                            )
                        )
                    setTextColor(textColor)
                    setChipIconResource(R.drawable.ic_departamento_black_24dp)
                    chipIconTint = textColor
                })
            }

            if (!auditoriaActiva.clasificacion.isNullOrEmpty()) {
                itemView.chip_group.addView(Chip(itemView.chip_group.context).apply {
                    text =
                        auditoriaActiva.clasificacion.toLowerCase(Locale.ENGLISH)
                            .capitalize()
                    chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.colorClasificacion_dark
                            )
                        )
                    setTextColor(textColor)
                    setChipIconResource(R.drawable.ic_clasificacion_black_24dp)
                    chipIconTint = textColor
                })
            }
        }
    }
}