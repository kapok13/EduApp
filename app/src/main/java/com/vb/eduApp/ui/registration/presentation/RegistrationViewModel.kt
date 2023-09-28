package com.vb.eduApp.ui.registration.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vb.eduApp.data.model.User
import com.vb.eduApp.data.preferences.UserPreferencesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationViewModel(private val userPreferencesRepo: UserPreferencesRepo) : ViewModel() {
    fun saveAuthData(user: User) {
        viewModelScope.launch(Dispatchers.Default) { userPreferencesRepo.saveUserInfo(user) }
    }
}
