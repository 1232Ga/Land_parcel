package com.example.land_parcel.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.land_parcel.PDFReport.ReportModel.Response.LandParcel
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.PrefManager
import com.example.land_parcel.View.Fragment.DashboardFragment.Companion.BBOX_LAYER
import com.example.land_parcel.View.Fragment.DashboardFragment.Companion.Village_Id
import com.example.land_parcel.View.Fragment.DashboardFragment.Companion.WMTS_SOURCE_CHANGES_URL
import com.example.land_parcel.View.Fragment.DashboardFragment.Companion.WMTS_SOURCE_POLYGON_URL
import com.example.land_parcel.db.dao.VillageJsonDao
import com.example.land_parcel.model.Pnil.PnilDao
import com.example.land_parcel.model.VillageModel.VillageItem
import com.example.land_parcel.model.data.GeoJson
import com.example.land_parcel.model.villageGeoJson.VillageGeoJson
import com.example.land_parcel.repositories.DashboardRepository
import com.example.land_parcel.repositories.SyncRepository
import com.google.gson.Gson
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val repository: DashboardRepository, prefManager: PrefManager, private var networkUtils: NetworkUtils):ViewModel(){

    val surveyData=repository.surveyData
    val villageResponse=repository.villageResponse

    private val _parcelReport = MutableLiveData<Result<LandParcel?>>()
    val parcelReport: LiveData<Result<LandParcel?>> = _parcelReport
    private var pollingJob: Job? = null

    var selectedVillage: VillageItem? = null
    var isVillageDataLoaded=false



     var currentStyleIndex = 0
     var isTileVisible = true
     var isLayerVisible = true
     var isPlotIdVisible = true
     var isBoundariesVisible = true
     var lati :Double?=0.0
     var longi: Double?=0.0

     lateinit var villageJsonDao: VillageJsonDao
      var maMap: MapboxMap?=null
     lateinit var locationEngine: LocationEngine
    var mapState: CameraPosition? = null
    init {
    viewModelScope.launch {
        getVillageResponse(prefManager.getToken()!!,prefManager.getUserId()!!)
    }
}
    suspend fun getSurveyData(){
        repository.getSurveyData()
    }

   suspend fun getVillageResponse(token:String,userID:String){
       repository.getvillage(token,userID)
   }

    private fun parseGeoJson(json: String): GeoJson {
        val gson = Gson()
        return gson.fromJson(json, GeoJson::class.java)
    }
    private fun getFirstCoordinate(geoJson: GeoJson): Pair<Double, Double>? {
        val firstFeature = geoJson.features.firstOrNull() ?: return null

        val coordinates = firstFeature.geometry.coordinates

        if (coordinates.isNotEmpty() && coordinates[0].isNotEmpty() && coordinates[0][0].isNotEmpty()) {
            val firstCoordinate = coordinates[0][0][0]
            val longitude = firstCoordinate[0]
            val latitude = firstCoordinate[1]
            return Pair(latitude, longitude)
        }
        return null
    }
    suspend fun setGeoJson(style: Style,context: Context) {
        try {    val geoJson = fetchGeoJson(WMTS_SOURCE_POLYGON_URL, Village_Id,context) // Fetch GeoJSON data

            val geoJsonData = parseGeoJson(geoJson.toString())
            val firstCoordinate = getFirstCoordinate(geoJsonData)
            lati = firstCoordinate?.first
            longi = firstCoordinate?.second
            val changesGeoJson=fetchChangesJson(WMTS_SOURCE_CHANGES_URL,Village_Id,context)
            val bBoxJson=extractVectorBBOX(fetchBBOXJson(BBOX_LAYER,Village_Id))

            val geoJsonSource = GeoJsonSource("polygon-source", geoJson)
            val changesGeoJsonSource = GeoJsonSource("changes-source", changesGeoJson)
            style.addSource(geoJsonSource)
            style.addSource(changesGeoJsonSource)

            bBoxJson?.let { CameraUpdateFactory.newLatLngBounds(it, 100) }
                ?.let { maMap?.easeCamera(it) } // 100 for padding

        } catch (e: Exception) {
            //   showToast(e.message)
        }



    }

    fun extractVectorBBOX(jsonString: String?): LatLngBounds? {
        jsonString?.let {
            try {
                val jsonArray = JSONArray(it)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    if (jsonObject.optString("type") == "VECTOR") {
                        val boundingBox = jsonObject.getJSONObject("bounding_box")
                        val minLng = boundingBox.getDouble("minx")
                        val minLat = boundingBox.getDouble("miny")
                        val maxLng = boundingBox.getDouble("maxx")
                        val maxLat = boundingBox.getDouble("maxy")

                        // Create LatLngBounds
                        return LatLngBounds.Builder()
                            .include(LatLng(minLat, minLng)) // Southwest
                            .include(LatLng(maxLat, maxLng)) // Northeast
                            .build()
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return null
    }
    suspend fun fetchBBOXJson(url: String,villageId:String): String? {
        return withContext(Dispatchers.IO) {
            try {
                if (networkUtils.isNetworkConnectionAvailable()) {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful && response.code == 200) {
                        val responseBody = response.body?.string() ?: return@withContext null // Read body once

                        val existingVillage = villageJsonDao.getVillageGeoJsonById(villageId)
                        if (existingVillage == null) {
                            val villageGeoJson = VillageGeoJson(villageId, "", "", responseBody)
                            villageJsonDao.addVillageGeoJson(villageGeoJson)
                        } else {
                            villageJsonDao.updateBBOXJson(villageId, responseBody)
                        }
                        return@withContext responseBody
                    } else {
                        val errorBody = response.body?.string() ?: "{}"
                        villageJsonDao.updateBBOXJson(villageId, errorBody)
                        return@withContext null
                    }
                } else {
                    return@withContext villageJsonDao.getVillageGeoJsonById(villageId)?.bBoxJson
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    suspend fun fetchChangesJson(url: String,villageId:String,context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {

                if (networkUtils.isNetworkConnectionAvailable()) {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    val result = response.body?.string() ?: "{\n" +
                    "  \"type\": \"FeatureCollection\",\n" +
                    "  \"features\": []\n" +
                    "}"
                    if(villageJsonDao.getVillageGeoJsonById(villageId)==null){
                        val villageGeoJson=VillageGeoJson(villageId,"",saveJsonToFile(context,result,villageId+"ChangesJson.json"),"")

                        villageJsonDao.addVillageGeoJson(villageGeoJson)

                    }else{
                        villageJsonDao.updateChangesJson(villageId,saveJsonToFile(context,result,villageId+"ChangesJson.json"))
                    }
                    result
                }
                else {
                    // prefManager.getGeoJson()
                    loadJsonFromFile(villageJsonDao.getVillageGeoJsonById(villageId)?.changesJson)?: "{\n" +
                    "  \"type\": \"FeatureCollection\",\n" +
                    "  \"features\": []\n" +
                    "}"
                }


            } catch (e: Exception) {
                e.printStackTrace()

                "{\n" +
                        "  \"type\": \"FeatureCollection\",\n" +
                        "  \"features\": []\n" +
                        "}"
            }
        }
    }

    suspend fun fetchGeoJson(url: String,villageId:String,context: Context): String {
        return withContext(Dispatchers.IO) {
            try {

                if (networkUtils.isNetworkConnectionAvailable()) {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    val result = response.body?.string() ?: "{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": []\n" + "}"
                    if(villageJsonDao.getVillageGeoJsonById(villageId)==null){
                        val villageGeoJson=
                            VillageGeoJson(villageId,saveJsonToFile(context,result,
                            villageId+"GeoJson.json"),"","")

                        villageJsonDao.addVillageGeoJson(villageGeoJson)

                    }else{
                        villageJsonDao.updateVillageJson(villageId,saveJsonToFile(context,result,villageId+"GeoJson.json"))
                    }
                    result
                } else {
                    loadJsonFromFile(villageJsonDao.getVillageGeoJsonById(villageId)?.villageJson)?: "{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": []\n" + "}"
                }


            } catch (e: Exception) {
                e.printStackTrace()
                "{\n" + "  \"type\": \"FeatureCollection\",\n" + "  \"features\": []\n" + "}"
            }
        }
    }

    private fun saveJsonToFile(context: Context, json: String, fileName: String): String {
        val file = File(context.filesDir, "$fileName.json")
        file.writeText(json, Charsets.UTF_8)
        return file.absolutePath
    }
    fun loadJsonFromFile(filePath: String?): String? {
        val file = filePath?.let { File(it) }
        return if (file?.exists() == true) file.readText(Charsets.UTF_8) else null
    }






//    fun fetchParcelReport(villageId: String?, khasraNumber: String,token: String) {
//        viewModelScope.launch {
//            val result = repository.getParcelReport(villageId, khasraNumber,token)
//            _parcelReport.value = result.mapCatching { apiResponse ->
//                apiResponse.Data?.JsonResultSet?.firstOrNull()
//            }
//        }
//    }

    fun fetchParcelReportPeriodically(villageId: String?, khasraNumber: String, token: String) {
        pollingJob?.cancel()

        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val result = repository.getParcelReport(villageId, khasraNumber, token)

                    // Post the first result to LiveData
                    val parcel = result.getOrNull()?.Data?.JsonResultSet?.firstOrNull()
                    _parcelReport.value = Result.success(parcel)
                    if (parcel?.Status == 2) {
                        break
                    }
                } catch (e: Exception) {
                    _parcelReport.value = Result.failure(e)
                    break // Stop polling on error (you can change this to continue if needed)
                }

                delay(5000L) // Wait 5 seconds
            }
        }
    }

}