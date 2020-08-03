package com.mif50.filedownloader.di.module

import androidx.lifecycle.ViewModelProvider
import com.mif50.filedownloader.data.repository.MainRepository
import com.mif50.filedownloader.ui.base.BaseActivity
import com.mif50.filedownloader.ui.main.MainViewModel
import com.mif50.filedownloader.ui.media.MediaPlayerViewModel
import com.mif50.filedownloader.utils.ViewModelProviderFactory
import com.mif50.filedownloader.utils.network.NetworkHelper
import com.mif50.filedownloader.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class ActivityModule(private val activity: BaseActivity<*>) {


    @Provides
    fun provideSplashViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        mainRepository: MainRepository
    ): MainViewModel = ViewModelProvider(
        activity, ViewModelProviderFactory(MainViewModel::class) {
            MainViewModel(schedulerProvider, compositeDisposable, networkHelper,mainRepository)
        }).get(MainViewModel::class.java)

    @Provides
    fun provideMediaPlayerViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        mainRepository: MainRepository
    ): MediaPlayerViewModel = ViewModelProvider(
        activity, ViewModelProviderFactory(MediaPlayerViewModel::class) {
            MediaPlayerViewModel(schedulerProvider, compositeDisposable, networkHelper,mainRepository)
        }).get(MediaPlayerViewModel::class.java)



}