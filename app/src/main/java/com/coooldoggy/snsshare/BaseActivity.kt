package com.coooldoggy.snsshare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ParseException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import java.util.*

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() , Observer, PassBaseDialogFragment.PassDialogListener {

    companion object {
        private val TAG: String =  BaseActivity::class.java.simpleName
        const val REFERRER_NAME = "android.intent.extra.REFERRER_NAME"
    }

    val lifecycleScopeForJava = lifecycleScope


    private val preventTouchView: View by lazy {
        View(this).apply {
            setOnTouchListener { _, _ ->
                Log.d(TAG, "PREVENT TOUCH!!!!")
                true
            }
            visibility = View.GONE
        }.also {
            val viewGroup = window.decorView as? ViewGroup
            viewGroup?.addView(it, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
    }

    private var mFragmentId = -1

    var isShowAllowSimStateChangeDialog = true

    fun getContext(): Context? = this@BaseActivity.baseContext

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Prevent : java.lang.IllegalStateException: Only fullscreen activities can request orientation
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPostResume() {
        super.onPostResume()

        preventTouchView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()

        preventTouchView.visibility = View.VISIBLE
    }

    fun setFragmentID(fragmentId: Int) {
        Log.d(TAG, "setFragmentID : $fragmentId")

        mFragmentId = fragmentId
    }

    fun switchFragment(fragment: Fragment) {
        Log.d(TAG, "switchFragment")
        switchFragment(mFragmentId, fragment)
    }

    fun switchFragment(@IdRes containerViewId: Int, fragment: Fragment) {
        Log.d(TAG, "switchFragment [$containerViewId, $fragment]")

        if(isFinishing || lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        if(fragment.isAdded || fragment.isResumed || fragment.isVisible) {
            return
        }

        runCatching {
            supportFragmentManager.commit(true) {
                replace(containerViewId, fragment)
            }
        }
    }

    fun switchFragment(fragment: Fragment, @AnimRes animEnter: Int, @AnimRes animExit: Int) {
        Log.d(TAG, "switchFragment with Animation")
        switchFragment(mFragmentId, fragment, animEnter, animExit)
    }

    fun switchFragment(@IdRes containerViewId: Int, fragment: Fragment, @AnimRes animEnter: Int, @AnimRes animExit: Int) {
        Log.d(TAG, "switchFragment with Animation [$containerViewId, $fragment]")
        if(isFinishing || lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        if(fragment.isAdded || fragment.isResumed || fragment.isVisible) {
            return
        }

        runCatching {
            supportFragmentManager.commit(true) {
                setCustomAnimations(animEnter, animExit, animEnter, animExit)
                replace(containerViewId, fragment)
            }
        }
    }

    fun addFragment(fragment: Fragment) {
        Log.d(TAG, "addFragment")
        addFragment(mFragmentId, fragment)
    }

    fun addFragment(@IdRes containerViewId: Int, fragment: Fragment) {
        Log.d(TAG, "addFragment [$containerViewId, $fragment]")
        if(isFinishing || lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        if(fragment.isAdded || fragment.isResumed || fragment.isVisible) {
            return
        }

        runCatching {
            supportFragmentManager.commit(true) {
                add(containerViewId, fragment)
            }
        }
    }

    fun addFragment(fragment: Fragment, @AnimRes animEnter: Int, @AnimRes animExit: Int) {
        Log.d(TAG, "addFragment")
        addFragment(mFragmentId, fragment, animEnter, animExit)
    }

    fun addFragment(@IdRes containerViewId: Int, fragment: Fragment, @AnimRes animEnter: Int, @AnimRes animExit: Int) {
        Log.d(TAG, "addFragment [$containerViewId, $fragment, $animEnter, $animExit]")

        if(isFinishing || lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        if(fragment.isAdded || fragment.isResumed || fragment.isVisible) {
            return
        }

        runCatching {
            supportFragmentManager.commit(true) {
                setCustomAnimations(animEnter, animExit, animEnter, animExit)
                add(containerViewId, fragment)
            }
        }
    }

    open fun finishFragment(fragment: Fragment) {
        Log.d(TAG, "finishFragment")

        if(isFinishing || lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        if(fragment.isRemoving || fragment.isDetached) {
            return
        }

        supportFragmentManager.commit(true) {
            remove(fragment)
        }
    }

    fun finishFragment(fragment: Fragment, @AnimRes animEnter: Int, @AnimRes animExit: Int) {
        Log.d(TAG, "finishFragment")

        if(isFinishing || lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }

        if(fragment.isRemoving || fragment.isDetached) {
            return
        }

        runCatching {
            supportFragmentManager.commit(true) {
                setCustomAnimations(animEnter, animExit, animEnter, animExit)
                remove(fragment)
            }
        }
    }

    fun findFragmentByTag(tag: String): Fragment? {
        Log.d(TAG, "findFragmentByTag [$tag]")
        return supportFragmentManager.findFragmentByTag(tag)
    }

    fun isExistDialogByTag(tag: String): Boolean {
        return findFragmentByTag(tag)?.let {
            it.isAdded || it.isResumed || it.isVisible || it.isInLayout
        } ?: false
    }

     fun showDialogFragment(dialogFragment: DialogFragment, tag: String) {
        Log.d(TAG, "showDialogFragment 2")
        showDialogFragment(dialogFragment, tag, R.id.dialog_no_request)
    }

    fun showDialogFragment(dialogFragment: DialogFragment, tag: String, @IdRes requestCode: Int) {
        var funTag: String? = tag
        Log.d(TAG, "showDialogFragment 3")
        Log.d(TAG, "showDialogFragment isFinishing : $isFinishing / lifecycle.currentState : ${lifecycle.currentState}")
        if(isFinishing || lifecycle.currentState == Lifecycle.State.DESTROYED) {
            Log.d(TAG, "showDialogFragment isFinishing : $isFinishing / lifecycle.currentState : ${lifecycle.currentState}")
            return
        }

        if(dialogFragment.isAdded || dialogFragment.isVisible || dialogFragment.isResumed) {
            Log.d(TAG, "showDialogFragment isAdded : ${dialogFragment.isAdded} / isVisible : ${dialogFragment.isVisible} / isResumed : ${dialogFragment.isResumed}")
            return
        }

        if (funTag.isNullOrEmpty()) {
            funTag = ""
        }

        Log.d(TAG, "DialogFragment : " + dialogFragment.javaClass.simpleName)
        Log.d(TAG, "TAG : $funTag")

        dialogFragment.setTargetFragment(null, requestCode)

        val result = runCatching {
            lifecycleScope.launch(Dispatchers.Main) {
                /*
                    PASSBYUP-2698
                    기존에는 commit 을 사용하였으나 해당 동작은 비동기라 비슷한 타이밍에 호출될 경우 isAdded 를 통해 예외 처리가 불가하여
                    commitNow 로 변경 (백 스택 사용 불가)
                 */
                supportFragmentManager.commitNow(true) {
                    add(dialogFragment, funTag)
                }
            }
        }

        result.exceptionOrNull()?.let {
            Log.e(TAG, Log.getStackTraceString(it))
        }
    }

//    override fun onBackPressed() {
//        val fm = supportFragmentManager
//        if (fm.fragments.size != 0) {
//            val fs = fm.fragments
//            val size = fs.size
//            for (i in size - 1 downTo 0) {
//                val f = fs[i]
//                if (f is BaseFragment) {
//                    if (!f.onBackPressed()) {
//                        if (fm.fragments.size <= 1) {
//                            super.onBackPressed()
//                            finish()
//                            return
//                        }
//                        f.finishThisFragment()
//                    }
//                    break
//                }
//            }
//        } else {
//            super.onBackPressed()
//            finish()
//        }
//    }

    fun getBaseActivity(): BaseActivity = this


    val handler: Handler = Handler()

    override fun onClickDialogPositive(requestCode: Int) {
        when(requestCode) {
            R.id.dialog_no_request -> {}
        }
    }

    override fun onClickDialogNegative(requestCode: Int) {
        when(requestCode) {
            R.id.dialog_no_request -> {}
        }
    }

    /** Returns the referrer who started the Activity. */
    override fun getReferrer(): Uri? {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                return super.getReferrer()
            }
            return getReferrerCompatible()
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }

        return null
    }

    /** Returns the referrer on devices running SDK versions lower than 22. */
    private fun getReferrerCompatible() : Uri? {
        val referrerUri : Uri? = intent?.getParcelableExtra(Intent.EXTRA_REFERRER)

        if(referrerUri != null) {
            return referrerUri
        }

        intent?.getStringExtra(REFERRER_NAME)?.let {
            // Try parsing the referrer URL; if it's invalid, return null
            return try {
                Uri.parse(it)
            } catch (e : ParseException) {
                null
            }
        }
        return null
    }


//    fun launchForJava (block : () -> Unit?, callback : () -> Unit?) {
//        lifecycleScope.launch {
//            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
//                Log.d(TAG, "launchForJava Dispatchers.Default withContext")
//                block.invoke()
//            }
//            Log.d(TAG, "launchForJava launch")
//            callback.invoke()
//        }
//    }

    inline fun <reified T : Activity> startActivity(initialize: Intent.() -> Unit) {
        startActivity(Intent(this, T::class.java).apply {
            initialize()
        })
    }

    inline fun <reified T : Activity> startActivityForResult(requestCode: Int, initialize: Intent.() -> Unit) {
        startActivityForResult(Intent(this, T::class.java).apply {
            initialize()
        }, requestCode)
    }

    override fun update(p0: Observable?, p1: Any?) {
        TODO("Not yet implemented")
    }
}