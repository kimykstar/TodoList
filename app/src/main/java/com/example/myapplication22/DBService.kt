package com.example.myapplication22

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class DBService{
    lateinit var myHelper : TodoListDBHelper
    lateinit var writeSqlDB :SQLiteDatabase
    lateinit var readSqlDB :SQLiteDatabase
    var todoList : ArrayList<Todo> = ArrayList<Todo>()

    // SQLiteOpenHelper클래스의 생성자는 context를 받아야 하기 때문에
    // 아예 인스턴스를 받아서 생성자에 대입한다.
    constructor(myHelper : TodoListDBHelper){
        this.myHelper = myHelper
        // 읽기 및 쓰기 DB를 연다.
        writeSqlDB = myHelper.writableDatabase
        readSqlDB = myHelper.readableDatabase
    }

    // 모든 Todo의 목록들을 데이터베이스로부터 가져와 List<Todo>자료형에 넣어 반환
    fun getAllTodo() : List<Todo> {
        var cursor : Cursor
        cursor = readSqlDB.rawQuery("SELECT * FROM todolist;", null)
        while(cursor.moveToNext()){
            var todo : Todo
            todo = Todo(cursor.getString(0), cursor.getInt(1), cursor.getInt(2))
            todoList.add(todo)
        }
        return todoList
    }

    // TodoList의 목록 중 지정한 Todo하나를 삭제한다.
    fun deleteTodo(todo : String){
        writeSqlDB.execSQL("DELETE FROM todolist WHERE list='" + todo + "';")
    }

    // TodoList목록에 Todo하나를 삽입
    fun insertTodo(todo : String){
        writeSqlDB.execSQL("INSERT INTO todolist VALUES ('" + todo + "', -1, -1);")
    }

    // 알람의 시간을 지정 시 시간정보(시, 분)을 데이터베이스에 넣는다.
    fun insertTime(list : String, hour : Int, min : Int){
        writeSqlDB.execSQL("update todolist set hour=" + hour + " where list='" + list + "'")
        Log.i("hourInsert", hour.toString())
        writeSqlDB.execSQL("update todolist set minute=" + min + " where list='" + list + "'")
        Log.i("mInsert", min.toString())
    }

    fun getTime(list : String) : String{
        var cursor = readSqlDB.rawQuery("SELECT * FROM todolist WHERE list='" + list + "';", null)
        var time = ""
        while(cursor.moveToNext()){
            time += cursor.getInt(1).toString()

            time += " " + cursor.getInt(2).toString()
            Log.i("hourDB", time)
        }
        return time
    }

    fun getAllTimes() : List<String>{
        var cursor = readSqlDB.rawQuery("SELECT hour, minute FROM todolist", null)
        var time = ""
        var list = ArrayList<String>()
        while(cursor.moveToNext()){
            time += cursor.getInt(0).toString()
            time += " " + cursor.getInt(1).toString()
            list.add(time)
        }
        return list


    }



}