package com.aman.audiorecorder.interfaces

import androidx.room.*
import com.aman.audiorecorder.AudioRecord

@Dao
interface AudioRecordsDao {
    @Query("SELECT * FROM audioRecords")
    fun getAllAudioRecords():List<AudioRecord>

    @Insert
    fun insert(vararg audioRecord:AudioRecord)

    @Delete
    fun deleteRecord(audioRecord: AudioRecord)
}