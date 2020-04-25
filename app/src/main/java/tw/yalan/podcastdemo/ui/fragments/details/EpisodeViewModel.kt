package tw.yalan.podcastdemo.ui.fragments.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tw.yalan.podcastdemo.usecase.PodcastUsecaseImpl
import tw.yalan.mvvm.core.data.Resource
import tw.yalan.mvvm.core.data.dto.Episode
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastDetailResponse
import tw.yalan.mvvm.core.ext.Event
import tw.yalan.mvvm.core.ui.base.BaseViewModel
import tw.yalan.podcastdemo.App

/**
 * Created by Yalan Ding on 2020/4/23.
 */
class EpisodeViewModel(private val usecase: PodcastUsecaseImpl) : BaseViewModel() {


    val detailResponse: LiveData<Resource<GetPodcastDetailResponse>?> =
        usecase.podcastDetailResponse
    var changeToPlayer: MutableLiveData<Event<Episode>?> =
        MutableLiveData<Event<Episode>?>().apply { postValue(null) }
    var currentEpisode: MutableLiveData<Episode?> = MutableLiveData()

    fun reloadDetail() {
        usecase.reloadPodcastDetail("id"/* fake id */)
    }

    fun updateCurrentEpisode(episodeId: String?) {
        val currentEpisodeObject =
            detailResponse.value?.data?.data?.collection?.episodes?.find { it.contentUrl == episodeId }
        fillContentFromPodcast(currentEpisodeObject)
        currentEpisode.postValue(currentEpisodeObject)
    }


    fun clickItem(episode: Episode) {
        if (currentEpisode.value == episode) {
            // If the clicked episode is the currently playing episode, user is going to the PlayerFragment.
            changeToPlayer.postValue(Event(episode))
        }
    }

    private fun fillContentFromPodcast(episode: Episode?): Episode? {
        if (episode == null) return null
        return episode.apply {
            val detail = detailResponse.value?.data?.data?.collection
            albumUrl = detail?.artworkUrl600
            podcastName = detail?.collectionName
        }

    }

    fun playEpisode(episode: Episode) {
        fillContentFromPodcast(episode)
        App.get().playerServiceManager.playEpisode(episode)
    }

    fun pauseEpisode() = App.get().playerServiceManager.pause()
    fun resumeEpisode() = App.get().playerServiceManager.play()
    fun fastForward() = App.get().playerServiceManager.fastForward()
    fun rewind() = App.get().playerServiceManager.rewind()
    fun seekToTimeBarPosition(position: Long) = App.get().playerServiceManager.seekTo(position)

}