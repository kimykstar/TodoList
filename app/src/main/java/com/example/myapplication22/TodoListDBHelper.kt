package com.example.myapplication22

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// todolist를 관리하기 위한 class
class TodoListDBHelper(context : Context) : SQLiteOpenHelper(context, "databases", null, 1){
    override fun onCreate(p0 : SQLiteDatabase?){
        p0!!.execSQL("CREATE TABLE todolist(list text PRIMARY KEY);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE todolist;")
        onCreate(db)
    }
}