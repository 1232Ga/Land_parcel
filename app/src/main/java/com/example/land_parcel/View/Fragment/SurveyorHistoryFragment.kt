package com.example.land_parcel.View.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.land_parcel.Adapters.SurveyorHistoryAdapter
import com.example.land_parcel.R
import com.example.land_parcel.Utils.BaseFragment
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.PrefManager
import com.example.land_parcel.databinding.FragmentSurveyorHistoryBinding
import com.example.land_parcel.model.VillageModel.VillageItem
import com.example.land_parcel.model.survey.SurveyData
import com.example.land_parcel.network.NetworkSealed
import com.example.land_parcel.viewmodel.DashboardViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject


@AndroidEntryPoint
class SurveyorHistoryFragment : BaseFragment(), View.OnClickListener,
    SurveyorHistoryAdapter.FilterResultsListener {

    lateinit var binding: FragmentSurveyorHistoryBinding
    lateinit var surveyorHistoryAdapter: SurveyorHistoryAdapter

    @Inject
    lateinit var networkUtils: NetworkUtils

    @Inject
    lateinit var prefManager:PrefManager

    private val viewmodel : DashboardViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSurveyorHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getview()
        setObserver()

    }

    fun setAdapter(village: String) {
          val  WMTS_SOURCE_CHANGES_URL = "http://geoserver.bluehawk.ai:8080/geoserver/${village}/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=${village}:Changes&srsName=EPSG:4326&outputFormat=application/json&CQL_FILTER=user_id='${prefManager.getUserId()}'"
     // val  WMTS_SOURCE_CHANGES_URL = "http://geoserver.bluehawk.ai:8080/geoserver/${village}/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=${village}:Changes&srsName=EPSG:4326&outputFormat=application/json&cql_filter=strLength(user_id)>0"

            if (networkUtils.isNetworkConnectionAvailable()) {
            CoroutineScope(Dispatchers.IO).launch {
                val arrayList = parseGeoJsonResponse(fetchSurveyorHistoryGeoJson(WMTS_SOURCE_CHANGES_URL))
                                withContext(Dispatchers.Main){
                    binding.surveyCount.text = "Total Survey (${arrayList.size})"

                    surveyorHistoryAdapter =
                        SurveyorHistoryAdapter(requireContext(),arrayList, this@SurveyorHistoryFragment)
                    binding.surveyHistoryRecycler.adapter = surveyorHistoryAdapter
                }
            }
        }
        else{
                showToast(getString(R.string.internet_not_available))
            }


    }

    private fun getview() {

        lifecycleScope.launch {
            viewmodel.getVillageResponse(prefManager.getToken()!!, prefManager.getUserId()!!)
        }
        binding.surveyHistoryRecycler.layoutManager =
            LinearLayoutManager(requireActivity())
        binding.backArrow.setOnClickListener(this)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
    }

    private fun setObserver() {

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                surveyorHistoryAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        viewmodel.villageResponse.observe(requireActivity()) {
            when (it) {
                is NetworkSealed.Loading -> {
                    //binding.progressCircular.progressCircular.visibility = View.VISIBLE
                }

                is NetworkSealed.Data -> {
                    val villageList = it.data?.map { village ->
                        VillageItem(
                            village.VillageId,
                            village.VillageName
                        )
                    } ?: emptyList()
                    val adapter: ArrayAdapter<*> =
                        ArrayAdapter<Any?>(requireActivity(), R.layout.spinneritemback_history, villageList)
                    //adapter.setDropDownViewResource(R.layout.spinnershowitemdropdown)
                    binding.villageSpin.setAdapter(adapter)

                    binding.villageSpin.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener { override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            var selectedVillage = villageList[position]
                            val villageIdWithoutHyphens = selectedVillage.villageId.replace("-", "")
                            setAdapter(selectedVillage.villageName.replace(" ","")+villageIdWithoutHyphens)
                        }
                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }

                }

                is NetworkSealed.Error -> {
                    showToast(it.message)
                }
            }
        }

    }

    fun parseGeoJsonResponse(jsonString: String): ArrayList<SurveyData> {
        val surveyList = mutableListOf<SurveyData>()

        try {
            val gson = Gson()
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            val features = jsonObject.getAsJsonArray("features")

            for (feature in features) {
                val featureObj = feature.asJsonObject
                val properties = featureObj.getAsJsonObject("properties")


                // Extract properties
                val surveyData = properties?.getAsSafeString("Khasra_No")?.takeIf { it.isNotEmpty() }?.let {
                    SurveyData(
                        Block = properties.getAsSafeString("Block"),
                        DistName = properties.getAsSafeString("DistName"),
                        HouseNo = properties.getAsSafeInt("HouseNo").toString(), // Ensuring numeric values are handled
                        LandType = properties.getAsSafeString("LandType"),
                        MobileNo = properties.getAsSafeString("MobileNo", "0"), // Default "0" if null or empty
                        Owner = properties.getAsSafeString("Owner"),
                        Latitude = properties.getAsSafeString("Latitude", "0.0"), // Ensuring default latitude
                        Longitude = properties.getAsSafeString("Longitude", "0.0"), // Ensuring default longitude
                        Area = properties.getAsSafeString("Area", "0"),
                        Land_Use = properties.getAsSafeString("Land_Use"),
                        Tehsil = properties.getAsSafeString("Tehsil"),
                        VillName = properties.getAsSafeString("VillName"),
                        Village_Id = properties.getAsSafeString("Village_Id"),
                        featureid = featureObj.getAsSafeString("id"),
                        SurveyDate = properties.getAsSafeString("date"),
                        Remark = properties.getAsSafeString("remark"),
                        Khasra_No = it,
                        photos = emptyList(),
                        filePath = properties.getAsSafeString("document"),
                        fileName = properties.getAsSafeString("fileName"),
                        PNIL_No = properties.getAsSafeString("PNIL_No"),
                        GovtID = properties.getAsSafeString("GovtID")
                    )
                }


                if (surveyData != null) {
                    surveyList.add(surveyData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return surveyList as ArrayList<SurveyData>
    }

    fun JsonObject?.getAsSafeString(key: String, defaultValue: String = ""): String {
        return try {
            this?.get(key)?.asString ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun JsonObject?.getAsSafeInt(key: String, defaultValue: Int = 0): Int {
        return try {
            this?.get(key)?.asInt ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    private suspend fun fetchSurveyorHistoryGeoJson(url: String): String {
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

                    result
                } else {
                    "{\n" +
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


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_arrow -> {
                findNavController().navigateUp()
            }

        }
    }

    override fun onFilterComplete(count: Int) {
        binding.surveyCount.text = "Total Survey ($count)"
    }


}