package com.pierluigifimiano.codeinputedittext

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class DefaultCodeInputDrawable : Drawable(), CodeInputBackgroundCallback {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var _width: Int = -1
    private var _start: Int = -1
    private var _count: Int = -1

    var padding: Float = 10.0F
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException()
            }
            if (field != value) {
                field = value
                invalidateSelf()
            }
        }

    override fun setAlpha(alpha: Int) {
        if (paint.alpha != alpha) {
            paint.alpha = alpha
            invalidateSelf()
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        if (paint.colorFilter != colorFilter) {
            paint.colorFilter = colorFilter
            invalidateSelf()
        }
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun draw(canvas: Canvas) {
        if (_width <= 0 || _count < 0) {
            return
        }

        val x: Float = bounds.left + _start.toFloat()
        val y: Float = bounds.bottom - 7.0F * canvas.density

        if (padding == 0.0F || _count == 0) {
            canvas.drawLine(x, y, x + _width, y, paint)
            return
        }

        val step: Float = _width / _count.toFloat()
        val length: Float = step - 2 * padding

        for (i: Int in 0 until _count) {
            val startX: Float = x + padding + step * i
            val endX: Float = startX + length
            canvas.drawLine(startX, y, endX, y, paint)
        }
    }

    override fun onMeasureChanged(
        width: Int,
        height: Int,
        paddingStart: Int,
        paddingTop: Int,
        paddingEnd: Int,
        paddingBottom: Int
    ) {
        if (_start != paddingStart || _width != width) {
            _width = width
            _start = paddingStart
            invalidateSelf()
        }
    }

    override fun onCodeLengthChanged(length: Int) {
        if (_count != length) {
            _count = length
            invalidateSelf()
        }
    }
}