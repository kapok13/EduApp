package com.vb.eduApp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.vb.eduApp.R
import com.vb.eduApp.data.model.User
import com.vb.eduApp.databinding.ActivityLoginBinding
import com.vb.eduApp.ui.login.presentation.LoginViewModel
import com.vb.eduApp.ui.main.MainActivity
import com.vb.eduApp.ui.registration.RegistrationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.checkForSavedUser()
        viewModel.authData.observe(this) {
            if (it.email.isNotEmpty() && it.password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(it.email, it.password)
                    .addOnSuccessListener {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
            }
        }

        binding.loginButton.setOnClickListener {
            var errorMessage: String? = null
            when {
                binding.username.text.isNullOrEmpty()
                        || binding.password.text.isNullOrEmpty() -> {
                    errorMessage = getString(R.string.login_empty_fields)
                }

                Patterns.EMAIL_ADDRESS.matcher(binding.username.text).matches().not() -> {
                    errorMessage = getString(R.string.registration_invalid_email)
                }

                binding.password.text.length < 6 -> {
                    errorMessage = getString(R.string.registration_short_password)
                }
            }
            if (errorMessage != null) {
                binding.password.error = errorMessage
            } else {
                binding.registrationProgress.isVisible = true
                auth.signInWithEmailAndPassword(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                ).addOnSuccessListener {
                    viewModel.saveAuthData(
                        User(
                            email = binding.username.text.toString(),
                            password = binding.password.text.toString()
                        )
                    )
                    binding.registrationProgress.isVisible = false
                }.addOnFailureListener {
                    binding.password.error = getString(R.string.login_error)
                    binding.registrationProgress.isVisible = false
                }
            }
        }
        binding.registrationButton.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}
