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

    fun setContext(context: Context){
        this.cont = context
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 현재 시간을 time picker의 기본 시간으로 설정
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        myHelper = TodoListDBHelper(cont)
        dbService = DBService(myHelper)

        return TimePickerDialog(activity, activity as TimePickerDialog.OnTimeSetListener, hour, minute, DateFormat.is24HourFormat(activity))
    }

}