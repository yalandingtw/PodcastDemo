package tw.yalan.podcastdemo.utils

import android.content.Context
import androidx.annotation.ColorRes
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration
import tw.yalan.podcastdemo.ui.epoxy.MarginAttacher

/**
 * Created by Yalan Ding on 2017/7/20.
 */
object RecyclerViewUtils {

    @JvmStatic
    fun createItemDecoration(
        context: Context?,
        marginAttacher: MarginAttacher?,
        @ColorRes color: Int? = null,
        margins: Array<Int>? = null,
        original: Int = androidx.recyclerview.widget.RecyclerView.VERTICAL
    ): androidx.recyclerview.widget.RecyclerView.ItemDecoration {

        val builder =
            if (original == androidx.recyclerview.widget.RecyclerView.VERTICAL) HorizontalDividerItemDecoration.Builder(
                context
            ) else VerticalDividerItemDecoration.Builder(context)

        builder.visibilityProvider { _, _ ->
            return@visibilityProvider false

        }.sizeProvider { position, _ ->
            return@sizeProvider marginAttacher?.attachMargin(position = position) ?: 0
        }
            .colorResId(color ?: android.R.color.transparent)
        if (builder is HorizontalDividerItemDecoration.Builder && margins?.size ?: 0 > 0) {
            builder.margin(margins?.get(0) ?: 0, margins?.get(1) ?: 0)
        }

        return if (builder is HorizontalDividerItemDecoration.Builder) builder.build() else (builder as VerticalDividerItemDecoration.Builder).build()
    }

    @JvmStatic
    fun createItemDecoration(
        context: Context?,
        size: Int = 0,
        @ColorRes color: Int? = null,
        margins: Array<Int>? = null,
        original: Int = androidx.recyclerview.widget.RecyclerView.VERTICAL
    ): androidx.recyclerview.widget.RecyclerView.ItemDecoration {

        val builder =
            if (original == androidx.recyclerview.widget.RecyclerView.VERTICAL) HorizontalDividerItemDecoration.Builder(
                context
            ) else VerticalDividerItemDecoration.Builder(context)

        builder.visibilityProvider({ _, _ ->
            return@visibilityProvider false

        }).sizeProvider { _, _ ->
            return@sizeProvider size
        }
            .colorResId(color ?: android.R.color.transparent)
        if (builder is HorizontalDividerItemDecoration.Builder && margins?.size ?: 0 > 0) {
            builder.margin(margins?.get(0) ?: 0, margins?.get(1) ?: 0)
        }

        return if (builder is HorizontalDividerItemDecoration.Builder) builder.build() else (builder as VerticalDividerItemDecoration.Builder).build()
    }


}