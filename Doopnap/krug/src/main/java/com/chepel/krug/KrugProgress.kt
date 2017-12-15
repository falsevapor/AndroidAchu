package com.chepel.krug

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.math.abs
import android.os.Build
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.view.ViewGroup
import kotlin.math.max
import kotlin.math.min
import android.support.v4.view.animation.LinearOutSlowInInterpolator


/**
 * TODO: document your custom view class.
 */
class KrugProgress : View {

    class ArgbInterpolator
    {
        private var mDelegate: ArgbInterpolator? = null
        fun evaluate(fraction: Float, startInt: Int, endInt: Int): Int
        {
            val result: Int

            if (mDelegate != null)
            {
                result = mDelegate!!.evaluate(fraction, startInt, endInt)
            }
            else
            {
                val startA = startInt shr 24 and 0xff
                val startR = startInt shr 16 and 0xff
                val startG = startInt shr 8 and 0xff
                val startB = startInt and 0xff

                val endA = endInt shr 24 and 0xff
                val endR = endInt shr 16 and 0xff
                val endG = endInt shr 8 and 0xff
                val endB = endInt and 0xff

                result = (startA + (fraction * (endA - startA)).toInt() shl 24) or
                        (startR + (fraction * (endR - startR)).toInt() shl 16) or
                        (startG + (fraction * (endG - startG)).toInt() shl 8) or
                        (startB + (fraction * (endB - startB)).toInt())
            }
            return result
        }

        private fun withDelegate(delegate: ArgbInterpolator): ArgbInterpolator
        {
            this.mDelegate = delegate
            return this
        }

