package com.example.videoeditortrim.util

import android.content.Context
import com.example.videoeditortrim.interfaces.MyCompleteListener
import com.example.videoeditortrim.util.Method.loadDirectory
import com.example.videoeditortrim.util.StorageUtil.getStorageDirectories
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

@DelicateCoroutinesApi
object Database {
    var allVideoList = ArrayList<File>()
    private lateinit var directory: File
    private lateinit var allPath: Array<String>
    fun loadAllVideos(context: Context?, myCompleteListener: MyCompleteListener) {
        GlobalScope.launch(Dispatchers.IO) {
            allPath = getStorageDirectories(context!!)
            for (path in allPath) {
                directory = File(path)
                loadDirectory(directory)
            }
            myCompleteListener.onSuccess()
        }

    }
}