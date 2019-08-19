package com.dicas.auditorias.ui.auditorias

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.BR

class RecyclerAuditoriasAdapter( var auditoriaViewModel: AuditoriaViewModel, var id_layout_item: Int) : RecyclerView.Adapter<RecyclerAuditoriasAdapter.AuditoriaCardHolder>() {

    var auditorias: List<Auditoria>? = null

    fun setAuditoriasList(coupons: List<Auditoria>?){
        this.auditorias= coupons
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AuditoriaCardHolder {
        var layoutInflater: LayoutInflater = LayoutInflater.from(p0.context)
        var binding: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, p1, p0, false)
        return AuditoriaCardHolder(binding)
    }

    override fun getItemCount(): Int {
        return auditorias?.size ?: 0
    }

    override fun onBindViewHolder(p0: AuditoriaCardHolder, p1: Int) {
        p0.setDataCard(auditoriaViewModel, p1)
    }

    override fun getItemViewType(index: Int): Int {
        return getLayoutIdForIndex(index)
    }

    fun getLayoutIdForIndex(index: Int): Int{
        return id_layout_item
    }

    class AuditoriaCardHolder(binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        private var binding: ViewDataBinding? = null

        init {
            this.binding = binding
        }

        fun setDataCard(couponViewModel: AuditoriaViewModel, index: Int){
            binding?.setVariable(BR.model, couponViewModel)
            binding?.setVariable(BR.index, index)
            binding?.executePendingBindings()
        }


    }

}