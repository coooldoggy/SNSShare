package com.coooldoggy.snsshare.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


val perms: Array<String> = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE
)

fun Context.getCheckPermission(perms: Array<String>): Boolean {
    perms.forEach {
        val result1: Int = ContextCompat.checkSelfPermission(this, it)
        if (result1 != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

fun Context.requestPermissions(activity: Activity, perms: Array<String>, permCode: Int) {
    ActivityCompat.requestPermissions(activity, perms, permCode)
}

fun Context.showCameraIntent(context: Context, code: Int) : String{
    var mCameraPath = ""
    val fileIntent = Intent(Intent.ACTION_GET_CONTENT, null)
    fileIntent.type = "image/*"

    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//    fileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    val chooser = Intent(Intent.ACTION_CHOOSER)
    chooser.putExtra(Intent.EXTRA_INTENT, fileIntent)
    var intentArray = arrayOf(cameraIntent, galleryIntent)
    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

    if (cameraIntent.resolveActivity(context.packageManager) != null) {
        var photoFile = createImageFile()
        cameraIntent.putExtra("PhotoPath",mCameraPath)
        if (photoFile != null){
            mCameraPath = photoFile.absolutePath
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
        }

        when(code){
            CAMERA_REQUEST_CODE ->{
                (context as Activity).startActivityForResult(chooser, CAMERA_REQUEST_CODE)
            }
        }
    }
    return mCameraPath
}

@Throws(IOException::class)
fun createImageFile(): File {
    // Create an image file name
    val builder = StrictMode.VmPolicy.Builder()
    StrictMode.setVmPolicy(builder.build())
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
    )
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}

fun scaleDown(path: String) {
    var photo = BitmapFactory.decodeFile(path)
    photo = Bitmap.createScaledBitmap(photo, 1000, 1000, false)
    var bytes = ByteArrayOutputStream()
    photo.compress(Bitmap.CompressFormat.JPEG, 80, bytes)

    var file = File(path)
    file.createNewFile()
    var output = FileOutputStream(file)
    output.write(bytes.toByteArray())
    output.close()
}