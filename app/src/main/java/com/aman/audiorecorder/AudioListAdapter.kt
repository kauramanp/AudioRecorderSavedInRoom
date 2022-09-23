package com.aman.audiorecorder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aman.audiorecorder.interfaces.OnItemClickListener

class AudioListAdapter(var audioList: ArrayList<AudioRecord>, var onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    var playingPosition: Int = -1
    var state = 0
    class ViewHolder(var view: View): RecyclerView.ViewHolder(view){
        var tvFileName: TextView = view.findViewById(R.id.tvFileName)
        var ibPlayPause: ImageButton = view.findViewById(R.id.ibPlayPause)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       var view = LayoutInflater.from(parent.context).inflate(R.layout.recordlist_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvFileName.setText(audioList[position].fileName)
        holder.itemView.setOnClickListener{
            onItemClickListener.onItemClickListener(position, audioList[position])
        }
        if (position == playingPosition && state == 1) {
            holder.ibPlayPause.setImageResource(R.drawable.ic_baseline_pause)
        } else if (position == playingPosition && state == 0) {
            holder.ibPlayPause.setImageResource(R.drawable.ic_baseline_play)
        }else{
            holder.ibPlayPause.setImageResource(R.drawable.ic_baseline_play)
        }
    }

    fun updatePosition(position: Int, state: Int) {
        this.playingPosition = position
        this.state = state
        notifyDataSetChanged()
        Log.d("UpdatePosition", "Position State: $position $state")
    }

    override fun getItemCount(): Int {
      return audioList.size
    }
}