package com.dicas.auditorias.ui.activos


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.dicas.auditorias.R
import kotlinx.android.synthetic.main.layout_activo_item.view.*

class RecyclerActivosAdapter(var viewModel: ActivosViewModel, var id_layout_item: Int) :
    RecyclerView.Adapter<RecyclerActivosAdapter.ActivoCardHolder>() {

    companion object {
        private const val TAG = "RecyclerActivosAdapter"
    }

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

    override fun getItemCount(): Int {
        return viewModel.activos.value?.count() ?: 0
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


            /** Cuando se da click en el chip_existencia_actual */
            binding?.root?.chip_existencia_actual?.setOnClickListener {
                /** Cuando se intente cambiar el estatus de existencia en la interfaz,
                 * conservar el status del modelo de datos y solo permitir alterarlo cuando no halla
                 * un estatus definido en el modelo*/
                when (viewModel.getObjectAt(position)?.existencia_actual) {
                    null -> {
                        binding?.root?.chip_existencia_actual?.setChipIconResource(R.drawable.ic_rounded_chip_false_black)
                        binding?.root?.chip_existencia_actual?.setChipIconTintResource(R.color.yellow_terminada)
                        binding?.root?.chip_existencia_actual?.setText(R.string.existencia_actual_false)

                    }
                }
            }

            itemView.test.text = viewModel.getObjectAt(position)?.existencia_actual



            binding?.setVariable(BR.modelAct, viewModel)
            binding?.setVariable(BR.position, position)
            binding?.executePendingBindings()
        }
    }
}