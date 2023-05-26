package com.example.myapplication22

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(),  TimePickerDialog.OnTimeSetListener {
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
    lateinit var dbService : DBService

    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    val imgBtnParams = LinearLayout.LayoutParams(
        80,
        LinearLayout.LayoutParams.MATCH_PARENT,
    )

    val alarmBtnParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    val textParam = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
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
            builder.setNegativeButton("취소", null)
            builder.show()
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
            var builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("목록 삭제").setMessage("'" + tt + "' 를 삭제하시겠습니까?")
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                var dbService = DBService(myHelper)
                dbService.deleteTodo(tt)
                // x버튼의 부모 레이아웃을 ViewGroup으로 받아 안에 속한 View들 모두 삭제
                group.removeAllViews()
            })
            builder.setNegativeButton("취소", null)
            builder.show()

        }
        return delete
    }

    fun createSucBtn(value : String) : Button{
        val sucBtn = Button(this)
        sucBtn.setBackgroundColor(Color.BLACK)
        sucBtn.setTextColor(Color.WHITE)
        alarmBtnParams.setMargins(-80, 0, 0, 0)
        sucBtn.layoutParams = alarmBtnParams
        sucBtn.textSize = 15f
        sucBtn.text = "알람"
        sucBtn.setOnClickListener {
            // Timpicker Dialog를 이용하여 시간을 선택
            var t_fragment = TimePickerFragment()
            t_fragment.setContext(this)
//            t_fragment.setMessage(value)
            t_fragment.show(supportFragmentManager, "timePicker")
        }
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

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // DB에 시, 분을 넣는다.
//        dbService.insertTime(message, hourOfDay, minute)

        var c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        Toast.makeText(applicationContext, String.format("%d시 %d분에 알람을 설정하였습니다.", hourOfDay, minute), Toast.LENGTH_SHORT)

        // 알람 설정
        startAlarm(c)
    }

    private fun startAlarm(c: Calendar) {
        // 알람매니저 선언
        var alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var intent = Intent(this, AlarmReceiver::class.java)

        var pendingIntent = PendingIntent.getBroadcast(this, 1, intent, FLAG_MUTABLE)

        // 설정 시간이 현재시간 이후라면 설정
        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE, 1)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }

}