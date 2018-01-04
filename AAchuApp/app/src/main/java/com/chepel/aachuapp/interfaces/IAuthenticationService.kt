package com.chepel.aachuapp.interfaces

import com.google.android.gms.tasks.Task

/**
 * Created by maksim.chepel on 12/6/17.
 */

interface IAuthenticationService {
    enum class AuthenticationError {
        None,
        Unknown,
        NetworkError,
        ServerError
    }

    companion object AuthenticationResult
    {
        var Success: Boolean = false
        var Error: AuthenticationError? = null
        var ServerErrorMessage: String = ""
    }
/*
    fun AuthenticateEmailAsync(email: String, password: String): Task<AuthenticationResult>

    val CurrentUser: IUser
    val NewUser: IUser
    fun ResetPasswordAsync(userID: String): Task<AuthenticationResult>

    fun AddUserAsync() : Task<AuthenticationResult>*/
}