package com.example.videoeditortrim.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoeditortrim.R
import com.example.videoeditortrim.activity.KVideoEditorDemoActivity
import com.example.videoeditortrim.util.Constants
import com.example.videoeditortrim.util.Database.allVideoList

class AllExistingVideosAdapter internal constructor(
    var mContext: Context
) :
    RecyclerView.Adapter<AllExistingVideosAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.videoitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoUri = Uri.fromFile(allVideoList[position])

        try {
            Glide.with(mContext).load(videoUri).thumbnail().into(holder.thumbnail)
            val mediaPlayer: MediaPlayer = MediaPlayer.create(holder.thumbnail.context, videoUri)
            val duration: Int = mediaPlayer.duration / 1000
            val hr = duration / 3600
            val rem = duration % 3600
            val min = rem / 60
            val sec = rem % 60
            val time = String.format("%02d", hr) + ":" + String.format(
                "%02d",
                min
            ) + ":" + String.format("%02d", sec)
            holder.duration.text = time
        } catch (e: Exception) {
            Log.e("TAG", "onBindViewHolder: ${e.message}")

        }
        /* finally {

                 Glide.with(mContext).load(videoUri).thumbnail().into(holder.thumbnail)
                 val mediaPlayer: MediaPlayer = MediaPlayer.create(holder.thumbnail.context, videoUri)
                 val duration: Int = mediaPlayer.duration / 1000
                 val hr = duration / 3600
                 val rem = duration % 3600
                 val min = rem / 60
                 val sec = rem % 60
                 val time = String.format("%02d", hr) + ":" + String.format(
                     "%02d",
                     min
                 ) + ":" + String.format("%02d", sec)
                 holder.duration.text = time
             }
 */


        holder.thumbnail.setOnClickListener {
//            val intent = Intent(holder.thumbnail.context, TrimVideoActivity::class.java)
            val intent = Intent(holder.thumbnail.context, KVideoEditorDemoActivity::class.java)
            intent.putExtra(Constants.VideoUri, videoUri.toString())
            holder.thumbnail.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return allVideoList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        var duration: TextView = itemView.findViewById(R.id.video_duration)

    }


}