package com.mif50.filedownloader.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefManager @Inject constructor(private val prefs: SharedPreferences): PrefManagerDelegate{

    companion object {
        private const val KEY_VIDEO_PATH = "pref_key_video_path"
    }

    override var videoPath: String
        get() = prefs.getString(KEY_VIDEO_PATH,"") ?: ""
        set(value) {
            setString(KEY_VIDEO_PATH,value)
        }



    /// ---- helper method ---------------
    private fun setBoolean(key: String,value: Boolean){
        prefs.edit().putBoolean(key,value).apply()
    }

    private fun setString(key: String,value: String?){
        prefs.edit().putString(key,value).apply()
    }

    private fun setInt(key: String,value: Int){
        prefs.edit().putInt(key,value).apply()
    }

    private fun removeKey(key: String){
        prefs.edit().remove(key).apply()
    }

    fun clear(){
        prefs.edit().clear().apply()
    }

}