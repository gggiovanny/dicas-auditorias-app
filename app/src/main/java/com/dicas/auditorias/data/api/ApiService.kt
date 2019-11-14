package com.dicas.auditorias.data.api


import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.*

interface ApiService {
    /** Authentication */

    @GET("auth")
    fun getToken(
        @Query("user") user: String,
        @Query("passwd") passwd: String
    ): Observable<JsonObject>

    /** Activos */

    @GET("activos")
    fun getActivos(
        @Query("auditoria_actual") auditoria_actual: String = "",
        @Query("empresa") empresa: String = "",
        @Query("departamentos") departamento: String = "",
        @Query("clasificacion") clasificacion: String = "",
        @Query("page_size") page_size: String = "100000"
    ): Observable<JsonObject>

    @GET("activos/{id}")
    fun getActivo(@Path("id") id: Int): Observable<JsonObject>

    @PUT("activos/{id}")
    fun saveAuditoria(
        @Path("id") id: Int,
        @Query("guardada") guardada: Boolean
    ): Observable<JsonObject>

    /** Auditorias */

    @GET("auditorias")
    fun getAuditorias(
        @Query("user") user: String = "",
        @Query("status") status: String = "",
        @Query("page_size") page_size: String = "100000"
    ): Observable<JsonObject>

    @GET("auditorias/{id}")
    fun getAuditoria(@Path("id") id: Int): Observable<JsonObject>

    @POST("auditorias")
    fun createAuditoria(
        @Query("descripcion") descripcion: String = "",
        @Query("empresa") empresa: String = "",
        @Query("departamento") departamento: String = "",
        @Query("clasificacion") clasificacion: String = ""
    ): Observable<JsonObject>

    @PUT("auditorias/{id}")
    fun finishAuditoria(
        @Path("id") id: Int,
        @Query("terminada") terminada: Boolean
    ): Observable<JsonObject>

    /** Auditorias_activos */

    @GET("activos/{id_auditoria}/activos")
    fun getAuditoriaActivos(
        @Path("id_auditoria") id_auditoria: Int,
        @Query("all") all: Boolean,
        @QueryMap parameters: Map<String, Int>
    ): Observable<JsonObject>

    @GET("activos/{id_auditoria}/activos/{id_activo}")
    fun getAuditoriaActivo(
        @Path("id_auditoria") id_auditoria: Int,
        @Path("id_activo") id_activo: Int
    ): Observable<JsonObject>

    @POST("auditorias/{id_auditoria}/activos/{id_activo}")
    fun setActivoExistenciaActual(
        @Path("id_auditoria") id_auditoria: Int,
        @Path("id_activo") id_activo: Int,
        @Query("existencia") existencia: Boolean
    ): Observable<JsonObject>

    @PUT("activos/{id_auditoria}/activos/{id_activo}")
    fun updateAuditoriaActivo(
        @Path("id_auditoria") id_auditoria: Int,
        @Path("id_activo") id_activo: Int,
        @Query("conteo") conteo: String
    ): Observable<JsonObject>

    /** Empresas */

    @GET("empresas")
    fun getEmpresas(): Observable<JsonObject>

    /** Departamento */

    @GET("departamentos")
    fun getDepartamentos(
        @Query("empresa") empresaID: String
    ): Observable<JsonObject>

    @GET("clasificaciones")
    fun getClasificaciones(): Observable<JsonObject>
}