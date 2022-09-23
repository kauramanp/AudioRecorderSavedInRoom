package com.aman.audiorecorder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aman.audiorecorder.interfaces.AudioRecordsDao

@Database(entities = [AudioRecord::class], version = 1)
abstract class AudioDatabase : RoomDatabase(){
    abstract fun audioRecordsDao(): AudioRecordsDao
    companion object{
        var audioDatabase: AudioDatabase?= null
        @Synchronized
        fun getDatabase(context: Context): AudioDatabase{
            if(audioDatabase == null){
                audioDatabase =  Room.databaseBuilder(
                    context,
                    AudioDatabase::class.java, context.getString(R.string.app_name)
                ).build()
            }
            return audioDatabase!!
        }
    }
}