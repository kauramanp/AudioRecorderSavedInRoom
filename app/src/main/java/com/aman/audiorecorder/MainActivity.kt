package com.aman.audiorecorder

import android.Manifest.permission.*
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aman.audiorecorder.databinding.ActivityMainBinding
import com.aman.audiorecorder.databinding.LayoutAudioRecordBinding
import com.aman.audiorecorder.interfaces.OnItemClickListener
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OnItemClickListener {
    lateinit var audioDatabase: AudioDatabase
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var binding: ActivityMainBinding
    var list = ArrayList<AudioRecord>()
    lateinit var adapter: AudioListAdapter
    private lateinit var recorder: MediaRecorder
    private var mediaPlayer =  MediaPlayer()

    var audioPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                ShowDialog()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                var alertDialog = AlertDialog.Builder(this)
                alertDialog.apply {
                    setTitle("Permission required")
                    setMessage("Permission required to run the app")
                    setCancelable(false)
                    setPositiveButton("Ok"){_,_-> openSettings()}
                }
                alertDialog.show()
            }
        }

    private fun openSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = AudioListAdapter(list, this)
        audioDatabase = AudioDatabase.getDatabase(this)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcList.layoutManager = linearLayoutManager
        binding.rcList.adapter = adapter

        binding.fabAdd.setOnClickListener {
            if( ContextCompat.checkSelfPermission(
                    this,
                    RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED){
                    audioPermission.launch(RECORD_AUDIO)
            }else if(ContextCompat.checkSelfPermission(
                    this,
                    RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED){
                ShowDialog()
            }
        }

        getAudioFiles()
        mediaPlayer.setOnCompletionListener {
            adapter.updatePosition(-1, 0)

        }
    }

    fun ShowDialog(){
        var dialogBinding = LayoutAudioRecordBinding.inflate(layoutInflater)
        var dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        recorder = MediaRecorder()
        var dirPath = "${externalCacheDir?.absolutePath}/"

        val simpleDateFormat = SimpleDateFormat("DD-MM-yyyy_mm.ss.hh")
        val date = simpleDateFormat.format(Date())
        var filename = "audio_record_$date"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.mp3")

            try {
                prepare()
                start()
            } catch (e: IOException) {

            }
        }
        dialogBinding.btnSave.setOnClickListener {
            recorder.stop()
            try {
                var fos = FileOutputStream("$dirPath$filename")
                var out = ObjectOutputStream(fos)
                out.close()
                fos.close()
            } catch (e: IOException) {

            }

            var record = AudioRecord()
            record.fileName = filename
            record.filePath = "$dirPath$filename.mp3"
            record.empsPath = "$dirPath$filename"

            class saveRecording:  AsyncTask<Void, Void, Void>(){
                override fun doInBackground(vararg p0: Void?): Void? {
                   audioDatabase.audioRecordsDao().insert(record)
                    return null
                }

                override fun onPostExecute(result: Void?) {
                    super.onPostExecute(result)
                    dialog.dismiss()
                    getAudioFiles()
                }
            }
            saveRecording().execute()

        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()

        }
        dialog.show()
    }

    fun getAudioFiles(){
        list.clear()
        class getFiles:  AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                list.addAll(audioDatabase.audioRecordsDao().getAllAudioRecords())
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                adapter.notifyDataSetChanged()
            }
        }
        getFiles().execute()
    }

    override fun onItemClickListener(position: Int, data: AudioRecord) {
        if(!mediaPlayer.isPlaying){
            mediaPlayer.reset()
            mediaPlayer.apply {
                setDataSource(data.filePath)
                prepare()
                start()
            }
            Log.d("Position State", "$position 1")
            adapter.updatePosition(position, 1)
        }else{
            mediaPlayer.pause()
            adapter.updatePosition(position, 0)

        }
    }
}