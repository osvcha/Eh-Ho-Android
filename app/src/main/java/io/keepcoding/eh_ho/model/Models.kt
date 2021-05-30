package io.keepcoding.eh_ho.model

sealed class LogIn {
    data class Success(val userName: String) : LogIn()
    data class Error(val error: String) : LogIn()
}

sealed class SignUp {
    data class Success(
        val success: Boolean,
        val message: String) : SignUp()
    data class Error(val error: String) : SignUp()
}

data class Topic(
    val id: Int,
    val title: String,
    val views: String,
    val likes: String
    //TODO
    //Añadir resto de atributos necesarios del modelo
)