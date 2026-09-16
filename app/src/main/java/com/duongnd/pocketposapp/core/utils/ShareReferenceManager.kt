package com.duongnd.pocketposapp.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.duongnd.pocketposapp.data.remote.dto.auth.UserDTO
import com.duongnd.pocketposapp.data.remote.dto.store.BankItem
import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class ShareReferenceManager @Inject constructor(
    context: Context,
    private val moshi: Moshi
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("PocketPOS_Prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_DATA = "user_data"
        private const val KEY_STORE_DATA = "store_data"
        private const val KEY_BANKS_DATA = "banks_data"
    }

    fun saveTokens(access: String, refresh: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, access)
            putString(KEY_REFRESH_TOKEN, refresh)
            apply()
        }
    }

    fun getAccessToken(): String? = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    fun saveUser(user: UserDTO) {
        val json = moshi.adapter(UserDTO::class.java).toJson(user)
        sharedPreferences.edit().putString(KEY_USER_DATA, json).apply()
    }

    fun getUser(): UserDTO? {
        val json = sharedPreferences.getString(KEY_USER_DATA, null) ?: return null
        return try {
            moshi.adapter(UserDTO::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun saveStore(store: StoreDTO) {
        val json = moshi.adapter(StoreDTO::class.java).toJson(store)
        sharedPreferences.edit().putString(KEY_STORE_DATA, json).apply()
    }

    fun getStore(): StoreDTO? {
        val json = sharedPreferences.getString(KEY_STORE_DATA, null) ?: return null
        return try {
            moshi.adapter(StoreDTO::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun saveBanks(banks: List<BankItem>) {
        val type = Types.newParameterizedType(List::class.java, BankItem::class.java)
        val json = moshi.adapter<List<BankItem>>(type).toJson(banks)
        sharedPreferences.edit().putString(KEY_BANKS_DATA, json).apply()
    }

    fun getBanks(): List<BankItem>? {
        val json = sharedPreferences.getString(KEY_BANKS_DATA, null) ?: return null
        return try {
            val type = Types.newParameterizedType(List::class.java, BankItem::class.java)
            moshi.adapter<List<BankItem>>(type).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
