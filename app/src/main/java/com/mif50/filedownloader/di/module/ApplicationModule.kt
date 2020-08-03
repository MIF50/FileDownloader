package com.mif50.filedownloader.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.mif50.filedownloader.App
import com.mif50.filedownloader.BuildConfig
import com.mif50.filedownloader.data.remote.NetworkService
import com.mif50.filedownloader.data.remote.Networking
import com.mif50.filedownloader.di.ApplicationContext
import com.mif50.filedownloader.utils.network.NetworkHelper
import com.mif50.filedownloader.utils.rx.RxSchedulerProvider
import com.mif50.filedownloader.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: App) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(): Context = application.applicationContext

    /**
     * Since this function do not have @Singleton then each time CompositeDisposable is injected
     * then a new instance of CompositeDisposable will be provided
     */
    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider = RxSchedulerProvider()

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences =
        application.getSharedPreferences("file-downloader-prefs", Context.MODE_PRIVATE)


    @Provides
    @Singleton
    fun provideNetworkService(): NetworkService =
        Networking().create(
            BuildConfig.BASE_URL,
            application.cacheDir,
            10 * 1024 * 1024 // 10MB
        )

    @Singleton
    @Provides
    fun provideNetworkHelper(): NetworkHelper = NetworkHelper(application)
}