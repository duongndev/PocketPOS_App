package com.duongnd.pocketposapp.core.di

import android.content.Context
import com.duongnd.pocketposapp.core.utils.ShareReferenceManager
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideShareReferenceManager(
        @ApplicationContext context: Context,
        moshi: Moshi
    ): ShareReferenceManager {
        return ShareReferenceManager(context, moshi)
    }
}
