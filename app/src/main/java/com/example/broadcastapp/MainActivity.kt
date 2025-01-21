package com.example.broadcastapp

import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var airplaneModeReceiver: AirplaneModeReceiver
    private lateinit var videoView: VideoView
    private lateinit var seekBar: SeekBar
    private lateinit var playButton: Button

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
            } else {
                videoView.pause()
                playButton.text = "Play"
            }
        }

        // SeekBar listener to update video progress
        videoView.setOnPreparedListener { mediaPlayer ->
            seekBar.max = mediaPlayer.duration
            mediaPlayer.setOnBufferingUpdateListener { _, percent ->
                seekBar.secondaryProgress = percent
            }
        }

        // Update SeekBar progress while playing
        videoView.setOnCompletionListener {
            playButton.text = "Play"
        }

        // Broadcast Receiver Setup
        val intentFilter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        airplaneModeReceiver = AirplaneModeReceiver()
        registerReceiver(airplaneModeReceiver, intentFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(airplaneModeReceiver)
        super.onDestroy()
    }
}
