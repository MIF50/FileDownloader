package com.mif50.filedownloader.ui.main

import androidx.lifecycle.MutableLiveData
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.mif50.filedownloader.R
import com.mif50.filedownloader.data.repository.MainRepository
import com.mif50.filedownloader.ui.base.BaseViewModel
import com.mif50.filedownloader.utils.common.Event
import com.mif50.filedownloader.utils.common.Resource
import com.mif50.filedownloader.utils.file.isFileExists
import com.mif50.filedownloader.utils.log.Logger
import com.mif50.filedownloader.utils.network.NetworkHelper
import com.mif50.filedownloader.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import java.io.IOException


class MainViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val mainRepository: MainRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private var downloadId = 0
    private val url = "https://bit.ly/30aWVDP"
    private val fileName = "tignum_x_video.mp4"

    val loadingLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val progressLiveData: MutableLiveData<Event<Long>> = MutableLiveData()
    val videoNameLiveData: MutableLiveData<Event<String>> = MutableLiveData()
    val isVideoPathCachedLiveData: MutableLiveData<Event<Pair<Boolean,String>>> = MutableLiveData()

    companion object {
        private const val TAG = "MainViewModel"
    }


    override fun onCreate() {
        checkIsFileStillCached()
    }

    private fun checkIsFileStillCached(){
        if (mainRepository.getVideoPath().isNotEmpty()){
            isVideoPathCachedLiveData.postValue(Event(Pair(true,fileName)))
        } else {
            isVideoPathCachedLiveData.postValue(Event(Pair(false,"")))
        }
    }

    fun downloadFile(dirPath: String) {
        Logger.d(TAG, dirPath)
        if (checkInternetConnectionWithMessage()) {
            if (!isFileExists("$dirPath/$fileName")) {
                loadingLiveData.postValue(Event(true))
                downloadId = PRDownloader.download(url, dirPath, fileName)
                    .build()
                    .setOnStartOrResumeListener {}
                    .setOnPauseListener {}
                    .setOnCancelListener {}
                    .setOnProgressListener { progress ->
                        val progressPercent: Long = progress.currentBytes * 100 / progress.totalBytes
                        progressLiveData.postValue(Event(progressPercent))
                    }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            mainRepository.cacheVideoPath("$dirPath/$fileName")
                            loadingLiveData.postValue(Event(false))
                            videoNameLiveData.postValue(Event(fileName))
                        }

                        override fun onError(error: com.downloader.Error?) {
                            loadingLiveData.postValue(Event(false))
                            messageString.postValue(Resource.error(error?.serverErrorMessage))
                        }
                    })
            } else {
                messageStringId.postValue(Resource.error(R.string.text_file_already_downloaded))
            }

        }

    }

    fun pauseDownload() {
        PRDownloader.pause(downloadId)
    }

    fun resumeDownload() {
        PRDownloader.resume(downloadId)
    }

    fun cancelDownload() {
        PRDownloader.cancel(downloadId)
    }

    fun deleteFiles(atPath: String) {
        val file = File(atPath)
        if (file.exists()) {
            val deleteCmd = "rm -r $atPath"
            val runtime = Runtime.getRuntime()
            try {
                runtime.exec(deleteCmd)
                mainRepository.clearVideoPath()
                checkIsFileStillCached()
                messageStringId.postValue(Resource.error(R.string.text_file_is_deleted))
            } catch (e: IOException) {
                Logger.d(TAG,e.message.toString())
            }
        }
    }


}