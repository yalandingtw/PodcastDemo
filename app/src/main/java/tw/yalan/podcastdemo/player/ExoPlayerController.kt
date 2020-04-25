package tw.yalan.podcastdemo.player

import android.app.PendingIntent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Pair
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import tw.yalan.mvvm.core.data.dto.Episode
import tw.yalan.mvvm.core.ext.Event
import tw.yalan.podcastdemo.App
import tw.yalan.podcastdemo.Keys


/**
 * Created by Yalan Ding on 2020/2/1.
 */
class ExoPlayerController : Player.EventListener {

    var player: ExoPlayer
    private val episodeLiveData: MutableLiveData<Episode> = MutableLiveData()
    private lateinit var source: ConcatenatingMediaSource
    private val playingLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        postValue(false)
    }

    val isPlaying: LiveData<Boolean> = playingLiveData
    var episode: LiveData<Episode?> = episodeLiveData
    val windowChangedLiveData: MutableLiveData<Event<kotlin.Pair<Episode?, Episode?>>> =
        MutableLiveData()
    var mSession: MediaSessionCompat
    var mediaSessionConnector: MediaSessionConnector
    var mediaControllerCompat: MediaControllerCompat
    var handler = Handler()
    var updateProgressAction = ::updateProgress
    var positionManager = PositionManager()

    companion object {
        // For Singleton instantiation.
        @Volatile
        private var instance: ExoPlayerController? = null
        private const val MAX_UPDATE_INTERVAL_MS = 1000L
        private const val MIN_UPDATE_INTERVAL_MS = 200L
        private const val DEFAULT_FORWARD_DURATION = 30000
        private const val DEFAULT_REWIND_DURATION = 30000

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ExoPlayerController()
                .also { instance = it }
        }
    }

    init {
        // setup player.
        player = SimpleExoPlayer.Builder(App.context).build()
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
        val playbackParameters = PlaybackParameters( /* speed= */1f, 1f, true)
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.setPlaybackParameters(playbackParameters)
        player.audioComponent?.setAudioAttributes(audioAttributes, true)
        player.addListener(this)
        // setup media session.
        val packageManager = App.context.packageManager
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(App.context.packageName)
                ?.let { sessionIntent ->
                    PendingIntent.getActivity(App.context, 0, sessionIntent, 0)
                }
        mSession = MediaSessionCompat(App.context, "AudioService")
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }
        val controlDispatcher = EpisodeControlDispatcher()
        mediaControllerCompat = MediaControllerCompat(App.context, mSession)
        mediaSessionConnector = MediaSessionConnector(mSession)
        mediaSessionConnector.setQueueNavigator(EpisodeQueueNavigator(mSession))
        mediaSessionConnector.setControlDispatcher(controlDispatcher)
        mediaSessionConnector.setPlaybackPreparer(EpisodePlaybackPreparer())
        mediaSessionConnector.setQueueEditor(EpisodeQueueEditor())
        mediaSessionConnector.setFastForwardIncrementMs(DEFAULT_FORWARD_DURATION)
        mediaSessionConnector.setRewindIncrementMs(DEFAULT_REWIND_DURATION)
        mediaSessionConnector.setPlayer(player)

        mediaSessionConnector.setErrorMessageProvider {
            it.printStackTrace()
            Pair<Int, String>(1, it.message ?: "")
        }

    }

    fun setPlaybackParams(speed: Float?, skipSilence: Boolean?) {
        val currentSkipSilence = player.playbackParameters.skipSilence
        val currentSpeed = player.playbackParameters.speed
        val hasChanged = (skipSilence != null && currentSkipSilence != skipSilence)
                || (speed != null && currentSpeed != speed)
        if (hasChanged) {
            val playbackParameters = PlaybackParameters( /* speed= */speed ?: currentSpeed
                , 1f
                , skipSilence ?: currentSkipSilence
            )
            player.setPlaybackParameters(playbackParameters)
        }
    }

    fun addToPlayList(episode: Episode, prepare: Boolean): Boolean {
        return addToPlayList(episode, null, prepare)
    }

    fun addToPlayList(episode: Episode, index: Int?, prepare: Boolean): Boolean {
        if (containsInPlayList(episode)) {
            return false
        }
        val path = episode.contentUrl
        if (path != null) {
            val uri = Uri.parse(path)
            val userAgent = Util.getUserAgent(App.context, App.context.packageName)

            // Default parameters, except allowCrossProtocolRedirects is true
            val httpDataSourceFactory = DefaultHttpDataSourceFactory(
                userAgent,
                null /* listener */,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true /* allowCrossProtocolRedirects */
            )
            val dataSourceFactory = DefaultDataSourceFactory(
                App.context,
                null, httpDataSourceFactory
            )
            // This is the MediaSource representing the media to be played.
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).setTag(episode)
                .createMediaSource(uri)
            // Plays the first video, then the second video.
            if (!::source.isInitialized) {
                source = ConcatenatingMediaSource()
            }
            if (index == null) {
                source.addMediaSource(mediaSource)
            } else {
                source.addMediaSource(index, mediaSource)
            }
            if (prepare) {
                player.prepare(source)
            }
        }
        return true
    }


    private fun containsInPlayList(episode: Episode): Boolean {
        //Source have not been created yet.
        if (!::source.isInitialized)
            return false

        for (i in 0 until source.size) {
            if ((source.getMediaSource(i).tag as Episode).contentUrl == episode.contentUrl) {
                return true
            }
        }
        return false
    }

    fun playEpisode(episode: Episode) {
        player.stop()
        if (::source.isInitialized)
            source.clear()
        addToPlayList(episode, false)
        play()
    }

    fun play() {
        if (playingLiveData.value == true) {
            return
        }
        if (!::source.isInitialized) {
            return
        }
        if (source.size == 0) {
            return
        }
        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare(source)
        } else if (player.playbackState == Player.STATE_ENDED) {
            player.seekTo(player.currentWindowIndex, C.TIME_UNSET)
        }
        player.playWhenReady = true
    }

    fun pause() {
        player.playWhenReady = false
        player.playbackState
    }

    fun stop() {
        player.playWhenReady = false
        player.playbackState
    }

    fun next() {
        if (player.hasNext()) {
            player.next()
        }
    }

    fun last() {
        if (player.hasPrevious()) {
            player.previous()
        }
    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {
        super.onTracksChanged(trackGroups, trackSelections)
        player.seekToDefaultPosition()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        error.printStackTrace()
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        super.onLoadingChanged(isLoading)
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        super.onTimelineChanged(timeline, reason)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)

        when (playbackState) {
            Player.STATE_ENDED -> {
            }
            Player.STATE_READY -> {
                updateProgress()
                val currentEpisode = player.currentTag as Episode
                episodeLiveData.value = currentEpisode
            }
            Player.STATE_BUFFERING -> {
                updateProgress()
            }
            Player.STATE_IDLE -> {
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        updateProgress()
        playingLiveData.value = isPlaying
    }

    override fun onPositionDiscontinuity(reason: Int) {
        super.onPositionDiscontinuity(reason)
        val lastEpisode = episodeLiveData.value
        val currentEpisode = player.currentTag as Episode?
        if (lastEpisode != currentEpisode) {

            windowChangedLiveData.postValue(Event(lastEpisode to currentEpisode))
        }
        episodeLiveData.value = currentEpisode
    }

    fun destroy() {

    }

    fun rewind() {
        mediaSessionConnector.setRewindIncrementMs(DEFAULT_REWIND_DURATION)
    }

    fun fastForward() {
        mediaSessionConnector.setFastForwardIncrementMs(DEFAULT_FORWARD_DURATION)
    }

    private fun updateProgress() {
        handler.removeCallbacks(updateProgressAction)
        val playbackState = player.playbackState
        // Cancel any pending updates and schedule a new one if necessary.
        if (player.isPlaying) {
            val position = player.contentPosition
            val duration = player.duration
            val id = (player.currentTag as? Episode)?.contentUrl
            id?.let {
                positionManager.putPosition(id, position)
                positionManager.putDuration(id, duration)
            }

            var mediaTimeDelayMs = MAX_UPDATE_INTERVAL_MS
            // Limit delay to the start of the next full second to ensure position display is smooth.
            val mediaTimeUntilNextFullSecondMs: Long = 1000 - position % 1000
            mediaTimeDelayMs = Math.min(mediaTimeDelayMs, mediaTimeUntilNextFullSecondMs)
            // Calculate the delay until the next update in real time, taking playbackSpeed into account.
            val playbackSpeed = player.playbackParameters.speed
            var delayMs =
                if (playbackSpeed > 0) (mediaTimeDelayMs / playbackSpeed).toLong() else MAX_UPDATE_INTERVAL_MS
            // Constrain the delay to avoid too frequent / infrequent updates.
            delayMs = Util.constrainValue(delayMs, MIN_UPDATE_INTERVAL_MS, MAX_UPDATE_INTERVAL_MS)
            handler.postDelayed(updateProgressAction, delayMs)
        } else if (playbackState != Player.STATE_ENDED && playbackState != Player.STATE_IDLE) {
            handler.postDelayed(updateProgressAction, MAX_UPDATE_INTERVAL_MS)
        }
    }

    class PositionManager() {
        var positionHolder = HashMap<String, Long>()
        var durationHolder = HashMap<String, Long>()

        fun putPosition(key: String, position: Long) {
            positionHolder[key] = position
            durationHolder[key] = position
        }

        fun putDuration(key: String, duration: Long) {
            durationHolder[key] = duration
        }

        fun getPosition(key: String) = positionHolder[key]
        fun getDuration(key: String) = durationHolder[key]

        fun clear() {
            positionHolder.clear()
            durationHolder.clear()
        }
    }

    inner class EpisodeQueueNavigator(session: MediaSessionCompat) :
        TimelineQueueNavigator(session) {
        val TAG = "EpisodeQueueNavigator"
        override fun onCommand(
            player: Player,
            controlDispatcher: ControlDispatcher,
            command: String,
            extras: Bundle,
            cb: ResultReceiver
        ): Boolean {
            return super.onCommand(player, controlDispatcher, command, extras, cb)
        }

        override fun getSupportedQueueNavigatorActions(player: Player): Long {
            return super.getSupportedQueueNavigatorActions(player)
        }

        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            val episode =
                player.currentTimeline.getWindow(windowIndex, Timeline.Window()).tag as Episode
            return MediaDescriptionCompat.Builder()
                .setMediaId(episode.contentUrl)
                .setTitle(episode.podcastName ?: "TITLE")
                .setDescription(episode.desc ?: "DESCRIPTION")
                .setSubtitle(episode.title ?: "SUBTITLE")
                .setIconUri(Uri.parse(episode.albumUrl ?: ""))
                .build()
        }

    }

    inner class EpisodeControlDispatcher : ControlDispatcher {
        val TAG = "EpisodeControlDispatcher"
        override fun dispatchSeekTo(player: Player, windowIndex: Int, positionMs: Long): Boolean {
            player.seekTo(windowIndex, positionMs)
            return true
        }

        override fun dispatchSetShuffleModeEnabled(
            player: Player,
            shuffleModeEnabled: Boolean
        ): Boolean {
            player.shuffleModeEnabled = shuffleModeEnabled
            return true
        }

        override fun dispatchSetPlayWhenReady(player: Player, playWhenReady: Boolean): Boolean {
            if (playWhenReady) play()
            else pause()
            return true
        }

        override fun dispatchSetRepeatMode(player: Player, repeatMode: Int): Boolean {
            return true
        }

        override fun dispatchStop(player: Player, reset: Boolean): Boolean {
            player.stop(reset)
            return true
        }
    }

    inner class EpisodePlaybackPreparer : MediaSessionConnector.PlaybackPreparer {
        val TAG = "EpisodePlaybackPreparer"
        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle) {
        }

        override fun onCommand(
            player: Player,
            controlDispatcher: ControlDispatcher,
            command: String,
            extras: Bundle,
            cb: ResultReceiver
        ): Boolean {
            return false
        }

        override fun getSupportedPrepareActions(): Long {
            return (PlaybackStateCompat.ACTION_PREPARE
                    or PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID
                    or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)
        }

        override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle) {
            if (mediaId.isNotEmpty()) {
                val episode = extras.getParcelable<Episode>(IPCManager.KEY_EPISODE)
                episode?.let {
                    if (playWhenReady) {
                        playEpisode(episode)
                    }
                }
            }
        }

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle) {

        }

        override fun onPrepare(playWhenReady: Boolean) {
        }

    }

    inner class EpisodeQueueEditor : MediaSessionConnector.QueueEditor {
        val TAG = "EpisodeQueueEditor"
        override fun onCommand(
            player: Player,
            controlDispatcher: ControlDispatcher,
            command: String,
            extras: Bundle,
            cb: ResultReceiver
        ): Boolean {
            return false
        }

        override fun onRemoveQueueItem(player: Player, description: MediaDescriptionCompat) {
        }

        override fun onAddQueueItem(player: Player, description: MediaDescriptionCompat) {
            description.extras?.getParcelable<Episode>(IPCManager.KEY_EPISODE)?.let {
                val prepare = description.extras?.getBoolean(IPCManager.KEY_PREPARE) ?: false
                addToPlayList(it, prepare)
            }
        }

        override fun onAddQueueItem(
            player: Player,
            description: MediaDescriptionCompat,
            index: Int
        ) {
            description.extras?.getParcelable<Episode>(IPCManager.KEY_EPISODE)?.let {
                val prepare = description.extras?.getBoolean(IPCManager.KEY_PREPARE) ?: false
                addToPlayList(it, index, prepare)
            }
        }

    }
}