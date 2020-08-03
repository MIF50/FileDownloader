package com.mif50.filedownloader.utils.common

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class LayoutRes(val layout: Int = 0)