package tw.yalan.podcastdemo.ui.fragments.details

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_details.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import tw.yalan.mvvm.core.AppLogger
import tw.yalan.mvvm.core.data.Resource
import tw.yalan.mvvm.core.data.dto.Episode
import tw.yalan.mvvm.core.data.dto.Podcast
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastDetailResponse
import tw.yalan.mvvm.core.ext.Event
import tw.yalan.mvvm.core.ext.dpToPx
import tw.yalan.mvvm.core.ext.getLastFragment
import tw.yalan.mvvm.core.ext.observe
import tw.yalan.mvvm.core.ui.base.BaseFragment
import tw.yalan.podcastdemo.App
import tw.yalan.podcastdemo.Keys
import tw.yalan.podcastdemo.R
import tw.yalan.podcastdemo.ext.corner
import tw.yalan.podcastdemo.ui.epoxy.EpisodeController
import tw.yalan.podcastdemo.ui.fragments.player.PlayerFragment
import tw.yalan.podcastdemo.utils.RecyclerViewUtils

/**
 * Created by Yalan Ding on 2020/4/24.
 */
class DetailsFragment : BaseFragment(), EpisodeController.Callback {

    private val viewModel: EpisodeViewModel by sharedViewModel()
    private val controller: EpisodeController by inject { parametersOf(this) }

    override val layoutId: Int = R.layout.fragment_details


    override fun afterViewCreated(view: View, savedInstanceState: Bundle?) {
        controller.callback = this
        setupToolbar(toolbar = toolbar, showArrow = true)
        recyclerView.addItemDecoration(
            RecyclerViewUtils.createItemDecoration(
                context,
                color = R.color.colorSubText,
                size = 1.dpToPx()
            )
        )
        recyclerView.adapter = controller.adapter
        AppLogger.e("$this created : $viewModel}")
    }

    override fun observeViewModel() {
        observe(viewModel.detailResponse, ::handleDetailResponse)
        observe(
            App.get().playerServiceManager.musicServiceConnection.nowPlaying,
            ::handleNowPlaying
        )

        observe(
            App.get().playerServiceManager.musicServiceConnection.playbackState,
            ::handlePlaybackState
        )

        observe(viewModel.changeToPlayer, ::handleNavigationEvent)
    }

    private fun handleNavigationEvent(event: Event<Episode>?) {
        event?.getContentIfNotHandled()?.let {
            PlayerFragment.launch(parentFragmentManager)
        }
    }

    private fun handlePlaybackState(playbackStateCompat: PlaybackStateCompat?) {
        playbackStateCompat?.let {
            val playing = it.state == PlaybackStateCompat.STATE_PLAYING
            controller.setPlayingState(playing)
        }
    }


    private fun handleDetailResponse(resource: Resource<GetPodcastDetailResponse>?) {
        when (resource) {
            is Resource.Loading -> {
                progress.visibility = View.VISIBLE
                tvLabelAllEpisodes.visibility = View.GONE
            }
            is Resource.Success<GetPodcastDetailResponse> -> {
                val detail = resource.data?.data?.collection

                progress.visibility = View.GONE
                tvLabelAllEpisodes.visibility = View.VISIBLE
                controller.data = detail?.episodes ?: emptyList()
                controller.requestModelBuild()
                viewModel.updateCurrentEpisode(App.get().playerServiceManager.getCurrentEpisodeId())

                Glide.with(App.get()).load(detail?.artworkUrl100).override(100, 100).corner(2)
                    .into(ivAlbum)
                tvName.text = detail?.collectionName ?: ""
                tvArtist.text = detail?.artistName ?: ""
            }
            is Resource.DataError -> {
                progress.visibility = View.GONE
                tvLabelAllEpisodes.visibility = View.VISIBLE
                controller.data = emptyList()
                controller.requestModelBuild()

            }
        }
    }

    private fun handleNowPlaying(mediaMetadataCompat: MediaMetadataCompat?) {
        if (mediaMetadataCompat != null) {
            val episodeId = mediaMetadataCompat.description.mediaId
            controller.setCurrentEpisodeId(episodeId) /* episodeId means content url */
            viewModel.updateCurrentEpisode(episodeId)
        } else {
            controller.setCurrentEpisodeId(null) /* episodeId means content url */
            viewModel.updateCurrentEpisode(null)
        }

    }

    override fun onClickItem(view: View, position: Int, data: Episode) {
        viewModel.clickItem(data)
    }

    override fun onClickButton(
        view: View,
        position: Int,
        data: Episode,
        currentPlayEpisode: Boolean
    ) {
        if (!currentPlayEpisode) {
            Toast.makeText(context, "Preparing to play....please wait.", Toast.LENGTH_LONG).show()
            viewModel.playEpisode(data)
        } else {
            val playing = App.get().playerServiceManager.isPlaying()
            if (playing) {
                viewModel.pauseEpisode()
            } else {
                viewModel.resumeEpisode()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.reloadDetail()
    }

    companion object {
        const val TAG = "DetailsFragment"
        fun newInstance(podcast: Podcast): DetailsFragment {
            return DetailsFragment().apply {
                arguments = Bundle().apply { putParcelable(Keys.EXTRA_PODCAST, podcast) }
            }
        }

        fun launch(fragmentManager: FragmentManager?, podcast: Podcast) {
            fragmentManager?.beginTransaction()
                ?.apply {
                    if (fragmentManager.findFragmentByTag(TAG) != null) {
                        replace(R.id.container, newInstance(podcast), TAG)
                    } else {
                        add(R.id.container, newInstance(podcast), TAG)
                    }

                    val lastFragment = fragmentManager.getLastFragment()
                    if (lastFragment != null && lastFragment.isAdded && lastFragment !is DetailsFragment) {
                        hide(lastFragment)
                    }
                    addToBackStack(TAG)
                }
                ?.commit()
        }
    }
}