package com.aman.audiorecorder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName="audioRecords")
 class AudioRecord(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "fileName")
    var fileName:String?= null,
    @ColumnInfo(name = "filePath")
    var filePath:String? = null,
    @ColumnInfo(name = "timeStamp")
    var timeStamp:Long?= 0L,
    @ColumnInfo(name = "duration")
    var duration:String?= null,
    @ColumnInfo(name = "empsPath")
    var empsPath:String?= null,
)