        companion object
        {
            val newInstance: ArgbInterpolator
                get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ArgbInterpolator().withDelegate(ArgbInterpolator())
                } else {
                    ArgbInterpolator()
                }
        }
    }

    enum class WHMode
    {
        default,
        W,
        H,
    }

    private val mTextPaint= TextPaint()
    private var mTextWidth = 0f
    private var mTextHeight = 0f

    private val paintTrack = Paint()
    private val paintMercury = Paint()

    private var paddingL = paddingLeft.toFloat()
    private var paddingT = paddingTop.toFloat()
    private var paddingR = paddingRight.toFloat()
    private var paddingB = paddingBottom.toFloat()
    private var contentW = (width - paddingL - paddingR).toFloat()
    private var contentH = (height - paddingT - paddingB).toFloat()
    private var colorz: IntArray = IntArray(0)
    private val rectTrack = RectF()
    private val rectMircury = RectF()

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

        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.KrugProgress, defStyle, 0)

        if (a.hasValue(R.styleable.KrugProgress_label))
            mLabelString = a.getString(R.styleable.KrugProgress_label)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mLabelSize = a.getDimension(R.styleable.KrugProgress_labelSize, mLabelSize)
        mLabelColor = a.getColor(R.styleable.KrugProgress_labelColor, mLabelColor)

        if (a.hasValue(R.styleable.KrugProgress_valuePrefix))
            valueprefix = a.getString(R.styleable.KrugProgress_valuePrefix)
        if (a.hasValue(R.styleable.KrugProgress_valueSuffix))
            valuesuffix = a.getString(R.styleable.KrugProgress_valueSuffix)
        valueF = a.getFloat(R.styleable.KrugProgress_value, valueF)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mValueSize = a.getDimension(R.styleable.KrugProgress_valueSize, mValueSize)
        mValueColor = a.getColor(R.styleable.KrugProgress_valueColor, mValueColor)
        if (a.hasValue(R.styleable.KrugProgress_valueColors))
        {
            val colors  = a.getString(R.styleable.KrugProgress_valueColors)
            val valuez = colors.split("[,;: ]".toRegex())
            colorz = IntArray(valuez.size)
            for (i in 0..(valuez.size-1))
            {
                colorz[i] = Color.parseColor(valuez[i])
            }
        }

        minF = a.getFloat(R.styleable.KrugProgress_minV, minF)
        maxF = a.getFloat(R.styleable.KrugProgress_maxV, maxF)

        mercuryWidthF = a.getDimension(R.styleable.KrugProgress_mercuryWidth, mercuryWidthF)
        trackWidthF = a.getDimension(R.styleable.KrugProgress_trackWidth, trackWidthF)
        mTrackColor = a.getColor(R.styleable.KrugProgress_trackColor, mTrackColor)

        startFromF = a.getFloat(R.styleable.KrugProgress_startFrom, startFromF)
        sweepF = a.getFloat(R.styleable.KrugProgress_sweep, sweepF)

        animate = a.getBoolean(R.styleable.KrugProgress_useAnimation, animate)
        aniDuration = a.getInt(R.styleable.KrugProgress_maxAnimationDuration, aniDuration)

        showminimum = a.getBoolean(R.styleable.KrugProgress_showMinValue, showminimum)

        if (a.getBoolean(R.styleable.KrugProgress_heightSameAsWidth, false))
            sizemode = WHMode.W
        else if (a.getBoolean(R.styleable.KrugProgress_widthSameAsHeight, false))
            sizemode = WHMode.H
        else
            sizemode = WHMode.default

        a.recycle()

        // Set up a default TextPaint object
        mTextPaint.flags = Paint.ANTI_ALIAS_FLAG
        mTextPaint.textAlign = Paint.Align.LEFT

        paintMercury.color = Color.rgb(72, 106, 176)
        paintMercury.isAntiAlias = true
        paintMercury.strokeWidth = mercuryWidthF
        paintMercury.style = Paint.Style.STROKE
        paintMercury.strokeCap = Paint.Cap.ROUND

        paintTrack.color = mTrackColor
        paintTrack.isAntiAlias = true
        paintTrack.strokeWidth = trackWidthF
        paintTrack.style = Paint.Style.STROKE
        paintTrack.strokeCap = Paint.Cap.ROUND
        //paintTrack.setShadowLayer(5f, 0f, 2f, Color.argb(32,0,0,0))

        // Important for certain APIs
        //setLayerType(LAYER_TYPE_SOFTWARE, paintTrack)

        paintWake.color = Color.rgb(0, 0, 0)
        paintWake.isAntiAlias = true
        paintWake.strokeWidth = 2f
        paintWake.style = Paint.Style.STROKE
        paintWake.strokeCap = Paint.Cap.ROUND


        val c = colorFromValue(valueF)
        animParams.colorTo = c
        animParams.colorFrom = c

        valueSweep = valueF

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements(true)
    }

    private fun calcText()
    {
        mTextPaint.textSize = mLabelSize
        mTextPaint.color = mValueColor
        mTextWidth = mTextPaint.measureText(mLabelString)

        val fontMetrics = mTextPaint.fontMetrics
        mTextHeight = fontMetrics.bottom
    }

    private fun invalidateTextPaintAndMeasurements(text:Boolean)
    {
        if (text)
            calcText()

        paddingL = paddingLeft.toFloat()
        paddingT = paddingTop.toFloat()
        paddingR = paddingRight.toFloat()
        paddingB = paddingBottom.toFloat()

        val w = width
        val h = height
        contentW = w - paddingL - paddingR
        contentH = h - paddingT - paddingB

        val half = min(contentW, contentH)/2
        val centerX = paddingL + contentW/2
        val centerY = paddingT + contentH/2

        rectTrack.set(centerX - half, centerY - half, centerX + half, centerY + half)
        rectMircury.set(centerX - half, centerY - half, centerX + half, centerY + half)
        invalidate()
    }

    private var mLabelString: String = ""
    var label: String
        get() = mLabelString
        set(v) {
            mLabelString = v
            invalidateTextPaintAndMeasurements(true)
        }

    private var mLabelSize = 0f
    var labelSize: Float
        get() = mLabelSize
        set(v) {
            mLabelSize = v
            invalidateTextPaintAndMeasurements(true)
        }

    private var mLabelColor = Color.RED
    var labelColor: Int
        get() = mLabelColor
        set(v) {
            mLabelColor = v
            invalidateTextPaintAndMeasurements(true)
        }

    private var valueprefix: String = ""
    var valuePrefix: String
        get() = valueprefix
        set(v) {
            valueprefix = v
            invalidateTextPaintAndMeasurements(true)
        }

    private var valuesuffix: String = ""

    var valueSuffix: String
        get() = valuesuffix
        set(v) {
            valuesuffix = v
            invalidateTextPaintAndMeasurements(true)
        }

    class AnimationRange
    {
        var colorFrom: Int = 0
        var colorTo: Int = 0
        var valueFrom: Float = 0f
        var valueTo: Float = 0f
    }

    private fun colorFromValue(value:Float):Int
    {
        var res = mValueColor

        if (1 < colorz.size)
        {
            val unitspercolor = (maxF - minF) / (colorz.size - 1)
            val colorindexA = ((value - minF) / unitspercolor).toInt()
            val colorindexB = colorindexA + 1
            res = colorz[colorindexA]
            if (colorindexB < colorz.size)
            {
                val vA = colorindexA * unitspercolor
                val fraction = ((value - minF) - vA) / unitspercolor
                res = ArgbInterpolator().evaluate(fraction, colorz[colorindexA], colorz[colorindexB])
            }
        }
        else if (colorz.isNotEmpty())
        {
            res = colorz[0]
        }

        return res
    }

    private var valueF: Float = 0f
    private val animParams: AnimationRange = AnimationRange()
    private var introVal : Float = 0f
    var value: Float
        get() = valueF
        set(v)
        {
            val newV = if (v < minF) minF else if (v > maxF) maxF else v

            if (introsteps.done != intro)
            {
                introVal = newV
                return
            }
            if (newV == valueF)
                return

            sweep = sweepF

            animParams.valueFrom = valueF
            animParams.valueTo = newV

            valueF = newV

            animParams.colorFrom = animParams.colorTo
            animParams.colorTo = colorFromValue(newV)

            if (!animate)
            {
                valueSweep = newV
                return
            }

            animValue(animParams)

            invalidateTextPaintAndMeasurements(true)
        }

    private fun animValue(params:AnimationRange)
    {
        val durRatio = maxAnimationDuration / (maxV - minV)
        val dur = abs(params.valueTo - params.valueFrom) * durRatio
        val animation = ObjectAnimator.ofFloat(this, "valueSweep", params.valueFrom, params.valueTo)
        animation.duration = dur.toLong()
        animation.interpolator = FastOutSlowInInterpolator()
        animation.start()
    }

    private var valueSweepF: Float = 0.0f
    var valueSweep: Float
        get() = valueSweepF
        set(v)
        {
            val n:Int = v.toInt()
            mLabelString = "$valueprefix$n$valuesuffix"

            valueSweepF = sweepF * ((v - minF)/(maxF - minF))
            if (showminimum && 0f == valueSweepF)
                valueSweepF = 0.1f

            var c = animParams.colorTo
            if (animParams.colorTo != animParams.colorFrom)
            {
                val vA = min(animParams.valueFrom, animParams.valueTo)
                val vZ = max(animParams.valueFrom, animParams.valueTo)
                val backwards: Boolean = animParams.valueFrom > animParams.valueTo
                val cA = if (backwards) animParams.colorTo else animParams.colorFrom
                val cZ = if (backwards) animParams.colorFrom else animParams.colorTo
                c = ArgbInterpolator().evaluate((v - vA) / (vZ - vA), cA, cZ)
            }
            paintMercury.color = c
            mValueColor = c

            paintTrack.color = (mTrackColor and -16777216) or (c and 16777215)

            invalidateTextPaintAndMeasurements(true)
        }


    private var mValueSize = 0f
    var valueSize: Float
        get() = mValueSize
        set(v) {
            mValueSize = v
            invalidateTextPaintAndMeasurements(true)
        }

    private var mValueColor = Color.RED
    var valueColor: Int
        get() = mValueColor
        set(v) {
            mValueColor = v
            invalidateTextPaintAndMeasurements(true)
        }

    private var minF: Float = 0f
    var minV: Float
        get() = minF
        set(v)
        {
            minF = v
            invalidateTextPaintAndMeasurements(false)
        }

    private var showminimum:Boolean = true
    var showMinV:Boolean
        get() = showminimum
        set(v)
        {
            showminimum = v
            invalidateTextPaintAndMeasurements(false)
        }

    private var maxF: Float = 0f
    var maxV: Float
        get() = maxF
        set(v)
        {
            maxF = v
            invalidateTextPaintAndMeasurements(false)
        }

    //degrees
    private var startFromF: Float = 0f
    var startFrom: Float
        get() = startFromF
        set(v)
        {
            startFromF = v
            invalidateTextPaintAndMeasurements(false)
        }

    //degrees
    private var sweepF: Float = 0f
    var sweep: Float
        get() = sweepF
        set(v)
        {
            sweepF = v
            invalidateTextPaintAndMeasurements(false)
        }

    private var trackWidthF: Float = 50.0f
    var trackWidth: Float
        get() = trackWidthF
        set(v)
        {
            trackWidthF = v
            paintTrack.strokeWidth = trackWidthF
            invalidateTextPaintAndMeasurements(false)
        }

    private var mTrackColor = Color.LTGRAY
    var trackColor: Int
        get() = mTrackColor
        set(v) {
            mTrackColor = v
            paintTrack.color = mTrackColor
            invalidateTextPaintAndMeasurements(false)
        }

    private var mercuryWidthF: Float = 50.0f
    var mercuryWidth: Float
        get() = mercuryWidthF
        set(v)
        {
            mercuryWidthF = v
            paintMercury.strokeWidth = mercuryWidthF
            invalidateTextPaintAndMeasurements(false)
        }

    private var animate: Boolean = true
    var useAnimation: Boolean
        get() = animate
        set(v)
        {
            animate = v
        }

    private var aniDuration: Int = 400
    var maxAnimationDuration: Int
        get() = aniDuration
        set(v)
        {
            aniDuration = v
        }

    private var sizemode:WHMode = WHMode.default
    var sizeMode: WHMode
        get() = sizemode
        set(v)
        {
            sizemode = v
            invalidateTextPaintAndMeasurements(true)
        }


    enum class introsteps
    {
        tointro,
        wake,
        track,
        done
    }

    private var intro:introsteps = introsteps.tointro

    private var wakeF:Float = 0f
    private val rectWakeIn = RectF()
    private val paintWake = Paint()
    var wake:Float
        get() = wakeF
        set(v)
        {
            wakeF = v
            val xy = (rectTrack.width()/2f) * v

            rectWakeIn.set(rectTrack)
            rectWakeIn.top += xy
            rectWakeIn.left += xy
            rectWakeIn.right -= xy
            rectWakeIn.bottom -= xy

            invalidate()
        }

    fun animTrack()
    {
        val anims = AnimatorSet()
        val a = ObjectAnimator.ofFloat(this, "wake", 1f, 0f)
        a.duration = 250
        //a.interpolator = LinearOutSlowInInterpolator()
        a.interpolator = FastOutSlowInInterpolator()
        //a.interpolator = FastOutLinearInInterpolator()
        a.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                intro = introsteps.track
            }
            override fun onAnimationCancel(p0: Animator?) {
                intro = introsteps.done
            }
            override fun onAnimationRepeat(p0: Animator?) {
            }
            override fun onAnimationStart(p0: Animator?) {
            }
        })
        val c = ValueAnimator.ofInt(0,255)
        c.duration = 250
        c.interpolator = LinearOutSlowInInterpolator()
        //c.interpolator = FastOutLinearInInterpolator()
        c.addUpdateListener {
            val i = paintWake.color and 0x00ffffff
            paintWake.color = i or ((it.animatedValue as Int) shl 24)
        }
        val b = ObjectAnimator.ofFloat(this, "trackWidth", 0f, trackWidthF)
        b.duration = 250
        b.interpolator = LinearOutSlowInInterpolator()
        b.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                intro = introsteps.done
                value = introVal
                postInvalidate()
            }
            override fun onAnimationCancel(p0: Animator?) {
                intro = introsteps.done
                value = introVal
                postInvalidate()
            }
            override fun onAnimationRepeat(p0: Animator?) {
            }
            override fun onAnimationStart(p0: Animator?) {
            }
        })
        anims.play(a).with(c)
        anims.play(b).after(a)
        anims.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //animTrack()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        invalidateTextPaintAndMeasurements(true)

        if (WHMode.default == sizemode || (w == h))
            return

        post {
            val lp:ViewGroup.LayoutParams = layoutParams
            lp.height = w
            layoutParams = lp
            postInvalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (introsteps.tointro == intro)
        {
            intro = introsteps.wake
            animTrack()
            return
        }
        else if (introsteps.wake == intro)
        {
            canvas.drawArc(rectWakeIn, 0f, 360f, false, paintWake)
            return
        }

        canvas.drawArc(rectTrack, startFromF, sweepF, false, paintTrack)

        if (introsteps.done == intro)
        {
            canvas.drawText(mLabelString, paddingL + (contentW - mTextWidth) / 2, paddingT + (contentH + mTextHeight) / 2, mTextPaint)

            canvas.drawArc(rectMircury, startFromF, valueSweepF, false, paintMercury)
        }
    }
}

