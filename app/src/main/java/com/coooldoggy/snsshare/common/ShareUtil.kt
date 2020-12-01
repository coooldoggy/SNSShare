package com.coooldoggy.snsshare.common

import android.content.Context
import android.content.Intent
import android.net.Uri

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

fun Context.isPackageInstalled(context: Context, platform: String): Boolean {
    var market = context.packageManager.getLaunchIntentForPackage(platform)
    return return market != null
}