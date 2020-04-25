package tw.yalan.podcastdemo.player

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.Player
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import tw.yalan.mvvm.core.data.dto.Episode


/**
 * Copyright (C) 2016 Alan Ding
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Created by Alan Ding on 2020/02/09.
 */
class AudioService : MediaBrowserServiceCompat(), Player.EventListener,
    CoroutineScope by MainScope() {

    private lateinit var controller: ExoPlayerController
    private var currentBitmapState: Pair<String, Bitmap>? = null
    private var currentNotification: Notification? = null
    private var lastTime: Long = 0L

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot("/", null)
    }

    override fun onCreate() {
        super.onCreate()
        NotificationCreator.createNotificationChannel(this)
        controller = ExoPlayerController.getInstance()
        sessionToken = controller.mSession.sessionToken
        currentNotification = NotificationCreator.createEmptyNotification(this, sessionToken)

        startForeground(1, currentNotification)
        stopForeground(true)
        controller.episode
            .observeForever {
                val timeId = System.currentTimeMillis()
                lastTime = timeId
                launch(Dispatchers.Default) {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                            as NotificationManager?
                    val notification = withContext(Dispatchers.IO) {
                        updateNotification(it)
                    }
                    if (timeId == lastTime) {
                        currentNotification = notification
                        val hasNotify = currentNotification != null
                        val isPlaying = controller.isPlaying.value == true

                        if (hasNotify) notificationManager?.notify(1, currentNotification)
                        if (hasNotify && isPlaying) {
                            startForeground(1, currentNotification)
                        } else if (controller.mSession.controller.playbackState.state == PlaybackStateCompat.STATE_STOPPED) {
                            stopForeground(false)
                        }
                    }
                }
            }
        controller.isPlaying
            .observeForever {
                val timeId = System.currentTimeMillis()
                lastTime = timeId
                if (controller.episode.value != null) {
                    launch(Dispatchers.Default) {

                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                                as NotificationManager?
                        val notification = withContext(Dispatchers.IO) {
                            updateNotification(controller.episode.value)
                        }

                        if (timeId == lastTime) {
                            currentNotification = notification
                            val hasNotify = currentNotification != null
                            val isPlaying = controller.isPlaying.value == true

                            if (hasNotify) notificationManager?.notify(1, currentNotification)
                            if (hasNotify && isPlaying) {
                                startForeground(1, currentNotification)
                            } else if (controller.isPlaying.value == false
                                && controller.mSession.controller.playbackState.state != PlaybackStateCompat.STATE_BUFFERING
                            ) {
                                stopForeground(false)
                            }
                        }
                    }
                }
            }
    }

    private fun updateNotification(episode: Episode?): Notification? {
        if (episode != null) {
            // Prepare bitmap for notification, if the image already existed in cache, then will skip pull image from internet.
            val bitmap =
                if (currentBitmapState != null && currentBitmapState?.first == episode.contentUrl ?: "") {
                    currentBitmapState!!.second
                } else {
                    Picasso.get().load(episode.albumUrl).get()
                }
            currentBitmapState = (episode.contentUrl ?: "") to bitmap
            val isPlaying = controller.isPlaying?.value == true
            return NotificationCreator.createNotification(
                this,
                sessionToken,
                episode,
                isPlaying,
                bitmap
            )

        } else {
            return NotificationCreator.createNotification(
                this,
                sessionToken,
                episode
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.let {
            val action = it.getInt(IPCManager.KEY_ACTION)
            val msg = Message()
            msg.what = action

            when (action) {
                IPCManager.PLAY -> {
                    controller.play()
                }
                IPCManager.PAUSE -> {
                    controller.pause()
                }
                IPCManager.DESTROY -> {
                    controller.destroy()
                }
                IPCManager.ADD_TO_PLAYLIST -> {
                    msg.data.classLoader = Episode::class.java.classLoader
                    it.getParcelable<Episode>(IPCManager.KEY_EPISODE)?.let { data ->
                        controller.addToPlayList(data, false)
                    }
                }
                IPCManager.NEXT -> {
                    controller.next()
                }
                IPCManager.LAST -> {
                    controller.last()
                }
                IPCManager.PLAY_EPISODE -> {
                    msg.data.classLoader = Episode::class.java.classLoader
                    it.getParcelable<Episode>(IPCManager.KEY_EPISODE)?.let { data ->
                        controller.playEpisode(data)
                    }
                }
                IPCManager.REWIND -> {
                    controller.rewind()
                }
                IPCManager.FASTFORWARD -> {
                    controller.fastForward()
                }
                else -> {
                    return START_NOT_STICKY
                }
            }

        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        controller.mSession?.run {
            isActive = false
            release()
        }
        controller.destroy()
    }


    companion object {
        private const val TAG = "SoundService"

    }
}