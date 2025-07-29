package com.example.land_parcel.Adapters

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.land_parcel.databinding.SurveyorHistoryCardBinding
import com.example.land_parcel.model.survey.SurveyData
import java.io.File


import java.util.Locale

class SurveyorHistoryAdapter(private val context: Context, val surveyHistoryList: ArrayList<SurveyData>, val filterResultsListener: FilterResultsListener) :
    RecyclerView.Adapter<SurveyorHistoryAdapter.SiteViewHolder>(),Filterable {

        val fullList=ArrayList<SurveyData>(surveyHistoryList)

    inner class SiteViewHolder(val binding:SurveyorHistoryCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(surveyorHistory: SurveyData) {
            binding.ownerName.text = surveyorHistory.Owner
            binding.plotNo.text = surveyorHistory.Khasra_No
            binding.updateDate.text=surveyorHistory.SurveyDate
            val surveyorHistory = fullList[position]

            val fileUrl = surveyorHistory.filePath  // S3 URL
            val fileName = "survey_${position}.pdf"
            val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

            binding.llDocFile.setOnClickListener{
                if (filePath.exists()) {
                    openPdf(context, filePath)  // Open if already downloaded
                } else {
                    downloadFile(context, fileUrl, fileName)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {
        val binding=SurveyorHistoryCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SiteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return surveyHistoryList.size
    }

    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) {
        holder.bind(surveyHistoryList[position])


    }

    override fun getFilter(): Filter {
        return Searched_Filter
    }

    private val Searched_Filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList:ArrayList<SurveyData> = ArrayList()
            if (constraint.isEmpty()) {
                filteredList.addAll(fullList)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in fullList) {
                    //have to check
                    if (item.Khasra_No.lowercase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }


        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            surveyHistoryList.clear()
            surveyHistoryList.addAll(results.values as ArrayList<SurveyData>)
            filterResultsListener.onFilterComplete(surveyHistoryList.size)
            notifyDataSetChanged()
        }
    }

    interface FilterResultsListener {
        fun onFilterComplete(count: Int)
    }


    private fun downloadFile(context: Context, fileUrl: String, fileName: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(fileUrl))
                .setTitle("Downloading $fileName")
                .setDescription("Please wait while the file is being downloaded...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(context, "Downloading started...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun openPdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Open PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}