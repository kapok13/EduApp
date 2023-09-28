package com.vb.eduApp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vb.eduApp.data.model.User
import kotlinx.coroutines.flow.map

private const val USER_PREFERENCES_DATASTORE_NAME = "user_datastore"
private const val USER_PREFERENCES_EMAIL_KEY = "user_email"
private const val USER_PREFERENCES_PASS_KEY = "user_pass"

class UserPreferencesRepoImpl(private val context: Context) : UserPreferencesRepo {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_DATASTORE_NAME)
    private val emailKey = stringPreferencesKey(USER_PREFERENCES_EMAIL_KEY)
    private val passKey = stringPreferencesKey(USER_PREFERENCES_PASS_KEY)

    override fun getUserInfo() = context.dataStore.data
        .map { preferences ->
            User(
                email = preferences[emailKey] ?: "",
                password = preferences[passKey] ?: ""
            )
        }

    override suspend fun saveUserInfo(user: User) {
        context.dataStore.edit { settings ->
            settings[emailKey] = user.email
            settings[passKey] = user.password
        }
    }
}
