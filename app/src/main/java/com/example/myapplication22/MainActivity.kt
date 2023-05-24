package com.example.myapplication22

import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
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
    lateinit var enterBtn : ImageButton
    lateinit var enterText : TextView
    lateinit var myHelper : TodoListDBHelper
    lateinit var initBtn : Button
    lateinit var sqlDB : SQLiteDatabase
    lateinit var todoList : TodoList
    lateinit var dbService : DBService

    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    val imgBtnParams = LinearLayout.LayoutParams(
        80,
        LinearLayout.LayoutParams.MATCH_PARENT,
    )

    val textParam = LinearLayout.LayoutParams(
        700,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        date = findViewById(R.id.date)
        date.text = dateText.format(formatter)
        table = findViewById(R.id.table)
        enterBtn = findViewById(R.id.enter)
        enterText = findViewById(R.id.inputText) // 리스트 입력칸
        initBtn = findViewById(R.id.initBtn)
        todoList = TodoList()
        // db helper객체 생성
        myHelper = TodoListDBHelper(this)
        dbService = DBService(myHelper)

        createList(applicationContext)



        // 텍스트를 가져오고 Todo객체 생성 및 TodoList에 add
        // TextView 생성 및 X버튼, 완료 버튼 생성
        // X버튼과 완료 버튼에 Listener달기
        enterBtn.setOnClickListener {
            var value = enterText.text.toString()
            if(value.length > 0){
                // 할일 중복
                try{
                    dbService.insertTodo(value)
                    Toast.makeText(applicationContext, "입력 완료", Toast.LENGTH_SHORT)
                    // LinearLayout생성
                    var ll = LinearLayout(this)
                    ll = createTodo(value)
                    table.addView(ll)
                    enterText.text = ""
                }catch(e : SQLException){
                    Toast.makeText(applicationContext, "이미 등록된 항목입니다!", Toast.LENGTH_SHORT)
                }
            }else{
                Toast.makeText(applicationContext, "할 일을 입력해주세요!", Toast.LENGTH_SHORT)
            }
        }


        initBtn.setOnClickListener {
            var builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("초기화").setMessage("해야할 목록을 초기화 하시겠습니까?")
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                sqlDB = myHelper.writableDatabase
                myHelper.onUpgrade(sqlDB, 1, 2)
                table.removeAllViews()
            })
        }
    }

    fun createTextView(value : String) : TextView{
        val text : TextView = TextView(this)
        text.gravity = Gravity.CENTER
        text.setTextColor(Color.BLACK)
        text.text = value
        text.textSize = 20f
        text.layoutParams = textParam

        return text
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createDelBtn(value : String) : ImageButton{
        val delete : ImageButton = ImageButton(this)
        delete.setImageResource(R.drawable.close)
        delete.scaleType= ImageView.ScaleType.FIT_CENTER
        delete.setBackgroundResource(R.drawable.imgbtn_layout)
        delete.layoutParams = imgBtnParams
        delete.setOnClickListener{
            //sql 삭제문
            var group = delete.parent as ViewGroup
            var t_view = group.getChildAt(1) as TextView
            var tt = t_view.text.toString()
            var dbService = DBService(myHelper)
            dbService.deleteTodo(tt)
            // x버튼의 부모 레이아웃을 ViewGroup으로 받아 안에 속한 View들 모두 삭제
            group.removeAllViews()
        }
        return delete
    }

    fun createSucBtn(value : String) : Button{
        val sucBtn = Button(this)
        sucBtn.setBackgroundColor(Color.BLACK)
        sucBtn.setTextColor(Color.WHITE)
        sucBtn.textSize = 15f
        sucBtn.text = "완료"
        return sucBtn
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTodo(value : String) : LinearLayout {
        val ll = LinearLayout(this)
        val btn = createDelBtn(value)
        val text = createTextView(value)
        val sucbtn = createSucBtn(value)
        ll.orientation=LinearLayout.HORIZONTAL
        layoutParams.setMargins(10,30, 10, 0)
        ll.layoutParams = layoutParams
        ll.setBackgroundResource(R.drawable.border_layout)
        ll.addView(btn)
        ll.addView(text)
        ll.addView(sucbtn)


        return ll
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createList(context : Context) {
        table = findViewById(R.id.table)
        var todoList = dbService.getAllTodo()
        val iter = todoList.iterator()
        while(iter.hasNext()){
            table.addView(createTodo(iter.next().getTodo()))
        }

    }

}