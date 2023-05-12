package com.example.myapplication22

class TodoList {

    private var todolist = ArrayList<String>()

    fun getAllList() : ArrayList<String>{
        return todolist
    }

    fun getTodo(todo : String){
        todolist.add(todo)
    }

    fun removeTodo(todo : String){
        todolist.remove(todo)
    }

}