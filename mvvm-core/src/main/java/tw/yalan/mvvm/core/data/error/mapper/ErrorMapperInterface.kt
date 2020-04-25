package tw.yalan.mvvm.core.data.error.mapper

import tw.yalan.mvvm.core.ui.base.CoreScope

@CoreScope
interface ErrorMapperInterface {
    fun getErrorString(errorId: Int): String
    val errorsMap: Map<Int, String>
}