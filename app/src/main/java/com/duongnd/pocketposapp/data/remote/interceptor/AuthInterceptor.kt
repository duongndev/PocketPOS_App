package com.duongnd.pocketposapp.data.remote.interceptor

import com.duongnd.pocketposapp.core.utils.ShareReferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sharedPrefs: ShareReferenceManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sharedPrefs.getAccessToken()

        val requestBuilder = originalRequest.newBuilder()
        if (token != null && !originalRequest.url.host.contains("vietqr.app")) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
