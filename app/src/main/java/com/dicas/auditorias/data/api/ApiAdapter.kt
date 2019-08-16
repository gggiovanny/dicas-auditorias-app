package com.dicas.auditorias.data.api


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiAdapter
{
    companion object {
        private const val URL_API = "http://grupodicas.com.mx/api/"
    }

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
            .baseUrl(URL_API)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return  retrofit.create(ApiService::class.java)
    }

    fun getRequestInterface() = Retrofit.Builder()
    .baseUrl(URL_API)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build().create(ApiService::class.java)
}