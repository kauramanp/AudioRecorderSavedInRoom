package com.aman.audiorecorder.interfaces

import com.aman.audiorecorder.AudioRecord

interface OnItemClickListener {
    fun onItemClickListener(position:Int, data: AudioRecord)
}