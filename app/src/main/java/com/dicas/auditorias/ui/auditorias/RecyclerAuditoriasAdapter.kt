package com.dicas.auditorias.ui.auditorias

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dicas.auditorias.BR
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.Auditoria
import kotlinx.android.synthetic.main.layout_auditoria_item.view.*


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

    override fun onBindViewHolder(p0: AuditoriaCardHolder, p1: Int) {
        p0.setDataCard(auditoriaViewModel, p1, clickListener)
    }

    override fun getItemViewType(index: Int): Int {
        return getLayoutIdForIndex(index)
    }

    fun getLayoutIdForIndex(index: Int): Int {
        return id_layout_item
    }

    class AuditoriaCardHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

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

            binding?.setVariable(BR.model, auditoriaViewModel)
            binding?.setVariable(BR.index, index)
            binding?.executePendingBindings()
        }


    }

}