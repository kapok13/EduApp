package com.vb.eduApp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.vb.eduApp.R
import com.vb.eduApp.data.model.User
import com.vb.eduApp.databinding.FragmentProfileBinding
import com.vb.eduApp.ui.login.LoginActivity
import com.vb.eduApp.ui.main.MainActivityView


class ProfileFragment : Fragment() {
    private val binding by lazy { FragmentProfileBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        binding.profileUsername.text = auth.currentUser?.displayName.toString()
        binding.profileEmail.text = auth.currentUser?.email.toString()
        binding.profileToMyLessonsBtn.setOnClickListener {
            findNavController().navigate(R.id.userLessonsFragment)
        }
        binding.profileLogout.setOnClickListener {
            (requireActivity() as MainActivityView).viewModel.saveAuthData(User("", ""))
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }
}
