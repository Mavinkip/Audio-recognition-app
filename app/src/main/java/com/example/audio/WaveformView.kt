package com.example.audio

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 2f
    }

    var audioData: FloatArray? = null
        set(value) {
            field = value
            invalidate() // Redraw when new audio data is set
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        audioData?.let {
            val width = width.toFloat()
            val height = height.toFloat()
            val midY = height / 2
            val step = width / it.size

            for (i in it.indices) {
                val x = i * step
                val y = midY + it[i] * (height / 2) // Scale the amplitude
                canvas.drawLine(x, midY, x, y, paint)
            }
        }
    }
}
