package tw.yalan.mvvm.core.ext

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Created by Yalan Ding on 2020/3/14.
 */

inline fun <reified T : Activity> Activity.startActivity(
    bundle: Bundle? = null,
    flags: Int? = null
) {
    startActivity(Intent(this, T::class.java).apply {
        if (bundle != null) putExtras(bundle)
        if (flags != null) setFlags(flags)
    })
}


inline fun <reified T : Activity> Fragment.startActivity(
    bundle: Bundle? = null,
    flags: Int? = null
) {
    activity?.startActivity(Intent(activity, T::class.java).apply {
        if (bundle != null) putExtras(bundle)
        if (flags != null) setFlags(flags)
    })
}