package io.keepcoding.eh_ho.repository

import io.keepcoding.eh_ho.model.LogIn
import io.keepcoding.eh_ho.model.SignUp
import io.keepcoding.eh_ho.model.Topic
import io.keepcoding.eh_ho.network.Client

class Repository(private val client: Client) {

	private var userName: String = ""

	fun signIn(
		userName: String,
		password: String,
		callback: Callback<LogIn>
	) {
		client.signIn(userName, password) {
			storeLogIn(it)
			callback.onResult(it)
		}
	}

	fun signup(
		username: String,
		email: String,
		password: String,
		callback: Callback<SignUp>,
	) {
		client.signUp(username, email, password) {
			callback.onResult(it)
		}
	}

	fun getTopics(callback: Callback<Result<List<Topic>>>) {
		client.getTopics {
			callback.onResult(it)
		}
	}

	//TODO
	//getposts

	private fun storeLogIn(logIn: LogIn) {
		userName = (logIn as? LogIn.Success)?.userName ?: userName
	}

	fun interface Callback<T> {
		fun onResult(t: T)
	}
}