package com.example.audio

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

class SoundRecognitionModel(context: Context) {
    private lateinit var interpreter: Interpreter
    private val classLabels: Map<Int, String>

    init {
        // Load the TensorFlow Lite model from the assets folder
        val tfliteModel = FileUtil.loadMappedFile(context, "1.tflite")
        interpreter = Interpreter(tfliteModel)

        // Load class labels from CSV
        classLabels = loadClassLabels(context)
    }

    private fun loadClassLabels(context: Context): Map<Int, String> {
        val labels = mutableMapOf<Int, String>()
        val inputStream = context.assets.open("yamnet_class_map.csv")

        inputStream.bufferedReader().useLines { lines ->
            lines.drop(1) // Skip header line
                .forEach { line ->
                    val parts = line.split(",")
                    if (parts.size >= 3) {
                        val index = parts[0].trim().toInt()
                        val displayName = parts[2].trim()
                        labels[index] = displayName
                    }
                }
        }
        return labels
    }

    fun classifySound(audioInput: FloatArray): Pair<String, Float> {
        // Define the output shape. Modify it according to your model (e.g., 521 for YAMNet).
        val output = Array(1) { FloatArray(521) }

        // Run inference
        interpreter.run(audioInput, output)

        // Find the most likely class
        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val maxScore = output[0][maxIndex]

        // Get the class name
        val className = classLabels[maxIndex] ?: "Unknown class"

        return Pair(className, maxScore)
    }
}
