package com.example.myapplication22

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
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

    // 위젯 동적생성을 위한 layout설정들
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
        // todo입력 후 enter버튼 클릭 시
        // 1. db에 할 일을 insert한다.
        // 2. Todo하나를 만들어 ScrollView의 LinearLayout에 넣는다.
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
                    Log.i("count : ", table.childCount.toString())
                    enterText.text = ""
                }catch(e : SQLException){
                    Toast.makeText(applicationContext, "이미 등록된 항목입니다!", Toast.LENGTH_SHORT)
                }
            }else{
                Toast.makeText(applicationContext, "할 일을 입력해주세요!", Toast.LENGTH_SHORT)
            }
        }

        // 모두 지우기 버튼 클릭 시
        // 1. dialog상자 생성
        // 2. 확인 누를 시 데이터베이스에서 테이블을 삭제하여 데이터 초기화
        // 3. cancelAllAlarm메소드를 통해 모든 알람 삭제
        // 4. Todo목록 위젯을 모두 삭제한다.
        initBtn.setOnClickListener {
            var builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("초기화").setMessage("해야할 목록을 초기화 하시겠습니까?")
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                sqlDB = myHelper.writableDatabase
                myHelper.onUpgrade(sqlDB, 1, 2)
                // 알람을 해제 후 모든 뷰를 지워야 참조가 됨
                cancelAllAlarm()
                table.removeAllViews()
            })
            builder.setNegativeButton("취소", null)
            builder.show()
        }
    }

    // TextView생성(할일을 입력하기 위한TextView)
    fun createTextView(value : String) : TextView{
        val text : TextView = TextView(this)
        text.gravity = Gravity.CENTER
        text.setTextColor(Color.BLACK)
        text.text = value
        text.textSize = 20f
        text.layoutParams = textParam

        return text
    }

    // Todo의 왼쪽에 있는 X이미지 버튼으로 누르면 목록을 삭제하고 알람을 자동으로 해제한다.
    @RequiresApi(Build.VERSION_CODES.O)
    fun createDelBtn(value : String) : ImageButton{
        // ImageButton 생성 및 속성 설정
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
            // 목록 삭제여부를 확인하기 위한 Alert Dialog생성
            var builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("목록 삭제").setMessage("'" + tt + "' 를 삭제하시겠습니까?")
            // 삭제 확인 시 db에서 알람 취소 및 자식 위젯들 제거
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                var dbService = DBService(myHelper)
                dbService.deleteTodo(tt)
                // x버튼의 부모 레이아웃을 ViewGroup으로 받아 안에 속한 View들 모두 삭제
                group.removeAllViews()
                group.removeView(group)
                cancelAlarm(value, getOrder(group.parent as LinearLayout))
                (group.parent as LinearLayout).removeAllViews()
            })
            builder.setNegativeButton("취소", null)
            builder.show()

        }
        return delete
    }

    // 알람 설정 버튼 생성 및 클릭 리스너 등록후 반환
    fun createAlarmBtn(value : String) : Button{
        val sucBtn = Button(this)
        sucBtn.setBackgroundColor(Color.BLACK)
        sucBtn.setTextColor(Color.WHITE)
        sucBtn.layoutParams = alarmBtnParams
        sucBtn.textSize = 15f
        sucBtn.text = "알람"
        // 클릭 시 TimePicker를 띄워 시간을 설정하도록 한다.(TimePickerFragment클래스 이용)
        sucBtn.setOnClickListener {
            // Timpicker Dialog를 이용하여 시간을 선택
            var t_fragment = TimePickerFragment()
            t_fragment.setContext(this)
            clickBtn = sucBtn
            t_fragment.show(supportFragmentManager, "timePicker")
        }
        return sucBtn
    }

    // 알람 취소버튼 생성 및 리스너 등록후 반환
    @RequiresApi(Build.VERSION_CODES.O)
    fun createAlarmCancelBtn(value : String) : Button{
        var cancelBtn = Button(this)
        cancelBtn.setBackgroundColor(Color.BLACK)
        cancelBtn.setTextColor(Color.WHITE)
        cancelBtn.text = "알람 해제"
        // 클릭 시 Dialog생성
        cancelBtn.setOnClickListener {var builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("알람 해제").setMessage(value + "알람을 해제하시겠습니까?")
            // 확인버튼 누를 시 알람을 취소하고 알람 취소버튼을 보이지 않고 알람 버튼을 다시 보인다.
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                cancelAlarm(value, getOrder(cancelBtn.parent.parent.parent as LinearLayout))
                cancelBtn.visibility = GONE
                var alarmBtn = (cancelBtn.parent as FrameLayout).getChildAt(0) as Button
                alarmBtn.visibility = VISIBLE
                ((alarmBtn.parent.parent.parent as LinearLayout).getChildAt(1)as TextView).text = ""
                Toast.makeText(applicationContext, "알람이 해제되었습니다.", Toast.LENGTH_LONG).show()
            })
            builder.setNegativeButton("취소", null)
            builder.show()
        }

        return cancelBtn
    }


    // 알람 설정 및 알람 취소버튼을 담기 위한 FrameLayout으로 버튼의 보임 안보임 설정을 위한 레이아웃
    fun createFrameLayout(sucbtn : Button, cancelBtn : Button, value : String) : FrameLayout{
        val frame = FrameLayout(this)
        frame.layoutParams = linearParam
        var time = dbService.getTime(value)
        var times = time.split(" ")
        Log.i("hour", times[0])
        Log.i("minute", times[1])
        // 어플리케이션을 켰을 때 보일 버튼을 설정하기 위한 조건문
        // hour : -1, minute : -1의 경우 예약이 안된걸로 간주
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

    // 알람 설정 시 예약정보를 띄우기 위한 TextView
    fun createTimeText(value : String) : TextView{
        val timeTextView = TextView(this)

        // DB로부터 예약 시간을 가져와 예약되어있다면 예약정보를 띄운다.
        var time = dbService.getTime(value).split(" ")
        if(!(time[0].equals("-1") && time[1].equals("-1"))){
            timeTextView.text = String.format("%s시 %s분 알람이 예약되었습니다.", time[0], time[1])
        }

        return timeTextView
    }


    // 정의한 create메소드들을 이용하여 전체적인 Todo목록을 만들어 반환한다.
    @RequiresApi(Build.VERSION_CODES.O)
    fun createTodo(value : String) : LinearLayout {
        val ll = LinearLayout(this)
        val btn = createDelBtn(value)
        val text = createTextView(value)
        val cancelBtn = createAlarmCancelBtn(value)
        val timeText = createTimeText(value) // 알람 시간을 보여주기 위한 text
        val linear = LinearLayout(this) // 목록의 전체 linearlayout

        val sucbtn = createAlarmBtn(value)
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


    // DB에 들어있는 Todo데이터들을 순차적으로 모두 읽어와 createTodo메소드를 이용하여 Todo목록을 만든다.
    @RequiresApi(Build.VERSION_CODES.O)
    fun createList(context : Context) {
        table = findViewById(R.id.table)
        var todoList = dbService.getAllTodo()
        val iter = todoList.iterator()

        while(iter.hasNext()){
            table.addView(createTodo(iter.next().getTodo()))
        }
    }

    // 알람을 시작하는 메소드
    // pendingIntent에 AlarmReceiver클래스로 가는 Intent를 설정하고 요청코드와 flag를 생성하여 pendingIntent를 생성한다.
    // pendingIntent를 alarmManager객체에 전달하여 알람에 맞춘 시간이 되면 알람을 시작하도록 한다.
    private fun startAlarm(c: Calendar, text : String, todoId: Int) {
        // 알람매니저 선언
        var alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("text", text)
        var pendingIntent = PendingIntent.getBroadcast(this, todoId, intent, FLAG_IMMUTABLE)

        // 설정 시간이 현재시간 이후라면 설정
        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE, 1)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)

    }

    // 설정되어진 알람을 순서대로 가져와 취소한다.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cancelAllAlarm(){
        var size = table.childCount
        for(i in 0..size - 1 step(1)){
            var text = (((table.getChildAt(i) as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(1) as TextView).text
            Log.i("text : ", text.toString())
            cancelAlarm(text.toString(), i)
        }
    }

    // 알람 취소 메소드
    private fun cancelAlarm(value : String, alarmId : Int){
        // alarmManager 생성
        var alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // AlarmReceiver로 이동하는 Intent객체 생성
        var intent = Intent(this, AlarmReceiver::class.java)
        // intent를 기반으로 pendingIntent의 객체 생성
        // 버전에 따라 다르게
        var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getBroadcast(this, alarmId, intent, FLAG_IMMUTABLE)
        }else{
            PendingIntent.getBroadcast(this, alarmId, intent, FLAG_IMMUTABLE)
        }
        // 알람 취소
        alarmManager.cancel(pendingIntent)
        // DB에 알람 정보(시간) 초기화
        dbService.insertTime(value, -1, -1)
    }

    // Todolist에서 자신이 몇번째에 속하는지 반환하는 함수
    // alarm id는 목록에서의 index로 설정하기 때문에 index를 받아오기 위한 메소드
    @RequiresApi(Build.VERSION_CODES.O)
    fun getOrder(todo : View) : Int{
        var size = table.childCount
        for(i in 0..size - 1){
            if(todo == table.getChildAt(i) as LinearLayout){
                return i
            }
        }
        return -1
    }

    // TimePickerDialog에서 시간을 설정하면 자동으로 실행되는 메소드
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // 설정된 시간의 정보를 가져와 Calendar객체에 시간을 저장한다.
        var c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        val group = clickBtn.parent as FrameLayout

        val text = ((group.parent as LinearLayout).getChildAt(1) as TextView).text

        ((group.parent.parent as LinearLayout).getChildAt(1) as TextView).text = String.format("%d시 %d분 알람이 예약되었습니다.", hourOfDay, minute)

        // 알람 설정 버튼을 숨기고 알람 해제 버튼을 보인다.
        (group.getChildAt(0) as Button).visibility = View.INVISIBLE
        (group.getChildAt(1) as Button).visibility = View.VISIBLE

        // DB에 시, 분을 넣는다.
        dbService.insertTime(text as String, hourOfDay, minute)

        Toast.makeText(this, String.format("%s를 %d시 %d분에 알람을 설정하였습니다.", text, hourOfDay, minute), Toast.LENGTH_LONG).show()

        var id = getOrder(group.parent.parent as LinearLayout)
        startAlarm(c, text.toString(), id)
    }
}