package com.mif50.filedownloader.data.repository

import com.mif50.filedownloader.data.local.prefs.PrefManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val prefManager: PrefManager
){

    fun cacheVideoPath(filePath: String){
        prefManager.videoPath = filePath
    }

    fun getVideoPath() = prefManager.videoPath

    fun clearVideoPath(){
        prefManager.clear()
    }

}