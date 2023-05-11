package com.hellodoctor.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.OutputStream
import java.util.*


class MainActivity : AppCompatActivity()
{
    private lateinit var cameraImage:ImageView
    private lateinit var btnCamera:Button
    private lateinit var btnGallery:Button
    private var onePermissionNotALlowed:String="done"
    private var imageUri:Uri?=null

    @SuppressLint("MissingInflatedId")


    /**
     * permission callBack
     */
    val permissionCallBack=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    {
          var allPermssionAllow=false

        for (type in it.keys)
        {
            if (!it[type]!!)
            {
                allPermssionAllow=false
                onePermissionNotALlowed=type
                break
            }else
            {
                allPermssionAllow=true
            }

        }
        if (allPermssionAllow)
        {
            Toast.makeText(this, "all permission allow", Toast.LENGTH_SHORT).show()
        }else
        {
           // openCamera()
            Toast.makeText(this, "all permission not  allow"+onePermissionNotALlowed, Toast.LENGTH_SHORT).show()

        }


    }

    /**
     * Taking the image from Camera
     */
    private val takeImageByLuncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            // we get bitmap as result directly

            if (it)
            {
               // doCrop(imageUri!!
                cameraImage.setImageURI(imageUri)

            }

        }

    /**
     * Taking the imge from camera by intent
     */
    private  val intentByUri=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        if (it.resultCode==-1)
        {
            Toast.makeText(this, "data found : "+it.data?.extras, Toast.LENGTH_SHORT).show()
            Log.w("Data","Value"+it.data?.data)
            Log.w("Data","Value"+it.data?.extras?.get(MediaStore.EXTRA_OUTPUT))
            if (imageUri.toString().length>10)
            {
              //  cameraImage.setImageURI(imageUri)
                imageUri=Uri.parse("h")

            }else
            {
             //   cameraImage.setImageURI(it.data?.data)
            }


          //  saveImageTOGallery()

          //  Log.w("Path","Path"+photoFile!!.absolutePath)
           // sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri))

        }

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraImage=findViewById<ImageView>(R.id.imgview)
        btnCamera=findViewById<Button>(R.id.btncamera)
        btnGallery=findViewById<Button>(R.id.btnGallery)
        btnGallery.setOnClickListener {
            onPickPhoto()
        }

        btnCamera.setOnClickListener {
          openCamera()
        }
    }

    private fun checkAllPermission(): String {
        val string="done"
        if (ContextCompat.checkSelfPermission(this, cameraPermission)!=PackageManager.PERMISSION_GRANTED)
        {
           requestPermissions(arrayOf(cameraPermission, writPermission, readPermission),100)
        }else if (ContextCompat.checkSelfPermission(this, writPermission)!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf(cameraPermission, writPermission, readPermission),101)

        }
        else if (ContextCompat.checkSelfPermission(this, readPermission)!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf(readPermission, writPermission, cameraPermission),102)

        }


        return "done"
    }

    private fun openCamera() {

        if (ContextCompat.checkSelfPermission(this, cameraPermission)!=PackageManager.PERMISSION_GRANTED)
           permissionCallBack.launch(arrayOf(cameraPermission,
             writPermission, readPermission))
        else
        {
            onLaunchCamera()
           // takePhotoByCamera()
        }
//        checkAllPermission()
//        val permssionValue=checkAllPermission()
//
//        if (permssionValue=="done")
//        {
//
//
//            return
//        }
//        else if (permssionValue== cameraPermission)
//        {
//            Toast.makeText(this, "enable the camera permission", Toast.LENGTH_SHORT).show()
//        }
//        else if (permssionValue== writPermission)
//        {
//            Toast.makeText(this, "enable the camera permission", Toast.LENGTH_SHORT).show()
//
//        }
//        else if (permssionValue== readPermission)
//        {
//            Toast.makeText(this, "need tp read permission", Toast.LENGTH_SHORT).show()
//
//        }

    }

    private fun takePhotoByCamera() {
        imageUri=createFileImage()
         /*
        var captureImage=Intent()
        takeImageByLuncher.launch(imageUri)*/

        val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        intentByUri.launch(intent)


    }


    /**
     * register call back for camera
     */


    companion object
    {
        val cameraPermission=android.Manifest.permission.CAMERA
        val writPermission=android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        val readPermission=android.Manifest.permission.READ_EXTERNAL_STORAGE
      //  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
     //   val readPermissionImage=android.Manifest.permission.READ_MEDIA_IMAGES


    }


    //this code working f9

private  fun createFileImage():Uri?
{
    val directory = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"camr")
    if(!directory.exists()){
        directory.mkdirs()
    }
    val file = File(directory,"${System.currentTimeMillis()}.png")
    return FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);

}

    var photoFile: File?=null
    fun onLaunchCamera() {
       // val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)//for taking the image from camera
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)//for taking the videos form camer

        //val path=filesDir"/Marriagsaga"
      // photoFile = File(filesDir,System.currentTimeMillis().toString()+".jpg")//getPhotoFileUri("myname.jpg")//all this line working
      //  photoFile=File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"1.jpg")//working but file not found
        imageUri=getImageUriTosaveCameraImage()
        Log.w("Path","ButtonCLick path :"+imageUri)//content://media/external/images/media/1000000800
    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)

            imageUri =
                FileProvider.getUriForFile(this, this.getPackageName() + ".provider", photoFile!!,);
        else
            imageUri = Uri.fromFile(photoFile)*/

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        if (intent.resolveActivity(packageManager) != null) {
            intentByUri.launch(intent)
        }
    }


    /**
     * create the file
     */
    // Returns the File for a photo stored on disk given the fileName
    open fun getPhotoFileUri(fileName: String): File? {
        val mediaStorageDir = File(getExternalFilesDir(""), "Myimage")
     //   val mediaStorageDir = File(filesDir, "Myimage")
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
           // Log.d(APP_TAG, "failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    fun onPickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            intentByUri.launch(intent)
        }
    }

    /**
     * This funtion working f9 for camera above 29
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveImageTOGallery()
    {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,"image.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM) // requires API level 29
        }
           val imageUrl1= contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            var outputStream:OutputStream?=null
        try {

            outputStream= contentResolver.openOutputStream(imageUrl1!!)
            contentResolver.openInputStream(imageUri!!)!!.copyTo(outputStream!!,1024)
          //  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            Toast.makeText(this, "file saved sucess full", Toast.LENGTH_SHORT).show()
            outputStream!!.flush()
            outputStream.close()
        }catch (e:java.lang.Exception)
        {
            Toast.makeText(this, "error "+e.message, Toast.LENGTH_SHORT).show()
            outputStream?.close()
        }


// this also adds it to the gallery



    }

    fun getImageUriTosaveCameraImage():Uri?
    {

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,"myphoto.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            // requires API level 29
          /*  put(MediaStore.MediaColumns.DISPLAY_NAME,"myvidoe0.mp4")
           put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)*/
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var path = ""
        if (contentResolver != null) {
            val cursor: Cursor? =
                contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }
}