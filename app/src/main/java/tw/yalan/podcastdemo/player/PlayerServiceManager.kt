package tw.yalan.podcastdemo.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import tw.yalan.mvvm.core.data.dto.Episode
import tw.yalan.podcastdemo.App
import tw.yalan.podcastdemo.player.common.MusicServiceConnection


/**
 * Created by Alan Ding on 2020/2/2.
 */
class PlayerServiceManager constructor(val context: Context) {

    var calledStartService = false
    var musicServiceConnection: MusicServiceConnection =
        MusicServiceConnection(context, ComponentName(context, AudioService::class.java))

    fun startService() {
        val intent = Intent(context, AudioService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        calledStartService = true
    }

    fun getCurrentEpisodeId(): String? =
        musicServiceConnection.nowPlaying.value?.description?.mediaId

    fun isPlaying(): Boolean =
        musicServiceConnection.playbackState.value?.state == PlaybackState.STATE_PLAYING

    fun playEpisode(episode: Episode) {
        musicServiceConnection.transportControls.playFromMediaId(
            episode.contentUrl,
            Bundle().apply {
                putInt(IPCManager.KEY_ACTION, IPCManager.PLAY_EPISODE)
                putParcelable(IPCManager.KEY_EPISODE, episode)
            })
    }

    fun play() {
        musicServiceConnection.transportControls.play()
    }

    fun pause() {
        musicServiceConnection.transportControls.pause()
    }

    fun next() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun previous() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun fastForward() {
        musicServiceConnection.transportControls.fastForward()
    }

    fun rewind() {
        musicServiceConnection.transportControls.rewind()
    }

    fun seekTo(position: Long) {
        musicServiceConnection.transportControls.seekTo(position)
    }
}