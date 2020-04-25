package com.yalan.podcast.host.data.error

import tw.yalan.mvvm.core.ui.base.CoreScope

/**
 * Created by Yalan Ding on 02/01/2020
 */

@CoreScope
class Error(val code: Int, val description: String) {
    constructor(exception: Exception) : this(code = DEFAULT_ERROR, description = exception.message
            ?: "")

    companion object {
        const val NO_INTERNET_CONNECTION = -1
        const val NETWORK_ERROR = -2
        const val DEFAULT_ERROR = -3
        const val DATABASE_ERROR = -4
    }
}