package com.dicas.auditorias.data.api


import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*
import io.reactivex.Observable

interface ApiService
{
    /** Authentication */

    @GET("auth")
    fun getToken(
        @Query("user") user: String,
        @Query("passwd") passwd: String
    ): Observable<JsonObject>

    /** Activos */

    @GET("activos")
    fun getActivos( @QueryMap parameters: Map<String, String> = HashMap() ): Observable<JsonObject>

    @GET("activos/{id}")
    fun getActivo( @Path("id") id:Int ): Call<JsonObject>

    /** Auditorias */

    @GET("auditorias")
    fun getAuditorias( @QueryMap parameters: Map<String, String> ): Call<JsonObject>

    @GET("auditorias/{id}")
    fun getAuditoria( @Path("id") id:Int ): Call<JsonObject>

    @GET("auditorias")
    fun createAuditoria( @Query("descripcion") descripcion: String = "" ): Call<JsonObject>

    @PUT("auditorias/{id}")
    fun finishAuditoria(@Path("id") id: Int, @Query("terminada") terminada: Boolean ): Call<JsonObject>

    @PUT("auditorias/{id}")
    fun saveAuditoria(@Path("id") id: Int, @Query("guardada") guardada: Boolean ): Call<JsonObject>

    /** Auditorias_activos */

    @GET("auditorias/{id_auditoria}/activos")
    fun getAuditoriaActivos(
        @Path("id_auditoria") id_auditoria: Int,
        @Query("all") all: Boolean,
        @QueryMap parameters: Map<String, Int>
    ): Call<JsonObject>

    @GET("auditorias/{id_auditoria}/activos/{id_activo}")
    fun getAuditoriaActivo(
        @Path("id_auditoria") id_auditoria: Int,
        @Path("id_activo") id_activo: Int
    ): Call<JsonObject>

    @POST("auditorias/{id_auditoria}/activos/{id_activo}")
    fun createAuditoriaActivo(
        @Path("id_auditoria") id_auditoria: Int,
        @Path("id_activo") id_activo: Int,
        @Query("conteo") conteo: String
    ): Call<JsonObject>

    @PUT("auditorias/{id_auditoria}/activos/{id_activo}")
    fun updateAuditoriaActivo(
        @Path("id_auditoria") id_auditoria: Int,
        @Path("id_activo") id_activo: Int,
        @Query("conteo") conteo: String
    ): Call<JsonObject>

    /** Empresas */

    @GET("empresas")
    fun getEmpresas(
        @QueryMap parameters: Map<String, String>
    ): Call<JsonObject>

    /** Departamentos */

    @GET("departamentos")
    fun getDepartamentos(
        @QueryMap parameters: Map<String, String>
    ): Call<JsonObject>
}