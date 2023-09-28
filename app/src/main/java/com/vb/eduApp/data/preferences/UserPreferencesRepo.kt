package com.vb.eduApp.data.preferences

import com.vb.eduApp.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepo {
    fun getUserInfo(): Flow<User>
    suspend fun saveUserInfo(user: User)
}
