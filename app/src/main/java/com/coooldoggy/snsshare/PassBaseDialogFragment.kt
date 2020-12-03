package com.coooldoggy.snsshare

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

open class PassBaseDialogFragment : DialogFragment() {

    companion object {
        val TAG: String = PassBaseDialogFragment::class.java.simpleName
        const val KEY_REQUEST_CODE = "KEY_REQUEST_CODE"
    }

    protected var isResultPositive: Boolean? = null
    protected var internalRequestCode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        savedInstanceState?.let {
            internalRequestCode = it.getInt(KEY_REQUEST_CODE, 0)
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
        isCancelable = false
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach isResultPositive : $isResultPositive")
        Log.d(TAG, "targetFragment : $targetFragment")
        Log.d(TAG, "parentFragment : $parentFragment")
        Log.d(TAG, "context : $context")

        val resultFragment = parentFragment ?: context

        super.onDetach()

        when(isResultPositive) {
            true -> {
                performOnClickPositive((resultFragment as? PassDialogListener))
            }

            false -> {
                performOnClickNegative((resultFragment as? PassDialogListener))
            }

            null -> {}
        }
    }

    protected open fun performOnClickPositive(listener: PassDialogListener?) {
        Log.d(TAG, "performOnClickPositive $listener / $internalRequestCode")

        if(internalRequestCode != R.id.dialog_no_request) {
            listener?.onClickDialogPositive(internalRequestCode)
        }
    }

    protected open fun performOnClickNegative(listener: PassDialogListener?) {
        Log.d(TAG, "performOnClickNegative $listener / $internalRequestCode")

        if(internalRequestCode != R.id.dialog_no_request) {
            listener?.onClickDialogNegative(internalRequestCode)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_REQUEST_CODE, internalRequestCode)
    }

    override fun setTargetFragment(fragment: Fragment?, requestCode: Int) {
        super.setTargetFragment(fragment, requestCode)

        internalRequestCode = requestCode
    }

    override fun dismissAllowingStateLoss() {
        try {
            super.dismissAllowingStateLoss()
        } catch (e : Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
    }

    interface PassDialogListener {

        fun onClickDialogPositive(requestCode: Int)

        fun onClickDialogNegative(requestCode: Int)

    }

}