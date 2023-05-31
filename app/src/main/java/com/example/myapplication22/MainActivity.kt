package com.example.myapplication22

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener{
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
    lateinit var clickBtn : Button
    var alarm : ArrayList<PendingIntent> = ArrayList<PendingIntent>()
    var id = 0
    var todoId = 0

    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    val imgBtnParams = LinearLayout.LayoutParams(
        80,
        LinearLayout.LayoutParams.MATCH_PARENT,
    )

    val timeText = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    val alarmBtnParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    val textParam = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    val linearParam = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
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
                    ll = createTodo(value, todoId++)
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

//                listViews.removeAll(listViews)
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

    // FrameLayout or textview or deleteBtn
    fun findAlarmId(view : View, value : String) : Int{
        var layout = view.parent.parent.parent as LinearLayout
        var size = layout.size

        for(i in 0..size){
            var temp = (((layout.getChildAt(i) as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(1) as TextView).text
            if(value.equals(temp))
                return i
        }
        return -1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createDelBtn(value : String, todoId : Int) : ImageButton{
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
                group.removeView(group)
                (group.parent as LinearLayout).removeAllViews()
//                var id = findAlarmId(delete, tt)
                cancelAlarm(value, todoId)
            })
            builder.setNegativeButton("취소", null)
            builder.show()

        }
        return delete
    }

    fun createSucBtn(value : String, todoId: Int) : Button{
        val sucBtn = Button(this)
        sucBtn.setBackgroundColor(Color.BLACK)
        sucBtn.setTextColor(Color.WHITE)
        sucBtn.layoutParams = alarmBtnParams
        sucBtn.textSize = 15f
        sucBtn.text = "알람"

        sucBtn.setOnClickListener {
            // Timpicker Dialog를 이용하여 시간을 선택
            var t_fragment = TimePickerFragment()
            t_fragment.setContext(this)
            clickBtn = sucBtn
            id = todoId
            t_fragment.show(supportFragmentManager, "timePicker")
        }
        return sucBtn
    }

    fun createAlarmCancelBtn(value : String, todoId: Int) : Button{
        var cancelBtn = Button(this)
        cancelBtn.setBackgroundColor(Color.BLACK)
        cancelBtn.setTextColor(Color.WHITE)
        cancelBtn.text = "알람 해제"
        dbService.getTime(value)
        // db에 시간이 설정되어있지 않다면 invisible
        cancelBtn.setOnClickListener {var builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("알람 해제").setMessage(value + "알람을 해제하시겠습니까?")
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                var id = findAlarmId(cancelBtn.parent as FrameLayout, value)
                cancelAlarm(value, todoId)
                cancelBtn.visibility = GONE
                var sucBtn = (cancelBtn.parent as FrameLayout).getChildAt(0) as Button
                sucBtn.visibility = VISIBLE
                ((sucBtn.parent.parent.parent as LinearLayout).getChildAt(1)as TextView).text = ""
                Toast.makeText(applicationContext, "알람이 해제되었습니다.", Toast.LENGTH_LONG).show()
            })
            builder.setNegativeButton("취소", null)
            builder.show()
        }

        return cancelBtn
    }

    fun createFrameLayout(sucbtn : Button, cancelBtn : Button, value : String) : FrameLayout{
        val frame = FrameLayout(this)
        frame.layoutParams = linearParam
        var time = dbService.getTime(value)
        var times = time.split(" ")
        Log.i("hour", times[0])
        Log.i("minute", times[1])
        // 알람이 설정되지 않은 경우
        if(times[0].equals("-1") && times[1].equals("-1")){
            sucbtn.visibility = VISIBLE
            cancelBtn.visibility = INVISIBLE

        }else{
            sucbtn.visibility = INVISIBLE
            cancelBtn.visibility = VISIBLE
        }
        frame.addView(sucbtn)
        frame.addView(cancelBtn)

        return frame
    }

    fun createTimeText(value : String) : TextView{
        val timeTextView = TextView(this)

        var time = dbService.getTime(value).split(" ")
        if(!(time[0].equals("-1") && time[1].equals("-1"))){
            timeTextView.text = String.format("%s시 %s분 알람이 예약되었습니다.", time[0], time[1])
        }

        return timeTextView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTodo(value : String, todoId : Int) : LinearLayout {
        val ll = LinearLayout(this)
        val btn = createDelBtn(value, todoId)
        val text = createTextView(value)
        val cancelBtn = createAlarmCancelBtn(value, todoId)
        val timeText = createTimeText(value) // 알람 시간을 보여주기 위한 text
        val linear = LinearLayout(this) // 목록의 전체 linearlayout

        val sucbtn = createSucBtn(value, todoId)
        val frame = createFrameLayout(sucbtn, cancelBtn, value)
        linear.orientation=LinearLayout.VERTICAL
        ll.orientation=LinearLayout.HORIZONTAL
        layoutParams.setMargins(10,30, 10, 0)
        linearParam.setMargins(-150, 0, 0, 0)
        ll.layoutParams = layoutParams
        ll.setBackgroundResource(R.drawable.border_layout)
        ll.addView(btn)
        ll.addView(text)
        ll.addView(frame)
        linear.addView(ll)
        linear.addView(timeText)

        return linear
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createList(context : Context) {
        table = findViewById(R.id.table)
        var todoList = dbService.getAllTodo()
        val iter = todoList.iterator()

        while(iter.hasNext()){
            table.addView(createTodo(iter.next().getTodo(), todoId++))
        }
    }

    private fun startAlarm(c: Calendar, text : String, todoId: Int) {
        // 알람매니저 선언
        var alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("text", text)
        var pendingIntent = PendingIntent.getBroadcast(this, todoId, intent, FLAG_MUTABLE)
        alarm.add(pendingIntent)
        Log.i("id", id.toString())

        // 설정 시간이 현재시간 이후라면 설정
        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE, 1)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)

    }

    private fun cancelAllAlarm(){
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        var iterator = alarm.iterator()
        while(iterator.hasNext()){
            alarmManager.cancel(iterator.next())
        }

    }


    private fun cancelAlarm(value : String, alarmId : Int){
        var alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getBroadcast(this,alarmId, intent,PendingIntent.FLAG_MUTABLE)
        }else{
            PendingIntent.getBroadcast(this, alarmId, intent,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmManager.cancel(pendingIntent)
//        id--
        dbService.insertTime(value, -1, -1)
    }

    // 시간 설정 시
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        var c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        val group = clickBtn.parent as FrameLayout

        val text = ((group.parent as LinearLayout).getChildAt(1) as TextView).text

        ((group.parent.parent as LinearLayout).getChildAt(1) as TextView).text = String.format("%d시 %d분 알람이 예약되었습니다.", hourOfDay, minute)

        (group.getChildAt(0) as Button).visibility = View.INVISIBLE
        (group.getChildAt(1) as Button).visibility = View.VISIBLE

        // DB에 시, 분을 넣는다.
        dbService.insertTime(text as String, hourOfDay, minute)

        Toast.makeText(this, String.format("%s를 %d시 %d분에 알람을 설정하였습니다.", text, hourOfDay, minute), Toast.LENGTH_LONG).show()

        startAlarm(c, text.toString(), id)
    }
}