package com.mif50.filedownloader.ui.media

import androidx.lifecycle.MutableLiveData
import com.mif50.filedownloader.data.repository.MainRepository
import com.mif50.filedownloader.ui.base.BaseViewModel
import com.mif50.filedownloader.utils.common.Event
import com.mif50.filedownloader.utils.network.NetworkHelper
import com.mif50.filedownloader.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class MediaPlayerViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val mainRepository: MainRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val videoPathLiveData: MutableLiveData<Event<String>> = MutableLiveData()

    override fun onCreate() {
     videoPathLiveData.postValue(Event(mainRepository.getVideoPath()))
    }


}