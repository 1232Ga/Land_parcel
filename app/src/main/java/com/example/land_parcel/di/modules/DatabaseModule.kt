package com.example.land_parcel.di.modules

import android.content.Context
import androidx.room.Room
import com.example.land_parcel.db.LandDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesDb(@ApplicationContext context: Context): LandDatabase {
        return Room.databaseBuilder(context, LandDatabase::class.java,"LandDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }
}