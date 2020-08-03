package com.mif50.filedownloader

import android.app.Application
import com.mif50.filedownloader.di.component.ApplicationComponent
import com.mif50.filedownloader.di.component.DaggerApplicationComponent
import com.mif50.filedownloader.di.module.ApplicationModule
import com.downloader.PRDownloader

class App: Application() {

    lateinit var applicationComponent: ApplicationComponent

    companion object {
        private const val TAG ="AppFileDownloader"
        private var mInstance: App? = null

        @Synchronized
        fun getInstance(): App {
            if (mInstance == null) mInstance = App()
            return mInstance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        injectDependencies()
        initPRDownloader()
    }



    private fun injectDependencies() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
        applicationComponent.inject(this)
    }

    private fun initPRDownloader(){
        PRDownloader.initialize(applicationContext);
    }

    // Needed to replace the component with a test specific one
    fun setComponent(applicationComponent:ApplicationComponent){
        this.applicationComponent = applicationComponent
    }
}