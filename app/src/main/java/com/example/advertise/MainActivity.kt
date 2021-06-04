package com.example.advertise

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.BoringLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.set
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.advertise.databinding.ActivityMainBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File


private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.ivImage.setImageResource(R.drawable.ic_image)
        SetInformationHide(true)
        SetInfoImageHide(true)
        SetPreViewButtonHide(true)
        SetPushButtonHide(true)
        binding.btnPush.setVisibility(View.INVISIBLE)
        //btn choose img clicked
//        binding.btnChooseImg.setOnClickListener {
//            //check run time permission
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
//                    // permission denied
//                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                    requestPermissions(permissions, PERMISSION_CODE)
//                } else {
//                    // permission already granted
//                    pickImageFromGallary()
//                }
//            } else {
//                // system OS is lower Marshallow
//            }
//        }
        binding.btnChoose.setOnClickListener {
            ImagePicker.with(this)
                    .crop()	    			                //Crop image(Optional), Check Customization for more option
                    .compress(1024)			        //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
        }
    }

    private fun pickImageFromGallary() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun SetInformationHide(status: Boolean){
        if (status == true) {
            binding.tvImgName.setVisibility(View.INVISIBLE)
            binding.tvImgSize.setVisibility(View.INVISIBLE)
            binding.tvImgFormat.setVisibility(View.INVISIBLE)
            binding.tvImgLocation.setVisibility(View.INVISIBLE)
            binding.tvImgTitle.setVisibility(View.INVISIBLE)
        } else {
            binding.tvImgName.setVisibility(View.VISIBLE)
            binding.tvImgSize.setVisibility(View.VISIBLE)
            binding.tvImgFormat.setVisibility(View.VISIBLE)
            binding.tvImgLocation.setVisibility(View.VISIBLE)
            binding.tvImgTitle.setVisibility(View.VISIBLE)
        }

    }

    private fun SetInfoImageHide(status: Boolean){
        if (status == true){
            binding.tvInfoName.setVisibility(View.INVISIBLE)
            binding.tvInfoSize.setVisibility(View.INVISIBLE)
            binding.tvInfoFormat.setVisibility(View.INVISIBLE)
            binding.tvInfoLocation.setVisibility(View.INVISIBLE)
        } else {
            binding.tvInfoName.setVisibility(View.VISIBLE)
            binding.tvInfoSize.setVisibility(View.VISIBLE)
            binding.tvInfoFormat.setVisibility(View.VISIBLE)
            binding.tvInfoLocation.setVisibility(View.VISIBLE)
        }
    }

    private fun SetPreViewButtonHide(status: Boolean){
        if (status == true){
            binding.btnPreview.setVisibility(View.INVISIBLE)
        } else {
            binding.btnPreview.setVisibility(View.VISIBLE)
        }
    }

    private fun SetPushButtonHide(status: Boolean){
        if (status == true){
            binding.btnPush.setVisibility(View.INVISIBLE)
        } else {
            binding.btnPush.setVisibility(View.VISIBLE)
        }
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

    //handle pick image result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK ) { // && requestCode == IMAGE_PICK_CODE
            val uri: Uri = data?.data!!
            binding.ivImage.setImageURI(uri)
            ConvertImageToHex(uri)
            SetPreViewButtonHide(false)
            SetPushButtonHide(false)
            binding.btnPush.setOnClickListener {
                val result = ConvertImageToHex(uri)
                Toast.makeText(this, "Push done!", Toast.LENGTH_SHORT).show()
            }
            PreviewImage(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    fun ConvertImageToHex(uri: Uri): MutableList<Byte>{
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        val width: Int = bitmap.width
        val height: Int = bitmap.height

        Log.e(TAG, "width, height = " + width.toString() + ", " + height.toString())
        val listPixel: MutableList<Byte> = ArrayList()
        for (color in 0 .. 2){
            for (y in 0 .. 64) {
                for (x in 0 .. 64) {
                    val p: Int = bitmap.getPixel(x, y)
                    if (color == 0) {
                        val redColor: Int = Color.red(p)
                        val redColorByte: Byte = redColor.toByte()
                        listPixel.add(redColorByte)
                    }
                    if (color == 1) {
                        val greenColor: Int = Color.green(p)
                        val greenColorByte: Byte = greenColor.toByte()
                        listPixel.add(greenColorByte)
                    }
                    if (color == 2) {
                        val blueColor: Int = Color.blue(p)
                        val blueColorByte: Byte = blueColor.toByte()
                        listPixel.add(blueColorByte)
                    }
                }
            }
        }
        Log.e(TAG, "listPixel = " + listPixel)
        binding.btnPreview.setOnClickListener {
            val imgToPreView = PreviewImage(uri)
            binding.ivPreview.setImageBitmap(imgToPreView)
        }
        return listPixel
    }

    fun PreviewImage(uri: Uri): Bitmap{
////        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
//        val width: Int = bitmap.width
//        val height: Int = bitmap.height
        LookupInfo(uri)
        val originBitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        val emptyImg: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.image_for_preview)
        val operation = Bitmap.createBitmap(530, 530, emptyImg.config)
        val width: Int = emptyImg.width
        val height: Int = emptyImg.height
        Log.e(TAG,"width = " + width )
        Log.e(TAG,"height = " + height )
        for (y in 0..64) {
            for (x in 0..64) {
                val p: Int = originBitmap.getPixel(x, y)
                var oriRed: Int = Color.red(p)
                var oriGreen: Int = Color.green(p)
                var oriBlue: Int = Color.blue(p)
                var oriAlpha: Int = Color.alpha(p)
                for (xpos in (x*8) .. (x+1)*8){
//                    Log.e(TAG, "xpos = " + xpos)
                    for (ypos in (y*8) .. (y+1)*8){
                        operation.setPixel(xpos, ypos, Color.argb(oriAlpha, oriRed, oriGreen, oriBlue))
//                        Log.e(TAG, "ypos = " + ypos)
                    }
//                    operation.setPixel(y, xpos, Color.argb(oriAlpha, oriRed, oriGreen, oriBlue))
                }

            }
        }
        return operation
    }

    fun LookupInfo(uri: Uri){
        val path: String? = uri.path
        Log.e(TAG, "path = " + path)
    }
}