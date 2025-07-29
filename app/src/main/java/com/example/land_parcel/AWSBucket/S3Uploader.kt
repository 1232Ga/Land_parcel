package com.example.land_parcel.AWSBucket


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.amazonaws.HttpMethod
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import java.io.File
import java.util.Date

object S3Uploader {
    private const val ACCESS_KEY = "AKIAY74WNG3KQBN5T6OI"
    private const val SECRET_KEY = "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"
    private const val BUCKET_NAME = "bluehawk-uat"
    private const val S3_BUCKET_URL = "https://bluehawk-uat.s3.ap-south-1.amazonaws.com/"
    private val REGION = Regions.AP_SOUTH_1

    fun uploadPdf(context: Context, pdfFile: File, onUploadComplete: (Boolean, String?) -> Unit) {
        val credentials: AWSCredentials = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
        val s3Client = AmazonS3Client(credentials, Region.getRegion(REGION))
        val transferUtility = TransferUtility.builder()
            .context(context)
            .s3Client(s3Client)
            .build()

        val s3Key = "pdfs/${pdfFile.name}" // Folder structure in S3

        val uploadObserver = transferUtility.upload(
            BUCKET_NAME,
            s3Key,
            pdfFile
        )

        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED) {
                    val uploadedUrl = "$S3_BUCKET_URL$s3Key"
                    Log.d("S3Upload", "Upload Successful: $uploadedUrl")
                    onUploadComplete(true, uploadedUrl)
                } else if (state == TransferState.FAILED) {
                    Log.d("S3Upload", "Upload Failed")
                    onUploadComplete(false, null)
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val progress = (bytesCurrent.toDouble() / bytesTotal * 100.0).toInt()
                Log.d("S3Upload", "Upload Progress: $progress%")
            }

            override fun onError(id: Int, ex: Exception?) {
                Log.e("S3Upload", "Error: ${ex?.message}")
                onUploadComplete(false, null)
            }
        })
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }


    fun getPresignedUrl(fileName: String): String? {
        return try {
            val credentials = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
            val s3Client = AmazonS3Client(credentials, Region.getRegion(REGION))

            val expiration = Date()
            val expTimeMillis = expiration.time + 1000 * 60 * 10 // URL valid for 10 minutes
            expiration.time = expTimeMillis

            val generatePresignedUrlRequest = GeneratePresignedUrlRequest(BUCKET_NAME, "pdfs/$fileName")
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration)

            val url = s3Client.generatePresignedUrl(generatePresignedUrlRequest)
            url.toString()
        } catch (e: Exception) {
            Log.e("S3Uploader", "Error generating pre-signed URL: ${e.message}")
            null
        }
    }

}



