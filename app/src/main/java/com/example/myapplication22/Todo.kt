package com.example.myapplication22

class Todo {
    private lateinit var todo : String
    private var hour = -1
    private var min = -1

    constructor(){}
    constructor(todo : String, hour : Int, min : Int){
        this.todo = todo
        this.hour = hour
        this.min = min
    }

    fun setTime(hour : Int, min : Int){
        this.hour = hour
        this.min = min
    }

    fun getTime() : String{
        return hour.toString() + " " + min.toString();
    }
    fun getTodo() : String{
        return todo;
    }

    fun setTodo(todo : String){
        this.todo = todo;
    }


}