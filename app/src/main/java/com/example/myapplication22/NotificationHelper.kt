package com.example.myapplication22

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class NotificationHelper (base: Context?) : ContextWrapper(base){
    private val channelID = "channelID"
    private val channelName = "channelName"

    init{
        // 안드로이드 버전이 오레오거나 이상이면 채널 생성
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //채널
            createChannel()
        }
    }
    // 채널 생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(){
        var channel = NotificationChannel(channelID, channelName,
            NotificationManager.IMPORTANCE_DEFAULT)

        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lightColor = Color.BLACK
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        // NotificationManager에 channel을 설정한다.
        getManager().createNotificationChannel(channel)
    }

    // NotificationManager 생성
    fun getManager() : NotificationManager {
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    // Notification 설정
    fun getChannelNotification(text : String) : NotificationCompat.Builder{
        return NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle("Todo Alarm")
            .setContentText(String.format("%s를 해야할 시간입니다.", text))
            .setSmallIcon(R.drawable.ic_launcher_background)
    }
}