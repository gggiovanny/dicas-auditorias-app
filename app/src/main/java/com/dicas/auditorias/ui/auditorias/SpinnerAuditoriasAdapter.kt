package com.dicas.auditorias.ui.auditorias

import android.content.Context
import android.widget.ArrayAdapter

class SpinnerAuditoriasAdapter<T>(context: Context) :
    ArrayAdapter<T>(context, android.R.layout.simple_spinner_item) {

    // Desabilita el primer item del spinner
    //override fun isEnabled(position: Int): Boolean = position != 0

    //Poner en gris el primer item

    /*
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getDropDownView(position, convertView, parent)

        if(getItem(position).toString().isNullOrEmpty()) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }

        return view
    }
    */

}