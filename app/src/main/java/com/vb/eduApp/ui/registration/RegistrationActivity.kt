package com.vb.eduApp.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.vb.eduApp.R
import com.vb.eduApp.data.model.User
import com.vb.eduApp.databinding.ActivityRegistrationBinding
import com.vb.eduApp.ui.main.MainActivity
import com.vb.eduApp.ui.registration.presentation.RegistrationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: RegistrationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.registrationRegistrationButton.setOnClickListener {
            var errorMessage: String? = null
            when {
                binding.registrationPassword.text.isNullOrEmpty()
                        || binding.registrationEmail.text.isNullOrEmpty()
                        || binding.registrationUsername.text.isNullOrEmpty() -> {
                    errorMessage = getString(R.string.registration_empty_fields)
                }

                Patterns.EMAIL_ADDRESS.matcher(binding.registrationEmail.text).matches().not() -> {
                    errorMessage = getString(R.string.registration_invalid_email)
                }

                binding.registrationPassword.text.length < 6 -> {
                    errorMessage = getString(R.string.registration_short_password)
                }
            }
            if (errorMessage != null) {
                binding.registrationPassword.error = errorMessage
            } else {
                binding.registrationProgress.isVisible = true
                auth.createUserWithEmailAndPassword(
                    binding.registrationEmail.text.toString(),
                    binding.registrationPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        auth.currentUser
                            ?.updateProfile(
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(binding.registrationUsername.text.toString())
                                    .build()
                            )
                        viewModel.saveAuthData(
                            User(
                                email = binding.registrationEmail.text.toString(),
                                password = binding.registrationPassword.text.toString()
                            )
                        )
                        binding.registrationProgress.isVisible = false
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        binding.registrationPassword.error =
                            getString(R.string.registration_already_registered)
                        binding.registrationProgress.isVisible = false
                    }
                }
            }
        }
    }
}
