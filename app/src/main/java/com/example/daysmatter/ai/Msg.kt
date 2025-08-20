package com.example.daysmatter.ai

class Msg(val content:String,val type:Int) {
    companion object{
        const val TYPE_RESERVED=0
        const val TYPE_SENT=1
    }
}