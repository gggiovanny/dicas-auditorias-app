package com.dicas.auditorias.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Auditoria(
    val id: String,
    val fechaCreacion: String,
    val status: String,
    val descripcion: String? = null,
    val username: String,
    val idEmpresa: String? = null,
    val idDepartamento: String? = null,
    val idClasificacion: String? = null,
    val terminada: String
) : Parcelable