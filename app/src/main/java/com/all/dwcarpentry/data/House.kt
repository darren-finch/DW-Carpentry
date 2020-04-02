package com.all.dwcarpentry.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude

@Entity(tableName = "houses")
class House
{
    @Exclude
    @PrimaryKey
    var id: Int = 0

    constructor()
    constructor(
        key: String,
        homeOwnerName: String,
        homeAddress: String,
        materialsUsed: String,
        homeImagesUrls: MutableList<String>,
        homeImagesNames: MutableList<String>)
    {
        this.key = key
        this.homeOwnerName = homeOwnerName
        this.homeAddress = homeAddress
        this.materialsUsed = materialsUsed
        this.homeImagesUrls = homeImagesUrls
        this.homeImagesNames = homeImagesNames
    }
    lateinit var key: String
    lateinit var homeOwnerName: String
    lateinit var homeAddress: String
    lateinit var materialsUsed: String
    lateinit var homeImagesUrls: MutableList<String>
    lateinit var homeImagesNames: MutableList<String>
}