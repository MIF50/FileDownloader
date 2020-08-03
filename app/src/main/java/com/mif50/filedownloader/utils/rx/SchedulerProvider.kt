package com.mif50.filedownloader.utils.rx

import io.reactivex.Scheduler
import javax.inject.Singleton

@Singleton
interface SchedulerProvider {

    fun ui(): Scheduler

    fun computation(): Scheduler

    fun io(): Scheduler

}