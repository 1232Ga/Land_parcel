package com.example.land_parcel.di.modules

import android.content.Context
import com.example.land_parcel.Utils.PrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class PrefModule {

    @Provides
    fun providesPrefManager(@ApplicationContext context: Context):PrefManager{
        return PrefManager(context)
    }
}