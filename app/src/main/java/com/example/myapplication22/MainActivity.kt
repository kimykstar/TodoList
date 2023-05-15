package com.example.myapplication22

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
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
    lateinit var table : LinearLayout
    lateinit var enterBtn : Button
    lateinit var enterText : TextView
    lateinit var completeBtn : Button
    lateinit var myHelper : TodoListDBHelper
    lateinit var initBtn : Button
    lateinit var sqlDB : SQLiteDatabase
    lateinit var todoList : TodoList

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
        todoList = TodoList()
        var textList = ArrayList<TextView>()

        // db helper객체 생성
        myHelper = TodoListDBHelper(this)

        // 텍스트를 가져오고 Todo객체 생성 및 TodoList에 add
        // TextView 생성 및 X버튼, 완료 버튼 생성
        // X버튼과 완료 버튼에 Listener달기
        enterBtn.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            var value = enterText.text.toString()
            sqlDB.execSQL("INSERT INTO todolist VALUES ('" + value + "');")
            sqlDB.close()
            Toast.makeText(applicationContext, "입력 완료", Toast.LENGTH_SHORT)
            // LinearLayout생성
            val ll = LinearLayout(this)
            ll.orientation = LinearLayout.HORIZONTAL
            ll.layoutParams=LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            // TextView 생성
            val content = createTextView(value)

            // Imagebutton생성
            val delBtn = createDelBtn()

            // SucBtn생성
            val successBtn = createSucBtn()

            enterText.text = ""


            ll.addView(delBtn)
            ll.addView(content)
            ll.addView(successBtn)
            table.addView(ll)
        }

        // 리스트 삭제 및 완료 테이블 갱신
        completeBtn.setOnClickListener {

        }

        initBtn.setOnClickListener {
            sqlDB = myHelper.writableDatabase
            myHelper.onUpgrade(sqlDB, 1, 2)
            sqlDB.close()
        }
    }

    fun createTextView(value : String) : TextView{
        val text : TextView = TextView(this)
        text.gravity = Gravity.CENTER
        text.text = value
        text.textSize = 20f

        return text
    }

    fun createDelBtn() : ImageButton{
        val delete : ImageButton = ImageButton(this)
        delete.setImageResource(R.drawable.close)
        delete.scaleType= ImageView.ScaleType.FIT_CENTER
        delete.setBackgroundColor(Color.WHITE)

        return delete
    }

    fun createSucBtn() : Button{
        val sucBtn = Button(this)
        sucBtn.text = "완료"

        return sucBtn
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