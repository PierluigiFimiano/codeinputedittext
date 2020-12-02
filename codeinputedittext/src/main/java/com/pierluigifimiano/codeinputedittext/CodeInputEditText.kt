@file:Suppress("MemberVisibilityCanBePrivate")

package com.pierluigifimiano.codeinputedittext

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.ceil
import kotlin.math.min
import kotlin.properties.Delegates

private const val DEFAULT_CODE_LENGTH: Int = 4
private const val DEFAULT_MIN_LETTER_SPACING: Float = 1.0F

class CodeInputEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var unpaddedWidth: Int by Delegates.notNull()

    private val backgroundCallback: CodeInputBackgroundCallback?
        get() = background as? CodeInputBackgroundCallback

    var minLetterSpacing: Float = 1.0F
        set(value) {
            if (field != value) {
                field = value

                requestLayout()
                invalidate()
            }
        }

    var codeLength: Int = 0
        set(value) {
            if (field != value) {
                if (value < 0) {
                    throw IllegalArgumentException()
                }

                field = value
                filters = arrayOf(*filters)

                requestLayout()
                invalidate()
            }
        }

    init {
        val cAttrs: TypedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.CodeInputEditText, defStyleAttr, 0
        )
        minLetterSpacing = cAttrs.getFloat(
            R.styleable.CodeInputEditText_minLetterSpacing, DEFAULT_MIN_LETTER_SPACING
        )
        try {
            codeLength = cAttrs.getInt(
                R.styleable.CodeInputEditText_codeLength, DEFAULT_CODE_LENGTH
            )
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

        val new: MutableList<InputFilter> = mutableListOf()
        var founded = false

        for (filter: InputFilter in filters) {
            if (filter is InputFilter.LengthFilter) {
                if (!founded && filter.max == codeLength) {
                    new.add(filter)
                    founded = true
                }
            } else {
                new.add(filter)
            }
        }

        if (!founded) {
            for (filter: InputFilter in this.filters) {
                if (filter is InputFilter.LengthFilter && filter.max == codeLength) {
                    new.add(filter)
                    founded = true
                    break
                }
            }
            if (!founded) {
                new.add(InputFilter.LengthFilter(codeLength))
            }
        }

        super.setFilters(new.toTypedArray())
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

        val (em1: Float, em2: Float) = paint.withTypeface(Typeface.MONOSPACE) {

            val em1: Float = withLetterSpacing(0.0F) { measureText("M") }
            val em2: Float = withLetterSpacing(1.0F) { measureText("M") }

            em1 to em2
        }

        val measuredWidth: Int = unpaddedWidth - 2
        val spacingPx: Float = (measuredWidth - codeLength * em1) / codeLength

        val ration: Float = 1.0F / (em2 - em1)

        val spacingEms: Float = spacingPx * ration

        return if (spacingEms >= 0.0F) spacingEms else 0.0F
    }

    private fun desiredWidth(): Int {
        val charWidth: Float = paint.withLetterSpacing(minLetterSpacing) {
            withTypeface(Typeface.MONOSPACE) { measureText("M") }
        }
        return ceil(charWidth * codeLength).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize: Int = MeasureSpec.getSize(widthMeasureSpec)

        val paddingStart: Int = compoundPaddingLeft
        val paddingEnd: Int = compoundPaddingRight

        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST &&
            measuredWidth < widthSize
        ) {
            var desWidth: Int = desiredWidth() + paddingStart + paddingEnd

            if (desWidth > measuredWidth) {
                desWidth = if (widthMode == MeasureSpec.AT_MOST) {
                    min(widthSize, desWidth)
                } else {
                    desWidth
                }

                super.onMeasure(
                    MeasureSpec.makeMeasureSpec(desWidth, MeasureSpec.EXACTLY),
                    heightMeasureSpec
                )
            }
        }

        val measuredHeight: Int = measuredHeight
        val paddingBottom: Int = measuredHeight - baseline
        val paddingTop: Int = compoundPaddingTop + paddingBottom - compoundPaddingBottom
        val unpaddedHeight: Int = measuredHeight - paddingTop - paddingBottom

        unpaddedWidth = measuredWidth - paddingStart - paddingEnd

        backgroundCallback?.onMeasureChanged(
            unpaddedWidth,
            unpaddedHeight,
            paddingStart,
            paddingTop,
            paddingEnd,
            paddingBottom
        )
    }
}