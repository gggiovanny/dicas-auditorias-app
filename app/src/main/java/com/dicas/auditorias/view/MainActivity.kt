package com.dicas.auditorias.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.dicas.auditorias.R
import com.dicas.auditorias.models.Repository

class MainActivity : AppCompatActivity() {

    lateinit var repository: Repository

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Init */
        repository = Repository()

        repository.callToken("ggonzalez", "Acm1pt.69")

        repository.getToken().observe(this, Observer {
            Log.d(TAG, "onCreate: $it")
        })


    }


}
