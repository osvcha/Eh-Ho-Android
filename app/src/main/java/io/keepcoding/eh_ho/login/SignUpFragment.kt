package io.keepcoding.eh_ho.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.keepcoding.eh_ho.common.TextChangedWatcher
import io.keepcoding.eh_ho.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private val vm: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSignUpBinding.inflate(inflater, container, false).apply {
        labelSignIn.setOnClickListener {
            vm.moveToSignIn()
        }
        vm.signUpData.observe(viewLifecycleOwner) {
            inputEmail.apply {
                setText(it.email)
                setSelection(it.email.length)
            }
            inputUsername.apply {
                setText(it.userName)
                setSelection(it.userName.length)
            }
            inputPassword.apply {
                setText(it.password)
                setSelection(it.password.length)
            }
            inputConfirmPassword.apply {
                setText(it.confirmPassword)
                setSelection(it.confirmPassword.length)
            }
        }
        vm.signUpEnabled.observe(viewLifecycleOwner) {
            buttonSignUp.isEnabled = it
        }

        vm.validateSignUpError.observe(viewLifecycleOwner) {
            when (it) {
                is LoginViewModel.ValidateSignUpError.emailError -> {
                    inputEmail.error = "Enter a valid email"
                }
                is LoginViewModel.ValidateSignUpError.userNameError -> {
                    inputUsername.error = "Username must be greater than 5"
                }
                is LoginViewModel.ValidateSignUpError.passwordError -> {
                    inputPassword.error = "The password must be at least 8 characters and must contains upper and lower case, numbers and symbols"
                }
                is LoginViewModel.ValidateSignUpError.confirmPasswordError -> {
                    inputConfirmPassword.error = "Passwords don't match"
                }
            }
        }

        vm.state.observe(viewLifecycleOwner) {
            when (it) {
                is LoginViewModel.State.SignedUp -> {
                    inputEmail.setText("")
                    inputUsername.setText("")
                    inputPassword.setText("")
                    inputConfirmPassword.setText("")
                }
            }
        }

        fun cleanFrom() {

        }

        inputEmail.apply {
            addTextChangedListener(TextChangedWatcher(vm::onNewSignUpEmail))
        }
        inputUsername.apply {
            addTextChangedListener(TextChangedWatcher(vm::onNewSignUpUserName))
        }
        inputPassword.apply {
            addTextChangedListener(TextChangedWatcher(vm::onNewSignUpPassword))
        }
        inputConfirmPassword.apply {
            addTextChangedListener(TextChangedWatcher(vm::onNewSignUpConfirmPassword))
        }
        buttonSignUp.setOnClickListener {
            println("JcLog: clicking signup button")
            vm.signUp()
        }
    }.root

    companion object {
        fun newInstance(): SignUpFragment = SignUpFragment()
    }

}