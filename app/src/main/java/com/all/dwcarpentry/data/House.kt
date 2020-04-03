package com.all.dwcarpentry.data

data class House(var key: String,
                 var homeOwnerName: String,
                 var homeAddress: String,
                 var materialsUsed: String,
                 var homeImagesUrls: MutableList<String>,
                 var homeImagesNames: MutableList<String>)
{
    constructor() : this("", "", "", "", mutableListOf(), mutableListOf())
}