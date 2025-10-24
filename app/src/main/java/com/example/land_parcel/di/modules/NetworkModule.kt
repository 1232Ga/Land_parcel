package com.example.land_parcel.di.modules

import com.example.land_parcel.Utils.Constants.BASEURL
import com.example.land_parcel.Utils.Constants.BASEURLUSER
import com.example.land_parcel.Utils.Constants.BASEURLVISULIZE
import com.example.land_parcel.di.qualifiers.LandQualifiers
import com.example.land_parcel.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @LandQualifiers.BaseRetrofit
    @Provides
    @Singleton
    fun providesRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
    @LandQualifiers.VisualizationRetrofit
    @Provides
    @Singleton
    fun providesRetrofitVizulization():Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASEURLVISULIZE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @LandQualifiers.UserRetrofit
    @Provides
    @Singleton
    fun providesRetrofitUser():Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASEURLUSER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @LandQualifiers.BaseRetrofit
    @Provides
    @Singleton
    fun providesApiService(@LandQualifiers.BaseRetrofit retrofit: Retrofit):ApiService{
        return retrofit.create(ApiService::class.java)
    }

    @LandQualifiers.VisualizationRetrofit
    @Provides
    @Singleton
    fun providesVizulizationApiService(@LandQualifiers.VisualizationRetrofit retrofit: Retrofit):ApiService{
        return retrofit.create(ApiService::class.java)
    }

    @LandQualifiers.UserRetrofit
    @Provides
    @Singleton
    fun providesUserApiService(@LandQualifiers.UserRetrofit retrofit: Retrofit):ApiService{
        return retrofit.create(ApiService::class.java)
    }
}