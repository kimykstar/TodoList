package com.example.myapplication22

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerFragment() : DialogFragment(){
    lateinit var cont : Context
    lateinit var myHelper : TodoListDBHelper
    lateinit var dbService : DBService
//    private var message = ""

    fun setContext(context: Context){
        this.cont = context
    }
//
//    fun setMessage(message : String){
//        this.message = message;
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 현재 시간을 time picker의 기본 시간으로 설정
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        myHelper = TodoListDBHelper(cont)
        dbService = DBService(myHelper)

        return TimePickerDialog(activity, activity as TimePickerDialog.OnTimeSetListener, hour, minute, DateFormat.is24HourFormat(activity))
    }

//    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
//        // DB에 시, 분을 넣는다.
////        dbService.insertTime(message, hourOfDay, minute)
//
//        var c = Calendar.getInstance()
//        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
//        c.set(Calendar.MINUTE, minute)
//        c.set(Calendar.SECOND, 0)
//
//        val group = btn.parent as ViewGroup
//        var textView = group.getChildAt(1) as TextView
//
////        -------------------------
//        // listViews를 이용해서 text읽어오기 추가
//        var text = textView.text
////        ---------------------------
//        Toast.makeText(cont, String.format("%s를 %d시 %d분에 알람을 설정하였습니다.", text, hourOfDay, minute), Toast.LENGTH_SHORT)
////        ------------------------
////        알람버튼을 알람 해제 버튼과 알람 시간 띄우기
//        //        -------------------------
//        // 알람 설정
//        startAlarm(c)
//    }

//    private fun startAlarm(c: Calendar) {
//// 알람매니저 선언
//        var alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        var intent = Intent(this, AlarmReceiver::class.java)
//
//        var pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)
//
//        // 설정 시간이 현재시간 이후라면 설정
//        if(c.before(Calendar.getInstance())){
//            c.add(Calendar.DATE, 1)
//        }
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
//    }


}