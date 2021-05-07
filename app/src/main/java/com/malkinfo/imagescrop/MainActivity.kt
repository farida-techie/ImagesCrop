package com.malkinfo.imagescrop

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var file:File
    private lateinit var uri :Uri
    private lateinit var camIntent:Intent
    private lateinit var galIntent:Intent
    private lateinit var cropIntent:Intent
    private lateinit var btnImg: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**set find id*/
        imageView = findViewById(R.id.mImgView)
        btnImg = findViewById(R.id.btnImg)
        /**get permission*/
        enableRuntimePermission()
        /**set open dialog*/
        imageView.setOnClickListener { openDialog() }
        btnImg.setOnClickListener { openDialog() }
    }

    private fun openDialog() {
        val openDialog = AlertDialog.Builder(this@MainActivity)
        openDialog.setIcon(R.drawable.ic_image)
        openDialog.setTitle("Choose your Image in...!!")
        openDialog.setPositiveButton("Camera"){
            dialog,_->
            openCamera()
            dialog.dismiss()

        }
        openDialog.setNegativeButton("Gallery"){
            dialog,_->
            openGallery()
            dialog.dismiss()
        }
        openDialog.setNeutralButton("Cancel"){
            dialog,_->
            dialog.dismiss()
        }
        openDialog.create()
        openDialog.show()

    }

    private fun openGallery() {
       galIntent = Intent(Intent.ACTION_PICK,
               MediaStore.Images.Media.EXTERNAL_CONTENT_URI
       )
        startActivityForResult(Intent.createChooser(galIntent,
                "Select Image From Gallery "),2)
    }

    private fun openCamera() {
        camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        file = File(Environment.getExternalStorageDirectory(),
                "file"+System.currentTimeMillis().toString()+".jpg"
        )
        uri = Uri.fromFile(file)
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri)
        camIntent.putExtra("return-data",true)
        startActivityForResult(camIntent,0)
    }

    private fun enableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,Manifest.permission.CAMERA
                )){
            Toast.makeText(this@MainActivity,
                    "Camera Permission allows us to Camera App",
                    Toast.LENGTH_SHORT).show()
        }
        else{
            ActivityCompat.requestPermissions(this@MainActivity,
            arrayOf(Manifest.permission.CAMERA),RequestPermissionCode)
        }
    }

    private fun cropImages(){
        /**set crop image*/
        try {
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri,"image/*")
            cropIntent.putExtra("crop",true)
            cropIntent.putExtra("outputX",180)
            cropIntent.putExtra("outputY",180)
            cropIntent.putExtra("aspectX",3)
            cropIntent.putExtra("aspectY",4)
            cropIntent.putExtra("scaleUpIfNeeded",true)
            cropIntent.putExtra("return-data",true)
            startActivityForResult(cropIntent,1)

        }catch (e:ActivityNotFoundException){
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK){
            cropImages()
        } else if (requestCode == 2){
            if (data != null){
                uri = data.data!!
                cropImages()
            }
        }
        else if (requestCode == 1){
            if (data != null){
                val bundle = data.extras
                val bitmap = bundle!!.getParcelable<Bitmap>("data")
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RequestPermissionCode-> if (grantResults.size>0
                    && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this@MainActivity,
                        "Permission Granted , Now your application can access Camera",
                        Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this@MainActivity,
                        "Permission Granted , Now your application can not  access Camera",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object{
        const val RequestPermissionCode = 111
    }


}