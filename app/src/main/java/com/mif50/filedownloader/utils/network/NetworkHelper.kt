package com.mif50.filedownloader.utils.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.mif50.filedownloader.utils.log.Logger
import com.google.gson.JsonSyntaxException
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.squareup.moshi.Moshi
import java.io.IOException
import java.net.ConnectException
import javax.inject.Singleton

@Singleton
class NetworkHelper constructor(private val context: Context) {

    companion object {
        private const val TAG = "NetworkHelper"
    }

    @SuppressLint("NewApi")
    fun isNetworkConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                    }
                }
            } else {

                try {
                    val activeNetworkInfo = connectivityManager.activeNetworkInfo
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                        Logger.i("update_statut", "Network is available : true")
                        return true
                    }
                } catch (e: Exception) {
                    Logger.i("update_statut", "" + e.message)
                }

            }
        }
        Logger.i("update_statut", "Network is available : FALSE ")
        return false
    }

    fun castToNetworkError(throwable: Throwable): NetworkError {
        val defaultNetworkError = NetworkError(message = throwable.message ?: "Something wrong happened, please retry again...")
        try {

            if (throwable is ConnectException){
                Logger.d(TAG,"ConnectException")
                return NetworkError(0, 0)
            }

            if (throwable !is HttpException) {
                Logger.d(TAG,"is not HttpException")
                return NetworkError(code = 0,message = throwable.message ?: "Something wrong happened, please retry again...")
            }


            val error = fromJson(throwable.response().errorBody()?.string())
            Logger.d(TAG,"error = ${error.message}")

            return NetworkError(throwable.code(), error.response, error.message)

        } catch (e: IOException) {
            Logger.e(TAG, e.toString())
        } catch (e: JsonSyntaxException) {
            Logger.e(TAG, e.toString())
        } catch (e: NullPointerException) {
            Logger.e(TAG, e.toString())
        }
        return defaultNetworkError
    }

    private fun fromJson(json:String?): NetworkError {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(NetworkError::class.java)
        return jsonAdapter.fromJson(json ?: "") ?: NetworkError()

    }
}