package com.mif50.filedownloader.utils.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.mif50.filedownloader.utils.common.LayoutRes

fun Activity.getLayoutRes(): LayoutRes {

    val annotation = this::class.java.annotations.find { it is LayoutRes } as? LayoutRes
    if (annotation != null) {
        return annotation
    } else {
        throw KotlinNullPointerException("Please add the LayoutRes annotation at the top of the class")
    }
}

inline fun <reified T : Any> Activity.startActivity() {
    startActivity(Intent(this, T::class.java))
}

inline fun <reified T : Any> Context.startActivity() {
    startActivity(Intent(this, T::class.java))
}

inline fun <reified T : Any> Context.startActivityWithClearTask() {
    startActivity(Intent(this, T::class.java).addClearFlags())
}

inline fun <reified T : Any> Activity.startActivity(data: Intent) {
    startActivity(Intent(this, T::class.java).putExtras(data))
}

inline fun <reified T : Any> Context.startActivity(data: Intent) {
    startActivity(Intent(this, T::class.java).putExtras(data))
}

inline fun <reified T : Any> Context.startActivityWithClearTask(data: Intent) {
    startActivity(Intent(this, T::class.java).putExtras(data).addClearFlags())
}

inline fun <reified T : Any> Activity.startActivityForResult(requestCode: Int) {
    startActivityForResult(Intent(this, T::class.java), requestCode)
}

inline fun <reified T : Any> Activity.startActivityForResult(data: Intent, requestCode: Int) {
    startActivityForResult(Intent(this, T::class.java).putExtras(data), requestCode)
}

fun Intent.addClearFlags() = addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)



fun Activity?.hideKeyboard( v: View) {
    if (this != null && this.window != null) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
        v.clearFocus()
    }
}