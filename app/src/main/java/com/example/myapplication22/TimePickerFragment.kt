package com.example.myapplication22

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    lateinit var cont : Context
    lateinit var myHelper : TodoListDBHelper
    lateinit var dbService : DBService
    private var message = ""

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
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // DB에 시, 분을 넣는다.
        Log.i("child : ", message)
        dbService.insertTime(message.toString(), hourOfDay, minute)

    }

}