package tw.yalan.mvvm.core.ext

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import tw.yalan.mvvm.core.ui.base.BaseFragment


/**
 * Created by Yalan Ding on 2017/7/13.
 */
@SuppressLint("RestrictedApi")
fun FragmentActivity.getLastFragment(): Fragment? {
    val fm = this.supportFragmentManager
    if (fm.fragments != null) {
        (fm.fragments.size - 1 downTo 0)
                .filter { fm.fragments[it] != null }
                .forEach { return fm.fragments[it] }
    }
    return null
}

fun FragmentManager?.getLastFragment(): Fragment? {
    val fm = this
    if (fm?.fragments != null) {
        (fm.fragments.size - 1 downTo 0)
                .filter { fm.fragments[it] != null && fm.fragments[it] is BaseFragment }
                .forEach { return fm.fragments[it] }
    }
    return null
}