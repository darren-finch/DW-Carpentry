package com.all.dwcarpentry.ui.recyclerviews

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.House
import com.bumptech.glide.Glide

class HouseViewHolder(itemView: View, onHouseCardClickedListener: OnHouseCardClickedListener) : RecyclerView.ViewHolder(itemView)
{
    private lateinit var houseKey: String //Initialized in HousesRecyclerViewAdapter
    private val homeOwnerName: TextView = itemView.findViewById(R.id.homeOwner)
    private val homeAddress: TextView = itemView.findViewById(R.id.homeAddress)
    private val houseImage: ImageView = itemView.findViewById(R.id.houseImage)
    private val onClickListener: View.OnClickListener

    init
    {
        onClickListener = View.OnClickListener {
            val index = adapterPosition
            if (index != RecyclerView.NO_POSITION) onHouseCardClickedListener.onHouseCardClicked(
                houseKey)
        }
        itemView.setOnClickListener(onClickListener)
    }

    @SuppressLint("SetTextI18n")
    fun bind(data: House, fragment: Fragment)
    {
        houseKey = data.key
        homeOwnerName.text = "Home Owner - " + data.homeOwnerName
        homeAddress.text = "Lot Number/Address - " + data.homeAddress
        try
        {
            if(data.homeImagesUrls.size > 0 && data.homeImagesUrls[0].isNotEmpty())
                Glide.with(fragment).asBitmap().load(data.homeImagesUrls[0]).into(houseImage)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}