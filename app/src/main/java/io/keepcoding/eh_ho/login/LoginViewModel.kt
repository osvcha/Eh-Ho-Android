package io.keepcoding.eh_ho.login

import androidx.lifecycle.*
import io.keepcoding.eh_ho.model.LogIn
import io.keepcoding.eh_ho.model.SignUp
import io.keepcoding.eh_ho.repository.Repository
import org.json.JSONObject

class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData<State>().apply { postValue(State.SignIn) }
    private val _signInData = MutableLiveData<SignInData>().apply { postValue(SignInData("", "")) }
    private val _signUpData = MutableLiveData<SignUpData>().apply { postValue(SignUpData("", "", "", "")) }
    private val _errorInLogin = MutableLiveData<String?>()
    private val _validateSignUpError = MutableLiveData<ValidateSignUpError>()

    val errorInLogin: LiveData<String?> = _errorInLogin
    val state: LiveData<State> = _state
    val signInData: LiveData<SignInData> = _signInData
    val signUpData: LiveData<SignUpData> = _signUpData
    val validateSignUpError: LiveData<ValidateSignUpError> = _validateSignUpError
    val signInEnabled: LiveData<Boolean> = Transformations.map(_signInData) { it?.isValid() ?: false }
    val signUpEnabled: LiveData<Boolean> = Transformations.map(_signUpData) { it?.isValid() ?: false }
    val loading: LiveData<Boolean> = Transformations.map(_state) {
        when (it) {
            State.SignIn,
            State.SignedIn,
            State.SignUp,
            State.SignedUp -> false
            State.SigningIn,
            State.SigningUp -> true
        }
    }



    fun onNewSignInUserName(userName: String) {
        onNewSignInData(_signInData.value?.copy(userName = userName))
    }

    fun onNewSignInPassword(password: String) {
        onNewSignInData(_signInData.value?.copy(password = password))
    }

    fun onNewSignUpUserName(userName: String) {
        if (!validateUserName(userName)) _validateSignUpError.postValue(ValidateSignUpError.userNameError)
        onNewSignUpData(_signUpData.value?.copy(userName = userName))
    }

    fun onNewSignUpEmail(email: String) {
        if(!validateEmail(email)) _validateSignUpError.postValue((ValidateSignUpError.emailError))
        onNewSignUpData(_signUpData.value?.copy(email = email))
    }

    fun onNewSignUpPassword(password: String) {
        if(!validatePassword(password)) _validateSignUpError.postValue((ValidateSignUpError.passwordError))
        onNewSignUpData(_signUpData.value?.copy(password = password))
    }

    fun onNewSignUpConfirmPassword(confirmPassword: String) {
        if(!validateConfirmPassword(_signUpData.value?.password, confirmPassword)) _validateSignUpError.postValue((ValidateSignUpError.confirmPasswordError))
        onNewSignUpData(_signUpData.value?.copy(confirmPassword = confirmPassword))
    }

    private fun onNewSignInData(signInData: SignInData?) {
        signInData?.takeUnless { it == _signInData.value }?.let(_signInData::postValue)
    }

    private fun onNewSignUpData(signUpData: SignUpData?) {
        signUpData?.takeUnless { it == _signUpData.value }?.let(_signUpData::postValue)
    }

    fun moveToSignIn() {
        _state.postValue(State.SignIn)
    }

    fun moveToSignUp() {
        _state.postValue(State.SignUp)
    }

    fun signIn() {
        _state.postValue(State.SigningIn)
        signInData.value?.takeIf { it.isValid() }?.let {
            repository.signIn(it.userName, it.password) {
                if (it is LogIn.Success) {
                    _state.postValue(State.SignedIn)
                } else {
                    _errorInLogin.postValue("User not found!")
                    _state.postValue(State.SignIn)

                }
            }
        }
    }

    fun signUp() {
        _state.postValue(State.SigningUp)
        signUpData.value?.takeIf { it.isValid() }?.let {
            repository.signup(it.userName, it.email, it.password) {
                if (it is SignUp.Success) {
                    _errorInLogin.postValue(it.message)
                    _state.postValue(State.SignedUp)
                } else {
                    _errorInLogin.postValue("SignUp Error!")
                    _state.postValue(State.SignUp)
                }
            }
        }
    }

    sealed class State {
        object SignIn : State()
        object SigningIn : State()
        object SignedIn : State()
        object SignUp : State()
        object SigningUp : State()
        object SignedUp : State()
    }

    sealed class ValidateSignUpError {
        object userNameError : ValidateSignUpError()
        object emailError : ValidateSignUpError()
        object passwordError : ValidateSignUpError()
        object confirmPasswordError : ValidateSignUpError()
    }



    data class SignInData(
        val userName: String,
        val password: String,
    )

    data class SignUpData(
        val email: String,
        val userName: String,
        val password: String,
        val confirmPassword: String
    )

    class LoginViewModelProviderFactory(private val repository: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(repository) as T
            else -> throw IllegalArgumentException("LoginViewModelFactory can only create instances of the LoginViewModel")
        }
    }
}


private fun LoginViewModel.SignInData.isValid(): Boolean {
    return userName.isNotBlank() && password.isNotBlank()
}
private fun LoginViewModel.SignUpData.isValid(): Boolean {
    if(!validateUserName(userName)) return false

    if(!validateEmail(email)) return false

    if(!validatePassword(password)) return false

    if(!validateConfirmPassword(password, confirmPassword)) return false

    return true
}

private fun validateUserName(userName: String): Boolean {
    return userName.length >= 6
}
private fun validatePassword(password: String): Boolean =
    "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]).{8,})".toRegex().matches(password)

private fun validateEmail(email: String): Boolean =
    "[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?".toRegex().matches(email)

private fun validateConfirmPassword(password: String?, confirmPassword: String): Boolean =
    password == confirmPassword