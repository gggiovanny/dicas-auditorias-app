package com.dicas.auditorias.ui.scanner


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.dicas.auditorias.R


class ScannerFragment : Fragment() {

    companion object {
        private const val TAG = "ScannerFragment"
    }

    private lateinit var codeScanner: CodeScanner
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)
        val openWeb: Boolean = arguments?.getBoolean("open_web")!!
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {

            if (openWeb) {
                openWebPage(it.text)
            } else {
                val idActivo = extractID(it.text)
                val bundle = bundleOf("text_display" to idActivo.toString())
                navController.navigate(R.id.action_scannerFragment_to_testFragment, bundle)
            }
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
        if (!url.startsWith("http://")) {
            webpage = Uri.parse("http://$url")
        } else {
            webpage = Uri.parse(url)
        }
        Log.d(TAG, "openWebPage: text_scanned=[$url], uri=[$webpage]")
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        startActivity(intent)
    }

    private fun extractID(textQR: String): Int {
        val baseUrl = "grupodicas.com.mx/a/index.php?f="

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
