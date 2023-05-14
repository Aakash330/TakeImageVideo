package com.photovideo.camera

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.photovideo.cameragallery.ImageListener
import com.photovideo.cameragallery.TakeImage

class ImageTesting : AppCompatActivity(), ImageListener {
    private lateinit var takeimage: TakeImage
    private lateinit var btnCamera: Button
    private lateinit var imageView: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_testing)
        takeimage= TakeImage(this@ImageTesting,this)
        btnCamera=findViewById(R.id.btnCamera)
        imageView =findViewById(R.id.imageView)

        btnCamera.setOnClickListener {
                 takeimage.getImageFromCamera()
        }


    }

    override fun takeImageFromCamera(path: Uri?) {
       imageView.setImageURI(path)
    }

    override fun takeImageFromGallery(path: Uri?) {
        imageView.setImageURI(path)
    }


    override fun permissionDeny(name: String) {
    }

    override fun allPermissionAllowed() {
    }

    override fun onError(error: String) {
        Toast.makeText(this, "on error", Toast.LENGTH_SHORT).show()
    }


}