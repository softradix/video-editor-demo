package com.example.videoeditortrim.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.videoeditortrim.databinding.ActivityFinaVideoBinding
import com.example.videoeditortrim.util.Constants
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class FinalVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinaVideoBinding
    var videoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinaVideoBinding.inflate(layoutInflater)
        binding.root.apply {
            setContentView(this)

        }
        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this, LoadAllExistingVideos::class.java))
            finishAffinity()
        }
        val getUri = intent
        val videoUriString = getUri.getStringExtra(Constants.VideoUri)
        videoUri = Uri.parse(videoUriString)
        val mediaController = MediaController(this)
        binding.finalVideoView.setMediaController(mediaController)
        binding.finalVideoView.setVideoURI(videoUri)
        binding.finalVideoView.start()
    }
}