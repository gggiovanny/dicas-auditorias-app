package com.dicas.auditorias.ui.activos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.Activo
import kotlinx.android.synthetic.main.layout_activo_item.view.*

class RecyclerActivosAdapter(var viewModel: ActivosViewModel, var id_layout_item: Int) :
    RecyclerView.Adapter<RecyclerActivosAdapter.ActivoCardHolder>() {

    var activos: List<Activo>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivoCardHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(layoutInflater, id_layout_item, parent, false)
        return ActivoCardHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerActivosAdapter.ActivoCardHolder,
        position: Int
    ) {
        holder.setDataCard(viewModel, position)
    }

    fun setActivosList(activos: List<Activo>?) {
        this.activos = activos
    }

    override fun getItemCount(): Int {
        return activos?.size ?: 0
    }

    override fun getItemViewType(index: Int): Int {
        return getLayoutIdForIndex(index)
    }

    fun getLayoutIdForIndex(index: Int): Int {
        return id_layout_item
    }

    class ActivoCardHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        private var binding: ViewDataBinding? = null

        init {
            this.binding = binding
        }

        fun setDataCard(viewModel: ActivosViewModel, position: Int) {
            setChipsExistenciasValue(viewModel.getObjectAt(position) ?: return)

            binding?.setVariable(BR.modelAct, viewModel)
            binding?.setVariable(BR.position, position)
            binding?.executePendingBindings()
        }

        private fun setChipsExistenciasValue(activoActual: Activo) {
            val trueChar: Char = '\u0001'
            val falseChar: Char = '\u0000'

            if (activoActual.existencia_guardada == trueChar.toString())
                itemView.chip_existencia_guardada.text =
                    itemView.context.getText(R.string.activo_guardado_si)
            else {
                if (!activoActual.existencia_guardada.isNullOrEmpty())
                    itemView.chip_existencia_guardada.text =
                        itemView.context.getText(R.string.activo_guardado_no)
            }

        }
    }
}