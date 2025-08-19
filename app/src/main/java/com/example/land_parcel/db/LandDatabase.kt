package com.example.land_parcel.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.land_parcel.Interfaces.VillageDao
import com.example.land_parcel.db.dao.SurveyDataDao
import com.example.land_parcel.db.dao.VillageJsonDao
import com.example.land_parcel.db.typeconvertors.PhotoUriConvertor
import com.example.land_parcel.model.Pnil.PnilDao
import com.example.land_parcel.model.VillageModel.Village
import com.example.land_parcel.model.survey.SurveyData
import com.example.land_parcel.model.villageGeoJson.VillageGeoJson

@Database(entities = [SurveyData::class, Village::class,VillageGeoJson::class], version = 5 )
@TypeConverters(PhotoUriConvertor::class)
//@TypeConverters(PhotoByteConvertor::class)
abstract class LandDatabase : RoomDatabase() {
    abstract fun getSurveyDataDao(): SurveyDataDao
    abstract fun getVillageDataDao():VillageDao
    abstract fun getVillageJsonDao():VillageJsonDao
}