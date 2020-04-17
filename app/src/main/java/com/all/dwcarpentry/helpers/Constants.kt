package com.all.dwcarpentry.helpers

import com.all.dwcarpentry.data.FirebaseDatabaseAccessor
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object Constants
{
//region Log tags
    const val TAG = "Log"
//endregion
//region Request codes
    const val CHOOSE_IMAGE_REQUEST = 1
//endregion
//region Field names
    const val FIREBASE_DATABASE_HOUSES_REF = "houses"
    const val FIREBASE_STORAGE_ALL_IMAGES_REF = "images/all"
//endregion
//region Misc
    const val PAGE_SIZE = 5
//endregion
}