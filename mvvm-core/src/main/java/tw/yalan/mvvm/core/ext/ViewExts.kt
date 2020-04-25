package tw.yalan.mvvm.core.ext

import android.content.res.Resources
import androidx.annotation.IntRange
import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by Yalan Ding on 2018/10/22.
 */
fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View?.visibleOrGone(block: () -> Boolean) {
    if (this == null) return

    this.visibility = if (block.invoke()) View.VISIBLE else View.GONE
}

fun View?.visibleOrInvisible(block: () -> Boolean) {
    if (this == null) return

    this.visibility = if (block.invoke()) View.VISIBLE else View.INVISIBLE
}

fun View.onSafeClick(
    @IntRange(from = 1) seconds: Long = 2
    , onError: ((Throwable) -> Unit)? = null
    , onNext: (View) -> Unit
): Disposable? {
    return this.clicks()
        .throttleFirst(seconds, TimeUnit.SECONDS)
        .subscribe({
            onNext.invoke(this)
        }) {
            if (onError != null) {
                onError(it)
            } else {
                throw it
            }
        }
}

fun Float.pxToDp(): Float {
    val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
    return this / (densityDpi / 160f)
}

fun Float.pxToDpInt(): Int {
    val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
    return (this / (densityDpi / 160f)).toInt()
}

fun Float.dpToPx(): Int {
    val density = Resources.getSystem().displayMetrics.density
    return Math.round(this * density)
}

fun Int.pxToDp(): Float {
    val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
    return this / (densityDpi / 160f)
}

fun Int.pxToDpInt(): Int {
    val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
    return (this / (densityDpi / 160f)).toInt()
}

fun Int.dpToPx(): Int {
    val density = Resources.getSystem().displayMetrics.density
    return Math.round(this * density)
}