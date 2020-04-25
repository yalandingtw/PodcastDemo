package tw.yalan.mvvm.core.ext

import tw.yalan.mvvm.core.ui.base.CoreScope
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Yalan Ding on 2020/3/14.
 */
@CoreScope
fun compositeDisposableFactory(): Lazy<CompositeDisposable> {
    return lazy(LazyThreadSafetyMode.NONE) { CompositeDisposable() }
}

