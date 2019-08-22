package com.dicas.auditorias.ui.auditorias

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.dicas.auditorias.R

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Redireccionando los datos del usuario recibidos en intent.extras a el fragment host
         * de la navegacion (que seria a AuditoriasFragment) */
        findNavController(R.id.nav_host_fragment).setGraph(R.navigation.nav_graph, intent.extras)

    }
}
