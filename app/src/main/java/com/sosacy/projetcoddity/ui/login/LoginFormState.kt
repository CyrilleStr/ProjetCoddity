package com.sosacy.projetcoddity.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = 1,
    val passwordError: Int? = 2,
    val isDataValid: Boolean = false
)