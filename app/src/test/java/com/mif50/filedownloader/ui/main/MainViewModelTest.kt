package com.mif50.filedownloader.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mif50.filedownloader.R
import com.mif50.filedownloader.data.repository.MainRepository
import com.mif50.filedownloader.utils.common.Event
import com.mif50.filedownloader.utils.common.Resource
import com.mif50.filedownloader.utils.file.isFileExists
import com.mif50.filedownloader.utils.network.NetworkHelper
import com.mif50.filedownloader.utils.rx.TestSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.mockito.ArgumentCaptor.*
import org.junit.Assert.*
import org.junit.Rule


@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    // region constants ----------------------------------------------------

    // end region constants -------------------------------------------------


    // region helper fields -----------------------------------------------------

    // end region helper fields --------------------------------------------------


    @get:Rule
    val rule = InstantTaskExecutorRule() // do tasks in the same thread

    @Mock
    private lateinit var networkHelper: NetworkHelper

    @Mock
    private lateinit var mainRepository: MainRepository

    private lateinit var SUT: MainViewModel

    private lateinit var testScheduler: TestScheduler


    @Mock
    private lateinit var loadingInObserver: Observer<Event<Boolean>>

    @Mock
    private lateinit var progressInObserver: Observer<Event<Long>>

    @Mock
    private lateinit var videoNameInObserver: Observer<Event<String>>

    @Mock
    private lateinit var isVideoPathCachedInObserver: Observer<Event<Pair<Boolean, String>>>

    @Mock
    private lateinit var messageStringIdObserver: Observer<Resource<Int>>

    companion object {
        private const val FAKE_DIR_PATH = "/storage/emulated/0/Android/data/com.mif50.filedownloader/files"
        private const val FAKE_URL = "https://bit.ly/30aWVDP"
        private const val FAKE_FILE_NAME = "tignum_x_video.mp4"
    }

    @Before
    fun setup() {
        val compositeDisposable = CompositeDisposable()
        testScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)

        SUT = MainViewModel(testSchedulerProvider,
            compositeDisposable,
            networkHelper,
            mainRepository)

        SUT.loadingLiveData.observeForever(loadingInObserver)
        SUT.progressLiveData.observeForever(progressInObserver)
        SUT.videoNameLiveData.observeForever(videoNameInObserver)
        SUT.isVideoPathCachedLiveData.observeForever(isVideoPathCachedInObserver)
        SUT.messageStringId.observeForever(messageStringIdObserver)

    }

    @Test
    fun downloadFile_noInternetConnection_returnMessageNoInternet(){
        // Arrange
        doReturn(false)
            .`when`(networkHelper)
            .isNetworkConnected()
        // Act
        SUT.downloadFile(FAKE_DIR_PATH)
        // Assert
        assert(SUT.messageStringId.value == Resource.error(R.string.network_connection_error))
        verify(messageStringIdObserver).onChanged(Resource.error(R.string.network_connection_error))
    }

    // User tries to download a file which is already downloaded.
    @Test
    fun downloadFile_fileAlreadyDownloaded_returnMessageFileDownloaded(){
        // Arrange
        doReturn(true)
            .`when`(networkHelper)
            .isNetworkConnected()

        doReturn(false)
            .`when`(SUT)
            .isFileNotExists("$FAKE_DIR_PATH/$FAKE_FILE_NAME")
        // Act
        SUT.downloadFile(FAKE_DIR_PATH)
        // Assert
        assert(SUT.messageStringId.value == Resource.error(R.string.text_file_already_downloaded))
        verify(messageStringIdObserver).onChanged(Resource.error(R.string.text_file_already_downloaded))
    }


    // User tries to download a file which is in downloading progress.

    // User tries to delete a file which is in downloading progress.

    // User tries to delete a file which is paused to download

    // User tries to download multiple files at the same time



    @After
    fun tearDown(){
        SUT.loadingLiveData.removeObserver(loadingInObserver)
        SUT.progressLiveData.removeObserver(progressInObserver)
        SUT.videoNameLiveData.removeObserver(videoNameInObserver)
        SUT.isVideoPathCachedLiveData.removeObserver(isVideoPathCachedInObserver)
        SUT.messageStringId.removeObserver(messageStringIdObserver)
    }


    // region helper methods ---------------------------------------------------

    // end region helper methods -------------------------------------------------

    // region helper classes ------------------------------------------------------

    // end region helper classes ----------------------------------------------------


}