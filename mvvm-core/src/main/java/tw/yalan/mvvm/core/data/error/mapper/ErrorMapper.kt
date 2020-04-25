package tw.yalan.mvvm.core.data.error.mapper

import tw.yalan.mvvm.core.R
import tw.yalan.mvvm.core.ui.base.CoreScope
import com.yalan.podcast.host.data.error.Error
import tw.yalan.mvvm.core.CoreApp

@CoreScope
class ErrorMapper constructor() : ErrorMapperInterface {

    override fun getErrorString(errorId: Int): String {
        return CoreApp.context.getString(errorId)
    }

    override val errorsMap: Map<Int, String>
        get() = mapOf(
                Pair(Error.NO_INTERNET_CONNECTION, getErrorString(R.string.no_internet)),
                Pair(Error.NETWORK_ERROR, getErrorString(R.string.network_error))
        ).withDefault { getErrorString(R.string.network_error) }
}