package com.example.broadcastapp

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.SeekBar
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var airplaneModeReceiver: AirplaneModeReceiver
    private lateinit var videoView: VideoView
    private lateinit var seekBar: SeekBar
    private lateinit var playButton: Button
    private val handler = Handler(Looper.getMainLooper())
    private val stopVideoHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        videoView = findViewById(R.id.videoView)
        seekBar = findViewById(R.id.seekBar)
        playButton = findViewById(R.id.btnPlay)

        // Set video path (From res/raw/sample_video.mp4)
        val videoPath = "android.resource://$packageName/${R.raw.demo}"
        val uri: Uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        // Play button functionality
        playButton.setOnClickListener {
            if (!videoView.isPlaying) {
                videoView.start()
                playButton.text = "Pause"
                updateSeekBar()  // Start updating SeekBar
                startVideoTimer()
            } else {
                videoView.pause()
                playButton.text = "Play"
            }
        }

        // SeekBar listener to update video position
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    videoView.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // When video is prepared, set SeekBar max value
        videoView.setOnPreparedListener { mediaPlayer ->
            seekBar.max = mediaPlayer.duration
            updateSeekBar() // Start SeekBar updates
        }

        // Reset button text when video completes
        videoView.setOnCompletionListener {
            playButton.text = "Play"
        }

        // Broadcast Receiver Setup
        val intentFilter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        airplaneModeReceiver = AirplaneModeReceiver()
        registerReceiver(airplaneModeReceiver, intentFilter)
    }

    // Function to update SeekBar position
    private fun updateSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (videoView.isPlaying) {
                    seekBar.progress = videoView.currentPosition
                    handler.postDelayed(this, 500)  // Update every 500ms
                }
            }
        }, 500)
    }
    
    private fun startVideoTimer() {
        stopVideoHandler.postDelayed({
            videoView.stopPlayback() // Stop video
            videoView.visibility = VideoView.GONE // Hide video
            seekBar.visibility = SeekBar.GONE // Hide SeekBar
            playButton.visibility = Button.GONE // Hide play button

            // Show "Time Expired" dialog
            showTimeExpiredDialog()
        }, 10000) // 10 seconds timer
    }

    // Function to show AlertDialog when time expires
    private fun showTimeExpiredDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Time Expired")
        builder.setMessage("The video time has ended.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }
    
    override fun onDestroy() {
        unregisterReceiver(airplaneModeReceiver)
        handler.removeCallbacksAndMessages(null)  // Stop SeekBar updates
        super.onDestroy()
    }
}
