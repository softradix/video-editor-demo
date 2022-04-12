package com.example.videoeditortrim.interfaces

interface OnK4LVideoListener {
    fun onTrimStarted()
    fun onError(message: String?)
    fun onVideoPrepared()
}