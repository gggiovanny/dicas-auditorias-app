package com.dicas.auditorias.ui.scanner


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.dicas.auditorias.R
import com.dicas.auditorias.ui.activos.ActivosViewModel
import com.dicas.auditorias.ui.activos.ActivosViewModelFactory
import com.dicas.auditorias.ui.common.SharedDataViewModel


class ScannerFragment : Fragment() {

    companion object {
        private const val TAG = "ScannerFragment"
        private const val baseUrl = "grupodicas.com.mx/a/index.php?f="

    }

    private lateinit var codeScanner: CodeScanner
    private lateinit var navController: NavController
    private lateinit var viewModel: ActivosViewModel
    private lateinit var sharedData: SharedDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /** Inicializando variables lateinit */
        viewModel = activity.run {
            ViewModelProviders.of(
                this ?: throw Exception("Invalid fragment activity"),
                ActivosViewModelFactory()
            ).get(ActivosViewModel::class.java)
        }
        navController = Navigation.findNavController(view)
        val returnId: Boolean = arguments?.getBoolean("return_id")!!
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        /** Obteniendo los datos de usuario compartidos */
        sharedData = activity.run {
            ViewModelProviders.of(this)[SharedDataViewModel::class.java]
        }

        /** Configurando callback para cuando el scanner encuentre algo */
        codeScanner.decodeCallback = DecodeCallback {

            if (returnId) { // si se solicita a traves del parametro que se regrese la ID, se navega al fragment anterior y se le pasa dicho parametro.
                activity.runOnUiThread {
                    viewModel.setActivoExistencia(
                        idActivo = extractID(it.text),
                        existe = true
                    )
                }
            } else { // Si no, por defecto se abre el QR como pagina web para consulta.
                openWebPage(it.text)
            }

            // Sin importar el resultado o la accion, regresar a la actividad anterior al terminar de usarse el scanner
            navController.popBackStack()
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun openWebPage(url: String) {
        lateinit var webpage: Uri
        if (url.startsWith(baseUrl)) {
            webpage = Uri.parse("http://$url")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        } else {
            activity?.runOnUiThread {
                Toast.makeText(context, "¡Código QR no válido!", Toast.LENGTH_LONG).show()
            }
        }
        Log.d(TAG, "openWebPage: text_scanned=[$url]")
    }

    private fun extractID(textQR: String): Int {

        if (textQR.startsWith(baseUrl)) {
            return Integer.parseInt(textQR.substring(baseUrl.length))
        } else {
            activity?.runOnUiThread {
                Toast.makeText(context, "¡Código QR no válido!", Toast.LENGTH_LONG).show()
            }
            return -1
        }

    }
}
