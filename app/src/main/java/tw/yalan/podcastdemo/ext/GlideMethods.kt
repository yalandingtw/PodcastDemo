package tw.yalan.podcastdemo.ext

import androidx.annotation.Px
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import tw.yalan.mvvm.core.ext.dpToPx

/**
 * Created by Yalan Ding on 2020-02-10.
 */
inline fun <reified T : Any> RequestBuilder<T>.corner(@Px radius: Int) : RequestBuilder<T>{
    var requestOptions = RequestOptions()
    requestOptions = requestOptions.centerCrop().transform(RoundedCorners(radius.dpToPx()))
    return this.apply(requestOptions)
}