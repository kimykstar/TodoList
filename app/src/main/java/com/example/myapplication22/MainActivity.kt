package com.example.myapplication22

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.marginTop
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    lateinit var date : TextView
    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    @RequiresApi(Build.VERSION_CODES.O)
    var dateText : LocalDate = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    lateinit var table : TableLayout
    lateinit var enterBtn : Button
    lateinit var enterText : TextView
    lateinit var completeBtn : Button
    lateinit var myHelper : TodoListDBHelper
    lateinit var initBtn : Button
    lateinit var sqlDB : SQLiteDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        date = findViewById(R.id.date)
        date.text = dateText.format(formatter)
        table = findViewById(R.id.table)
        enterBtn = findViewById(R.id.enter)
        enterText = findViewById(R.id.inputText) // 리스트 입력칸
        completeBtn = findViewById(R.id.complete)
        initBtn = findViewById(R.id.initBtn)
        var todoList = ArrayList<RadioButton>()
        var rButton = ArrayList<RadioButton>()

        // db helper객체 생성
        myHelper = TodoListDBHelper(this)

        // todolist테이블을 참조하여 테이블 정보를 가져와 생성

        enterBtn.setOnClickListener {
//            val row = TableRow(this)
//            val radioBtn = RadioButton(this)
//            if(enterText.text.length > 0){
//                radioBtn.text = enterText.text
//                enterText.text = ""
//                radioBtn.textSize=25f
//                todoList.add(radioBtn)
//                row.addView(radioBtn)
//                table.addView(row)
//            }
        }

        // 리스트 삭제 및 완료 테이블 갱신
        completeBtn.setOnClickListener {
//            var size = todoList.size
//            for(i in size - 1 downTo 0){
//                if(todoList[i].isChecked == true){
//                    var row : TableRow = todoList[i].parent as TableRow
//                    var table : LinearLayout = row.parent as LinearLayout
//                    row.removeView(todoList[i])
//                    table.removeView(row)
//                    todoList.remove(todoList[i])
//                }
//            }
        }

        initBtn.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            myHelper.onUpgrade(sqlDB, 1, 2)
            sqlDB.close()
        }
    }

    fun createList() : ArrayList<String> {
        var todolist : ArrayList<String> = ArrayList<String>()
        sqlDB = myHelper.readableDatabase
        var cursor: Cursor
        cursor = sqlDB.rawQuery("SELECT * FROM todolist;", null)

        while(cursor.moveToNext()){
            todolist.add(cursor.getString(0))
        }

        return todolist
    }





}