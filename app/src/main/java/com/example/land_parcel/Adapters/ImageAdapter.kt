import android.content.Context
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.land_parcel.R

class ImageAdapter(
    private val context: Context,
    private val imageList: MutableList<Uri>,
    private val onImageClick: (Int) -> Unit
) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var visibleItemCount = 4

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val overlayText: TextView = itemView.findViewById(R.id.overlayText)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onImageClick(position)
        }

        if (position < visibleItemCount - 1) {
            // Show normal images
            holder.imageView.visibility = View.VISIBLE
            holder.overlayText.visibility = View.GONE
            Glide.with(context).load(imageList[position]).into(holder.imageView)
        } else {
            // Last visible item should show "+count" overlay
            holder.imageView.visibility = View.VISIBLE
            Glide.with(context).load(imageList[position]).into(holder.imageView)

            val remainingCount = imageList.size - visibleItemCount
            if (remainingCount > 0) {
                holder.overlayText.text = "+$remainingCount"
                holder.overlayText.visibility = View.VISIBLE
            } else {
                holder.overlayText.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return if (imageList.size > visibleItemCount) visibleItemCount else imageList.size
    }

    fun removeItem(position: Int) {
        imageList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, imageList.size)
    }

/*
    fun calculateVisibleItems(context: Context, itemWidth: Int): Int {
        // Get the screen width in pixels
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        // Calculate the number of items that can fit
        val itemsThatFit = screenWidth / itemWidth

        // Return the minimum of calculated items or maxVisibleItems (5)
        return itemsThatFit
    }
*/
}

