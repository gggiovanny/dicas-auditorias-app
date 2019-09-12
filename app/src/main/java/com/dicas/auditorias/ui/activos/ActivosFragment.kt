package com.dicas.auditorias.ui.activos

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.Activo
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.data.model.LoggedInUser
import com.dicas.auditorias.ui.login.LoginActivity
import com.dicas.auditorias.ui.utils.setupAppBarScrollFade
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_activos.*
import java.util.*
import kotlin.collections.ArrayList

class ActivosFragment : Fragment() {

    companion object {
        fun newInstance() = ActivosFragment()
        private const val TAG = "ActivosFragment"

    }

    private lateinit var viewModel: ActivosViewModel
    private lateinit var userData: LoggedInUser
    private lateinit var auditoriaActiva: Auditoria

    private var firstError = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /** Obteniendo los datos de usuario que vienen del login */
        try {
            userData = arguments?.getParcelable<LoggedInUser>("user_data")!!
        } catch (ex: Throwable) {
            throw Exception("$TAG: No se recibieron los datos del usuario desde el login!", ex)
        }

        /** Obteniendo auditoria activa */
        try {
            auditoriaActiva = arguments?.getParcelable<Auditoria>("auditoria_activa")!!
        } catch (ex: Throwable) {
            throw Exception("$TAG: No se recibió la auditoria activa!", ex)
        }

        /** Inicializando view model */
        viewModel = ViewModelProviders.of(this, ActivosViewModelFactory())
            .get(ActivosViewModel::class.java)
        setupLoginIfExpiredToken()

        /*val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activos, container, false)*/

        return inflater.inflate(R.layout.fragment_activos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ActivosViewModel::class.java)

        setupRecyclerView()

        setupAppBarScrollFade(app_bar_layout, ArrayList<View>().apply {
            add(chip_group)
        })

        addDescriptionChipsInToolbar()
    }

    private fun setupRecyclerView() {
        rv_activos.adapter = viewModel.recyclerActivosAdapter

        viewModel.callActivosAPI(
            apiKey = userData.token,
            auditoriaActual = auditoriaActiva.id
        )

        viewModel.activos.observe(this, Observer { auditorias: List<Activo> ->
            viewModel.setupRecyclerAdapter(auditorias)
            Log.d(TAG, "setupRecyclerView: observe done!")
        })
    }

    private fun setupLoginIfExpiredToken() {
        viewModel.response.observe(this, Observer {
            val response: ApiResponse = it ?: return@Observer
            Log.d(TAG, "setupLoginIfExpiredToken: ${response.status}: ${response.description}")
            if (!response.isOk && firstError) {
                if (!(response.status ?: return@Observer).contains("app")) {
                    firstError = false
                    showSesionCaducada(response)
                    val login = Intent(context, LoginActivity::class.java).apply {
                        putExtra("login_failed", true)
                    }
                    startActivity(login)
                    this.activity?.finish()
                } else {
                    Toast.makeText(
                        context, R.string.error_api,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        Log.d(TAG, "setupLoginIfExpiredToken: created observer done!")
    }

    private fun showSesionCaducada(response: ApiResponse) {
        if (response.description == "Expired token") {
            Log.d(TAG, "showSesionCaducada: ${response.status}: ${response.description}")
            Toast.makeText(context, R.string.invalid_token, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "${response.status}: ${response.description}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addDescriptionChipsInToolbar() {
        val textColor = getColorStateList(context ?: return, R.color.text_primary_light)

        chip_group.addView(Chip(chip_group.context).apply {
            text = (auditoriaActiva.empresa ?: return).toLowerCase(Locale.ENGLISH).capitalize()
            chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorEmpresa))
            setTextColor(textColor)
            setChipIconResource(R.drawable.ic_empresa_black_24dp)
            chipIconTint = textColor
        })

        chip_group.addView(Chip(chip_group.context).apply {
            text = (auditoriaActiva.departamento ?: return).toLowerCase(Locale.ENGLISH).capitalize()
            chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorDepartamento))
            setTextColor(textColor)
            setChipIconResource(R.drawable.ic_departamento_black_24dp)
            chipIconTint = textColor
        })

        chip_group.addView(Chip(chip_group.context).apply {
            text =
                (auditoriaActiva.clasificacion ?: return).toLowerCase(Locale.ENGLISH).capitalize()
            chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorClasificacion))
            setTextColor(textColor)
            setChipIconResource(R.drawable.ic_clasificacion_black_24dp)
            chipIconTint = textColor
        })
    }


}
