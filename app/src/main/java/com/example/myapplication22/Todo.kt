package com.example.myapplication22

class Todo {

    private lateinit var todo : String
    private lateinit var date : String

    constructor(todo : String, date : String){
        this.todo = todo
        this.date = date
    }

    fun getTodo() : String{
        return todo;
    }

    fun setTodo(todo : String){
        this.todo = todo;
    }

    fun getDate() : String{
        return date;
    }

    fun setDate(){
        this.date = date
    }

}