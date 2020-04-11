package com.all.dwcarpentry.helpers

import com.all.dwcarpentry.data.House

object Utilities
{
    fun getEmptyHouse() : House
    {
        return House("", "No Homeowner", "123 Default Road", "5 - 2x4", mutableListOf(), mutableListOf())
    }
}