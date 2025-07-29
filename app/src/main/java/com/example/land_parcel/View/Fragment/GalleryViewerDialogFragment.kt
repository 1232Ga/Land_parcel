package com.example.land_parcel.View.Fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.transition.Visibility
import androidx.viewpager2.widget.ViewPager2
import com.example.land_parcel.Adapters.GalleryShowAdapter
import com.example.land_parcel.R

class GalleryViewerDialogFragment (
    private val imageList: MutableList<Uri>,
    private var startPosition: Int,
    private val flag:String="",
    private val onImageDeleted: ((Int) -> Unit)? = null
) : DialogFragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnPrev: ImageView
    private lateinit var btnNext: ImageView
    private lateinit var btnClose: ImageView
    private lateinit var btnDelete: ImageView
    private lateinit var imageCount:TextView
    private  var adapter: GalleryShowAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_show_layout, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout( (resources.displayMetrics.widthPixels * 0.9).toInt(), (resources.displayMetrics.heightPixels * 0.4).toInt())
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Transparent background
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        imageCount=view.findViewById(R.id.img_count)
        btnClose=view.findViewById(R.id.btnclose)
        btnDelete=view.findViewById(R.id.btnDelete)

        if(flag == "syncPage"){
            btnDelete.visibility=View.GONE
        }

        adapter= context?.let { GalleryShowAdapter(imageList, it) }
        viewPager.adapter = adapter

        // Set the starting position
        viewPager.setCurrentItem(startPosition, false)
        imageCount.text="${(viewPager.currentItem+1)}/${imageList.size}"

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                btnPrev.isEnabled = position > 0
                btnNext.isEnabled = position < imageList.size - 1
                imageCount.text="${(viewPager.currentItem+1)}/${imageList.size}"

            }
        })



        // Button Click Listeners
        btnPrev.setOnClickListener {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
            imageCount.text="${(viewPager.currentItem+1)}/${imageList.size}"

        }

        btnDelete.setOnClickListener {
            val currentPosition = viewPager.currentItem
            if (imageList.isNotEmpty()) {
              //  imageList.removeAt(currentPosition) // Remove from list
             //   viewPager.adapter?.notifyItemRemoved(currentPosition) // Notify adapter
               // viewPager.adapter?.notifyItemRangeChanged(currentPosition, imageList.size) // Update positions
                onImageDeleted?.invoke(currentPosition)
            //    adapter?.notifyDataSetChanged()
                  viewPager.adapter?.notifyItemRemoved(currentPosition) // Notify adapter
                 viewPager.adapter?.notifyItemRangeChanged(currentPosition, imageList.size) // Update positions

                // If the list is empty, close the dialog
                if (imageList.isEmpty()) {
                    dialog?.dismiss()
                } else {
                    // Ensure the ViewPager updates correctly
                    val newPosition = if (currentPosition >= imageList.size) imageList.size - 1 else currentPosition
                    viewPager.setCurrentItem(newPosition, false)
                    imageCount.text = "${newPosition + 1}/${imageList.size}"
                }

                // Notify parent fragment/activity about the deletion

            }
        }


        btnClose.setOnClickListener{
            dialog?.dismiss()
        }

        btnNext.setOnClickListener {
            viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            imageCount.text="${(viewPager.currentItem+1)}/${imageList.size}"

        }
    }
}