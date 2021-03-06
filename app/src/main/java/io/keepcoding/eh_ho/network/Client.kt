package io.keepcoding.eh_ho.network

import io.keepcoding.eh_ho.model.LogIn
import io.keepcoding.eh_ho.model.SignUp
import io.keepcoding.eh_ho.model.Topic
import okhttp3.*
import java.io.IOException

class Client(
        baseUrl: String,
        apiKey: String,
        apiUsername: String,
        private val okHttpClient: OkHttpClient,
) {
    private val requestBuilder = RequestBuilder(baseUrl, apiKey, apiUsername)

    fun signIn(
        userName: String,
        password: String,
        callback: Callback<LogIn>) {
        runRequest(
                requestBuilder.signInRequest(userName),
                callback,
                IOException::toSignInModel,
                Response::toSignInModel,
        )
    }

    fun signUp(
            username: String,
            email: String,
            password: String,
            callback: Callback<SignUp>,
    ) {
        runRequest(
                requestBuilder.signUpRequest(username, email, password),
                callback,
                IOException::toSignUpModel,
                Response::toSignUpModel,
        )
    }

    fun getTopics(callback: Callback<Result<List<Topic>>>) {
        runRequest(
            requestBuilder.topicsRequest(),
            { callback.onResponse(it) },
            IOException::toTopicsModel,
            Response::toTopicsModel,
        )
    }

    //TODO
    //getposts dentro de un topic
    fun getPosts(id: Int) {

    }

    private fun <T> runRequest(
            request: Request,
            callback: Callback<T>,
            exceptionTransform: IOException.() -> T,
            responseTransform: Response.() -> T,) {
        okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) = callback.onResponse(e.exceptionTransform())
            override fun onResponse(call: Call, response: Response) {
                response.let(responseTransform)?.let{ callback.onResponse(it) }
            }
        })
    }

    fun interface Callback<T> {
        fun onResponse(t: T)
    }
}