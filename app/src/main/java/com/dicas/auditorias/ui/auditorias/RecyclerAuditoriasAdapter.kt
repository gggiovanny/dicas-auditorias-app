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
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.layout_auditoria_item.view.*
import java.util.*


class RecyclerAuditoriasAdapter(
    var auditoriaViewModel: AuditoriaViewModel,
    var id_layout_item: Int
) : RecyclerView.Adapter<RecyclerAuditoriasAdapter.AuditoriaCardHolder>() {

    var auditorias: List<Auditoria>? = null
    lateinit var clickListener: (index: Int) -> Unit

    fun setAuditoriasList(auditorias: List<Auditoria>?) {
        this.auditorias = auditorias
    }

    fun setOnClickListenner(clickListener: (index: Int) -> Unit) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AuditoriaCardHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(p0.context)
        val binding: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, p1, p0, false)
        return AuditoriaCardHolder(binding)
    }

    override fun getItemCount(): Int {

        return auditorias?.size ?: 0
    }

    override fun onBindViewHolder(holder: AuditoriaCardHolder, position: Int) {
        holder.setDataCard(
            auditoriaViewModel,
            position,
            clickListener
        )
    }

    override fun getItemViewType(index: Int): Int {
        return getLayoutIdForIndex(index)
    }

    fun getLayoutIdForIndex(index: Int): Int {
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
            clickListener: (index: Int) -> Unit
        ) {

            /** Configurando onClickListener */
            binding?.root?.setOnClickListener {
                Log.d(TAG, "setDataCard: clicked: $index")
                clickListener(index)
            }

            /** Configurando color e icono de chip de status */
            when (auditoriaViewModel.getAuditoriaAt(index)?.status) {
                "En curso" -> {
                    itemView.chip_status_auditoria.chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.sucess_green
                        )
                    )
                    itemView.chip_status_auditoria.setChipIconResource(R.drawable.ic_in_progres_white_24dp)
                }
                "Terminada" -> {
                    itemView.chip_status_auditoria.chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.yellow_pastel
                        )
                    )
                    itemView.chip_status_auditoria.setChipIconResource(R.drawable.ic_finished_white_24dp)
                }
                "Guardada" -> {
                    itemView.chip_status_auditoria.chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.error_red
                            )
                        )
                    itemView.chip_status_auditoria.setChipIconResource(R.drawable.ic_saved_white_24dp)
                }
            }

            val auditoriaActiva = auditoriaViewModel.getAuditoriaAt(index) ?: return
            /** Agregar en el item chips para indicar de que empresa, departamentos y categoria es. */
            addDescriptionChipsInToolbar(auditoriaActiva)

            if (auditoriaActiva.descripcion.isNullOrEmpty())
                itemView.description_auditoria.visibility = View.GONE

            /** Se bindean las variables del layouts */
            binding?.setVariable(BR.model, auditoriaViewModel)
            binding?.setVariable(BR.index, index)
            binding?.executePendingBindings()
        }

        private fun addDescriptionChipsInToolbar(auditoriaActiva: Auditoria) {
            if (!itemView.id_auditoria.text.isNullOrEmpty())
                return

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