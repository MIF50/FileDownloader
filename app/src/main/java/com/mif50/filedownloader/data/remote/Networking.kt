package com.mif50.filedownloader.data.remote

import androidx.multidex.BuildConfig
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Networking @Inject constructor() {

    companion object {
        private const val NETWORK_CALL_TIMEOUT = 60
        private const val AUTHORIZATION = "Authorization"
    }

    private fun getClient(
        cacheDir: File,
        cacheSize: Long
    ): OkHttpClient {
        val httpClient = OkHttpClient.Builder().cache(Cache(cacheDir, cacheSize))
        httpClient.addInterceptor {
            val original = it.request()

            val request = original.newBuilder()
                .header("accept", "application/json")

            it.proceed(
                request.build()
            )
        }
        httpClient.addInterceptor(HttpLoggingInterceptor()
            .apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            })

            .readTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
        return httpClient.build()
    }

    fun create(
        baseUrl: String,
        cacheDir: File,
        cacheSize: Long
    ): NetworkService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getClient(cacheDir, cacheSize))
            .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(NetworkService::class.java)
    }

    private fun getMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}