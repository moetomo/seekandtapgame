package com.example.seekandtapgame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView

class HighlightableTile @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private var highlight = false
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }

    fun setHighlighted(highlighted: Boolean) {
        highlight = highlighted
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (highlight) {
            val radius = Math.min(width, height) / 2f - 10f
            canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        }
    }
}
