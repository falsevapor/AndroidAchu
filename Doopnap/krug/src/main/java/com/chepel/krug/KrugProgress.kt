package com.chepel.krug

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

/**
 * TODO: document your custom view class.
 */
class KrugProgress : View {
    private var mExampleString: String? = null // TODO: use a default from R.string...
    private var mExampleColor = Color.RED // TODO: use a default from R.color...
    private var mExampleDimension = 0f // TODO: use a default from R.dimen...
    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    var exampleDrawable: Drawable? = null

    private var mTextPaint: TextPaint? = null
    private var mTextWidth: Float = 0.toFloat()
    private var mTextHeight: Float = 0.toFloat()

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    var exampleString: String?
        get() = mExampleString
        set(exampleString) {
            mExampleString = exampleString
            invalidateTextPaintAndMeasurements()
        }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    var exampleColor: Int
        get() = mExampleColor
        set(exampleColor) {
            mExampleColor = exampleColor
            invalidateTextPaintAndMeasurements()
        }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    var exampleDimension: Float
        get() = mExampleDimension
        set(exampleDimension) {
            mExampleDimension = exampleDimension
            invalidateTextPaintAndMeasurements()
        }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    var paintB = Paint()
    var paint = Paint()

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        mExampleString = ""
        colorz[0] = Color.BLUE
        colorz[1] = Color.GREEN
        colorz[2] = Color.YELLOW
        colorz[3] = Color.RED
        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.KrugProgress, defStyle, 0)

        mExampleString = a.getString(
                R.styleable.KrugProgress_exampleString)
        mExampleColor = a.getColor(
                R.styleable.KrugProgress_exampleColor,
                mExampleColor)

        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.KrugProgress_exampleDimension,
                mExampleDimension)
/*
        if (a.hasValue(R.styleable.KrugProgress_exampleDrawable)) {
            exampleDrawable = a.getDrawable(
                    R.styleable.KrugProgress_exampleDrawable)
            exampleDrawable!!.callback = this
        }
*/
        a.recycle()

        // Set up a default TextPaint object
        mTextPaint = TextPaint()
        mTextPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mTextPaint!!.textAlign = Paint.Align.LEFT

        paint.color = Color.rgb(72, 106, 176)
        paint.isAntiAlias = true
        paint.strokeWidth = 50.0f
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

        paintB.color = Color.argb(64, 128, 128, 128)
        paintB.isAntiAlias = true
        paintB.strokeWidth = 50.0f
        paintB.style = Paint.Style.STROKE
        paintB.strokeCap = Paint.Cap.ROUND

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    var paddingL = paddingLeft
    var paddingT = paddingTop
    var paddingR = paddingRight
    var paddingB = paddingBottom
    var contentW = width - paddingL - paddingR
    var contentH = height - paddingT - paddingB

    private fun invalidateTextPaintAndMeasurements() {
        mTextPaint!!.textSize = mExampleDimension
        mTextPaint!!.color = mExampleColor
        mTextWidth = mTextPaint!!.measureText(mExampleString)

        val fontMetrics = mTextPaint!!.fontMetrics
        mTextHeight = fontMetrics.bottom

        paddingL = paddingLeft
        paddingT = paddingTop
        paddingR = paddingRight
        paddingB = paddingBottom

        contentW = width - paddingL - paddingR
        contentH = height - paddingT - paddingB

        rectBase.set(paddingL.toFloat(), paddingT.toFloat(), (paddingL + contentW).toFloat(), (paddingT + contentH).toFloat())
        rectMircury.set(paddingL.toFloat(), paddingT.toFloat(), (paddingL + contentW).toFloat(), (paddingT + contentH).toFloat())
    }

    val bit: Float = 360.0f/100.0f
    private var progreSS: Float = 0.0f
    var colorz: IntArray = intArrayOf(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED)
    var rectBase = RectF()
    var rectMircury = RectF()

    var progress: Float
        get() = progreSS
        set(value)
        {
            val n:Int = value.toInt()
            progreSS = value * bit
            mExampleString = "$n%"
            paint.color = colorz[n/25]
            invalidateTextPaintAndMeasurements()

            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.


        // Draw the text.
        canvas.drawText(mExampleString!!,
                paddingL + (contentW - mTextWidth) / 2,
                paddingT + (contentH + mTextHeight) / 2,
                mTextPaint!!)

        canvas.drawArc(rectBase, 0.0f, 360.0f, false, paintB)
        canvas.drawArc(rectMircury, 270.0f, progress, false, paint)
    }
}
