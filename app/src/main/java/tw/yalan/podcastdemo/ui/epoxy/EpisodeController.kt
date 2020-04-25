package tw.yalan.podcastdemo.ui.epoxy

import android.view.View
import com.airbnb.epoxy.EpoxyController
import tw.yalan.mvvm.core.data.dto.Episode
import tw.yalan.podcastdemo.ui.epoxy.model.EpisodeItemEpoxyModel_

/**
 * Created by Yalan Ding on 2020/4/23.
 */
class EpisodeController(var callback: Callback? = null) : EpoxyController() {

    interface Callback {
        fun onClickItem(view: View, position: Int, data: Episode)
        fun onClickButton(view: View, position: Int, data: Episode, currentPlayEpisode: Boolean)
    }

    var data: List<Episode> = emptyList()
    var currentPlayingUrl: String? = null
    var playing: Boolean = false
    override fun buildModels() {
        data.forEachIndexed { index, episode ->
            add(
                EpisodeItemEpoxyModel_()
                    .id(episode.contentUrl ?: index.toString())
                    .playing(episode.contentUrl == currentPlayingUrl && playing)
                    .title(episode.title)
                    .onClickItem { v ->
                        callback?.onClickItem(v, index, episode)
                    }
                    .onClickButton { v ->
                        callback?.onClickButton(
                            v,
                            index,
                            episode,
                            episode.contentUrl == currentPlayingUrl
                        )
                    }

            )
        }

    }

    fun setCurrentEpisodeId(episodeId: String?) {
        currentPlayingUrl = episodeId
        requestModelBuild()
    }

    fun setPlayingState(playing: Boolean) {
        this.playing = playing
        requestModelBuild()
    }

}