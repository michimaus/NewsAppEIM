package com.example.newsappeim

import android.R
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.*
import android.app.NotificationManager

import android.app.NotificationChannel

import android.app.PendingIntent


class NotificationService : Service() {
    var counter = 0
    lateinit var notification: Notification
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startMyOwnForeground()
        } else {
            startForeground(1, Notification())
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {

        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val channelName = "Background Service"

        val targetIntent = Intent(this.applicationContext, MainAppActivity::class.java)

//        val contentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_MUTABLE)
////            PendingIntent.getActivity(this, 0, targetIntent, 0)
//        } else {
//            TODO("VERSION.SDK_INT < S")
//        }

        val contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_MUTABLE)


        notificationChannel =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationChannel.enableVibration(true)

//
        notificationManager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        notificationManager.createNotificationChannel(notificationChannel)

        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder = notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.ic_menu_more)
            .setContentTitle("Notification Title")
            .setContentText("Extra extra!! News incoming!!")
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setCategory(Notification.CATEGORY_PROMO)

        notification = notificationBuilder.build()

        notificationManager.notify(0, notificationBuilder.build())


//        val mBuilder = NotificationCompat.Builder(this, "notify_001")
//        val ii = Intent(this.applicationContext, MainAppActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, ii, 0)
//
//        val bigText = NotificationCompat.BigTextStyle()
//        bigText.bigText("verseurl")
//        bigText.setBigContentTitle("Today's Bible Verse")
//        bigText.setSummaryText("Text in detail")
//
//        mBuilder.setContentIntent(pendingIntent)
//        mBuilder.setSmallIcon(R.drawable.ic_dialog_alert)
//        mBuilder.setContentTitle("Your Title")
//        mBuilder.setContentText("Your text")
//        mBuilder.setStyle(bigText)
//
//        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = "Your_channel_id"
//            val channel = NotificationChannel(
//                channelId,
//                "Channel human readable title",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//            mBuilder.setChannelId(channelId)
//        }
//
//        notificationManager.notify(0, mBuilder.build())


    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        stopTimerTask()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestartManager::class.java)
//        this.sendBroadcast(broadcastIntent)
    }

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                Log.i("Count", "=========  " + counter++)

                if (counter % 5 == 0 && Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    Log.i("Notification", "yeeee")

                    notificationManager.notify(0, notificationBuilder.build())

                }
            }
        }
        timer!!.schedule(timerTask, 1000, 1000) //
    }

    private fun stopTimerTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}