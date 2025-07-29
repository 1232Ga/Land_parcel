package com.example.land_parcel.View.Fragment

import SyncAdapter
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.example.land_parcel.AWSBucket.S3Uploader
import com.example.land_parcel.R
import com.example.land_parcel.Utils.BaseFragment
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.PrefManager
import com.example.land_parcel.databinding.DialogSyncProgressBinding
import com.example.land_parcel.databinding.DialogSyncSurveyBinding
import com.example.land_parcel.databinding.FragmentSyncBinding
import com.example.land_parcel.di.modules.RetrofitClient
import com.example.land_parcel.model.survey.SurveyData
import com.example.land_parcel.viewmodel.SyncViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SyncFragment : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentSyncBinding
    lateinit var adapter: SyncAdapter
    lateinit var imageUris: MutableList<Uri>
    private val viewModel: SyncViewModel by viewModels()
    var syncingDialog:Dialog?=null
    @Inject
    lateinit var prefManager: PrefManager
    @Inject
    lateinit var networkUtils: NetworkUtils
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSyncBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.syncRecycler.layoutManager = LinearLayoutManager(requireActivity())
        getSurveyData()
        getview()
    }
    private fun getSurveyData() {
        viewModel.surveyData.observe(requireActivity()) {
            adapter = SyncAdapter(
                requireActivity(), it as MutableList<SurveyData>,
                onSyncItemClick = { syncItem,position ->
                    syncDialog(syncItem,position)
                },
                onImageClick = { position ->
                    imageUris = it[position].photos as MutableList<Uri>
                    // showImageDialog(0)
                    showPDFDialog(it[position])
                }
            )

            binding.syncRecycler.adapter = adapter
            if (it.isEmpty()) {
                binding.mainLayout.visibility = View.GONE
                binding.noDataLayout.visibility = View.VISIBLE
            } else {
                binding.mainLayout.visibility = View.VISIBLE
                binding.noDataLayout.visibility = View.GONE
            }

        }
    }
    private fun syncDialog(syncItem: SurveyData, position: Int) {
        val syncDialogBinding: DialogSyncSurveyBinding =
            DialogSyncSurveyBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity())
        dialog.setContentView(syncDialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        syncDialogBinding.btnSave.setOnClickListener {
            syncItem(syncItem,position)
            dialog.dismiss()
        }
        syncDialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun syncItem(synItem: SurveyData, position: Int) {
        val progressBarDialogBinding: DialogSyncProgressBinding =
            DialogSyncProgressBinding.inflate(layoutInflater)
        syncingDialog = Dialog(requireActivity())
        syncingDialog?.setContentView(progressBarDialogBinding.root)
        syncingDialog?.setCancelable(false)
        syncingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        syncingDialog?.show()
        if (networkUtils.isNetworkConnectionAvailable()) {
            syncPdfToS3(synItem,position)
        }else{
            syncingDialog?.dismiss()
            Toast.makeText(requireActivity(), "No Internet", Toast.LENGTH_SHORT).show()

        }
    }
    private fun syncPdfToS3(synItem: SurveyData, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val unsyncedPdfs = synItem.filePath
            if (unsyncedPdfs.isNotEmpty()) {
                    val file = File(synItem.filePath)
                    if (file.exists() && S3Uploader.isInternetAvailable(requireActivity())) {
                        S3Uploader.uploadPdf(requireActivity(), file) { success, s3Url ->
                            if (success && s3Url != null) {
                                runOnUiThread {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (networkUtils.isNetworkConnectionAvailable()) {
                                            synApi(s3Url,synItem,position)
                                        }else{
                                            syncingDialog?.dismiss()
                                            Toast.makeText(requireActivity(), "No Internet", Toast.LENGTH_SHORT).show()

                                        }

                                    }
                                }
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(requireActivity(), "No Internet or File Not Found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
             else {
                runOnUiThread {
                    Toast.makeText(requireActivity(), "No Unsynced PDFs Found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private suspend fun synApi(s3Url: String, synItem: SurveyData, position: Int) {
        val xmlRequest = createWfsUpdateXml(synItem,s3Url)
        val requestBody = RequestBody.create("application/xml".toMediaTypeOrNull(), xmlRequest)
        val fullUrl = "https://geoserver.bluehawk.ai:8443/geoserver/${synItem.Village_Id}/wfs?service=WFS&version=1.1.0&request=Transaction&typeName=${synItem.Village_Id}:Changes"
        val call: Call<ResponseBody> = RetrofitClient.instance.sendWfsTransaction(fullUrl, requestBody)
        try {
            val response: Response<ResponseBody> = call.execute()
            syncingDialog?.dismiss()
            if (response.isSuccessful) {
                if(response.code()==200){
                   withContext(Dispatchers.Main){
                       adapter.removeItem(position)
                       showToast("Data sync successfully !")
                   }
                    viewModel.deleteSurveyDataByPlotId(synItem.Khasra_No)
                    viewModel.getSurveyData()
                }
                else
                {
                    showToast(response.body().toString())
                }
            }
            else
            {
                showToast(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }
    private fun getview() {
        binding.backArrow.setOnClickListener(this)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
    }
    private fun showPDFDialog(surveyData: SurveyData) {
        CoroutineScope(Dispatchers.IO).launch {
            val pdfEntity = surveyData
            withContext(Dispatchers.Main) {
                if (pdfEntity == null || !File(pdfEntity.filePath).exists()) {
                    Toast.makeText(requireActivity(), "PDF file not found", Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                val uri = FileProvider.getUriForFile(requireActivity(),
                    "${requireActivity().applicationContext.packageName}.provider",
                    File(pdfEntity.filePath)
                )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/pdf")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(Intent.createChooser(intent, "Open PDF"))
            }
        }
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.back_arrow->{    findNavController().navigateUp()}

        }
    }
    fun createWfsUpdateXml(synItem: SurveyData, s3Url: String): String {
        val synItemFeatureId = synItem.featureid
        val updatedFeatureId = synItemFeatureId.replace("Polygon", "Changes")

        return """
        <wfs:Transaction service="WFS" version="1.1.0"
          xmlns:wfs="http://www.opengis.net/wfs"
          xmlns:gml="http://www.opengis.net/gml"
          xmlns:ogc="http://www.opengis.net/ogc"
          xmlns:${synItem.VillName}="http://www.opengis.net/gml">
        
          <wfs:Update typeName="${synItem.Village_Id}:Changes">
            <wfs:Property>
              <wfs:Name>LandType</wfs:Name>
              <wfs:Value>${synItem.LandType}</wfs:Value>
            </wfs:Property>
            
            <wfs:Property>
              <wfs:Name>VillName</wfs:Name>
              <wfs:Value>${synItem.VillName}</wfs:Value>
            </wfs:Property>
            
             <wfs:Property>
              <wfs:Name>DistName</wfs:Name>
              <wfs:Value>${synItem.DistName}</wfs:Value>
            </wfs:Property> 
            
            <wfs:Property>
              <wfs:Name>Block</wfs:Name>
              <wfs:Value>${synItem.Block}</wfs:Value>
            </wfs:Property>
            
             <wfs:Property>
              <wfs:Name>Tehsil</wfs:Name>
              <wfs:Value>${synItem.Tehsil}</wfs:Value>
            </wfs:Property>
            
             <wfs:Property>
              <wfs:Name>Khasra_No</wfs:Name>
              <wfs:Value>${synItem.Khasra_No}</wfs:Value>
            </wfs:Property>
            
            <wfs:Property>
              <wfs:Name>HouseNo</wfs:Name>
              <wfs:Value>${synItem.HouseNo}</wfs:Value>
            </wfs:Property>
            
            <wfs:Property>
              <wfs:Name>Owner</wfs:Name>
              <wfs:Value>${synItem.Owner}</wfs:Value>
            </wfs:Property> 
            
            
            
            <wfs:Property>
              <wfs:Name>Land_Use</wfs:Name>
              <wfs:Value>${synItem.Land_Use}</wfs:Value>
            </wfs:Property>
            
            <wfs:Property>
              <wfs:Name>MobileNo</wfs:Name>
              <wfs:Value>${synItem.MobileNo}</wfs:Value>
            </wfs:Property>
            
           <wfs:Property>
             <wfs:Name>GovtID</wfs:Name>
              <wfs:Value>${synItem.GovtID}</wfs:Value>
            </wfs:Property>
            
         
            <wfs:Property>
              <wfs:Name>Area</wfs:Name>
              <wfs:Value>${synItem.Area}</wfs:Value>
            </wfs:Property>
            
            
            <wfs:Property>
              <wfs:Name>Latitude</wfs:Name>
              <wfs:Value>${synItem.Latitude}</wfs:Value>
            </wfs:Property>
            
            
            <wfs:Property>
              <wfs:Name>Longitude</wfs:Name>
              <wfs:Value>${synItem.Longitude}</wfs:Value>
            </wfs:Property>
            
            
            <wfs:Property>
              <wfs:Name>PNIL_No</wfs:Name>
              <wfs:Value>${synItem.PNIL_No}</wfs:Value>
            </wfs:Property>
           
            <wfs:Property>
              <wfs:Name>date</wfs:Name>
              <wfs:Value>${synItem.SurveyDate}</wfs:Value>
            </wfs:Property>
            
             <wfs:Property>
              <wfs:Name>user_id</wfs:Name>
              <wfs:Value>${prefManager.getUserId()}</wfs:Value>
            </wfs:Property>
            
            <wfs:Property>
              <wfs:Name>document</wfs:Name>
              <wfs:Value>$s3Url</wfs:Value>
            </wfs:Property>
            
            <wfs:Property>
              <wfs:Name>remark</wfs:Name>
              <wfs:Value>${synItem.Remark}
              </wfs:Value>
            </wfs:Property>
            
         
   
           <ogc:Filter>
              <ogc:FeatureId fid="${updatedFeatureId}"/>
            </ogc:Filter>
          </wfs:Update>
        </wfs:Transaction>
    """.trimIndent()
    }
}