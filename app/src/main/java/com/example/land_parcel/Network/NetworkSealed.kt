package com.example.land_parcel.network

sealed class NetworkSealed<T>(val data:T?=null,val message:String?=null) {
    class Loading<T>():NetworkSealed<T>()
    class Data<T>(data: T?):NetworkSealed<T>(data)
    class Error<T>(data: T?,message: String?):NetworkSealed<T>(data,message)
}