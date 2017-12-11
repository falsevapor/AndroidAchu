package com.chepel.krug

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

/**
 * TODO: document your custom view class.
 */
class KrugProgress : View {

    private var mTextPaint: TextPaint? = null
    private var mTextWidth: Float = 0f
    private var mTextHeight: Float = 0f

    var paintTrack = Paint()
    var paintMercury = Paint()

    var paddingL = paddingLeft
    var paddingT = paddingTop
    var paddingR = paddingRight
    var paddingB = paddingBottom
    var contentW = width - paddingL - paddingR
    var contentH = height - paddingT - paddingB
    val bit: Float = 3.6f
    var colorz: IntArray = intArrayOf(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED)
    var rectBase = RectF()
    var rectMircury = RectF()

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        mLabelString = ""
        colorz[0] = Color.BLUE
        colorz[1] = Color.GREEN
        colorz[2] = Color.YELLOW
        colorz[3] = Color.RED
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.KrugProgress, defStyle, 0)

        mLabelString = a.getString(R.styleable.KrugProgress_label)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mLabelSize = a.getDimension(R.styleable.KrugProgress_labelSize, mLabelSize)
        mLabelColor = a.getColor(R.styleable.KrugProgress_labelColor, mLabelColor)

        valueF = a.getFloat(R.styleable.KrugProgress_value, valueF)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mValueSize = a.getDimension(R.styleable.KrugProgress_valueSize, mValueSize)
        mValueColor = a.getColor(R.styleable.KrugProgress_valueColor, mValueColor)

        minF = a.getFloat(R.styleable.KrugProgress_minV, minF)
        maxF = a.getFloat(R.styleable.KrugProgress_maxV, maxF)

        mercuryWidthF = a.getDimension(R.styleable.KrugProgress_mercuryWidth, mercuryWidthF)
        trackWidthF = a.getDimension(R.styleable.KrugProgress_trackWidth, trackWidthF)

        startFromF = a.getFloat(R.styleable.KrugProgress_startFrom, startFromF)
        sweepF = a.getFloat(R.styleable.KrugProgress_sweep, sweepF)

        animate = a.getBoolean(R.styleable.KrugProgress_useAnimation, animate)
        aniDuration = a.getInt(R.styleable.KrugProgress_maxAnimationDuration, aniDuration)

        a.recycle()

        // Set up a default TextPaint object
        mTextPaint = TextPaint()
        mTextPaint!!.flags = Paint.ANTI_ALIAS_FLAG
        mTextPaint!!.textAlign = Paint.Align.LEFT

        paintMercury.color = Color.rgb(72, 106, 176)
        paintMercury.isAntiAlias = true
        paintMercury.strokeWidth = mercuryWidthF
        paintMercury.style = Paint.Style.STROKE
        paintMercury.strokeCap = Paint.Cap.ROUND

        paintTrack.color = Color.argb(255, 250, 250, 250)
        paintTrack.isAntiAlias = true
        paintTrack.strokeWidth = trackWidthF
        paintTrack.style = Paint.Style.STROKE
        paintTrack.strokeCap = Paint.Cap.ROUND
        paintTrack.setShadowLayer(20f, 0f, 10f, Color.BLACK)

        // Important for certain APIs
        setLayerType(LAYER_TYPE_SOFTWARE, paintTrack)

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
        invalidate()
    }

    private fun invalidateTextPaintAndMeasurements() {
        mTextPaint!!.textSize = mLabelSize
        mTextPaint!!.color = mValueColor
        mTextWidth = mTextPaint!!.measureText(mLabelString)

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

    private var mLabelString: String? = null // TODO: use a default from R.string...
    var label: String?
        get() = mLabelString
        set(v) {
            mLabelString = v
            invalidateTextPaintAndMeasurements()
        }

    private var mLabelSize = 0f // TODO: use a default from R.dimen...
    var labelSize: Float
        get() = mLabelSize
        set(v) {
            mLabelSize = v
            invalidateTextPaintAndMeasurements()
        }

    private var mLabelColor = Color.RED // TODO: use a default from R.color...
    var labelColor: Int
        get() = mLabelColor
        set(v) {
            mLabelColor = v
            invalidateTextPaintAndMeasurements()
        }

    private var valueF: Float = 0.0f
    var value: Float
        get() = valueF
        set(v)
        {
            valueF = v * bit
            val n:Int = v.toInt()
            mLabelString = "$n%"
            //paintMercury.color = colorz[n/25]
            invalidateTextPaintAndMeasurements()
            invalidate()
        }

    private var mValueSize = 0f // TODO: use a default from R.dimen...
    var valueSize: Float
        get() = mValueSize
        set(v) {
            mValueSize = v
            invalidateTextPaintAndMeasurements()
        }

    private var mValueColor = Color.RED // TODO: use a default from R.color...
    var valueColor: Int
        get() = mValueColor
        set(v) {
            mValueColor = v
            invalidateTextPaintAndMeasurements()
        }

    private var minF: Float = 0.0f
    var minV: Float
        get() = minF
        set(v)
        {
            minF = v
            invalidateTextPaintAndMeasurements()
        }

    private var maxF: Float = 0.0f
    var maxV: Float
        get() = maxF
        set(v)
        {
            maxF = v
            invalidateTextPaintAndMeasurements()
        }

    //degrees
    private var startFromF: Float = 0.0f
    var startFrom: Float
        get() = startFromF
        set(v)
        {
            startFromF = v
            invalidateTextPaintAndMeasurements()
            invalidate()
        }

    //degrees
    private var sweepF: Float = 0.0f
    var sweep: Float
        get() = sweepF
        set(v)
        {
            sweepF = v
            invalidateTextPaintAndMeasurements()
            invalidate()
        }

    private var trackWidthF: Float = 50.0f
    var trackWidth: Float
        get() = trackWidthF
        set(v)
        {
            trackWidthF = v
            paintTrack.strokeWidth = trackWidthF
            invalidate()
        }

    private var mercuryWidthF: Float = 50.0f
    var mercuryWidth: Float
        get() = mercuryWidthF
        set(v)
        {
            mercuryWidthF = v
            paintMercury.strokeWidth = mercuryWidthF
            invalidate()
        }

    private var animate: Boolean = true
    var useAnimation: Boolean
        get() = animate
        set(v)
        {
            animate = v
            invalidate()
        }

    private var aniDuration: Int = 400
    var maxAnimationDuration: Int
        get() = aniDuration
        set(v)
        {
            aniDuration = v
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paddingL = paddingLeft
        paddingT = paddingTop
        paddingR = paddingRight
        paddingB = paddingBottom

        contentW = width - paddingL - paddingR
        contentH = height - paddingT - paddingB
        rectBase.set(paddingL.toFloat(), paddingT.toFloat(), (paddingL + contentW).toFloat(), (paddingT + contentH).toFloat())
        rectMircury.set(paddingL.toFloat(), paddingT.toFloat(), (paddingL + contentW).toFloat(), (paddingT + contentH).toFloat())

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.

        // Draw the text.
        canvas.drawText(mLabelString!!,
                paddingL + (contentW - mTextWidth) / 2,
                paddingT + (contentH + mTextHeight) / 2,
                mTextPaint!!)

        canvas.drawArc(rectBase, startFromF, sweepF, false, paintTrack)
        canvas.drawArc(rectMircury, startFromF, valueF, false, paintMercury)
    }
}
