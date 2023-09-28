package com.vb.eduApp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vb.eduApp.databinding.ActivityMainBinding
import com.vb.eduApp.ui.main.presentation.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), MainActivityView {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainVM: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
    }

    override val viewModel: MainViewModel
        get() = mainVM
}
