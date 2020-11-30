package com.coooldoggy.snsshare.common

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.shareText(context: Context, content: Any, platform: String) {
    Intent(Intent.ACTION_SEND).apply {
        type = CONTENT_TYPE_TEXT
        putExtra(Intent.EXTRA_TEXT, content as String)
        setPackage(platform)
    }.runCatching {
        if (isPackageInstalled(context, platform)) {
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
        putExtra(Intent.EXTRA_STREAM, Uri.parse(content as String))
        setPackage(platform)
    }.runCatching {
        if (isPackageInstalled(context, platform)) {
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