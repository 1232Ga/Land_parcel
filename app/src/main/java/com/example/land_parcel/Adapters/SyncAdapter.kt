import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.land_parcel.R
import com.example.land_parcel.model.survey.SurveyData
import java.util.Locale

class SyncAdapter(
    private val context: Context,
    private val imageList: MutableList<SurveyData>   ,
 private val onImageClick: (Int) -> Unit,
 private val onEditClick: (SurveyData,Int) -> Unit,
 private val onSyncItemClick: (SurveyData,Int) -> Unit
) :
    RecyclerView.Adapter<SyncAdapter.ImageViewHolder>() {


    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val img_count: TextView = itemView.findViewById(R.id.img_count)
        val imageView:LinearLayout=itemView.findViewById(R.id.view_lay)
        val syncData:LinearLayout=itemView.findViewById(R.id.sncdata)
        val editdata:ImageView=itemView.findViewById(R.id.editdata)
        val serial_number:TextView=itemView.findViewById(R.id.serial_number)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sync_item, parent, false)


        // Get the count of visible items

        return ImageViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setOnClickListener {
            onImageClick(position)
        }
        holder.syncData.setOnClickListener{
            onSyncItemClick(imageList[position],position)
        }
        holder.editdata.setOnClickListener{
            onEditClick(imageList[position],position)
        }
        val formattedDate = formatSurveyDate(imageList[position].SurveyDate)
        holder.date.text = formattedDate
        holder.img_count.text=imageList[position].Khasra_No
        holder.serial_number.text = "${position + 1}."



        //   Glide.with(context).load(imageList[position]).into(holder.imageView)

    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun removeItem(position: Int) {
        imageList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, imageList.size)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun formatSurveyDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())  // 24-hour format
        val outputFormat = SimpleDateFormat("dd-MM-yyyy/hh:mm a", Locale.getDefault())  // 12-hour format with AM/PM
        val date = inputFormat.parse(dateString) ?: return dateString  // Parse and handle null case
        return outputFormat.format(date)  // Convert to desired format
    }
}

