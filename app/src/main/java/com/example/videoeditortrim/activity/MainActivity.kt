package com.example.videoeditortrim.activity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.videoeditortrim.databinding.ActivityMainBinding
import com.example.videoeditortrim.interfaces.MyCompleteListener
import com.example.videoeditortrim.util.Constants.STORAGE_PERMISSION_CODE
import com.example.videoeditortrim.util.Database
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.File
import java.util.*


@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var allVideoList: ArrayList<File>

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.root.apply {
            setContentView(this)
        }
        allVideoList = ArrayList()
        allClicks()
    }

    private fun allClicks(){
        binding.pickVideo.setOnClickListener {
            askForPermissions()
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) +
                ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                /*Toast.makeText(
                    this@MainActivity, "You have already granted this permission! Please Click on get Videos",
                    Toast.LENGTH_LONG
                ).show()*/
                binding.progressBar.visibility = View.VISIBLE
                binding.pickVideo.visibility = View.GONE
                Database.loadAllVideos(applicationContext, object : MyCompleteListener {

                    override fun onSuccess() {
                        startActivity(Intent(applicationContext, LoadAllExistingVideos::class.java))
                    }
                    override fun onFailure() {
                        binding.progressBar.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE

                    }

                })

            }  else {
                askForPermissions()
                requestStoragePermission()
            }
        }
    }


//    private fun fetchLoad(Directory: File, myCompleteListener: MyCompleteListener) {
//        binding.progressBar.visibility = View.VISIBLE
//        val fileList = Directory.listFiles()
//        GlobalScope.launch(Dispatchers.IO) {
//            if (fileList != null && fileList.isNotEmpty()) {
//                for (i in fileList.indices) {
//                    if (fileList[i].isDirectory) {
//                        fetchLoad(fileList[i], myCompleteListener)
//                    } else {
//                        val name = fileList[i].name.toString()
//                        for (extension in MethodExtension.videoExtension) {
//                            if (name.endsWith(extension)) {
//                                allVideoList.add(fileList[i])
//                                break
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        myCompleteListener.onSuccess()
//    }

    private fun askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
                return
            }
        }
    }

    private fun requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) &&
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage("Please give permissions to see video,upload and download videos")
                .setPositiveButton(
                    "ok"
                ) { _, _ ->
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        STORAGE_PERMISSION_CODE
                    )
                }
                .setNegativeButton(
                    "cancel"
                ) { dialog, _ -> dialog.dismiss() }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }
}
