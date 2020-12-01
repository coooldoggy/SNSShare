package com.coooldoggy.snsshare

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coooldoggy.snsshare.common.*
import com.coooldoggy.snsshare.databinding.ActivityShareBinding
import com.bumptech.glide.Glide

class ShareActivity : AppCompatActivity() {
    private val TAG = ShareActivity::class.java.simpleName
    private lateinit var shareBinding: ActivityShareBinding
    private lateinit var platform: String
    private var imagePath = ""
    private lateinit var imageUri: Uri
    private var photolist = arrayListOf<Uri?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareBinding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(shareBinding.root)
        intent?.extras?.let {
            platform = it.getSerializable(PLATFORM_KEY) as? String ?: PLATFORM_ALL_AVAILABLE
            Log.d(TAG, "platform = $platform")
        }
        setView()
    }

    private fun setView() {
        shareBinding.ivTextShare.setOnClickListener {
            share(
                CONTENT_TYPE_TEXT,
                PLATFORM_KAKAO, shareBinding.etText.text.toString()
            )
        }

        shareBinding.ivImage.setOnClickListener {
            if (!getCheckPermission(perms)) {
                requestPermissions(this@ShareActivity, perms, PERM_REQUEST_CODE_CAMERA)
            } else {
                imagePath = showCameraIntent(this@ShareActivity, CAMERA_REQUEST_CODE)
            }
        }

        shareBinding.ivImageShare.setOnClickListener {
            if (!::imageUri.isInitialized) {
                share(CONTENT_TYPE_IMG, platform, imagePath)
                Log.d(TAG, "imagePath = $imagePath")
            } else {
                share(CONTENT_TYPE_IMG, platform, imageUri)
                Log.d(TAG, "imageUri = $imageUri")
            }
        }

        photolist.add(null)
        shareBinding.rvMultiImage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        shareBinding.rvMultiImage.apply {
            this.setHasFixedSize(true)
            this.adapter = PhotoListAdapter(this@ShareActivity, photolist)
        }

        shareBinding.ivMultiImageShare.setOnClickListener {
            share(CONTENT_TYPE_IMG, platform, photolist)
        }
    }

    private fun share(contentType: String, platform: String, content: Any) {
        when (contentType) {
            CONTENT_TYPE_TEXT -> {
                shareText(this@ShareActivity, content, platform)
            }
            CONTENT_TYPE_IMG -> {
                if (content is ArrayList<*>){
                    shareMultiImg(this@ShareActivity, content as ArrayList<Uri>, platform)
                }else{
                    shareImg(this@ShareActivity, content, platform)
                }
            }
        }
    }

    private fun loadUriIntoIV(imageUri: Uri) {
        Glide.with(applicationContext).load(imageUri).into(shareBinding.ivImage)
    }

    private fun loadUriIntoIV(pathString: String) {
        Glide.with(applicationContext).load(pathString).into(shareBinding.ivImage)
    }

    private fun addPhoto(data: Uri){
        when(photolist.size){
            1 ->{
                photolist.clear()
                photolist.add(data)
                photolist.add(null)
            }
            else ->{
                photolist.remove(photolist[photolist.lastIndex])
                photolist.add(data)
                photolist.add(null)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    /**
                     * 카메라로 찍은 경우
                     */
                    if (data?.data == null && data?.clipData == null) {
                        loadUriIntoIV(imagePath)
                    } else {
                        val clipData = data.clipData
                        val dataString = data.data
                        if (clipData != null) {
                            for (i in 0 until clipData.itemCount) {
                                val item = clipData.getItemAt(i)
                                imageUri = item.uri
                                loadUriIntoIV(imageUri)
                            }
                        } else {
                            dataString?.let {
                                imageUri = it
                                loadUriIntoIV(imageUri)
                            }
                        }
                    }
                }

                CAMERA_REQUEST_MULTIPLE_CODE ->{
                    val clipData = data?.clipData
                    val dataString = data?.data
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val item = clipData.getItemAt(i)
                            addPhoto(item.uri)
                        }
                    } else {
                        dataString?.let {
                            addPhoto(it)
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERM_REQUEST_CODE_CAMERA -> {
                if (grantResults.isNotEmpty()) {
                    imagePath = showCameraIntent(this@ShareActivity, CAMERA_REQUEST_CODE)
                }
            }
            PERM_REQUEST_CODE_MULTIPLE ->{
                if (grantResults.isNotEmpty()) {
                    showMultipleCameraIntent(this@ShareActivity, CAMERA_REQUEST_MULTIPLE_CODE)
                }
            }
        }
    }


    class PhotoListAdapter(val context: Context, val photoList: ArrayList<Uri?>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            private const val ADD_PHOTO_TYPE = 0
            private const val PHOTO_PREVIEW_TYPE = 1
        }

        override fun getItemViewType(position: Int): Int {
            return if (photoList[position] == null) {
                ADD_PHOTO_TYPE
            } else {
                PHOTO_PREVIEW_TYPE
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                ADD_PHOTO_TYPE -> {
                    val v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_photo_add, parent, false)
                    PhotoAddHolder(v)
                }
                PHOTO_PREVIEW_TYPE -> {
                    val v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_photo, parent, false)
                    PhotoPreviewHolder(v)
                }
                else -> throw IllegalStateException("Illegal view type!!")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var type = getItemViewType(position)
            when (type) {
                ADD_PHOTO_TYPE -> {
                    var viewHolder = holder as PhotoAddHolder
                    viewHolder.addItem.setOnClickListener {
                        if (!context.getCheckPermission(perms)) {
                            context.requestPermissions(
                                context as Activity, perms, PERM_REQUEST_CODE_MULTIPLE
                            )
                        } else {
                            context.showMultipleCameraIntent(context, CAMERA_REQUEST_MULTIPLE_CODE)
                        }
                    }
                }
                PHOTO_PREVIEW_TYPE -> {
                    var viewHolder = holder as PhotoPreviewHolder
                    viewHolder.bind(photoList[position]!!, context)
                    viewHolder.delete.setOnClickListener {
                        if (photoList[photoList.lastIndex] == null) {
                            photoList.remove(photoList[position])
                        } else {
                            photoList.remove(photoList[position])
                            photoList.add(null)
                        }
                        notifyDataSetChanged()
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return photoList.size
        }

        class PhotoPreviewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val preview = view.findViewById<ImageView>(R.id.iv_preview)
            val delete = view.findViewById<LinearLayout>(R.id.ll_delete)

            fun bind(path: Uri, context: Context) {
                Glide.with(context.applicationContext).load(path).override(77, 77).fitCenter()
                    .into(preview)
            }
        }

        class PhotoAddHolder(view: View) : RecyclerView.ViewHolder(view) {
            val addItem = view.findViewById<ImageView>(R.id.iv_addPhoto)
        }
    }

}