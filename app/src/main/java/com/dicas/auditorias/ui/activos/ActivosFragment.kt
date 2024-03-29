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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.dicas.auditorias.R
import com.dicas.auditorias.data.model.ApiResponse
import com.dicas.auditorias.data.model.Auditoria
import com.dicas.auditorias.ui.common.SharedDataViewModel
import com.dicas.auditorias.ui.common.setupAppBarScrollFade
import com.dicas.auditorias.ui.login.LoginActivity
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_activos.*
import kotlinx.android.synthetic.main.layout_toolbar_general.*
import java.util.*
import kotlin.collections.ArrayList

class ActivosFragment : Fragment() {

    companion object {
        fun newInstance() = ActivosFragment()
        private const val TAG = "ActivosFragment"

    }

    private lateinit var viewModel: ActivosViewModel
    private lateinit var sharedData: SharedDataViewModel
    private lateinit var navController: NavController

    private var firstError = true
    private var mismaAuditoria = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /** Obteniendo los datos de usuario compartidos */
        sharedData = activity?.run {
            ViewModelProviders.of(this)[SharedDataViewModel::class.java]
        } ?: throw Exception("No se recibieron los datos del usuario desde el login!")

        /** Obteniendo auditoria activa */
        try {
            sharedData.auditoriaActiva = arguments?.getParcelable<Auditoria>("auditoria_activa")!!
        } catch (ex: Throwable) {
            val msg = "No se recibió la auditoria activa!"
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            throw Exception(msg, ex)
        }

        /** Inicializando view model */
        viewModel = activity.run {
            ViewModelProviders.of(
                this ?: throw Exception("Invalid fragment activity"),
                ActivosViewModelFactory()
            ).get(ActivosViewModel::class.java)
        }
//        //No usar aqui, porque causa conflicto con el recycler view y no se muestra :c
//        val viewBinding: ViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activos, container, false)
//        viewBinding.setVariable(BR.modelActFr, viewModel)
//        viewBinding.executePendingBindings()

        return inflater.inflate(R.layout.fragment_activos, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navController = Navigation.findNavController(view ?: return)

        if (sharedData.auditoriaActiva?.id == viewModel.ultimaAuditoriaConsultada) mismaAuditoria = true

        Log.d(
            TAG,
            "onActivityCreated: ${sharedData.auditoriaActiva?.id} ${viewModel.ultimaAuditoriaConsultada} progressbar: ${progressBar.visibility}"
        )
        setupRecyclerView()
        setupAppBarScrollFade(app_bar_layout, ArrayList<View>().apply {
            add(chip_group)
        })
        addDescriptionChipsInToolbar()
        setupScannerButton()
        setupResponseHandler()
    }

    override fun onResume() {
        super.onResume()
        Log.d(
            TAG,
            "onResume: ${sharedData.auditoriaActiva?.id} ${viewModel.ultimaAuditoriaConsultada} progressbar: ${progressBar.visibility}"
        )
        setLoading(true)
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ${sharedData.auditoriaActiva?.id} ${viewModel.ultimaAuditoriaConsultada}")
    }

    private fun setupRecyclerView() {
        rv_activos.adapter = viewModel.recyclerActivosAdapter

        /** Solo actualizar el modelo si su valor es nulo o es diferente a a la anteriormente consultada */
        if (viewModel.activos.value == null || sharedData.auditoriaActiva?.id != viewModel.ultimaAuditoriaConsultada) {
            viewModel.callActivosAPI(
                apiKey = sharedData.token,
                clasificacion = sharedData.auditoriaActiva?.idClasificacion,
                departamento = sharedData.auditoriaActiva?.idDepartamento,
                empresa = sharedData.auditoriaActiva?.idEmpresa,
                auditoriaActual = sharedData.auditoriaActiva?.id ?: ""
            )
        }

        viewModel.activos.observe(this, Observer {
            viewModel.recyclerActivosAdapter.notifyDataSetChanged()
            viewModel.ultimaAuditoriaConsultada = sharedData.auditoriaActiva?.id!!
            setLoading(false)
            Log.d(TAG, "activos observer: RecyclerView updated! WIII")
        })
    }


    private fun setupResponseHandler() {
        viewModel.response.observe(this, Observer {
            val response: ApiResponse = it ?: return@Observer
            if (response.status == null) return@Observer

            if (response.status.contains("show")) {
                Log.d(TAG, "setupResponseHandler: show: ${response.description}")
                Toast.makeText(this.requireContext(), response.description, Toast.LENGTH_LONG)
                    .show()
                return@Observer
            }

            Log.d(TAG, "setupResponseHandler: ${response.status}: ${response.description}")
            if (!response.isOk && firstError) {
                if (!response.status.contains("app")) {
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
        Log.d(TAG, "setupResponseHandler: created observer done!")
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

        if (!sharedData.auditoriaActiva?.empresa.isNullOrEmpty()) {
            chip_group.addView(Chip(chip_group.context).apply {
                text = (sharedData.auditoriaActiva?.empresa ?: "").toLowerCase(Locale.ENGLISH).capitalize()
                chipBackgroundColor =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorEmpresa))
                setTextColor(textColor)
                setChipIconResource(R.drawable.ic_empresa_black_24dp)
                chipIconTint = textColor
            })
        }

        if (!sharedData.auditoriaActiva?.departamento.isNullOrEmpty()) {
            chip_group.addView(Chip(chip_group.context).apply {
                text = (sharedData.auditoriaActiva?.departamento ?: "").toLowerCase(Locale.ENGLISH).capitalize()
                chipBackgroundColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.colorDepartamento
                        )
                    )
                setTextColor(textColor)
                setChipIconResource(R.drawable.ic_departamento_black_24dp)
                chipIconTint = textColor
            })
        }

        if (!sharedData.auditoriaActiva?.clasificacion.isNullOrEmpty()) {
            chip_group.addView(Chip(chip_group.context).apply {
                text =
                    (sharedData.auditoriaActiva?.clasificacion ?: "").toLowerCase(Locale.ENGLISH).capitalize()
                chipBackgroundColor =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.colorClasificacion
                        )
                    )
                setTextColor(textColor)
                setChipIconResource(R.drawable.ic_clasificacion_black_24dp)
                chipIconTint = textColor
            })
        }
    }

    private fun setupScannerButton() {
        img_scanner.setOnClickListener {
            navController.navigate(
                R.id.action_activosFragment_to_scannerFragment,
                bundleOf("return_id" to true)
            )
        }
    }

    private fun setLoading(loaing: Boolean) {


        when (loaing) {
            true -> {
                // Solo se ejecuta cuando no sea la misma pantalla que la anteriormente abierta, pues cuando regresa a la misma pantalla, no tiene sentido estar en "loading"
                if (mismaAuditoria)
                    return

                rv_activos.visibility = View.GONE
                text_auditoria_empty.visibility = View.GONE
                progressBar.visibility = View.VISIBLE

            }

            false -> {
                if (viewModel.activos.value.isNullOrEmpty()) {
                    rv_activos.visibility = View.GONE
                    text_auditoria_empty.visibility = View.VISIBLE
                } else {
                    rv_activos.visibility = View.VISIBLE
                    text_auditoria_empty.visibility = View.GONE
                }

                progressBar.visibility = View.GONE
            }
        }

        Log.d(TAG, "onSetLoading($loaing): Progressbar: ${progressBar.visibility}")
    }


}
