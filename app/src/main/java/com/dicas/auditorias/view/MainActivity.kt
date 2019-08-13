package com.dicas.auditorias.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dicas.auditorias.R
import com.dicas.auditorias.models.Activo
import com.dicas.auditorias.models.ApiAdapter
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getActivos()

    }

    fun getActivos()
    {
        val activos = ArrayList<Activo>()

        val apiAdapter = ApiAdapter()
        val apiService = apiAdapter.getClientService()
        val call = apiService.getToken("ggonzalez", "Acm1pt.69")


        call.enqueue(object : Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable)
            {
                Log.e("ERROR: ", t.message)
                t.stackTrace
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>)
            {
                val token = response.body()!!.get("token").asString
                Log.d("Description: ", token)

            }
        })
        //CONTROLLER
    }
}
