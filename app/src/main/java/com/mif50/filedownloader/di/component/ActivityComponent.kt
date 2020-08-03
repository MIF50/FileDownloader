package com.mif50.filedownloader.di.component

import com.mif50.filedownloader.ui.main.MainActivity
import com.mif50.filedownloader.di.ActivityScope
import com.mif50.filedownloader.di.module.ActivityModule
import com.mif50.filedownloader.ui.media.MediaPlayerActivity

import dagger.Component

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [ActivityModule::class]
)
interface ActivityComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: MediaPlayerActivity)


}