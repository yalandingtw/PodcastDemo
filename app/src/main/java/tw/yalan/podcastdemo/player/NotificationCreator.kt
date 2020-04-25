package tw.yalan.podcastdemo.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import tw.yalan.mvvm.core.data.dto.Episode
import tw.yalan.podcastdemo.Keys
import tw.yalan.podcastdemo.R


/**
 * Created by Yalan Ding on 2020/4/13.
 */
object NotificationCreator {
    const val CHNNAL_ID = "audio_player_channel"
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            val channelName = "撥放器通知面板"
            val importance = NotificationManager.IMPORTANCE_NONE
            val notificationChannel =
                NotificationChannel(CHNNAL_ID, channelName, importance)
            notificationManager?.createNotificationChannel(notificationChannel
                .apply {
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0L)
                })
        }
    }

    fun createEmptyNotification(
        context: Context,
        sessionToken: MediaSessionCompat.Token?
    ): Notification {
        return NotificationCompat.Builder(context, CHNNAL_ID)
            .setContentTitle("播放標題")
            .setContentText("播放內容")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(null)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(sessionToken)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context, PlaybackStateCompat.ACTION_STOP
                        )
                    )
            )
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context, PlaybackStateCompat.ACTION_STOP
                )
            )
            .build()
    }

    fun createNotification(
        context: Context,
        sessionToken: MediaSessionCompat.Token?,
        episode: Episode?,
        isPlaying: Boolean = false,
        bitmap: Bitmap? = null
    ): Notification? {
        if (episode != null) {
            val builder =
                NotificationCompat.Builder(context, CHNNAL_ID).setContentTitle(episode.podcastName)
                    .setContentText(
                        episode.title
                            ?: ""
                    )
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setVibrate(null)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(
                        R.drawable.exo_controls_previous,
                        "",
                        createPendingIntent(context, IPCManager.LAST)
                    )
                    .addAction(
                        R.drawable.exo_controls_rewind,
                        "",
                        createPendingIntent(context, IPCManager.REWIND)
                    )
                    .apply {
                        if (!isPlaying) {
                            addAction(
                                R.drawable.exo_controls_play,
                                "",
                                createPendingIntent(context, IPCManager.PLAY)
                            )
                        } else {
                            addAction(
                                R.drawable.exo_controls_pause,
                                "",
                                createPendingIntent(context, IPCManager.PAUSE)
                            )
                        }
                    }
                    .addAction(
                        R.drawable.exo_controls_fastforward,
                        "",
                        createPendingIntent(context, IPCManager.FASTFORWARD)
                    )
                    .addAction(
                        R.drawable.exo_controls_next,
                        "",
                        createPendingIntent(context, IPCManager.NEXT)
                    )
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setLargeIcon(bitmap)
                    .setContentIntent(getLaunchIntent(context, episode))
                    .setDeleteIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context, PlaybackStateCompat.ACTION_STOP
                        )
                    )
            androidx.media.app.NotificationCompat.MediaStyle(builder)
                .setMediaSession(sessionToken)
                .setShowCancelButton(true)
                .setShowActionsInCompactView(1, 2, 3)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_STOP
                    )
                )
            return builder.build()
        } else {
            return null
        }
    }

    private fun createPendingIntent(context: Context, action: Int): PendingIntent {
        val intent = Intent(context, AudioService::class.java)
        intent.putExtras(Bundle().apply {
            putInt(IPCManager.KEY_ACTION, action)
        })
        return PendingIntent.getService(context, action, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getLaunchIntent(context: Context, episode: Episode?): PendingIntent? {
        return context.packageManager?.getLaunchIntentForPackage(context.packageName)
            ?.let { sessionIntent ->
                sessionIntent.putExtras(Bundle().apply {
                    episode?.run {
                        putString(Keys.EXTRA_LAUNCH_ACTION, Keys.LAUNCH_ACTION_OPEN_DETAIL)
                    }
                })

                PendingIntent.getActivity(
                    context,
                    0,
                    sessionIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
    }
}