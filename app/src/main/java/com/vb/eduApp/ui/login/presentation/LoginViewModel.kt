package com.vb.eduApp.ui.login.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vb.eduApp.data.model.User
import com.vb.eduApp.data.preferences.UserPreferencesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val userPreferencesRepo: UserPreferencesRepo) : ViewModel() {
    val authData: MutableLiveData<User> = MutableLiveData()

    fun checkForSavedUser() {
        viewModelScope.launch(Dispatchers.Main) {
            userPreferencesRepo.getUserInfo().collect {
                authData.value = it
            }
        }
    }

    fun saveAuthData(user: User) {
        viewModelScope.launch(Dispatchers.Default) { userPreferencesRepo.saveUserInfo(user) }
    }
}
