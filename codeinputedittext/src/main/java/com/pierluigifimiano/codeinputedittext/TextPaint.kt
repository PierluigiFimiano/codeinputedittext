package com.pierluigifimiano.codeinputedittext

import android.graphics.Typeface
import android.text.TextPaint

internal fun <T> TextPaint.withTypeface(typeface: Typeface, compute: TextPaint.() -> T): T {
    val tf: Typeface = this.typeface
    this.typeface = typeface
    val result: T = compute.invoke(this)
    this.typeface = tf
    return result
}

internal fun <T> TextPaint.withLetterSpacing(letterSpacing: Float, compute: TextPaint.() -> T): T {
    val ls: Float = this.letterSpacing
    this.letterSpacing = letterSpacing
    val result: T = compute.invoke(this)
    this.letterSpacing = ls
    return result
}