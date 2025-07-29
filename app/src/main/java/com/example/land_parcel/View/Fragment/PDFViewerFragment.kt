package com.example.land_parcel.View.Fragment

import ImageAdapter
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.example.land_parcel.AWSBucket.S3Uploader
import com.example.land_parcel.R
import com.example.land_parcel.databinding.FragmentPDFViewerBinding
import com.example.offlinemapshow.RoomDB.PdfDatabase
import com.example.offlinemapshow.RoomDB.PdfEntity
import com.mapbox.mapboxsdk.Mapbox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class PDFViewerFragment : Fragment() {
    private lateinit var bindingAnalytics: FragmentPDFViewerBinding
    private val bindings get() = bindingAnalytics

    private val imageUris = ArrayList<Uri>()
    private val REQUEST_CAMERA = 100
    private val REQUEST_GALLERY = 101
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var pdfFile: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(requireActivity(),getString(R.string.mapbox_access_token))
        bindingAnalytics = FragmentPDFViewerBinding.inflate(inflater, container, false)
        val view = bindings.root
        getview()
        return view
    }

    private fun getview() {
        bindings.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = ImageAdapter(requireActivity(), imageUris){}
        bindings.recyclerView.adapter = imageAdapter

        bindings.btnCamera.setOnClickListener { openCamera() }
        bindings.btnGallery.setOnClickListener { openGallery() }
        bindings.btnConvertToPdf.setOnClickListener {
            convertImagesToPdf()
            imageUris.clear()
        }
        bindings.btnViewPdf.setOnClickListener { viewPdf() }
        bindings.btnSyncPdf.setOnClickListener { syncPdfToS3() }
        bindings.btnViewPdffromS3.setOnClickListener { downloadPdf("SaveImagesOffline.pdf") }
    }
    private var photoUri: Uri? = null
    private fun openCamera() {
        val photoFile = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "IMG_${System.currentTimeMillis()}.jpg")

        // 🔹 Use FileProvider to get the URI
        photoUri = FileProvider.getUriForFile(
            requireActivity(), "${requireActivity().applicationContext.packageName}.provider", photoFile
        )

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                photoUri?.let {
                    imageUris.add(it)
                    imageAdapter.notifyDataSetChanged()
                }
            } else if (requestCode == REQUEST_GALLERY && data?.data != null) {
                imageUris.add(data.data!!)
                imageAdapter.notifyDataSetChanged()
            }
        }
//        if (resultCode == Activity.RESULT_OK) {
//            val uri: Uri? = data?.data
//            if (requestCode == REQUEST_CAMERA && data?.extras != null) {
//                val bitmap = data.extras?.get("data") as Bitmap
//                val imageUri = saveImageToCache(bitmap)
//                imageUris.add(imageUri)
//            } else if (requestCode == REQUEST_GALLERY && uri != null) {
//                imageUris.add(uri)
//            }
//            imageAdapter.notifyDataSetChanged()
//        }
    }

    //Convert Image into PDF File
    private fun convertImagesToPdf() {
        val document = PdfDocument()
        for ((index, uri) in imageUris.withIndex()) {
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
            val page = document.startPage(pageInfo)
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            document.finishPage(page)
        }

        // Save PDF file
        pdfFile = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "SaveImagesOffline.pdf")
        document.writeTo(FileOutputStream(pdfFile))
        document.close()
        Toast.makeText(requireActivity(), "PDF Saved: ${pdfFile.absolutePath}", Toast.LENGTH_SHORT).show()
        // Save PDF details in Room Database
        savePdfToDatabase(pdfFile)
    }
    //Save PDF into RoomDatabse
    private fun savePdfToDatabase(pdfFile: File) {
        val db = PdfDatabase.getDatabase(requireActivity())

        val pdfEntity = PdfEntity(
            filePath = pdfFile.absolutePath,
            fileName = pdfFile.name,
            isSynced = false // Initially not synced with S3
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.pdfDao().insert(pdfEntity)
        }
    }
    //For View PDF File
    private fun viewPdf() {
        val db = PdfDatabase.getDatabase(requireActivity())

        CoroutineScope(Dispatchers.IO).launch {
            val pdfEntity = db.pdfDao().getPdfByPath(pdfFile.absolutePath)

            withContext(Dispatchers.Main) {
                if (pdfEntity == null || !File(pdfEntity.filePath).exists()) {
                    Toast.makeText(requireActivity(), "PDF file not found", Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                val uri = FileProvider.getUriForFile(
                    requireActivity(),
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
    //sync pdf to s3
    private fun syncPdfToS3() {
        val db = PdfDatabase.getDatabase(requireActivity())

        CoroutineScope(Dispatchers.IO).launch {
            val unsyncedPdfs = db.pdfDao().getUnsyncedPdfs()

            if (unsyncedPdfs.isNotEmpty()) {
                for (pdf in unsyncedPdfs) {
                    val file = File(pdf.filePath)
                    if (file.exists() && S3Uploader.isInternetAvailable(requireActivity())) {
                        S3Uploader.uploadPdf(requireActivity(), file) { success, s3Url ->
                            if (success && s3Url != null) {
                                pdf.isSynced = true
                                pdf.s3Url = s3Url // Save S3 URL in Room Database
                                CoroutineScope(Dispatchers.IO).launch {
                                    db.pdfDao().update(pdf)
                                }
                                runOnUiThread {
                                    Toast.makeText(requireActivity(), "PDF Synced: $s3Url", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(requireActivity(), "No Internet or File Not Found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(requireActivity(), "No Unsynced PDFs Found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Download PDF from S3 & Save to Local Storage
    private fun downloadPdf(fileName: String) {
        val url = S3Uploader.getPresignedUrl(fileName)
        println("vggfhffgfghf"+url.toString())

        if (url.isNullOrEmpty()) {
            Toast.makeText(requireActivity(), "Failed to get S3 URL", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfFile = File(requireActivity().getExternalFilesDir(null), fileName)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val urlConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    pdfFile.outputStream().use { output ->
                        urlConnection.inputStream.use { input ->
                            input.copyTo(output)
                        }
                    }
                    runOnUiThread {
                        Toast.makeText(requireActivity(), "Download Complete", Toast.LENGTH_SHORT).show()
                        openPdf(pdfFile) // Open PDF after download
                    }
                } else {
                    Log.e("Download", "Failed: ${urlConnection.responseMessage}")
                }
            } catch (e: Exception) {
                Log.e("Download", "Error: ${e.message}")
            }
        }
    }

    //Open PDF After Download
    private fun openPdf(file: File) {
        val uri = FileProvider.getUriForFile(
            requireActivity(),
            "${requireActivity().packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireActivity(), "No PDF Viewer Installed", Toast.LENGTH_SHORT).show()
        }
    }

}