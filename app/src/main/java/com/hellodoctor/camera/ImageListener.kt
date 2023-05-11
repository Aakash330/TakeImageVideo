package com.hellodoctor.camera

import android.net.Uri

interface ImageListener {
    fun takeImageFromCamera(path:Uri?)
    fun takeImageFromGallery(path: Uri?)
    fun permissionDeny(name:String)
    fun allPermissionAllowed()
    fun onError(error:String)
}