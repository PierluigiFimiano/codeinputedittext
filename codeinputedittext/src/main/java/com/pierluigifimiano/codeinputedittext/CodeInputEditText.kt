@file:Suppress("MemberVisibilityCanBePrivate")

package com.pierluigifimiano.codeinputedittext

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import kotlin.properties.Delegates

private const val DEFAULT_CODE_LENGTH: Int = 4

class CodeInputEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var totalWidth: Int by Delegates.notNull()

    private val backgroundCallback: CodeInputBackgroundCallback?
        get() = background as? CodeInputBackgroundCallback

    var codeLength: Int = 0
        set(value) {
            if (value <= 0) {
                throw IllegalArgumentException()
            }

            if (field == value) {
                return
            }
            field = value

            val new: MutableList<InputFilter> = mutableListOf()
            for (filter: InputFilter in filters) {
                if (filter is InputFilter.LengthFilter) {
                    if (filter.max == value) {
                        return
                    }
                } else {
                    new.add(filter)
                }
            }
            new.add(InputFilter.LengthFilter(value))

            filters = new.toTypedArray()
        }

    init {
        val cAttrs: TypedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.CodeInputEditText, defStyleAttr, 0
        )
        try {
            codeLength =
                cAttrs.getInt(R.styleable.CodeInputEditText_codeLength, DEFAULT_CODE_LENGTH)
        } finally {
            cAttrs.recycle()
        }
    }

    override fun setBackground(background: Drawable?) {
        if (background is CodeInputBackgroundCallback) {
            background.onCodeLengthChanged(codeLength)
        }
        super.setBackground(background)
    }

    override fun setFilters(filters: Array<out InputFilter>) {
        var new: Array<out InputFilter> = filters

        if (findLengthFilter(filters) == null) {
            val size: Int = filters.size
            new = Array(size + 1) {
                if (it == size) {
                    findLengthFilter(this.filters) ?: InputFilter.LengthFilter(codeLength)
                } else {
                    filters[it]
                }
            }
        }

        super.setFilters(new)
    }

    private fun findLengthFilter(filters: Array<out InputFilter>): InputFilter.LengthFilter? {
        for (filter: InputFilter in filters) {
            if (filter is InputFilter.LengthFilter && filter.max == codeLength) {
                return filter
            }
        }
        return null
    }

    override fun onPreDraw(): Boolean {
        if (!super.onPreDraw()) {
            return false
        }

        var canDraw = true

        if (typeface != Typeface.MONOSPACE) {
            typeface = Typeface.MONOSPACE
            canDraw = false
        }

        val spacing: Float = measureSpacing()
        if (letterSpacing != spacing) {
            letterSpacing = spacing
            canDraw = false
        }

        return canDraw
    }

    private fun measureSpacing(): Float {
        // Save paint state
        val letterSpacing: Float = paint.letterSpacing

        paint.letterSpacing = 1.0F
        val em2: Float = paint.measureText("M")

        paint.letterSpacing = 0.0F
        val em1: Float = paint.measureText("M")

        // Restore paint state
        paint.letterSpacing = letterSpacing

        val measuredWidth: Int = totalWidth - 2
        val spacingPx: Float = (measuredWidth - codeLength * em1) / codeLength

        val ration: Float = 1.0F / (em2 - em1)

        val spacingEms: Float = spacingPx * ration

        return if (spacingEms >= 0.0F) spacingEms else 0.0F
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val paddingStart: Int = compoundPaddingLeft
        val paddingEnd: Int = compoundPaddingRight
        val paddingBottom: Int = measuredHeight - baseline
        val paddingTop: Int = compoundPaddingTop + paddingBottom - compoundPaddingBottom
        val height: Int = measuredHeight - paddingTop - paddingBottom

        totalWidth = measuredWidth - paddingStart - paddingEnd

        backgroundCallback?.onMeasureChanged(
            totalWidth,
            height,
            paddingStart,
            paddingTop,
            paddingEnd,
            paddingBottom
        )
    }
}