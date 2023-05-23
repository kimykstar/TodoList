package com.example.myapplication22

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity

class DBService{
    lateinit var myHelper : TodoListDBHelper
    lateinit var writeSqlDB :SQLiteDatabase
    lateinit var readSqlDB :SQLiteDatabase
    var todoList = TodoList()

    constructor(myHelper : TodoListDBHelper){
        this.myHelper = myHelper
        writeSqlDB = myHelper.writableDatabase
        readSqlDB = myHelper.readableDatabase
    }

    // 모든 Todo를 가져와 todoList에 넣는다.
    fun getAllTodo() : List<Todo> {
        var cursor : Cursor
        todoList.removeAllTodo()
        cursor = readSqlDB.rawQuery("SELECT * FROM todolist;", null)

        while(cursor.moveToNext()){
            var todo : Todo = Todo(cursor.getString(0))
            todoList.addTodo(todo)
        }
        return todoList.getAllList()
    }

    // Todo하나 삭제
    fun deleteTodo(todo : String){
        writeSqlDB.execSQL("DELETE FROM todolist WHERE list='" + todo + "';")
        todoList.removeTodo(Todo(todo))
    }

    // Todo하나 삽입
    fun insertTodo(todo : String){
        writeSqlDB.execSQL("INSERT INTO todolist VALUES ('" + todo + "');")
        todoList.addTodo(Todo(todo))
    }


}