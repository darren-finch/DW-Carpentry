package com.all.dwcarpentry.ui.recyclerviews

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.House

class HousesRecyclerViewAdapter(private val housesData: MutableList<House>, private val fragment: Fragment, private val onHouseCardClickedListener : OnHouseCardClickedListener) : RecyclerView.Adapter<HouseViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder
    {
        return HouseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.house_card, parent, false),
            onHouseCardClickedListener)
    }

    override fun getItemCount(): Int
    {
        return housesData.size
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int)
    {
        holder.bind(housesData[position], fragment)
    }

    fun updateHouses(newHouses: List<House>)
    {
        housesData.clear()
        housesData.addAll(newHouses)
        notifyDataSetChanged()
//        notifyItemRangeInserted(allHouses.size, newHouses.size)
    }
}

interface OnHouseCardClickedListener
{
    fun onHouseCardClicked(houseKey: String);
}