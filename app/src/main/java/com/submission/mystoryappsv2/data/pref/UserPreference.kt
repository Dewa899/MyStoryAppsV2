package com.submission.mystoryappsv2.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.stringPreferencesKey

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
val loginStatusKey = booleanPreferencesKey("user_login_status")
private val tokenKey = stringPreferencesKey("user_token")

class UserPreference(private val dataStore: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }


    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            val token = preferences[tokenKey] ?: ""
            val isLogin = preferences[loginStatusKey] ?: false
            UserModel(preferences[tokenKey] ?: "", token, isLogin)
        }
    }

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = user.token
            preferences[loginStatusKey] = user.isLogin 
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(tokenKey)
            preferences[loginStatusKey] = false
        }
    }
}


