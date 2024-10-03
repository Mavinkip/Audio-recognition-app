package com.example.audio

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var soundModel: SoundRecognitionModel
    private lateinit var audioRecorder: AudioRecord

    private val sampleRate = 16000 // Sample rate for AudioRecord
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var isRecording = false
    private val audioBuffer = mutableListOf<Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize sound recognition model
        soundModel = SoundRecognitionModel(this)

        // Request microphone permission if not already granted
        requestMicrophonePermission()

        val buttonRecord: Button = findViewById(R.id.buttonRecord)
        val buttonAnalyze: Button = findViewById(R.id.buttonAnalyze)
        val textView: TextView = findViewById(R.id.textView)
        val waveformView: WaveformView = findViewById(R.id.waveformView)

        buttonRecord.setOnClickListener {
            if (isRecording) {
                stopRecording()
                buttonRecord.text = "Record"
                waveformView.audioData = audioBuffer.toFloatArray() // Set audio data for waveform
            } else {
                startRecording()
                buttonRecord.text = "Stop"
            }
        }

        buttonAnalyze.setOnClickListener {
            analyzeSound(textView)
        }
    }

    private fun startRecording() {
        audioBuffer.clear()
        isRecording = true

        // Initialize AudioRecord
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        audioRecorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecorder.startRecording()

        // Start a new thread for audio processing
        thread {
            val buffer = ShortArray(bufferSize)

            while (isRecording) {
                val readSize = audioRecorder.read(buffer, 0, buffer.size)
                if (readSize > 0) {
                    // Convert short[] to float[] and add to buffer
                    val floatBuffer = FloatArray(readSize) // Create a float array for the read size
                    for (i in 0 until readSize) {
                        floatBuffer[i] = buffer[i].toFloat() / Short.MAX_VALUE
                    }
                    audioBuffer.addAll(floatBuffer.toList()) // Add to audioBuffer
                }
            }
        }
    }

    private fun stopRecording() {
        isRecording = false
        audioRecorder.stop()
        audioRecorder.release()
    }

    private fun analyzeSound(textView: TextView) {
        if (audioBuffer.isNotEmpty()) {
            val audioInput = audioBuffer.toFloatArray()
            // Classify sound using the TensorFlow Lite model
            val (predictedClass, confidence) = soundModel.classifySound(audioInput)

            // Update the TextView with the result
            textView.text = "Predicted class: $predictedClass, Confidence: $confidence"
            Log.d("SoundClassification", "Predicted class: $predictedClass, Confidence: $confidence")
        } else {
            textView.text = "No audio recorded!"
        }
    }

    private fun requestMicrophonePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                200
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::audioRecorder.isInitialized) {
            audioRecorder.stop()
            audioRecorder.release()
        }
    }
}
