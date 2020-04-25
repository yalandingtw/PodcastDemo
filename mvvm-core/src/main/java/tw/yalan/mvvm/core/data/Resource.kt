package tw.yalan.mvvm.core.data

import tw.yalan.mvvm.core.ui.base.CoreScope

// A generic class that contains data and status about loading this data.
@CoreScope
sealed class Resource<T>(
        val data: T? = null,
        val errorCode: Int? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class DataError<T>(errorCode: Int) : Resource<T>(null, errorCode)
}