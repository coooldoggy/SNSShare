package com.coooldoggy.snsshare.common

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * text 공유
 */
fun Context.shareText(context: Context, content: Any, platform: String) {
    Intent(Intent.ACTION_SEND).apply {
        type = CONTENT_TYPE_TEXT
        putExtra(Intent.EXTRA_TEXT, content as String)
        if (platform != PLATFORM_ALL_AVAILABLE){
            setPackage(platform)
        }
    }.runCatching {
        if (isPackageInstalled(context, platform) || platform == PLATFORM_ALL_AVAILABLE) {
            startActivity(Intent.createChooser(this, "공유하기"))
        } else {
            var url = "market://details?id=$platform"
            var market = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(market)
        }
    }
}

fun Context.sendSMSText(context: Context, content: Any){
    Intent(Intent.ACTION_VIEW, Uri.parse("sms:")).apply {
        type = PLATFORM_SMS
        putExtra("sms_body", content as String)
    }.runCatching {
        startActivity(Intent.createChooser(this, "공유하기"))
    }
}

/**
 * image 공유
 */
fun Context.shareImg(context: Context, content: Any, platform: String){
    Intent(Intent.ACTION_SEND).apply {
        type = CONTENT_TYPE_IMG
        if (content is String) {
            putExtra(Intent.EXTRA_STREAM, Uri.parse(content))
        }else{
            putExtra(Intent.EXTRA_STREAM, content as Uri)
        }
        if (platform != PLATFORM_ALL_AVAILABLE){
            setPackage(platform)
        }
    }.runCatching {
        if (isPackageInstalled(context, platform) || platform == PLATFORM_ALL_AVAILABLE) {
            startActivity(Intent.createChooser(this, "공유하기"))
        } else {
            var url = "market://details?id=$platform"
            var market = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(market)
        }
    }
}

/**
 * image 여러장 공유
 */
fun Context.shareMultiImg(context: Context, content: ArrayList<Uri>, platform: String){

    for (i in 0 until content.size){
        if (content[i] == null){
            content.removeAt(i)
        }
    }

    Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = CONTENT_TYPE_IMG
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, content)
        if (platform != PLATFORM_ALL_AVAILABLE){
            setPackage(platform)
        }
    }.runCatching {
        if (isPackageInstalled(context, platform) || platform == PLATFORM_ALL_AVAILABLE) {
            startActivity(Intent.createChooser(this, "공유하기"))
        } else {
            var url = "market://details?id=$platform"
            var market = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(market)
        }
    }
}

fun Context.shareFile(context: Context, content: Any, fileType: String, platform: String){
    Intent(Intent.ACTION_SEND).apply {
        type = fileType
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_ACTIVITY_NO_HISTORY
        if (platform != PLATFORM_ALL_AVAILABLE){
            setPackage(platform)
        }
        putExtra(Intent.EXTRA_STREAM, content as Uri)
    }.runCatching {
        if (isPackageInstalled(context, platform) || platform == PLATFORM_ALL_AVAILABLE) {
            startActivity(Intent.createChooser(this, "공유하기"))
        } else {
            var url = "market://details?id=$platform"
            var market = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(market)
        }
    }
}

fun Context.shareContact(context: Context, content: Any, platform: String){
//    var lookup = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, content as String)
//    var uri = ""
//    contentResolver.query(lookup, null, null, null, null)?.let { cursor ->
//        cursor.moveToFirst()
//        uri = cursor.getString(0)
//        cursor.close()
//    }
    //TODO 연락처 공유 안됨
    Intent(Intent.ACTION_SEND).apply {
        type = "*/*"
//        type = ContactsContract.Contacts.CONTENT_VCARD_TYPE
        putExtra(Intent.EXTRA_STREAM, content as Uri)
        if (platform != PLATFORM_ALL_AVAILABLE){
            setPackage(platform)
        }
        startActivity(Intent.createChooser(this, "공유하기"))
    }.runCatching {
        if (isPackageInstalled(context, platform) || platform == PLATFORM_ALL_AVAILABLE) {
            startActivity(Intent.createChooser(this, "공유하기"))
        } else {
            var url = "market://details?id=$platform"
            var market = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(market)
        }
    }.onFailure {
        it.printStackTrace()
    }
}

fun isPackageInstalled(context: Context, platform: String): Boolean {
    var market = context.packageManager.getLaunchIntentForPackage(platform)
    return return market != null
}