package com.dicas.auditorias.models


import android.content.res.Resources
import com.dicas.auditorias.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiAdapter
{
    private val urlApi = "http://grupodicas.com.mx/api/"
    private val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NjY1MjMwMDIsImNpZCI6ImI4ZWFlMjViOWNkNTU0ZjFjNzNlNWZkZDY0Yjg0NjBhODhjYjAyZmUiLCJkYXRhIjp7InVzZXJuYW1lIjoiZ2dvbnphbGV6IiwiaWQiOjE3fX0.7rI-VPYlSpnu5ERTa7i160GH9c7nzwB-C6O3Zgr5d6c"

    fun getClientService(): ApiService
    {
        val authInterceptor = Interceptor { chain ->
            val url = chain.request().url().newBuilder()
                .build()

            val newRequest = chain.request()
                .newBuilder()
                .url(url)
                .build()

            chain.proceed(newRequest)
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return  retrofit.create(ApiService::class.java)
    }
}