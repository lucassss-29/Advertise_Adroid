package com.example.advertise

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.advertise.databinding.ActivityMainBinding
import java.io.File


private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //btn choose img clicked
        binding.btnChooseImg.setOnClickListener {
            //check run time permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    // permission denied
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    // permission already granted
                    pickImageFromGallary()
                }
            } else {
                // system OS is lower Marshallow
            }
        }
    }

    private fun pickImageFromGallary() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object{
        //image pick code
        private val IMAGE_PICK_CODE = 1000
        //Permission code
        private val PERMISSION_CODE = 1001
        private val TAG = "MainActivity"

    }

    // handle the permission dialog
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //permission from popup granted
                    pickImageFromGallary()
                } else {
                    // permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle image pick
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri: Uri = data?.data!!
            binding.imageView.setImageURI(uri)
            ConvertImageToHex(uri)
        }
    }

    fun ConvertImageToHex(uri: Uri){
        val path: File = File(uri.path)
        Log.e(TAG, path.toString()) // /-1/1/content:/media/external/images/media/61/ORIGINAL/NONE/image/jpeg/2008101006
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        val width: Int = bitmap.width
        val height: Int = bitmap.height
        val pixels = IntArray(width * height)

        Log.e(TAG, "width, height = " + width.toString() + ", " + height.toString())
        val listPixel: MutableList<String> = ArrayList()
        for (color in 0 .. 2){
            for (y in 0 .. 64) {
                for (x in 0 .. 64) {
                    val p: Int = bitmap.getPixel(x, y)
                    if (color == 0) {
                        val redColor: Int = Color.red(p)
                        val hexRed = String.format("0x%02X", redColor)
                        listPixel.add(hexRed)
                    }
                    if (color == 1) {
                        val greenColor: Int = Color.green(p)
                        val hexGreen = String.format("0x%02X", greenColor)
                        listPixel.add(hexGreen)
                    }
                    if (color == 2) {
                        val blueColor: Int = Color.blue(p)
                        val hexBlue = String.format("0x%02X", blueColor)
                        listPixel.add(hexBlue)
                    }
                }
            }
        }
        Log.e(TAG, listPixel.toString())
//        Log.e(TAG, "Redpixels: " + Redpixels)
    }
}