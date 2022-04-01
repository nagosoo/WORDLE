package com.example.wordle


fun main(){
    val a = listOf<String>("hihi")
    val b = a.iterator()
    while(b.hasNext()){
        val c = b.next()
        print("in iterator $c")
    }
}