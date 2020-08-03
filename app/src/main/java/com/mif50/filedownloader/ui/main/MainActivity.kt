package com.mif50.filedownloader.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.mif50.filedownloader.R
import com.mif50.filedownloader.di.component.ActivityComponent
import com.mif50.filedownloader.ui.base.BaseActivity
import com.mif50.filedownloader.ui.base.custom.CustomProgress
import com.mif50.filedownloader.ui.media.MediaPlayerActivity
import com.mif50.filedownloader.utils.common.LayoutRes
import com.mif50.filedownloader.utils.file.getRootDirPath
import com.mif50.filedownloader.utils.ktx.gone
import com.mif50.filedownloader.utils.ktx.startActivity
import com.mif50.filedownloader.utils.ktx.visible
import kotlinx.android.synthetic.main.activity_main.*


@LayoutRes(layout = R.layout.activity_main)
class MainActivity : BaseActivity<MainViewModel>(), CustomProgress.CustomProgressDelegate {

    private val dirPath: String by lazy {
        getRootDirPath(applicationContext)
    }

    private val customProgress: CustomProgress by lazy {
        CustomProgress.instance
    }

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        setListener()
    }

    override fun setupObservers() {
        super.setupObservers()
        observeIsVideoCached()
        observeLoading()
        observeProgress()
        observeVideoName()
    }

    private fun observeIsVideoCached(){
        viewModel.isVideoPathCachedLiveData.observe(this, Observer {
            it.getIfNotHandled()?.run {
                if (this.first) {
                    cardVideo.visible()
                    tvMediaPlayer.text = this.second
                } else {
                    cardVideo.gone()
                }
            }
        })
    }

    private fun observeLoading(){
        viewModel.loadingLiveData.observe(this, Observer {
            it.getIfNotHandled()?.run {
                if (this) showProgress() else hideProgress()
            }
        })
    }

    private fun observeProgress(){
        viewModel.progressLiveData.observe(this, Observer {
            it.getIfNotHandled()?.run {
                // we need to update progress bar value here
                customProgress.updateProgress(progress = this.toInt())
            }
        })
    }

    private fun observeVideoName(){
        viewModel.videoNameLiveData.observe(this, Observer {
            it.getIfNotHandled()?.run {
                cardVideo.visible()
                tvMediaPlayer.text = this
            }
        })
    }

    private fun setListener(){
        btnDownload.setOnClickListener {
            viewModel.downloadFile(dirPath)
        }

        cardVideo.setOnClickListener {
            startActivity<MediaPlayerActivity>()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.ivDelete -> {
                viewModel.deleteFiles(dirPath)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    private fun showProgress(){
        customProgress.showProgress(this,this)
    }

    private fun hideProgress(){
        customProgress.hideProgress()
    }

    override fun onCancelTapped() {
        viewModel.cancelDownload()
    }

    override fun onPauseTapped() {
        viewModel.pauseDownload()
    }

    override fun onResumeTapped() {
        viewModel.resumeDownload()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgress()
    }

}