package com.dicas.auditorias.ui.login

import com.dicas.auditorias.data.model.LoggedInUser

/**
 * Authentication result : success (userDataSource details) or error message.
 */
data class LoginResult(
    val success: LoggedInUser? = null,
    val error: Int? = null,
    val description: String? = null
)
