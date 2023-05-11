package com.hellodoctor.camera

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class TakeImage(private val activity: AppCompatActivity, private val listener: ImageListener) {

    private var onePermissionNotALlowed: String = ""
    private var singlePermsiion: String = ""
    private var imageUri: Uri? = null

    /**
     * permission callBack
     */
    val permissionsCallBack =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {
            var allPermssionAllow = false

            for (type in it.keys) {
                if (!it[type]!!) {
                    allPermssionAllow = false
                    onePermissionNotALlowed = type
                    break
                } else {
                    allPermssionAllow = true
                }

            }
            if (allPermssionAllow) {
                Toast.makeText(activity, "all permission allow", Toast.LENGTH_SHORT).show()
                listener.allPermissionAllowed()
            } else {
                // openCamera()
                listener.permissionDeny(onePermissionNotALlowed)
                Toast.makeText(
                    activity,
                    "all permission not  allow" + onePermissionNotALlowed,
                    Toast.LENGTH_SHORT
                ).show()

            }


        }

    val singlePermissionCallback =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission())
        {
            if (it)
                listener.allPermissionAllowed()
            else
                listener.permissionDeny(singlePermsiion)

        }


    /**
     * Taking the imge from camera by intent
     */
    private val intentByUri =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == -1) {
                Log.w("Data", "Value" + it.data?.data)
                Log.w("Data", "Value" + it.data?.extras?.get(MediaStore.EXTRA_OUTPUT))
                if (imageUri.toString().length > 3) {
                    listener.takeImageFromCamera(imageUri)
                    imageUri = Uri.parse("h")

                } else {
                    listener.takeImageFromGallery(it.data?.data)
                    //   cameraImage.setImageURI(it.data?.data)
                }


                //  saveImageTOGallery()

                //  Log.w("Path","Path"+photoFile!!.absolutePath)
                // sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri))

            }

        }

    fun getImageFromCamera() {
        if (checkPermissionBeforeAction()) {
            imageUri = getImageUriTosaveCameraImage()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            intentByUri.launch(intent)
            // intentByUri.launch()
        }
    }

    fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intentByUri.launch(intent)
    }

    fun lunchPermissions() {
        permissionsCallBack.launch(getListPermission().toTypedArray())
    }

    fun lunchSinglePermission(name: String) {
        singlePermsiion = name
        singlePermissionCallback.launch(singlePermsiion)
    }



    private fun getImageUriTosaveCameraImage(): Uri? {

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            // requires API level 29
            /*  put(MediaStore.MediaColumns.DISPLAY_NAME,"myvidoe0.mp4")
             put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
              put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)*/
        }
        return activity.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    /**
     * from here all below code related to permission
     */


    private fun getListPermission(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add(CAMERA)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            list.add(WRITE)
        list.add(READ)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            list.add(READMEDIAIMAGE)
        return list

    }


    private fun checkPermissionBeforeAction(): Boolean {
        val list = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(
                activity,
                CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        )
            list.add(CAMERA)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            if (ContextCompat.checkSelfPermission(
                    activity,
                    WRITE
                ) != PackageManager.PERMISSION_GRANTED
            )
                list.add(WRITE)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            if (ContextCompat.checkSelfPermission(
                    activity,
                    READ
                ) != PackageManager.PERMISSION_GRANTED
            )
                list.add(READ)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            if (ContextCompat.checkSelfPermission(
                    activity,
                    READMEDIAIMAGE
                ) != PackageManager.PERMISSION_GRANTED
            )
                list.add(READMEDIAIMAGE)
        if (list.size > 0) {
            permissionsCallBack.launch(list.toTypedArray())
            return false
        } else {
            return true
        }
    }

    /**
     * all permission constent
     */
    companion object {
        private const val CAMERA = android.Manifest.permission.CAMERA
        private const val WRITE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val READ = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val READMEDIAIMAGE = android.Manifest.permission.READ_MEDIA_IMAGES
    }


}