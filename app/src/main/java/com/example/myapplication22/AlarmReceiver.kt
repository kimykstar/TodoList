package com.example.myapplication22

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver(){

    private lateinit var manager : NotificationManager
    private lateinit var builder : NotificationCompat.Builder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                "channel",
                "channel1",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        builder = NotificationCompat.Builder(context, "channel");

        val intent2 = Intent(context, AlarmService::class.java)
        val requestCode = intent?.extras!!.getInt("alarm_rqCode")
        val title = intent.extras!!.getString("content")

        val pendingIntent = if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            PendingIntent.getActivity(context,requestCode,intent2,PendingIntent.FLAG_IMMUTABLE); //Activity를 시작하는 인텐트 생성
        }else {
            PendingIntent.getActivity(context,requestCode,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
        }

        val notification = builder.setContentTitle(title)
            .setContentText("SCHEDULE MANAGER")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(1, notification)
    }


}