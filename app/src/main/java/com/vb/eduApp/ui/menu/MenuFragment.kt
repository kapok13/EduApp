package com.vb.eduApp.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vb.eduApp.R
import com.vb.eduApp.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private val binding by lazy { FragmentMenuBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileBtn.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
        binding.createLessonBtn.setOnClickListener {
            findNavController().navigate(R.id.lessonFragment)
        }
        binding.lessonBtn.setOnClickListener {
            findNavController().navigate(R.id.lessonsFragment)
        }
    }
}
