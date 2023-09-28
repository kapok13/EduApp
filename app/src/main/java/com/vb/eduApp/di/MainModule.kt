package com.vb.eduApp.di

import com.vb.eduApp.data.preferences.UserPreferencesRepo
import com.vb.eduApp.data.preferences.UserPreferencesRepoImpl
import com.vb.eduApp.ui.login.presentation.LoginViewModel
import com.vb.eduApp.ui.main.presentation.MainViewModel
import com.vb.eduApp.ui.registration.presentation.RegistrationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

val mainModule = module {
    single<UserPreferencesRepo> { UserPreferencesRepoImpl(androidContext()) }

    viewModel {
        MainViewModel(get(UserPreferencesRepo::class.java))
    }
    viewModel {
        LoginViewModel(get(UserPreferencesRepo::class.java))
    }
    viewModel {
        RegistrationViewModel(get(UserPreferencesRepo::class.java))
    }
}
