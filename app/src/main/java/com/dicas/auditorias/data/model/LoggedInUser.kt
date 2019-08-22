package com.dicas.auditorias.data.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Parcelize
data class LoggedInUser(
    val token: String,
    val name: String? = null,
    val fromMemory: Boolean = false
): Parcelable