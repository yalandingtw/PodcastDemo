/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package tw.yalan.mvvm.core

import timber.log.Timber

object AppLogger {
    fun init() {
    }
    fun d(s: String?, vararg objects: Any?) {
        if (BuildConfig.DEBUG) {
            Timber.d(s, *objects)
        }
    }

    fun d(
        throwable: Throwable?,
        s: String?,
        vararg objects: Any?
    ) {
        if (BuildConfig.DEBUG) {
            Timber.d(throwable, s, *objects)
        }
    }

    fun i(s: String?, vararg objects: Any?) {
        if (BuildConfig.DEBUG) {
            Timber.i(s, *objects)
        }
    }

    fun i(
        throwable: Throwable?,
        s: String?,
        vararg objects: Any?
    ) {
        if (BuildConfig.DEBUG) {
            Timber.i(throwable, s, *objects)
        }
    }

    fun w(s: String?, vararg objects: Any?) {
        if (BuildConfig.DEBUG) {
            Timber.w(s, *objects)
        }
    }

    fun w(
        throwable: Throwable?,
        s: String?,
        vararg objects: Any?
    ) {
        if (BuildConfig.DEBUG) {
            Timber.w(throwable, s, *objects)
        }
    }

    fun e(s: String?, vararg objects: Any?) {
        if (BuildConfig.DEBUG) {
            Timber.e(s, *objects)
        }
    }

    fun e(
        throwable: Throwable?,
        s: String?,
        vararg objects: Any?
    ) {
        if (BuildConfig.DEBUG) {
            Timber.e(throwable, s, *objects)
        }
    }
}