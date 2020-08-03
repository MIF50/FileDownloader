package com.mif50.filedownloader.ui.base.custom

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.mif50.filedownloader.R


class CustomProgress {
    private var mDialog: Dialog? = null



    private var actionType: ActionType = ActionType.Pause
    var delegate: CustomProgressDelegate? = null
    // widget
    private lateinit var mProgressBar: ProgressBar
    private lateinit var btnAction: Button
    private lateinit var tvPercentage: TextView

    companion object {
        private var customProgress: CustomProgress? = null
        val instance: CustomProgress
            get() {
                if (customProgress == null) {
                    customProgress = CustomProgress()
                }
                return customProgress!!
            }
    }

    fun showProgress(context: Context,delegate: CustomProgressDelegate) {
        mDialog =  Dialog(context).apply {
            // no tile for the dialog
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.prograss_bar_dialog)
        }
        // setup delegate
        this.delegate = delegate

        mProgressBar = mDialog?.findViewById(R.id.progressBar) as ProgressBar
        tvPercentage = mDialog?.findViewById(R.id.tvPercentage) as TextView
        val btnCancel = mDialog?.findViewById(R.id.btnCancel) as Button
        btnAction = mDialog?.findViewById(R.id.btnAction) as Button
        //  mProgressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
        // .getColor(R.color.material_blue_gray_500), PorterDuff.Mode.SRC_IN);
        mProgressBar.visibility = View.VISIBLE
        // you can change or add this line according to your need
        mProgressBar.isIndeterminate = false
        mDialog?.setCancelable(false)
        mDialog?.setCanceledOnTouchOutside(false)
        // handle action buttons
        btnCancel.setOnClickListener { cancelProgress() }
        btnAction.setOnClickListener { setActionType() }
        mDialog?.show()
    }

    fun updateProgress(progress: Int){
        mProgressBar.progress = progress
        tvPercentage.text = "$progress/100"
    }

    fun hideProgress() {
        mDialog?.dismiss()
        mDialog = null
        delegate = null
    }

    private fun cancelProgress(){
        this.delegate?.onCancelTapped()
        hideProgress()
    }

    private fun setActionType(){
        if (actionType == ActionType.Pause){
            delegate?.onPauseTapped()
            // change text to resume and change action type
            actionType = ActionType.Resume
            btnAction.text = "Resume"
        } else if (actionType == ActionType.Resume){
            delegate?.onResumeTapped()
            actionType = ActionType.Pause
            btnAction.text = "Pause"
        }
    }

    interface CustomProgressDelegate {
        fun onCancelTapped()
        fun onPauseTapped()
        fun onResumeTapped()
    }

    enum class ActionType{
        Pause,Resume
    }


}