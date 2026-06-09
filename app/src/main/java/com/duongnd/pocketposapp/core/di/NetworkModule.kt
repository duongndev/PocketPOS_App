package com.duongnd.pocketposapp.core.di

import com.duongnd.pocketposapp.BuildConfig
import com.duongnd.pocketposapp.data.remote.api.AuthAPI
import com.duongnd.pocketposapp.data.remote.api.CategoryAPI
import com.duongnd.pocketposapp.data.remote.api.OrderAPI
import com.duongnd.pocketposapp.data.remote.api.ProductAPI
import com.duongnd.pocketposapp.data.remote.api.StatisticsAPI
import com.duongnd.pocketposapp.data.remote.api.StoreAPI
import com.duongnd.pocketposapp.data.remote.interceptor.AuthInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl() = "https://natural-overuse-antelope.ngrok-free.dev/"

//    @Provides
//    fun provideBaseUrl() = "https://pocketpos-epmd.onrender.com/api/"


    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideProductAPI(retrofit: Retrofit): ProductAPI {
        return retrofit.create(ProductAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryAPI(retrofit: Retrofit): CategoryAPI {
        return retrofit.create(CategoryAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthAPI(retrofit: Retrofit): AuthAPI {
        return retrofit.create(AuthAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideStoreAPI(retrofit: Retrofit): StoreAPI {
        return retrofit.create(StoreAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderAPI(retrofit: Retrofit): OrderAPI {
        return retrofit.create(OrderAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideStatisticAPI(retrofit: Retrofit): StatisticsAPI {
        return retrofit.create(StatisticsAPI::class.java)
    }

}
