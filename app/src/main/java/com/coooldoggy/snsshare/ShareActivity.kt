package com.coooldoggy.snsshare

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.coooldoggy.snsshare.common.*
import com.coooldoggy.snsshare.databinding.ActivityShareBinding
import com.bumptech.glide.Glide

class ShareActivity : AppCompatActivity() {
    private val TAG = ShareActivity::class.java.simpleName
    private lateinit var shareBinding: ActivityShareBinding
    private lateinit var platform: String
    private var imagePath = ""
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareBinding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(shareBinding.root)
        intent?.extras?.let {
            platform = it.getSerializable(PLATFORM_KEY) as? String ?: ""
            Log.d(TAG, "platform = $platform")
        }
        setView()
    }

    private fun setView() {
        shareBinding.ivTextShare.setOnClickListener {
            share(
                CONTENT_TYPE_TEXT,
                PLATFORM_KAKAO, shareBinding.etText.text.toString())
        }

        shareBinding.ivImage.setOnClickListener {
            if (!getCheckPermission(perms)){
                requestPermissions(this@ShareActivity, perms, PERM_REQUEST_CODE_CAMERA)
            }else{
                imagePath = showCameraIntent(this@ShareActivity, CAMERA_REQUEST_CODE)
            }
        }

        shareBinding.ivImageShare.setOnClickListener {
            if (imagePath.isEmpty()){
                share(CONTENT_TYPE_IMG, PLATFORM_KAKAO, imageUri.toString())
            }else{
                share(CONTENT_TYPE_IMG, PLATFORM_KAKAO, imagePath)
            }
        }
    }

    private fun share(contentType: String, platform: String, content: Any) {
        when (contentType) {
            CONTENT_TYPE_TEXT -> {
                shareText(this@ShareActivity, content, platform)
            }
            CONTENT_TYPE_IMG ->{
                //TODO 갤러리 사진 안됨
                shareImg(this@ShareActivity, content, platform)
            }
        }
    }

    private fun loadUriIntoIV(imageUri : Uri){
        Glide.with(applicationContext).
        load(imageUri).
        into(shareBinding.ivImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                CAMERA_REQUEST_CODE -> {
                    /**
                     * 카메라로 찍은 경우
                     */
                    if (data?.data == null && data?.clipData == null){
                        loadUriIntoIV(Uri.parse(imagePath))
                    }else{
                        val clipData = data.clipData
                        val dataString = data.data
                        if (clipData != null){
                            for (i in 0 until clipData.itemCount){
                                val item = clipData.getItemAt(i)
                                imageUri = item.uri
                                loadUriIntoIV(imageUri)
                            }
                        }else{
                            dataString?.let {
                                imageUri = it
                                loadUriIntoIV(imageUri)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERM_REQUEST_CODE_CAMERA -> {
                if (grantResults.isNotEmpty()){
                    imagePath = showCameraIntent(this@ShareActivity, CAMERA_REQUEST_CODE)
                }
            }
        }
    }
}