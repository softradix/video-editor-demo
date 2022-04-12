package com.example.videoeditortrim.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videoeditortrim.adapter.AllExistingVideosAdapter
import com.example.videoeditortrim.databinding.ActivityLoadAllExistingVideosBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.File

@DelicateCoroutinesApi
class LoadAllExistingVideos : AppCompatActivity() {


    private lateinit var binding: ActivityLoadAllExistingVideosBinding

    private var allExistingVideosAdapter: AllExistingVideosAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadAllExistingVideosBinding.inflate(layoutInflater)
        binding.root.apply {
            setContentView(this)
        }
        binding.toolbarLoadAll.title = "Video Editor"
        setSupportActionBar(binding.toolbarLoadAll)
        allViews()
    }


    private fun allViews(){
        allExistingVideosAdapter = AllExistingVideosAdapter(this)

        val gridLayoutManager = GridLayoutManager(applicationContext, 3)
        //        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerViewLoadAllExisting.layoutManager = gridLayoutManager
        binding.recyclerViewLoadAllExisting.adapter = allExistingVideosAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(applicationContext, MainActivity::class.java)
        overridePendingTransition(0, 0)
        startActivity(intent)
    }
}