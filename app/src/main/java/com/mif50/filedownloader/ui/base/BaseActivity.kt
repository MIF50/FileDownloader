package com.mif50.filedownloader.ui.base


import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mif50.filedownloader.App
import com.mif50.filedownloader.di.component.ActivityComponent
import com.mif50.filedownloader.di.component.DaggerActivityComponent
import com.mif50.filedownloader.di.module.ActivityModule
import com.mif50.filedownloader.utils.ktx.getLayoutRes
import com.mif50.filedownloader.utils.ktx.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject


/**
 * Reference for generics: https://kotlinlang.org/docs/reference/generics.html
 * Basically BaseActivity will take any class that extends BaseViewModel
 */
abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {

    @Inject
    lateinit var viewModel: VM



    open fun getRootView(): View? {
        val contentViewGroup = findViewById<View>(android.R.id.content) as ViewGroup
        var rootView: View?
        rootView = contentViewGroup.getChildAt(0)
        if (rootView == null) rootView = window.decorView.rootView
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(buildActivityComponent())
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes().layout)
        setupObservers()
        setupView(savedInstanceState)
        viewModel.onCreate()
    }



    override fun onResume() {
        super.onResume()
    }







    private fun buildActivityComponent() =
        DaggerActivityComponent
            .builder()
            .applicationComponent((application as App).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()

    protected open fun setupObservers() {
        viewModel.messageString.observe(this, Observer {
            it.data?.run {
                showMessage(this)
                showError(this)
            }
        })

        viewModel.messageStringId.observe(this, Observer {
            it.data?.run {
                showMessage(this)
                showError(getString(this))
            }
        })


    }


    open fun showSnackBar(@StringRes res: Int) {
        val rootView = getRootView()
        if (rootView != null) Snackbar.make(rootView, res, Snackbar.LENGTH_LONG).show()
    }

    open fun showSnackBar(message: String) {
        val rootView = getRootView()
        if (rootView != null) Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }


    open fun showMessage(message: String) = showSnackBar(message)


    open fun showMessage(@StringRes resId: Int) = showSnackBar(resId)

    open fun showError(message: String){}

    open fun goBack() = onBackPressed()

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStackImmediate()
        else super.onBackPressed()
    }



    // hide keyboard when click out side EditText and clear Focus
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v = currentFocus

        if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            v is EditText && !v.javaClass.name.startsWith("android.webkit.")
        ) {

            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x = ev.rawX + v.left - scrcoords[0]
            val y = ev.rawY + v.top - scrcoords[1]

            if (x < v.left || x > v.right || y < v.top || y > v.bottom)
                this.hideKeyboard(v)
        }
        return super.dispatchTouchEvent(ev)
    }


    protected abstract fun injectDependencies(activityComponent: ActivityComponent)

    protected abstract fun setupView(savedInstanceState: Bundle?)



}