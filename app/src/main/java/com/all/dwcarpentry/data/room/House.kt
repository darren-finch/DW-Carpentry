package com.all.dwcarpentry.data.room

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "houses")
data class House(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = -1,
                 val homeOwnerName: String,
                 val homeAddress: String,
                 val materialsUsed: String,
                 val homeImagesUris: List<String>)