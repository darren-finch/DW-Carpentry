package com.all.dwcarpentry.ui.recyclerviews

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.House
import com.all.dwcarpentry.ui.recyclerviews.HousesRecyclerViewAdapter.*

class HouseViewHolder(itemView: View, onHouseCardClickedListener: OnHouseCardClickedListener) : RecyclerView.ViewHolder(itemView)
{
    lateinit var house: House //Initialized in HousesRecyclerViewAdapter
    val homeOwnerName: TextView = itemView.findViewById(R.id.homeOwner)
    val homeAddress: TextView = itemView.findViewById(R.id.homeAddress)
    val houseImage: ImageView = itemView.findViewById(R.id.houseImage)
//    private val viewHouse: Button = itemView.findViewById(R.id.viewHouse)
    private val onClickListener: View.OnClickListener

    init
    {
        onClickListener = View.OnClickListener {
            val index = adapterPosition
            if (index != RecyclerView.NO_POSITION) onHouseCardClickedListener.onHouseCardClicked(
                house)
        }
//        viewHouse.setOnClickListener(onClickListener)
        itemView.setOnClickListener(onClickListener)
    }
}