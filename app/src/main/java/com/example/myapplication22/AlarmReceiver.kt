package com.example.myapplication22

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


class AlarmReceiver() : BroadcastReceiver(){

    lateinit var notificationManager : NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {
        var text = intent!!.getStringExtra("text")
        var notificationHelper : NotificationHelper = NotificationHelper(context)

        var nb : NotificationCompat.Builder = notificationHelper.getChannelNotification(text!!)

        // 알림 호출
        notificationHelper.getManager().notify(1, nb.build())
    }


}