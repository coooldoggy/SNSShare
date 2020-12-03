package com.coooldoggy.snsshare

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import com.coooldoggy.snsshare.common.*
import com.coooldoggy.snsshare.databinding.FragmentShareDialogBinding

class ShareDialogFragment : PassBaseDialogFragment() {
    private var _shareDialogBinding: FragmentShareDialogBinding? = null
    private val shareDialogBinding get() = _shareDialogBinding!!
    private var targetUrl = ""
    private var targetPlatform = ""

    companion object{
        val TAG: String = ShareDialogFragment::class.java.simpleName
        const val SHARE_URL_STRING = "SHARE_URL_STRING"
        const val SHARE_TARGET_PLATFORM = "SHARE_TARGET_PLATFORM"

        @JvmStatic
        fun buildDialog(shareUrl: CharSequence, platform: CharSequence) : ShareDialogFragment = ShareDialogFragment().apply{
            arguments = Bundle().apply {
                putCharSequence(SHARE_URL_STRING, shareUrl)
                putCharSequence(SHARE_TARGET_PLATFORM, platform)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val savedUrl = it.getString(SHARE_URL_STRING)
            if (targetUrl.isEmpty() && !savedUrl.isNullOrEmpty()){
                targetUrl = savedUrl
            }

            val savedPlatform = it.getString(SHARE_TARGET_PLATFORM)
            if (targetPlatform.isEmpty() && !savedPlatform.isNullOrEmpty()){
                targetPlatform = savedPlatform
            }
            Log.d(TAG, "targetUrl= $targetUrl targetPlatform= $targetPlatform")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _shareDialogBinding = FragmentShareDialogBinding.inflate(inflater, container, false)

        shareDialogBinding.ivClose.setOnClickListener {
            if(isDetached || isRemoving) {
                return@setOnClickListener
            }
            isResultPositive = true
            dismissAllowingStateLoss()
        }

        shareDialogBinding.ivKakaotalk.setOnClickListener(onclickListener)
        shareDialogBinding.ivFacebook.setOnClickListener(onclickListener)
        shareDialogBinding.ivTwitter.setOnClickListener(onclickListener)
        shareDialogBinding.ivMessage.setOnClickListener(onclickListener)
        shareDialogBinding.btCopy.setOnClickListener(onclickListener)

        return shareDialogBinding.root
    }

    private var onclickListener = View.OnClickListener {
        when(it.id){
            R.id.iv_kakaotalk -> {
                context?.let { context ->
                    context.shareText(context, targetUrl, PLATFORM_KAKAO)
                }
            }
            R.id.iv_facebook -> {
                context?.let { context ->
                    context.shareText(context, targetUrl, PLATFORM_FACEBOOK)
                }
            }
            R.id.iv_twitter -> {
                context?.let { context ->
                    context.shareText(context, targetUrl, PLATFORM_TWITTER)
                }
            }
            R.id.iv_message -> {
                context?.let { context ->
                    context.sendSMSText(context, targetUrl)
                }
            }
            R.id.bt_copy ->{
                setClipBoard()
            }
        }
    }

    private fun setClipBoard(){
        val clipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("pasteboard", targetUrl)
        clipboard.setPrimaryClip(clipData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _shareDialogBinding = null
    }
}