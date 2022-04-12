package com.example.videoeditortrim.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoeditortrim.databinding.ActivityTrimVideoActivtyBinding
import com.example.videoeditortrim.interfaces.OnK4LVideoListener
import com.example.videoeditortrim.util.Constants
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener

class TrimVideoActivity : AppCompatActivity(), OnTrimVideoListener, OnK4LVideoListener {

    private lateinit var binding: ActivityTrimVideoActivtyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrimVideoActivtyBinding.inflate(layoutInflater)
        binding.root.apply {
            setContentView(this)
        }
        val extraIntent: Intent = intent
        val path: String = extraIntent.getStringExtra(Constants.VideoUri).toString()

        //setting progressbar
        binding.progressBarTrim.isIndeterminate = true
        binding.trimVideoViewTrimVideoAc.setMaxDuration(60)
        binding.trimVideoViewTrimVideoAc.setOnTrimVideoListener(this)
        binding.trimVideoViewTrimVideoAc.setDestinationPath("/storage/emulated/0/Edited/")
        binding.trimVideoViewTrimVideoAc.setVideoURI(Uri.parse(path))
    }

    override fun onTrimStarted() {
        binding.progressBarTrim.visibility = View.VISIBLE
    }

    override fun getResult(uri: Uri) {
        binding.progressBarTrim.visibility = View.GONE
        runOnUiThread {
            Toast.makeText(
                this@TrimVideoActivity,
                uri.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
        val intent = Intent(applicationContext, FinalVideoActivity::class.java)
        intent.putExtra(Constants.VideoUri, uri.toString())
        startActivity(intent)
    }

    override fun cancelAction() {
        binding.progressBarTrim.visibility = View.GONE
        binding.trimVideoViewTrimVideoAc.destroy()
        finish()
    }

    override fun onError(message: String?) {
        binding.progressBarTrim.visibility = View.GONE
        runOnUiThread {
            Toast.makeText(this@TrimVideoActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onVideoPrepared() {
        runOnUiThread {
            Toast.makeText(
                this@TrimVideoActivity,
                "onVideoPrepared",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}