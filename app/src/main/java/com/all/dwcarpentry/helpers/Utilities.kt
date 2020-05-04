package com.all.dwcarpentry.helpers

import com.all.dwcarpentry.data.room.House

object Utilities
{
    fun getEmptyHouse() : House
    {
        return House(id = 0, homeOwnerName = "No Homeowner", homeAddress = "", materialsUsed = "",
            homeImagesUris = mutableListOf())
    }
}