package tw.yalan.podcastdemo

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/**
 * Created by Yalan Ding on 2020-02-08.
 */
@GlideModule
open class AppModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
    }
    override fun applyOptions(context: Context, builder: GlideBuilder) {

    }
}