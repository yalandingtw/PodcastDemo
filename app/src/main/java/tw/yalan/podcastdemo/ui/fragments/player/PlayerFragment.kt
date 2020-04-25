package tw.yalan.podcastdemo.ui.fragments.player

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.util.Util
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_player.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import tw.yalan.mvvm.core.AppLogger
import tw.yalan.mvvm.core.data.dto.Episode
import tw.yalan.mvvm.core.ext.getLastFragment
import tw.yalan.mvvm.core.ext.observe
import tw.yalan.mvvm.core.ui.base.BaseFragment
import tw.yalan.podcastdemo.App
import tw.yalan.podcastdemo.R
import tw.yalan.podcastdemo.ext.corner
import tw.yalan.podcastdemo.player.ExoPlayerController
import tw.yalan.podcastdemo.ui.fragments.details.EpisodeViewModel
import java.util.concurrent.TimeUnit

/**
 * Created by Yalan Ding on 2020/2/7.
 */
class PlayerFragment : BaseFragment() {

    private val viewModel: EpisodeViewModel by sharedViewModel()
    private var disposable: Disposable? = null
    private val formatBuilder = java.lang.StringBuilder()
    private val formatter = java.util.Formatter(formatBuilder, java.util.Locale.getDefault())
    private var canFetch: Boolean = false
        get() = isVisible
        private set
    private var scrubbing = false

    private val onScrubListener = object : TimeBar.OnScrubListener {

        override fun onScrubMove(timeBar: TimeBar, position: Long) {
            tvPosition?.text = getProgressText(position)
        }

        override fun onScrubStart(timeBar: TimeBar, position: Long) {
            scrubbing = true
            tvPosition?.text = getProgressText(position)
        }

        override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
            scrubbing = false
            if (!canceled) {
                viewModel.seekToTimeBarPosition(position)
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_player

    override fun observeViewModel() {
        observe(viewModel.currentEpisode, ::handleEpisodeChanged)
        observe(
            App.get().playerServiceManager.musicServiceConnection.playbackState,
            ::handleOnPlaybackStateChanged
        )
    }

    private fun handleOnPlaybackStateChanged(playbackStateCompat: PlaybackStateCompat?) {
        if (playbackStateCompat?.state == PlaybackStateCompat.STATE_PLAYING) {
            btnCenter.setBackgroundResource(R.drawable.ic_control_pause)
        } else {
            btnCenter.setBackgroundResource(R.drawable.ic_control_play)
        }
    }


    override fun afterViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar(toolbar = toolbar, showArrow = true)
        btnRewind.setOnClickListener { viewModel.rewind() }
        btnFastForward.setOnClickListener { viewModel.fastForward() }
        btnCenter.setOnClickListener {
            val state =
                App.get().playerServiceManager.musicServiceConnection.playbackState.value?.state
            if (state == PlaybackStateCompat.STATE_PLAYING) {
                viewModel.pauseEpisode()
            } else {
                viewModel.resumeEpisode()
            }
        }
        timeBar.addListener(onScrubListener)
        AppLogger.e("$this created : $viewModel}")

    }

    private fun handleEpisodeChanged(episode: Episode?) {
        Glide.with(App.get()).load(episode?.albumUrl).override(600, 600).corner(2)
            .into(ivAlbum)
        tvName.text = episode?.title ?: ""

        startFetchPosition()
    }

    private fun onTimeBarStateChanged(state: TimeBarState) {
        if (canFetch) {
            timeBar.setDuration(state.totalDuration)
            timeBar.setPosition(state.position)
            timeBar.setBufferedPosition(state.bufferedDuration)
            tvTotal.text = getProgressText(state.totalDuration)
            if (!scrubbing) tvPosition.text = getProgressText(state.position)
        }
    }

    private fun startFetchPosition() {
        if (disposable?.isDisposed == false) disposable?.dispose()

        disposable = Observable.interval(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                val player = ExoPlayerController.getInstance().player
                val totalDuration = player.duration
                val currentPosition = player.currentPosition
                TimeBarState(totalDuration, currentPosition, player.bufferedPosition)
            }
            .subscribe({
                onTimeBarStateChanged(it)
            }) {
                onTimeBarStateChanged(TimeBarState(0, 0, 0))
                AppLogger.e(it, "Try to fetch position error.")
            }
        disposable?.addDisposeQueue()
    }

    override fun onResume() {
        super.onResume()
        startFetchPosition()
    }

    override fun onDestroyView() {
        timeBar?.removeListener(onScrubListener)

        super.onDestroyView()
    }

    private fun getProgressText(position: Long): String? {
        return Util.getStringForTime(
            formatBuilder,
            formatter,
            position
        )
    }

    companion object {
        const val TAG = "PlayerFragment"

        private data class TimeBarState(
            var totalDuration: Long = 0L,
            var position: Long = 0L,
            var bufferedDuration: Long = 0L
        )

        fun newInstance(): PlayerFragment {
            return PlayerFragment()
        }

        fun launch(fragmentManager: FragmentManager?) {
            fragmentManager?.beginTransaction()
                ?.apply {
                    if (fragmentManager.findFragmentByTag(TAG) != null) {
                        replace(R.id.container, newInstance(), TAG)
                    } else {
                        add(R.id.container, newInstance(), TAG)
                    }

                    val lastFragment = fragmentManager.getLastFragment()
                    if (lastFragment != null && lastFragment.isAdded && lastFragment !is PlayerFragment) {
                        hide(lastFragment)
                    }
                    addToBackStack(TAG)
                }
                ?.commit()
        }
    }


}