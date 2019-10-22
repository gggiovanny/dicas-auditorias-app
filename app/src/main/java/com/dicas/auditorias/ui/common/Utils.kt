package com.dicas.auditorias.ui.common

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import com.google.android.material.appbar.AppBarLayout

/**
 * static function to fade desired elements inside an AppBarLayout
 */
fun setupAppBarScrollFade(appBarLayout: AppBarLayout, widgets: List<View>) {
    appBarLayout.addOnOffsetChangedListener(object :
        AppBarLayout.BaseOnOffsetChangedListener<AppBarLayout> {
        override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
            try {
                val alpha =
                    (appBarLayout!!.totalScrollRange + verticalOffset).toFloat() / appBarLayout.totalScrollRange
                widgets.forEach { it.alpha = alpha }

                if (alpha > 0)
                    widgets.forEach { it.visibility = View.VISIBLE }
                else
                    widgets.forEach { it.visibility = View.INVISIBLE }

            } catch (e: Throwable) {
                Exception(
                    "setupAppBarScrollFade: No se pudo configurar el fade del los widgets al hacer scroll",
                    e
                )
            }
        }

    })
}

/**
 * Extension function to simplify setting an OnItemSelectedListener action to EditText components.
 */
fun AdapterView<*>.OnItemSelectedListener(lamdaListener: (parent: AdapterView<*>, position: Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            lamdaListener.invoke(parent, position)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}


/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

/*

val empresa = empresa_spinner.selectedItem as? Empresa
            Log.d(
                TAG,
                "Empresa_nombre: ${empresa_spinner.selectedItem} Empresa_id: ${empresa?.id} Empresa_pos: ${empresa_spinner.selectedItemPosition}}"
            )
 */