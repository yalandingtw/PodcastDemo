package tw.yalan.podcastdemo.ui.epoxy

import android.view.View
import com.airbnb.epoxy.EpoxyController
import tw.yalan.mvvm.core.data.dto.Podcast
import tw.yalan.podcastdemo.ui.epoxy.model.PodcastItemEpoxyModel_

/**
 * Created by Yalan Ding on 2020/4/23.
 */
class PodcastDashboardController(var callback: Callback? = null) : EpoxyController() {

    interface Callback {
        fun onClickItem(view: View, position: Int, data: Podcast)
    }

    var data: List<Podcast> = emptyList()

    override fun buildModels() {
        data.forEachIndexed { index, podcast ->
            add(
                PodcastItemEpoxyModel_().id(podcast.id ?: index.toString())
                    .imageUrl(podcast.artworkUrl100)
                    .title(podcast.name)
                    .artistName(podcast.artistName)
                    .onClickItem { v ->
                        callback?.onClickItem(v, index, podcast)
                    }
            )
        }

    }

}