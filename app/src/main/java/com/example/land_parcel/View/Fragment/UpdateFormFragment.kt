package com.example.land_parcel.View.Fragment

import ImageAdapter
import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.land_parcel.R
import com.example.land_parcel.Utils.BaseFragment
import com.example.land_parcel.databinding.DialogCancelSurveyBinding
import com.example.land_parcel.databinding.DialogChoosMideaBinding
import com.example.land_parcel.databinding.DialogUpdateSurveyBinding
import com.example.land_parcel.databinding.FragmentUpdateFormBinding
import com.example.land_parcel.model.survey.SurveyData
import com.example.land_parcel.viewmodel.UpdateSurveyViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class UpdateFormFragment : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentUpdateFormBinding
    private val imageUris = ArrayList<Uri>()
    private lateinit var imageAdapter: ImageAdapter
    private var photoUri: Uri? = null
    private lateinit var pdfFile: File
    private var pdfPath: String = ""
    private var pdfName: String = ""
    private var plotId = ""
    private var Village_Id = ""
    private var featureid = ""
    private var PNIL_No = ""
    private var area = ""
    private var govt_id = ""
    private var latitude = ""
    private var longitude = ""
    private var selectedDocument = ""
    private lateinit var documentTypes: List<String>

    private val viewmodel: UpdateSurveyViewmodel by viewModels()
    private val REQUEST_CAMERA = 100
    private val REQUEST_GALLERY = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUpdateFormBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.photoRecycler.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        setValues()
        setTextWatchers()
        checkValidation()
        requestPermission()
        imageAdapter = ImageAdapter(requireActivity(), imageUris) { position ->
            showImageDialog(position)
        }
        binding.photoRecycler.adapter = imageAdapter
        binding.addImgCamera.setOnClickListener(this)
        binding.addImg.setOnClickListener(this)
        binding.addImgGallery.setOnClickListener(this)
        binding.backArrow.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    cancelDialog()
                }
            })

    }

    private fun showImageDialog(position: Int) {
        val dialog = GalleryViewerDialogFragment(imageUris, position) { deletedPosition ->
            // Handle UI update after deletion if needed
            imageUris.removeAt(deletedPosition)
            imageAdapter.notifyDataSetChanged()
            checkValidation()
            if (imageUris.isEmpty()) {
                binding.imageLay.visibility = View.VISIBLE
                binding.imageScroller.visibility = View.GONE
                binding.imagePreviewTxt.visibility = View.GONE
            }
        }

        dialog.isCancelable = false
        dialog.show(childFragmentManager, "ImageViewerDialog")

    }

    private fun setValues() {
        val bundle = arguments
        if (bundle != null) {
            binding.blockNameEt.setText(bundle.getString("block"))
            binding.districtName.text = bundle.getString("district_name")
            binding.exHouseNumEt.setText(bundle.getInt("house_no").toString())
            binding.landTypeEt.setText(bundle.getString("land_type"))
            binding.mobileNoEt.setText(bundle.getString("mobile_no"))
            binding.ownerNameEt.setText(bundle.getString("owner_name"))
            //  binding.parcelIdEt.setText(bundle.getString("parcel_id"))
            //  binding.previousOwnerEt.setText(bundle.getString("Pro__Owner"))
            binding.landUseEt.setText(bundle.getString("utility"))
            binding.tehsilName.text = bundle.getString("tehsil")
            binding.villageNameEt.text = bundle.getString("village_name")
            binding.plotIdEt.setText(bundle.getInt("unique_code").toString())
            Village_Id = bundle.getString("village_id").toString()
            featureid = bundle.getString("featureid").toString()
            PNIL_No = bundle.getString("PNIL_No").toString()
            latitude = bundle.getString("latitude").toString()
            longitude = bundle.getString("longitude").toString()
            area = bundle.getString("area").toString()
            govt_id = bundle.getString("govt_id").toString()
            documentTypes = listOf(getString(R.string.aadhar_card), getString(R.string.voter_id), getString(R.string.registry_copy), getString(R.string.land_naksha), getString(R.string.driving_licence), getString(R.string.other_document))
            val adapter = ArrayAdapter(requireContext(), R.layout.spinneritembacknew, documentTypes)
            adapter.setDropDownViewResource(R.layout.spinnershowitemdropdownnew)
            binding.documentTypeSpinner.adapter = adapter
            val index = documentTypes.indexOf(govt_id)
            if (index >= 0) { binding.documentTypeSpinner.setSelection(index) }
            binding.documentTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedDocument = documentTypes[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            binding.pnilIdEt.setText(PNIL_No)
            binding.remarkEt.setText(bundle.getString("remark").toString())
        }
    }

    private suspend fun saveUpdatedData() {
        binding.progress.visibility = View.VISIBLE
        val blockName = binding.blockNameEt.text.toString()
        val districtName = binding.districtName.text.toString()
        var exHouseName = binding.exHouseNumEt.text.toString()
        val landtype = binding.landTypeEt.text.toString()
        val mobileNum = binding.mobileNoEt.text.toString()
        val ownerName = binding.ownerNameEt.text.toString()
        // val parcelId = binding.parcelIdEt.text.toString()
        //  val previousOwner = binding.previousOwnerEt.text.toString()
        val utility = binding.landUseEt.text.toString()
        val tehsilName = binding.tehsilName.text.toString()
        val villageName = binding.villageNameEt.text.toString()
        val Remark = binding.remarkEt.text.toString()
        plotId = binding.plotIdEt.text.toString()
        convertImagesToPdf()
        if (exHouseName.isEmpty()) {
            exHouseName = "0"
        }
        val surveyData = SurveyData(blockName, districtName, exHouseName, landtype, mobileNum,
            ownerName,
            latitude,
            longitude,
            area,
            utility,
            tehsilName,
            villageName,
            Village_Id,
            featureid,
            getCurrentDateTime(),
            Remark,
            plotId,
            imageUris,
            pdfPath,
            pdfName,
            PNIL_No,
            selectedDocument
        )
        viewmodel.updateSurveyData(surveyData)
        binding.progress.visibility = View.GONE
    }

    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun openCamera() {
        val photoFile = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(requireActivity(), "${requireActivity().applicationContext.packageName}.provider", photoFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, REQUEST_CAMERA)
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }


    private suspend fun convertImagesToPdf() = withContext(Dispatchers.IO) {
        try {
            val document = PdfDocument()
            val contentResolver = requireActivity().contentResolver
            for ((index, uri) in imageUris.withIndex()) {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
                val page = document.startPage(pageInfo)
                page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                document.finishPage(page)
            }
            pdfFile = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "land_survey_${plotId}.pdf")
            FileOutputStream(pdfFile).use { outputStream -> document.writeTo(outputStream) }
            document.close()
            pdfPath = pdfFile.absolutePath
            pdfName = pdfFile.name
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkValidation() {
        if ((binding.mobileNoEt.text.toString().isNotEmpty() &&
                    binding.ownerNameEt.text.toString().isNotEmpty() &&
                    imageUris.isNotEmpty())
        ) {
            binding.saveBtn.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.primary_color
                )
            )
            binding.saveBtn.setOnClickListener {
                updateDialog()
            }
        } else {
            binding.saveBtn.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.not_clickable
                )
            )
            binding.saveBtn.setOnClickListener {
                showToast("Please fill all details.")
            }

        }


    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            checkValidation()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun setTextWatchers() {
        // binding.blockNameEt.addTextChangedListener(textWatcher)
        // binding.exHouseNumEt.addTextChangedListener(textWatcher)
        // binding.landTypeEt.addTextChangedListener(textWatcher)
        binding.mobileNoEt.addTextChangedListener(textWatcher)
        binding.ownerNameEt.addTextChangedListener(textWatcher)
        //    binding.parcelIdEt.addTextChangedListener(textWatcher)
        //   binding.previousOwnerEt.addTextChangedListener(textWatcher)
        // binding.previousUseEt.addTextChangedListener(textWatcher)
        //  binding.plotIdEt.addTextChangedListener(textWatcher)
        //   binding.remarkEt.addTextChangedListener(textWatcher)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                photoUri?.let {
                    imageUris.add(it)
                    binding.imageLay.visibility = View.GONE
                    binding.imagePreviewTxt.visibility = View.VISIBLE

                    binding.imageScroller.visibility = View.VISIBLE
                    imageAdapter.notifyDataSetChanged()
                    checkValidation()
                }
            } else if (requestCode == REQUEST_GALLERY && data?.data != null) {
                imageUris.add(data.data!!)
                binding.imageLay.visibility = View.GONE
                binding.imagePreviewTxt.visibility = View.VISIBLE

                binding.imageScroller.visibility = View.VISIBLE
                imageAdapter.notifyDataSetChanged()
                checkValidation()
            }
        }

    }

    fun showMediaDialog() {
        val dialogMediaBinding = DialogChoosMideaBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity())
        dialog.setContentView(dialogMediaBinding.root)
        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false)
        dialog.show()

        dialogMediaBinding.addImgCamera.setOnClickListener {
            if (hasPermissions(
                    requireActivity(), arrayOf(
                        Manifest.permission.CAMERA
                    )
                )
            ) {
                openCamera()
            } else {
                showToast("Permission not granted")
                /* requestPermission(arrayOf(
                     Manifest.permission.CAMERA),CAMERA_PERMISSION_REQUEST_CODE )*/
            }
            dialog.dismiss()
        }

        dialogMediaBinding.addImgGallery.setOnClickListener {
            openGallery()
            /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (hasPermissions(
                            requireActivity(), arrayOf(
                                Manifest.permission.ACCESS_MEDIA_LOCATION
                            )
                        )
                    ) {
                        openGallery()
                    } else {
                        showToast("Permission not granted")
                        *//* requestPermission(arrayOf(
                         Manifest.permission.CAMERA),CAMERA_PERMISSION_REQUEST_CODE )*//*
                }
            } else {
                openGallery()
            }*/
            dialog.dismiss()

        }
        dialogMediaBinding.closeDialog.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun updateDialog() {
        val updateSurveyBinding: DialogUpdateSurveyBinding =
            DialogUpdateSurveyBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity())
        dialog.setContentView(updateSurveyBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        updateSurveyBinding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                saveUpdatedData()
                showToast("Saved successfully")
                findNavController().navigateUp()
            }
            dialog.dismiss()
        }
        updateSurveyBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun cancelDialog() {
        val dialogCancelSurveyBinding: DialogCancelSurveyBinding =
            DialogCancelSurveyBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity())
        dialog.setContentView(dialogCancelSurveyBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        dialogCancelSurveyBinding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                findNavController().navigateUp()
            }
            dialog.dismiss()
        }
        dialogCancelSurveyBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.add_img_camera -> {
                if (hasPermissions(
                        requireActivity(), arrayOf(
                            Manifest.permission.CAMERA
                        )
                    )
                ) {
                    openCamera()
                } else {
                    showToast("Permission not granted")
                    /* requestPermission(arrayOf(
                         Manifest.permission.CAMERA),CAMERA_PERMISSION_REQUEST_CODE )*/
                }
            }

            R.id.add_img -> {
                showMediaDialog()
            }

            R.id.add_img_gallery -> {
                openGallery()
                /*      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                          if (hasPermissions(
                                  requireActivity(), arrayOf(
                                      Manifest.permission.ACCESS_MEDIA_LOCATION
                                  )
                              )
                          ) {
                              openGallery()
                          } else {
                              showToast("Permission not granted")
                              *//* requestPermission(arrayOf(
                             Manifest.permission.CAMERA),CAMERA_PERMISSION_REQUEST_CODE )*//*
                    }
                } else {
                    openGallery()
                }*/
            }

            R.id.back_arrow -> {
                findNavController().navigateUp()
            }

            R.id.cancel_btn -> {
                cancelDialog()
            }

        }
    }


}