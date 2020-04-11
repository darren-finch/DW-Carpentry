package com.all.dwcarpentry.ui.recyclerviews

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.House
import com.bumptech.glide.Glide
import java.util.*

class HousesRecyclerViewAdapter(private val fragment: Fragment, private val allHouses: MutableList<House>,
                                private val onHouseCardClickedListener: OnHouseCardClickedListener) : RecyclerView.Adapter<HouseViewHolder>(), Filterable
{
    var allHousesFiltered: MutableList<House> = allHouses
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.house_card, parent, false)
        return HouseViewHolder(view, onHouseCardClickedListener)
    }

    override fun getItemCount(): Int
    {
        return allHousesFiltered.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HouseViewHolder, position: Int)
    {
        val house = allHousesFiltered[position]
        holder.houseKey = house.key
        holder.homeOwnerName.text = "Home Owner - " + house.homeOwnerName
        holder.homeAddress.text = "Address - " + house.homeAddress
        if(house.homeImagesUrls.size > 0 && house.homeImagesUrls[0].isNotEmpty())
            Glide.with(fragment).asBitmap().load(house.homeImagesUrls[0]).into(holder.houseImage)
    }
    fun updateHouses(newHouses: MutableList<House>)
    {
        allHouses.clear()
        allHouses.addAll(newHouses)
        notifyDataSetChanged()
    }
    interface OnHouseCardClickedListener
    {
        fun onHouseCardClicked(houseKey: String)
    }
    override fun getFilter(): Filter
    {
        return object : Filter()
        {
            override fun performFiltering(constraint: CharSequence?): FilterResults
            {
                val query = constraint.toString().toLowerCase(Locale.ROOT)
                if(query.isEmpty())
                {
                    allHousesFiltered = allHouses
                }
                else
                {
                    val resultList = mutableListOf<House>()
                    for(house in allHouses)
                    {
                        if(house.homeOwnerName.toLowerCase(Locale.ROOT).startsWith(query) || house.homeAddress.toLowerCase(Locale.ROOT).startsWith(query))
                        {
                            resultList.add(house)
                        }
                    }
                    allHousesFiltered = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = allHousesFiltered
                return filterResults
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?)
            {
                allHousesFiltered = results?.values as MutableList<House>
                notifyDataSetChanged()
            }
        }
    }
}