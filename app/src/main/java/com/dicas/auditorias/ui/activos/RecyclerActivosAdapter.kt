package com.dicas.auditorias.ui.activos


import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.Activo
import kotlinx.android.synthetic.main.layout_activo_item.view.*

class RecyclerActivosAdapter(var viewModel: ActivosViewModel, var id_layout_item: Int) :
    RecyclerView.Adapter<RecyclerActivosAdapter.ActivoCardHolder>() {

    companion object {
        private const val TAG = "RecyclerActivosAdapter"
    }

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
            //setChipsExistenciasValue(viewModel.getObjectAt(position) ?: return)

            binding?.setVariable(BR.modelAct, viewModel)
            binding?.setVariable(BR.position, position)
            binding?.executePendingBindings()
        }

        private fun setChipsExistenciasValue(activoActual: Activo) {

            if (activoActual.id == "65") {
                Log.d(TAG, "setChipsExistenciasValue: anuma")
            }

            if (activoActual.existencia_guardada == "1") {
                itemView.chip_existencia_guardada.chipIcon =
                    itemView.context.getDrawable(R.drawable.ic_check_box_true_black_24dp)
                itemView.chip_existencia_guardada.chipIconTint = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.sucess_green
                    )
                )
            }
            if (activoActual.existencia_guardada == "0") {
                itemView.chip_existencia_guardada.chipIcon =
                    itemView.context.getDrawable(R.drawable.ic_check_box_false_blank_black_24dp)
                itemView.chip_existencia_guardada.chipIconTint = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.error_red
                    )
                )
            }

        }
    }
}