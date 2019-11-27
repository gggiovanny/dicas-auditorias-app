package com.dicas.auditorias.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Auditoria(
    val id: String? = null,
    val fechaCreacion: String? = null,
    var status: String? = null,
    val descripcion: String? = null,
    val username: String? = null,
    val idEmpresa: String? = null,
    val empresa: String? = null,
    val idDepartamento: String? = null,
    val departamento: String? = null,
    val idClasificacion: String? = null,
    val clasificacion: String? = null,
    var terminada: String? = null,
    var fechaGuardada: String? = null
) : Parcelable