package com.all.dwcarpentry.ui.recyclerviews

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.room.House
import com.all.dwcarpentry.helpers.Utilities

class HouseViewHolder(itemView: View, onHouseCardClickedListener: OnHouseCardClickedListener, contentResolver: ContentResolver) : RecyclerView.ViewHolder(itemView)
{
    private var houseId = -1 //Initialized in HousesRecyclerViewAdapter
    private var myContentResolver: ContentResolver = contentResolver
    private val homeOwnerName: TextView = itemView.findViewById(R.id.homeOwner)
    private val homeAddress: TextView = itemView.findViewById(R.id.homeAddress)
    private val houseImage: ImageView = itemView.findViewById(R.id.houseImage)
    private val onClickListener: View.OnClickListener

    init
    {
        onClickListener = View.OnClickListener {
            val index = adapterPosition
            if (index != RecyclerView.NO_POSITION) onHouseCardClickedListener.onHouseCardClicked(
                houseId)
        }
        itemView.setOnClickListener(onClickListener)
    }

    @SuppressLint("SetTextI18n")
    fun bind(data: House)
    {
        houseId = data.id
        homeOwnerName.text = "Home Owner - " + data.homeOwnerName
        homeAddress.text = "Lot Number/Address - " + data.homeAddress
        if(data.homeImagesUris.isNotEmpty() && data.homeImagesUris[0].isNotEmpty())
            houseImage.setImageBitmap(Utilities.getImageFromMediaStore(contentResolver = myContentResolver,
                uri = Uri.parse(data.homeImagesUris[0])))
    }
}