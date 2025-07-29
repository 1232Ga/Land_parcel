package com.example.land_parcel.di.modules

import android.content.Context
import com.example.land_parcel.Utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class InternetModule {
    @Provides
    @Singleton
    fun isInternetAvailable(@ApplicationContext context: Context): NetworkUtils {
        return NetworkUtils(context)
    }
}