package com.example.land_parcel.View.Fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.room.RoomDatabase
import com.bumptech.glide.Glide
import com.example.land_parcel.PDFReport.Interface.RetrofitClientReport
import com.example.land_parcel.PDFReport.Model.LandSurveyRequest
import com.example.land_parcel.R
import com.example.land_parcel.Utils.BaseFragment
import com.example.land_parcel.Utils.NetworkConnectivityCallback
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.PrefManager
import com.example.land_parcel.databinding.DialogConfirmationBinding
import com.example.land_parcel.databinding.DialogExitBinding
import com.example.land_parcel.databinding.DialogSyncReportBinding
import com.example.land_parcel.databinding.FragmentDashboardBinding
import com.example.land_parcel.db.LandDatabase
import com.example.land_parcel.model.VillageModel.VillageItem
import com.example.land_parcel.network.NetworkSealed
import com.example.land_parcel.viewmodel.DashboardViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.geojson.Feature
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.RasterLayer
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.RasterSource
import com.mapbox.mapboxsdk.style.sources.TileSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : BaseFragment(), OnMapReadyCallback, View.OnClickListener, NetworkConnectivityCallback {
    private lateinit var bindingAnalytics: FragmentDashboardBinding
    private val bindings get() = bindingAnalytics
    private val viewmodel:DashboardViewModel by viewModels()
    @Inject
    lateinit var prefManager: PrefManager
    @Inject
    lateinit var networkUtils: NetworkUtils
    @Inject
    lateinit var landDatabase: LandDatabase
    private var syncingDialog: Dialog? = null
    private var progressDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(requireActivity(), getString(R.string.mapbox_access_token))
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bindingAnalytics = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = bindings.root
        return view
    }
    private fun getview() {
        requireActivity().registerReceiver(this.mConnReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        bindings.mapView.getMapAsync(this)
        viewmodel.locationEngine = LocationEngineProvider.getBestLocationEngine(requireActivity())
        bindings.btnToggleMap.setOnClickListener(this)
        bindings.btnToggleTile.setOnClickListener(this)
        bindings.btnTogglelayer.setOnClickListener(this)
        bindings.btnGetLocation.setOnClickListener(this)
        bindings.toolbar.profileView.setOnClickListener(this)
        bindings.btnMapType.setOnClickListener(this)
        bindings.btnLayer.setOnClickListener(this)
        bindings.closeLayer.setOnClickListener(this)
        bindings.closeMapType.setOnClickListener(this)
        bindings.parcelLayer.setOnClickListener(this)
        bindings.geoJsonLayer.setOnClickListener(this)
        bindings.plotIdLayer.setOnClickListener(this)
        bindings.surveyedPlotLayer.setOnClickListener(this)
        bindings.remainingPlotLayer.setOnClickListener(this)
        bindings.terrainBtn.setOnClickListener(this)
        bindings.satelliteBtn.setOnClickListener(this)
        bindings.streetBtn.setOnClickListener(this)
        bindings.btnMapType.setOnClickListener(this)
        bindings.legendBtn.setOnClickListener(this)
        bindings.toolbar.syncView.setOnClickListener(this)
        bindings.currentLocBtn.setOnClickListener(this)
        bindings.btnOrthoLoc.setOnClickListener(this)
        bindings.streetRel.background = ContextCompat.getDrawable(requireContext(), R.drawable.map_view_bg)
        bindings.satelliteRel.background = null
        bindings.terrainRel.background = null
        viewmodel.villageJsonDao = landDatabase.getVillageJsonDao()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exit()
                }
            })
    }
    private fun exit() {
        val syncCheckDialogBinding: DialogExitBinding = DialogExitBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity())
        dialog.setContentView(syncCheckDialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        syncCheckDialogBinding.btnSave.setOnClickListener {
            requireActivity().finish()
            dialog.dismiss()
        }
        syncCheckDialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
    companion object {
        var WMS_SOURCE_URL = ""
        var Village_Id = ""
        var BBOX_LAYER = ""
        var WMTS_SOURCE_POLYGON_URL = ""
        var WMTS_SOURCE_CHANGES_URL = ""
        const val WMS_SOURCE_ID = "web-map-source"
        const val RASTER_LAYER_ID = "web-map-layer"
        const val BELOW_LAYER_ID = "tunnel-simple"
        const val TILESET_JSON = "tileset"
        private const val PERMISSION_REQUEST_CODE = 1001
    }
    private fun showShapefile(village: String) {
        Village_Id = village
        WMS_SOURCE_URL = "https://geoserver.bluehawk.ai:8443/geoserver/${village}/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image/png&TRANSPARENT=true&STYLES&LAYERS=${village}:Orthophoto&exceptions=application/vnd.ogc.se_inimage&SRS=EPSG:900913&WIDTH=684&HEIGHT=768&bbox={bbox-epsg-3857}"
        WMTS_SOURCE_POLYGON_URL = "https://geoserver.bluehawk.ai:8443/geoserver/${village}/ows?" +
                "service=WFS&version=1.0.0&request=GetFeature&typeName=${village}:Polygon" +
                "&srsName=EPSG:4326&outputFormat=application/json"
        WMTS_SOURCE_CHANGES_URL ="https://geoserver.bluehawk.ai:8443/geoserver/${village}/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=${village}:Changes&srsName=EPSG:4326&outputFormat=application/json&cql_filter=strLength(user_id)>0"
        BBOX_LAYER = "https://geoserver.bluehawk.ai:8045/land-survey/${village}/layers"

    }
    @SuppressLint("SuspiciousIndentation")
    override fun onMapReady(mapboxMap: MapboxMap) {
        if (viewmodel.maMap != null) {
            val marker = viewmodel.maMap!!.markers
            viewmodel.maMap = mapboxMap
            if (marker.isNotEmpty()) {
                val markerOptions = MarkerOptions().position(LatLng(marker.first().position.latitude, marker.first().position.longitude))
                    viewmodel.maMap?.addMarker(markerOptions)
            }
        }
        else {
            viewmodel.maMap = mapboxMap

        }
        mapboxMap.addOnCameraIdleListener {
            viewmodel.mapState = mapboxMap.cameraPosition
        }
        if (viewmodel.mapState != null) {
            mapboxMap.cameraPosition = viewmodel.mapState!!
        }
        else {
            bindings.mapView.getMapAsync { mapboxMap ->
                val indiaBounds = LatLngBounds.Builder()
                    .include(LatLng(35.5087, 68.1113)) // North-West
                    .include(LatLng(6.4627, 97.3954)) // South-East
                    .build()
                mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(indiaBounds, 5))
            }
        }
        setObservers()
        setMapStyleAndWMS(viewmodel.maMap!!, mapStyles[viewmodel.currentStyleIndex])
    }
    override fun onResume() {
        super.onResume()
        getview()
    }
    private fun setMapStyleAndWMS(mapboxMap: MapboxMap, styleUrl: String) {
        setMapType(viewmodel.currentStyleIndex)
        mapboxMap.setStyle(styleUrl) { style ->
            val rasterSource =
                RasterSource(WMS_SOURCE_ID, TileSet(TILESET_JSON, WMS_SOURCE_URL), 256)
            if (style.getSource(WMS_SOURCE_ID) == null) {
                style.addSource(rasterSource)
            }
            val rasterLayer = RasterLayer(RASTER_LAYER_ID, WMS_SOURCE_ID)
            if (style.getLayer(BELOW_LAYER_ID) != null) {
                style.addLayerBelow(rasterLayer, BELOW_LAYER_ID)
            } else {
                style.addLayer(rasterLayer)
            }

            lifecycleScope.launch {
                viewmodel.getSurveyData()
            }
            CoroutineScope(Dispatchers.Main).launch {

                if (style.isFullyLoaded) {
                    addGeoJsonLayer(style)
                }

            }
            updateTileVisibility(style)
        }
    }
    private fun updateTileVisibility(style: Style?) {
        style?.let {
            val rasterLayer = it.getLayer(RASTER_LAYER_ID)
            rasterLayer?.setProperties(
                PropertyFactory.visibility(
                    if (viewmodel.isTileVisible) Property.VISIBLE
                    else Property.NONE
                )
            )
        }
    }
    private suspend fun addGeoJsonLayer(style: Style) {
        viewmodel.setGeoJson(style, requireActivity())
        if (style.isFullyLoaded) {
            val existingLayer = style.getLayer("polygon-layer")
            if (existingLayer == null) {
                val fillLayer = FillLayer("polygon-layer", "polygon-source").withProperties(
                    PropertyFactory.fillColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.unsurvey_color_new
                        )
                    ),
                    PropertyFactory.fillOpacity(0.5f)
                )
                style.addLayer(fillLayer)
            }
            val outlineLayer = LineLayer("polygon-outline-layer", "polygon-source").withProperties(
                PropertyFactory.lineColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.primary_color_line
                    )
                ), // Outline color
                PropertyFactory.lineWidth(1.5f), // Bold outline (Increase if needed)
                PropertyFactory.lineOpacity(1f) // Fully visible outline
            )
            // Add the outline above the fill layer
            style.addLayerAbove(outlineLayer, "polygon-layer")
            // Add Symbol Layer for Khasra_No labels

            val textColor = if (isDarkModeEnabled()) R.color.white else R.color.primary_color_line
            val symbolLayer = SymbolLayer("polygon-label-layer", "polygon-source").withProperties(
                PropertyFactory.textField(Expression.get("Khasra_No")),
                PropertyFactory.textSize(14f),
                PropertyFactory.textColor(ContextCompat.getColor(requireActivity(), textColor)),
                PropertyFactory.textHaloColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.white
                    )
                ), // Outline for readability
                PropertyFactory.textHaloWidth(1f),
                PropertyFactory.textJustify(Property.TEXT_JUSTIFY_AUTO),
                PropertyFactory.textAnchor(Property.TEXT_ANCHOR_CENTER),
                PropertyFactory.textAllowOverlap(true),
            )
            style.addLayer(symbolLayer)
            viewmodel.maMap?.let { gsonClickListener(it) }
        }
    }
    private fun isDarkModeEnabled(): Boolean {
        return (requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
    private fun setObservers() {
        try {
            viewmodel.villageResponse.observe(viewLifecycleOwner) {
                when (it) {
                    is NetworkSealed.Loading -> {
                        //binding.progressCircular.progressCircular.visibility = View.VISIBLE
                    }

                    is NetworkSealed.Data -> {
                        var villageList = it.data?.map { village ->
                            VillageItem(village.VillageId, village.VillageName) } ?: emptyList()
                        villageList = villageList.asReversed()
                        if (villageList.isEmpty()) {
                            villageList = listOf(VillageItem("VillageId", "No village assigned"))
                        }
                        resetLayerVisibility()
                        CoroutineScope(Dispatchers.IO).launch {
                            if (networkUtils.isNetworkConnectionAvailable() && !viewmodel.isVillageDataLoaded) {
                                for (i in 0..villageList.lastIndex) {
                                    val villageIdWithoutHyphens = villageList[i].villageId.replace("-", "")
                                    val villname = villageList[i].villageName.replace(" ", "")
                                    val tempVillageId = "${villname}$villageIdWithoutHyphens"
                                    showShapefile(tempVillageId)
                                    viewmodel.fetchChangesJson(WMTS_SOURCE_CHANGES_URL, tempVillageId, requireActivity())
                                    viewmodel.fetchGeoJson(WMTS_SOURCE_POLYGON_URL, tempVillageId, requireActivity())
                                    viewmodel.fetchBBOXJson(BBOX_LAYER, tempVillageId)
                                }
                                viewmodel.isVillageDataLoaded = true
                            }
                            withContext(Dispatchers.Main) {
                                val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(requireActivity(), R.layout.spinneritemback, villageList)
                                adapter.setDropDownViewResource(R.layout.spinnershowitemdropdown)
                                bindings.villageSpin.setAdapter(adapter)
                                viewmodel.selectedVillage?.let { selected ->
                                    val position = villageList.indexOfFirst { it.villageId == selected.villageId }
                                    if (position != -1) {
                                        bindings.villageSpin.setSelection(position)
                                        val selectedVillage = villageList[position]
                                        viewmodel.selectedVillage = selectedVillage
                                        val villageIdWithoutHyphens = selectedVillage.villageId.replace("-", "")
                                        bindings.parcelLayer.text = selectedVillage.villageName
                                        val villname = selectedVillage.villageName.replace(" ", "")
                                        resetLayerVisibility()
                                        showShapefile("${villname}$villageIdWithoutHyphens")
                                    }
                                }
                                bindings.villageSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                        if (position >= 0 && position < villageList.size) {
                                            val selectedVillage = villageList[position]
                                            viewmodel.selectedVillage = selectedVillage
                                            val villageIdWithoutHyphens = selectedVillage.villageId.replace("-", "")
                                            bindings.parcelLayer.text = selectedVillage.villageName
                                            val villname = selectedVillage.villageName.replace(" ", "")
                                            resetLayerVisibility()
                                            showShapefile("${villname}$villageIdWithoutHyphens")
                                            viewmodel.maMap?.let { it1 -> setMapStyleAndWMS(it1, mapStyles[viewmodel.currentStyleIndex])
                                            }
                                        }
                                    }
                                    override fun onNothingSelected(parent: AdapterView<*>) {}
                                }
                            }
                        }
                    }

                    is NetworkSealed.Error -> {
                        showToast(it.message)
                    }
                }
            }
            viewmodel.surveyData.observe(this) { surveyDataList ->
                val style = viewmodel.maMap?.style
                lifecycleScope.launch {

                    val storedGeoJson = withContext(Dispatchers.IO) {
                        viewmodel.loadJsonFromFile(
                            viewmodel.villageJsonDao.getVillageGeoJsonById(
                                Village_Id
                            )?.villageJson
                        ) ?: ""
                    }

                    //   val storedChangesGeoJson = villageJsonDao.getVillageGeoJsonById(Village_Id)?.changesJson?:""

                    // Start building the switchCase expression dynamically
                    val conditions = mutableListOf<Expression>()

                    for (item in surveyDataList) {
                        if (storedGeoJson.contains(item.Khasra_No)) {
                            conditions.add(
                                Expression.eq(
                                    Expression.get("Khasra_No"),
                                    Expression.literal(item.Khasra_No.toIntOrNull() ?: item.Khasra_No)
                                )
                            )
                            conditions.add(
                                Expression.color(
                                    ContextCompat.getColor(requireActivity(), R.color.survey_color)
                                )
                            )
                        }
                    }

                    val blockColorExpression = if (conditions.isNotEmpty()) {
                        Expression.switchCase(
                            *conditions.toTypedArray(),
                            Expression.color(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.unsurvey_color_new
                                )
                            ) // Default color
                        )
                    } else {
                        Expression.color(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.unsurvey_color_new
                            )
                        )
                    }
                    val blockChangesColorExpression = Expression.color(ContextCompat.getColor(requireActivity(), R.color.green))


                    if (style?.isFullyLoaded == true) {
                        val existingLayer = style.getLayer("polygon-layer") as? FillLayer
                        val existingChangesLayer = style.getLayer("changes-layer") as? FillLayer
                        if (existingLayer != null) {
                            existingLayer.setProperties(PropertyFactory.fillColor(blockColorExpression))

                        } else {
                            val fillLayer = FillLayer("polygon-layer", "polygon-source").withProperties(
                                PropertyFactory.fillColor(blockColorExpression),
                                PropertyFactory.fillOpacity(0.5f)
                            )
                            style.addLayer(fillLayer)
                        }
                        if (existingChangesLayer != null) {
                            existingChangesLayer.setProperties(
                                PropertyFactory.fillColor(
                                    blockChangesColorExpression
                                )
                            )
                        } else {
                            val fillLayer = FillLayer("changes-layer", "changes-source").withProperties(
                                PropertyFactory.fillColor(blockChangesColorExpression),
                                PropertyFactory.fillOpacity(0.5f)
                            )

                            //  style.addLayer(fillLayer)
                            val symbolLayer = style.getLayer("polygon-label-layer")
                            if (symbolLayer != null) {
                                style.addLayerBelow(fillLayer, symbolLayer.id)

                            } else {
                                style.addLayer(fillLayer)
                            }

                        }
                    }
                }
            }
        }
        catch (e:Exception){
          Log.d("Dropdown_Dash",e.message.toString())
        }


    }
    private fun gsonClickListener(mapboxMap: MapboxMap) {
        mapboxMap.addOnMapClickListener { point ->
            val screenPoint = mapboxMap.projection.toScreenLocation(point)
            // First, check for features in "changes-layer"
            val changesLayerFeatures = mapboxMap.queryRenderedFeatures(screenPoint, "changes-layer")
            if (changesLayerFeatures.isNotEmpty()) {
                handleFeatureClick(changesLayerFeatures[0], "Changes_layer")
                return@addOnMapClickListener true // Stop further processing
            }
            // If no features in "changes-layer", check "polygon-layer"
            val polygonLayerFeatures = mapboxMap.queryRenderedFeatures(screenPoint, "polygon-layer")
            if (polygonLayerFeatures.isNotEmpty()) {
                handleFeatureClick(polygonLayerFeatures[0], "Polygon_layer")
                return@addOnMapClickListener true
            }

            false // No feature was clicked
        }
    }
    private fun handleFeatureClick(feature: Feature, s: String) {
        val properties = feature.properties()
        val featureId = feature.id() ?: "Unknown"
        val geometryJson = feature.geometry()?.toJson()
        if (geometryJson.isNullOrEmpty()) {
            println("Error: Geometry is null or empty.")
            return
        }

        try {
            val encodedPoint = findPointOnSurface(geometryJson)
            println("Encoded point: $encodedPoint")

            val bundle = Bundle().apply {
                putString("owner_name", properties?.get("Owner").getAsSafeString())
                putString("district_name", properties?.get("DistName").getAsSafeString())
                putInt("house_no", properties?.get("HouseNo").getAsSafeInt(0))
                putString("block", properties?.get("Block").getAsSafeString("Unknown"))
                putString("land_type", properties?.get("LandType").getAsSafeString())
                putString("tehsil", properties?.get("Tehsil").getAsSafeString())
                putString("utility", properties?.get("Land_Use").getAsSafeString())
                putString("mobile_no", properties?.get("MobileNo").getAsSafeString())
                putString("village_name", properties?.get("VillName").getAsSafeString())
                val villageIdWithoutHyphens = viewmodel.selectedVillage?.villageId?.replace("-", "")
                bindings.parcelLayer.text = viewmodel.selectedVillage?.villageName
                val villname = viewmodel.selectedVillage?.villageName?.replace(" ", "")
                putString("village_id", "${villname}$villageIdWithoutHyphens")
                putInt("unique_code", properties?.get("Khasra_No").getAsSafeInt(-1))
                putString("featureid", featureId)
                putString("PNIL_No", encodedPoint)
                putString("remark", properties?.get("remark").getAsSafeString())
                putString("govt_id", properties?.get("GovtID").getAsSafeString())
                putString("area", properties?.get("Area").getAsSafeString())
                putString("latitude", properties?.get("Latitude").getAsSafeString())
                putString("longitude", properties?.get("Longitude").getAsSafeString())
            }

            updateDialog(
                bundle, properties?.get("Block").getAsSafeString("Unknown"),
                properties?.get("Khasra_No").getAsSafeInt(-1),
                properties?.get("VillName").getAsSafeString(), s, feature
            )

        } catch (e: IllegalArgumentException) {
            println("Error: ${e.message}")
        }
    }
    private fun JsonElement?.getAsSafeString(defaultValue: String = ""): String {
        return if (this != null && !this.isJsonNull) this.asString else defaultValue
    }
    private fun JsonElement?.getAsSafeInt(defaultValue: Int = -1): Int {
        return if (this != null && !this.isJsonNull) this.asInt else defaultValue
    }
    private fun updateDialog(bundle: Bundle, blockName: String, khasraNo: Int, ownerName: String, layers_type: String, feature: Feature) {
        val updateSurveyBinding: DialogConfirmationBinding =
            DialogConfirmationBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity())
        dialog.setContentView(updateSurveyBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = dialog.window?.attributes
        layoutParams?.width =
            (requireContext().resources.displayMetrics.widthPixels * 0.8).toInt() // 90% of screen width
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
        dialog.show()
        updateSurveyBinding.bloackName.text = "Block: $blockName"
        updateSurveyBinding.plotNoNew.text = khasraNo.toString()
        updateSurveyBinding.ownersName.text = ownerName
        if (layers_type.equals("Polygon_layer")) {
            updateSurveyBinding.genrateReport.visibility = View.VISIBLE
            updateSurveyBinding.genrateReport.setOnClickListener {
                dialog.dismiss()
                downloadreportDialog(feature)
            }
        } else if (layers_type.equals("Changes_layer")) {
            updateSurveyBinding.genrateReport.visibility = View.VISIBLE
            updateSurveyBinding.genrateReport.setOnClickListener {
                dialog.dismiss()
                downloadreportDialog(feature)
            }
        }

        updateSurveyBinding.viewBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_dashboard_Fragment_to_updateFormFragment,
                bundle
            )
            dialog.dismiss()
        }
        updateSurveyBinding.closeBtn.setOnClickListener {
            dialog.dismiss()
        }

    }
    private fun downloadreportDialog(feature: Feature) {
        val syncDialogBinding: DialogSyncReportBinding =
            DialogSyncReportBinding.inflate(layoutInflater)
        syncingDialog = Dialog(requireActivity())
        syncingDialog?.setContentView(syncDialogBinding.root)
        syncingDialog?.setCancelable(false)
        syncingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        syncingDialog?.show()
        syncDialogBinding.btnSave.setOnClickListener {
            progressDialog = ProgressDialog(requireActivity())
            (progressDialog as ProgressDialog).setMessage("Downloading...")
            progressDialog?.setCancelable(false)
            progressDialog?.show()
            if (networkUtils.isNetworkConnectionAvailable()) {
                generateReport(feature)
            } else {
                showToast("No Internet..")
                progressDialog?.dismiss()
            }

            syncingDialog?.dismiss()
        }
        syncDialogBinding.btnCancel.setOnClickListener {
            syncingDialog?.dismiss()
        }
    }
    private fun generateReport(feature: Feature) {
        val properties = feature.properties()
        val geometryJson = feature.geometry()?.toJson()
        if (geometryJson.isNullOrEmpty()) {
            println("Error: Geometry is null or empty.")
            return
        }
        val encodedPoint = findPointOnSurface(geometryJson)

        val request = LandSurveyRequest(
            district_name = properties?.get("DistName").getAsSafeString(),
            tehsil = properties?.get("Tehsil").getAsSafeString(),
            village_name = properties?.get("VillName").getAsSafeString(),
            village_id = viewmodel.selectedVillage?.villageId.toString(),
            unique_id = properties?.get("Khasra_No").getAsSafeString(),
            house_no = properties?.get("HouseNo").getAsSafeString(),
            owner_name = properties?.get("Owner").getAsSafeString(),
            land_type = properties?.get("LandType").getAsSafeString(),
            mobile_no = properties?.get("MobileNo").getAsSafeString(),
            govt_id = properties?.get("GovtID").getAsSafeString(),
            block = properties?.get("Block").getAsSafeString("Unknown"),
            land_use = properties?.get("Land_Use").getAsSafeString(),
            area = properties?.get("Area").getAsSafeString(),
            latitude = properties?.get("Latitude").getAsSafeString(),
            longitude = properties?.get("Longitude").getAsSafeString(),
            pnil_no = encodedPoint,
            date = properties?.get("date").getAsSafeString(),
            user_id = prefManager.getUserId().toString(),
            document = properties?.get("document").getAsSafeString(),
            remark = properties?.get("remark").getAsSafeString()
        )
        lifecycleScope.launch {
            val gson = Gson()
            val json = gson.toJson(request)
            Log.d("Request_JSON", json.trimIndent())
            val response = RetrofitClientReport.instance.sendSurveyReport(request)
            if (response.code() == 200) {
                if (response.isSuccessful) {
                    progressDialog?.dismiss()
                    val result = response.body()
                    //Toast.makeText(requireActivity(),result?.message,Toast.LENGTH_SHORT).show()
                    val fileUrl = result!!.report_link  // S3 URL
                    val timestamp = System.currentTimeMillis()  // Or use server timestamp if available
                    val fileName = "survey_${properties?.get("Khasra_No").getAsSafeString()}_$timestamp.pdf"
                    downloadFile(requireContext(), fileUrl, fileName)
                } else {
                    progressDialog?.dismiss()
                    showToast(response.errorBody().toString())
                }
            }

            else {
                progressDialog?.dismiss()
                showToast(response.errorBody().toString())
            }

        }
    }
    private var downloadId: Long = 0
    private var progressDialogPercentage: ProgressDialog? = null
    private fun downloadFile(context: Context, fileUrl: String?, fileName: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(fileUrl))
                .setTitle("$fileName")
                .setDescription("Please wait while the file is being downloaded...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadId = downloadManager.enqueue(request)
            progressDialogPercentage = ProgressDialog(context)
            progressDialogPercentage?.setTitle("Downloading...")
            progressDialogPercentage?.setMessage("0% completed")
            progressDialogPercentage?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialogPercentage?.max = 100
            progressDialogPercentage?.isIndeterminate = false
            progressDialogPercentage?.setCancelable(false)
            progressDialogPercentage?.show()
            trackDownloadProgress(context, downloadManager, downloadId)
        } catch (e: Exception) {
            progressDialogPercentage?.dismiss()
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun trackDownloadProgress(context: Context, downloadManager: DownloadManager, downloadId: Long) {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)
                if (cursor != null && cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    val downloadedBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val totalBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                        cursor.close()
                        progressDialogPercentage?.dismiss()
                        Toast.makeText(context, "Download complete!", Toast.LENGTH_SHORT).show()
                        return
                    }

                    if (totalBytes > 0) {
                        val progress = (downloadedBytes * 100L / totalBytes).toInt()
                        progressDialogPercentage?.progress = progress
                        progressDialogPercentage?.setMessage("$progress% completed")
                    }
                    cursor.close()
                }
                handler.postDelayed(this, 500)
            }
        })
    }
    private fun togglePlotId(style: Style) {
        style.let {
            val symbolLayer = it.getLayer("polygon-label-layer")
            symbolLayer?.setProperties(
                PropertyFactory.visibility(
                    if (viewmodel.isPlotIdVisible) Property.VISIBLE
                    else Property.NONE
                )
            )
        }
    }
    private fun toggleBoundariesVisibility(style: Style) {
        style.let {
            val lineLayer = it.getLayer("polygon-outline-layer")
            lineLayer?.setProperties(
                PropertyFactory.visibility(
                    if (viewmodel.isBoundariesVisible) Property.VISIBLE
                    else Property.NONE
                )
            )
        }
    }
    private fun toggleGeoJsonVisibility(style: Style) {
        style.let {
            val rasterLayer = it.getLayer("polygon-layer")
            val lineLayer = it.getLayer("polygon-outline-layer")
            val symbolLayer = it.getLayer("polygon-label-layer")
            val changesLayer = it.getLayer("changes-layer")

            if (viewmodel.isLayerVisible) {
                viewmodel.isBoundariesVisible = true
                viewmodel.isPlotIdVisible = true
                bindings.plotIdLayer.isChecked = true
                bindings.surveyedPlotLayer.isChecked = true
                bindings.remainingPlotLayer.isChecked = true
            } else {
                viewmodel.isBoundariesVisible = false
                viewmodel.isPlotIdVisible = false
                bindings.plotIdLayer.isChecked = false
                bindings.surveyedPlotLayer.isChecked = false
                bindings.remainingPlotLayer.isChecked = false

            }
            rasterLayer?.setProperties(
                PropertyFactory.visibility(
                    if (viewmodel.isLayerVisible) Property.VISIBLE
                    else Property.NONE
                )
            )
            lineLayer?.setProperties(
                PropertyFactory.visibility(
                    if (viewmodel.isLayerVisible) {
                        Property.VISIBLE

                    } else Property.NONE
                )
            )
            changesLayer?.setProperties(
                PropertyFactory.visibility(
                    if (viewmodel.isLayerVisible) {
                        Property.VISIBLE

                    } else Property.NONE
                )
            )
            symbolLayer?.setProperties(
                PropertyFactory.visibility(
                    if (viewmodel.isLayerVisible) Property.VISIBLE
                    else Property.NONE
                )
            )
        }
    }
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        } else {
            fetchCurrentLocation()
        }
    }
    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        viewmodel.locationEngine.getLastLocation(object :
            LocationEngineCallback<LocationEngineResult> {
            override fun onSuccess(result: LocationEngineResult?) {
                result?.lastLocation?.let { location ->
                    updateMapLocation(location)
                } ?: run {
                    Toast.makeText(requireActivity(), "Location not available", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(exception: Exception) {
                Toast.makeText(requireActivity(), "Failed to get location", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
    private fun updateMapLocation(location: Location) {
        bindings.mapView.getMapAsync { mapboxMap ->
            val latLng = LatLng(location.latitude, location.longitude)
            mapboxMap.cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(15.0)
                .build()
            addMarker(location.latitude, location.longitude)
        }
    }
    private fun addMarker(lat: Double, lng: Double) {
        val markerOptions = MarkerOptions()
            .position(LatLng(lat, lng))
        viewmodel.maMap?.addMarker(markerOptions)
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation()
            } else {
                Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setMapType(type: Int) {
        if (!networkUtils.isNetworkConnectionAvailable()) {
            val message = when (type) {
                0 -> "Street is not available offline"
                1 -> "Satellite is not available offline"
                else -> "Terrain is not available offline"
            }
            showToast(message)
            return
        }

        resetLayerVisibility()

        val (iconRes, selectedRel, selectedText) = when (type) {
            0 -> Triple(R.drawable.ic_street, bindings.streetRel, bindings.imgStreetText)
            1 -> Triple(R.drawable.ic_satellite, bindings.satelliteRel, bindings.imgSatelliteText)
            else -> Triple(R.drawable.ic_terrain, bindings.terrainRel, bindings.imgTerrainText)
        }

        Glide.with(requireActivity()).load(iconRes).into(bindings.btnMapType)

        bindings.streetRel.background = null
        bindings.satelliteRel.background = null
        bindings.terrainRel.background = null
        selectedRel.background = ContextCompat.getDrawable(requireContext(), R.drawable.map_view_bg)

        listOf(bindings.imgStreetText, bindings.imgSatelliteText, bindings.imgTerrainText).forEach {
            it.setTextColor(ContextCompat.getColor(requireActivity(), R.color.layer_text_color))
        }
        selectedText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.primary_color))
    }
    private fun resetLayerVisibility() {
        viewmodel.isTileVisible = true// Toggle state
        bindings.parcelLayer.isChecked = true
        updateTileVisibility(viewmodel.maMap?.style)
        viewmodel.isBoundariesVisible = true
        bindings.surveyedPlotLayer.isChecked = true
        viewmodel.maMap?.style?.let { toggleBoundariesVisibility(it) }
        viewmodel.isPlotIdVisible = true
        bindings.plotIdLayer.isChecked = true
        viewmodel.maMap?.style?.let { togglePlotId(it) }
    }
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.profile_view -> {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_dashboard_Fragment_to_profile_Fragment)
            }

            R.id.btnGetLocation -> checkLocationPermission()

            R.id.geo_json_layer -> {
                // isLayerVisible = !isLayerVisible // Toggle state
                // toggleGeoJsonVisibility(maMap.style!!) // Show or hide tile
            }

            R.id.parcel_layer -> {
                viewmodel.isTileVisible = !viewmodel.isTileVisible // Toggle state
                updateTileVisibility(viewmodel.maMap?.style) // Show or hide tile
                viewmodel.isLayerVisible = !viewmodel.isLayerVisible // Toggle state
                toggleGeoJsonVisibility(viewmodel.maMap?.style!!) // Show or hide tile
            }

            R.id.current_loc_btn -> {
                fetchCurrentLocation()
            }

            R.id.btnToggleMap -> {
                viewmodel.currentStyleIndex = (viewmodel.currentStyleIndex + 1) % mapStyles.size
                viewmodel.maMap?.let {
                    setMapStyleAndWMS(
                        it,
                        mapStyles[viewmodel.currentStyleIndex]
                    )
                }
            }

            R.id.btn_layer -> {
                if (bindings.layerCard.isVisible) {
                    bindings.layerCard.visibility = View.GONE
                } else {
                    bindings.layerCard.visibility = View.VISIBLE

                }
            }

            R.id.btn_map_type -> {
                if (bindings.mapViewCard.isVisible) {
                    bindings.mapViewCard.visibility = View.GONE
                } else {
                    bindings.mapViewCard.visibility = View.VISIBLE
                }
            }

            R.id.close_layer -> {
                bindings.layerCard.visibility = View.GONE

            }

            R.id.plot_id_layer -> {
                viewmodel.isPlotIdVisible = !viewmodel.isPlotIdVisible
                togglePlotId(viewmodel.maMap?.style!!)

            }

            R.id.surveyed_plot_layer -> {
                viewmodel.isBoundariesVisible = !viewmodel.isBoundariesVisible
                toggleBoundariesVisibility(viewmodel.maMap?.style!!)

            }

            R.id.close_map_type -> {
                bindings.mapViewCard.visibility = View.GONE

            }

            R.id.terrain_btn -> {
                viewmodel.currentStyleIndex = 3
                viewmodel.maMap?.let { setMapStyleAndWMS(it, mapStyles[viewmodel.currentStyleIndex]) }
                setMapType(3)

            }

            R.id.satellite_btn -> {
                viewmodel.currentStyleIndex = 1
                viewmodel.maMap?.let { setMapStyleAndWMS(it, mapStyles[viewmodel.currentStyleIndex]) }
                setMapType(1)

            }

            R.id.street_btn -> {
                viewmodel.currentStyleIndex = 0
                viewmodel.maMap?.let { setMapStyleAndWMS(it, mapStyles[viewmodel.currentStyleIndex]) }
                setMapType(0)

            }

            R.id.sync_view -> {
                findNavController().navigate(R.id.action_dashboard_Fragment_to_syncFragment)
            }

            R.id.legend_btn -> {
                if (bindings.legendImg.isVisible) {
                    bindings.legendImg.visibility = View.GONE

                } else {
                    bindings.legendImg.visibility = View.VISIBLE
                }
            }

            R.id.btn_ortho_loc -> {
                if (viewmodel.lati == null || viewmodel.longi == null ||
                    viewmodel.lati!!.isNaN() || viewmodel.longi!!.isNaN() ||
                    viewmodel.lati == 0.0 || viewmodel.longi == 0.0
                ) {
                    Toast.makeText(requireContext(), "Shapefile not found", Toast.LENGTH_SHORT).show()
                } else {
                    fetchCurrentLocationotho(viewmodel.lati!!, viewmodel.longi!!)
                }


            }

        }
    }
    private fun fetchCurrentLocationotho(lati: Double, longi: Double) {
        bindings.mapView.getMapAsync { mapboxMap ->
            val latLng = LatLng(lati, longi)
            mapboxMap.cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(15.0)
                .build()
        }
    }
    override fun onInternetConnected() {
        viewmodel.maMap?.let { setMapStyleAndWMS(it, mapStyles[viewmodel.currentStyleIndex]) }

    }
    override fun onInternetDisconnected() {
       // showToast("Internet disconnected")
    }
    
}