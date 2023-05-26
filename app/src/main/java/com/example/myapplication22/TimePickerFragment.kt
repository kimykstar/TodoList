package com.example.myapplication22

import android.app.AlarmManager
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.util.Calendar

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    lateinit var cont : Context
    lateinit var myHelper : TodoListDBHelper
    lateinit var dbService : DBService
    private var message = ""
    var c = Calendar.getInstance()

    fun setContext(context: Context){
        this.cont = context
    }

    fun setMessage(message : String){
        this.message = message;
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 현재 시간을 time picker의 기본 시간으로 설정
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        myHelper = TodoListDBHelper(cont)
        dbService = DBService(myHelper)

        return TimePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    // user에 의해 time이 설정된 경우(시, 분)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // DB에 시, 분을 넣는다.
        Log.i("child : ", message)
        dbService.insertTime(message, hourOfDay, minute)
        startAlarm(c)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startAlarm(c : Calendar){
        var alarmManager : AlarmManager = cont.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(cont, AlarmReceiver::class.java)


    }

    fun calcelAlarm(){

    }

}