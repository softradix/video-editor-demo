package com.example.videoeditortrim.util

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.File

@DelicateCoroutinesApi
object Method {
    fun loadDirectory(Directory: File) {
        val fileList = Directory.listFiles()
        if (fileList != null && fileList.isNotEmpty()) {
            for (i in fileList.indices) {
                if (fileList[i].isDirectory) {
                    loadDirectory(fileList[i])
                } else {
                    val name = fileList[i].name.toString()
                    for (extension in MethodExtension.videoExtension) {
                        if (name.endsWith(extension)) {
                            Database.allVideoList.add(fileList[i])
                            Log.d("Video List", name)
                            break
                        }
                    }
                }
            }
        }
    }
}