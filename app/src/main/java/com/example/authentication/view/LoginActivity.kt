package com.example.authentication.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.authentication.R
import com.example.authentication.data.LoginBody
import com.example.authentication.databinding.ActivityLoginBinding
import com.example.authentication.repository.AuthRepository
import com.example.authentication.utils.APIService
import com.example.authentication.view_model.LoginActivityViewModel
import com.example.authentication.view_model.LoginActivityViewModelFactory

class LoginActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener{
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mViewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mBinding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        mBinding.loginWithGoogleBt.setOnClickListener(this)
        mBinding.loginBtn.setOnClickListener(this)
        mBinding.registerBtn.setOnClickListener(this)
        mBinding.emailEt.onFocusChangeListener = this
        mBinding.passwordEt.onFocusChangeListener = this
        mBinding.passwordEt.setOnKeyListener(this)

        mViewModel = ViewModelProvider(this, LoginActivityViewModelFactory(AuthRepository(APIService.getService()),application)).get(LoginActivityViewModel::class.java)

        setupObservers()

    }
    private fun setupObservers() {
        mViewModel.getIsLoading().observe(this) {
//            mBinding.progressBar.isVisible = it
        }

        mViewModel.getErrorMessage().observe(this) {
            // full name, email, password
            val fromErrorKeys = arrayOf("fullName", "email", "password")
            val message = StringBuilder()
            it.map { entry ->
                if (fromErrorKeys.contains(entry.key)) {
                    when (entry.key) {

                        "email" -> {
                            mBinding.emailTil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }
                        "password" -> {
                            mBinding.passwordTil.apply {
                                isErrorEnabled = true
                                error = entry.value
                            }
                        }
                    }
                } else {
                    message.append(entry.value).append("\n")
                }
                if (message.isNotEmpty()) {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.info_24)
                        .setTitle("INFORMATION")
                        .setMessage(message)
                        .setPositiveButton("OK") { dialog, _ -> dialog!!.dismiss() }
                        .show()
                }
            }
        }
        mViewModel.getUser().observe(this) {
            if (it != null) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }


    private fun validateEmail(shouldUpdateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value = mBinding.emailEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            errorMessage = "Email address is invalid"
        }
        if (errorMessage != null && shouldUpdateView) {
            mBinding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    private fun validatePassword(shouldUpdateView: Boolean = true): Boolean {
        var errorMessage: String? = null
        val value = mBinding.passwordEt.text.toString()
        if (value.isEmpty()) {
            errorMessage = "Password is required"
        } else if (value.length < 6) {
            errorMessage = "password must be 6 characters long"
        }
        if (errorMessage != null && shouldUpdateView) {
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }
    private fun validate(): Boolean {
        var isValid = true
        if (!validateEmail()) isValid = false
        if (!validatePassword()) isValid = false

        return isValid
    }

    override fun onClick(view: View?) {
        if (view !=null){
            when(view.id){
                R.id.loginBtn -> {
                    submitFrom()
                }
                R.id.registerBtn -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            }
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {

                R.id.emailEt -> {
                    if (hasFocus) {
                        if (mBinding.emailTil.isErrorEnabled) {
                            mBinding.emailTil.isErrorEnabled = false
                        }
                    } else {
                         validateEmail()
                    }
                }
                R.id.passwordEt -> {
                    if (hasFocus) {
                        if (mBinding.passwordTil.isErrorEnabled) {
                            mBinding.passwordTil.isErrorEnabled = false
                        }
                    } else {
                        validatePassword()
                    }
                }
            }
        }
    }

    private fun submitFrom(){
        if (validate()){
            // verify user credential
            mViewModel.loginUser(LoginBody(mBinding.emailEt.text!!.toString(),mBinding.passwordEt.text!!.toString()))
        }
    }
    override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent!!.action == KeyEvent.ACTION_UP){
            submitFrom()
        }

        return false
    }
}