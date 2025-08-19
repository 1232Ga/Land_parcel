package com.example.land_parcel.di.qualifiers

import javax.inject.Qualifier

object LandQualifiers {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class BaseRetrofit

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class VisualizationRetrofit

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class UserRetrofit


    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class ReportRetrofit
}