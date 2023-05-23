package com.example.myapplication22

class Todo {

    private lateinit var todo : String

    constructor(todo : String){
        this.todo = todo
    }

    fun getTodo() : String{
        return todo;
    }

    fun setTodo(todo : String){
        this.todo = todo;
    }


}