package com.coooldoggy.snsshare

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import java.lang.ref.WeakReference

class Application  : MultiDexApplication() {

    companion object{
        val TAG = Application::class.java.simpleName

        private var sAppContext: WeakReference<Context>? = null

        @JvmStatic
        fun getContext(): Context? {
            Log.d(TAG, "getContext")
            val context = sAppContext?.get()
            Log.d(TAG, "getContext context : $context")
            return context
        }
    }
}