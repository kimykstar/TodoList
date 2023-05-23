package com.example.myapplication22

class TodoList {

    private var todoList = ArrayList<Todo>()

    constructor(){}

    // Todo의 모든 항목들을 가져온다.
    fun getAllList() : List<Todo>{
        return todoList
    }
    // Todo의 항목 하나를 추가한다.
    fun addTodo(todo : Todo){
        todoList.add(todo)
    }
    // Todo의 항목 하나를 삭제한다.
    fun removeTodo(todo : Todo){
        todoList.remove(todo);
    }

    fun removeAllTodo(){
        var iter = todoList.iterator()
        while(iter.hasNext()){
            todoList.remove(iter.next())
        }
    }

}