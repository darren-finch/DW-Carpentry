package com.all.dwcarpentry.helpers

import org.junit.Assert.*
import org.junit.Test

class ImagesMetaDataTest
{
    @Test
    fun isNewImageTest()
    {
        val metaData = ImageUriTracker()
        //        metaData.addNewMetaData()
        assertEquals(true, metaData.isNewImageUri(0))
        metaData.removeImageUri(0)
        assertEquals(false, metaData.isNewImageUri(0))
    }
    @Test
    fun removeImageTest()
    {
        val metaData = ImageUriTracker()
        metaData.removeImageUri(4) //invalid index
        metaData.removeImageUri(0) //valid index
    }
}