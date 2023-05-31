package com.example.myapplication22

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat


class AlarmReceiver() : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        var text = intent!!.getStringExtra("text").toString()
        var notificationHelper : NotificationHelper = NotificationHelper(context)
        Log.i("notification : ", notificationHelper.toString())
        var nb : NotificationCompat.Builder = notificationHelper.getChannelNotification(text!!)
        // 알림 호출
        notificationHelper.getManager().notify(0, nb.build())
    }
}