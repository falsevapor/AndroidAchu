package com.chepel.krug

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.math.abs
import android.os.Build
import android.view.ViewGroup
import kotlin.math.max
import kotlin.math.min
import android.support.v4.view.animation.LinearOutSlowInInterpolator


/**
 * TODO: document your custom view class.
 */
class GaugeProgress : View {

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

    enum class Aligns(val bit:Int)
    {
        none(0),
        top(1),
        bottom(2),
        start(4),
        end(8),
        center(16),
    }

    private val paintTrack = Paint()
    private val paintTrackN = Paint()
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
        mainLabel.value = ""


        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.GaugeProgress, defStyle, 0)

        startFromF = a.getFloat(R.styleable.GaugeProgress_startFrom, startFromF)
        sweepF = a.getFloat(R.styleable.GaugeProgress_sweep, sweepF)

        minF = a.getFloat(R.styleable.GaugeProgress_minV, minF)
        maxV = a.getFloat(R.styleable.GaugeProgress_maxV, maxF)

        mainLabel.readSettings(a,
                R.styleable.GaugeProgress_label,
                R.styleable.GaugeProgress_labelSize,
                R.styleable.GaugeProgress_labelColor,
                R.styleable.GaugeProgress_labelAlignment)

        subLabel.readSettings(a,
                R.styleable.GaugeProgress_subLabel,
                R.styleable.GaugeProgress_subLabelSize,
                R.styleable.GaugeProgress_subLabelColor,
                R.styleable.GaugeProgress_subLabelAlignment,
                mainLabel)

        prefix.readSettings(a,
                R.styleable.GaugeProgress_valuePrefix,
                R.styleable.GaugeProgress_valuePrefixSize,
                R.styleable.GaugeProgress_valuePrefixColor,
                -1,
                mainLabel)

        suffix.readSettings(a,
                R.styleable.GaugeProgress_valueSuffix,
                R.styleable.GaugeProgress_valueSuffixSize,
                R.styleable.GaugeProgress_valueSuffixColor,
                -1,
                mainLabel)
        prefix.align = mainLabel.align
        suffix.align = mainLabel.align

        valueF = a.getFloat(R.styleable.GaugeProgress_value, valueF)

        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mValueColor = a.getColor(R.styleable.GaugeProgress_valueColor, mValueColor)
        mValueColorN = a.getColor(R.styleable.GaugeProgress_negativeValueColor, mValueColorN)
        if (a.hasValue(R.styleable.GaugeProgress_valueColors))
        {
            val colors  = a.getString(R.styleable.GaugeProgress_valueColors)
            val valuez = colors.split("[,;: ]".toRegex())
            colorz = IntArray(valuez.size)
            for (i in 0..(valuez.size-1))
            {
                colorz[i] = Color.parseColor(valuez[i])
            }
        }

        mercuryWidthF = a.getDimension(R.styleable.GaugeProgress_mercuryWidth, mercuryWidthF)
        trackWidthF = a.getDimension(R.styleable.GaugeProgress_trackWidth, trackWidthF)

        bUseTrackColor = (a.hasValue(R.styleable.GaugeProgress_trackColor))
        mTrackColor = a.getColor(R.styleable.GaugeProgress_trackColor, mTrackColor)
        bUseTrackColorN = (a.hasValue(R.styleable.GaugeProgress_negativeTrackColor))
        mTrackColorN = a.getColor(R.styleable.GaugeProgress_negativeTrackColor, mTrackColorN)
        trackAlpha = a.getInt(R.styleable.GaugeProgress_trackAlpha, trackalpha)

        animateV = a.getBoolean(R.styleable.GaugeProgress_animateValue, animateV)
        aniDuration = a.getInt(R.styleable.GaugeProgress_maxAnimationDuration, aniDuration)
        showIntro = a.getBoolean(R.styleable.GaugeProgress_intro, showIntro)

        showminimum = a.getBoolean(R.styleable.GaugeProgress_showMinValue, showminimum)

        showplus = a.getBoolean(R.styleable.GaugeProgress_showPlus, showplus)

        if (a.getBoolean(R.styleable.GaugeProgress_heightSameAsWidth, false))
            sizemode = WHMode.W
        else if (a.getBoolean(R.styleable.GaugeProgress_widthSameAsHeight, false))
            sizemode = WHMode.H
        else
            sizemode = WHMode.default

        a.recycle()

        // Set up a default TextPaint object
        paintMercury.isAntiAlias = true
        paintMercury.strokeWidth = mercuryWidthF
        paintMercury.style = Paint.Style.STROKE
        paintMercury.strokeCap = Paint.Cap.ROUND

        paintTrack.color = mTrackColor
        paintTrack.isAntiAlias = true
        paintTrack.strokeWidth = trackWidthF
        paintTrack.style = Paint.Style.STROKE
        paintTrack.strokeCap = Paint.Cap.ROUND

        paintTrackN.color = mTrackColorN
        paintTrackN.isAntiAlias = true
        paintTrackN.strokeWidth = trackWidthF
        paintTrackN.style = Paint.Style.STROKE
        paintTrackN.strokeCap = Paint.Cap.ROUND

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

    class ALabel
    {
        private val paint = TextPaint()
        var x:Float = 0f
        var y:Float = 0f
        var width:Float = 0f
        var height:Float = 0f
        var value: String = ""
        var size:Float = 0f
        var useSize:Boolean = false
        var color:Int = Color.BLACK
        var useColor:Boolean = false
        var align:Int = 0

        /*debug
        val rr:RectF = RectF()
        val rrpaint = Paint()
        */

        fun readSettings(a: TypedArray, valueid:Int, sizeid:Int, colorid:Int, alignid:Int = -1, referenceLabel:ALabel? = null)
        {
            /*debug
            rrpaint.isAntiAlias = true
            rrpaint.strokeWidth = 1f
            rrpaint.style = Paint.Style.STROKE
            rrpaint.strokeCap = Paint.Cap.ROUND
            rrpaint.color = Color.RED
            */

            if (a.hasValue(valueid))
                value = a.getString(valueid)
            // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
            // values that should fall on pixel boundaries.
            useSize = (a.hasValue(sizeid))
            size = a.getDimension(sizeid, size)
            useColor = (a.hasValue(colorid))
            color = a.getColor(colorid, color)
            if (-1 < alignid)
                align = a.getInt(alignid, align)

            paint.flags = Paint.ANTI_ALIAS_FLAG
            paint.textAlign = Paint.Align.LEFT
            if (useSize)
                paint.textSize = size
            else if (null != referenceLabel)
                paint.textSize = referenceLabel.size
        }

        fun measure(r:RectF, referenceColor:Int = Color.BLACK)
        {
            paint.color = if (useColor) color else referenceColor

            val txtr = Rect()
            paint.getTextBounds("Wqtyipdfghjklb,/;'[]`1!@$%^&*()",0,value.length,txtr) //measure tallest chars
            width = paint.measureText(value)// txtr.width().toFloat()
            height = txtr.height().toFloat()

            x = r.left
            y = r.top + height

            if (0 < (align and Aligns.end.bit))
            {
                x = r.right - width
            }
            if (0 < (align and Aligns.bottom.bit))
            {
                y = r.bottom
            }
            if (0 < (align and Aligns.center.bit))
            {
                if (0 == (align and (Aligns.start.bit or Aligns.end.bit)))
                {
                    x = r.centerX() - (width/2)
                }
                if (0 == (align and (Aligns.top.bit or Aligns.bottom.bit)))
                {
                    y = r.centerY() + (height/2)
                }
            }
            /*debug
            rr.set(r)
            */
        }

        fun alignWith(reference:ALabel, mode:Aligns)
        {
            if (reference.align != align)
                return

            when(mode) {
                Aligns.start ->
                {
                    if (0 < (align and Aligns.start.bit))
                    {
                        reference.x += width
                    }
                    else
                    {
                        x = reference.x - width
                    }
                }
                Aligns.top ->
                {
                    if (0 < (align and Aligns.top.bit))
                    {
                        reference.y += height
                    }
                    else
                    {
                        y = reference.y - reference.height
                    }
                }
                Aligns.end ->
                {
                    if (0 < (align and Aligns.end.bit))
                    {
                        reference.x -= width
                    }
                    else
                    {
                        x = reference.x + reference.width
                    }
                }
                Aligns.bottom ->
                {
                    if (0 < (align and Aligns.bottom.bit))
                    {
                        reference.y -= height
                    }
                    else
                    {
                        y = reference.y + height
                    }
                }
                else -> return
            }
        }

        fun draw(canvas: Canvas)
        {
            if (value.isEmpty())
                return

            canvas.drawText(value, x, y, paint)

            /*debug
            canvas.drawRect(rr,rrpaint)
            canvas.drawLine(rr.left,rr.centerY(), rr.right, rr.centerY(), rrpaint)
            canvas.drawLine(rr.centerX(), rr.top, rr.centerX(), rr.bottom, rrpaint)
            */
        }
    }

    private fun invalidateTextPaintAndMeasurements(text:Boolean)
    {
        paddingL = paddingLeft.toFloat()
        paddingT = paddingTop.toFloat()
        paddingR = paddingRight.toFloat()
        paddingB = paddingBottom.toFloat()

        val w = width
        val h = height
        contentW = w - paddingL - paddingR
        contentH = h - paddingT - paddingB

        val r:RectF = RectF()
        r.left = paddingL
        r.top = paddingT
        r.right = w - paddingR
        r.bottom = h - paddingB

        val half = min(contentW, contentH)/2
        val centerX = paddingL + contentW/2
        val centerY = paddingT + contentH/2

        rectTrack.set(centerX - half, centerY - half, centerX + half, centerY + half)
        rectMircury.set(centerX - half, centerY - half, centerX + half, centerY + half)

        if (text)
        {
            mainLabel.measure(r, paintMercury.color)
            subLabel.measure(r, mainLabel.color)
            prefix.measure(r, mainLabel.color)
            suffix.measure(r, mainLabel.color)

            subLabel.alignWith(mainLabel, Aligns.bottom)

            prefix.y = mainLabel.y
            suffix.y = mainLabel.y
            prefix.alignWith(mainLabel, Aligns.start)
            suffix.alignWith(mainLabel, Aligns.end)
        }

        invalidate()
    }


    private val mainLabel:ALabel = ALabel()
    private val subLabel:ALabel = ALabel()
    private val prefix:ALabel = ALabel()
    private val suffix:ALabel = ALabel()
    //todo: add properties for programmatic access to configuration attributes

    class AnimationRange
    {
        var colorFrom: Int = 0
        var colorTo: Int = 0
        var valueFrom: Float = 0f
        var valueTo: Float = 0f
    }

    private fun colorFromValue(value:Float):Int
    {
        var res = if (0 > value) mValueColorN else mValueColor

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

            if (!animateV)
            {
                valueSweep = newV
                return
            }

            animValue(animParams)

            invalidateTextPaintAndMeasurements(true)
        }

    private var valueSweepF: Float = 0.0f
    var valueSweep: Float
        get() = valueSweepF
        set(v)
        {
            val n:Int = v.toInt()
            //mLabelString = "$valueprefix$n$valuesuffix"
            mainLabel.value = if (showplus && 0 < n) "+$n" else "$n"

            valueSweepF = sweepF * ((v - minF)/(maxF - minF)) - zeroSweep
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
            //mValueColor = c
            //mValueColorN = c


            if (!bUseTrackColor)
                paintTrack.color = (trackalpha shl 24) or (c and 16777215)
            if (!bUseTrackColorN)
                paintTrackN.color = (trackalpha shl 24) or (c and 16777215)

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


    private var mValueColor = Color.RED
    private var mValueColorN = Color.BLUE
    var valueColor: Int
        get() = mValueColor
        set(v) {
            mValueColor = v
            invalidateTextPaintAndMeasurements(true)
        }
    var valueColorN: Int
        get() = mValueColorN
        set(v) {
            mValueColorN = v
            invalidateTextPaintAndMeasurements(true)
        }

    private var minF: Float = 0f
    var minV: Float
        get() = minF
        set(v)
        {
            minF = v
            if (minF > maxF)
                minF = maxF
            calcZero()
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
            if (maxF < minF)
                maxF = minF
            calcZero()
            invalidateTextPaintAndMeasurements(false)
        }

    private var showplus:Boolean = false
    var showPlus:Boolean
        get() = showplus
        set(v)
        {
            showplus = v
            invalidateTextPaintAndMeasurements(true)
        }

    //degrees
    private var startFromF: Float = 0f
    private var zeroF: Float = 0f
    private var zeroSweep: Float = 0f
    var startFrom: Float
        get() = startFromF
        set(v)
        {
            startFromF = v
            calcZero()
            invalidateTextPaintAndMeasurements(false)
        }

    //degrees
    private var sweepF: Float = 0f
    var sweep: Float
        get() = sweepF
        set(v)
        {
            sweepF = v
            calcZero()
            invalidateTextPaintAndMeasurements(false)
        }

    private fun calcZero()
    {
        zeroF = startFromF
        zeroSweep = 0f // all positive > zero at beginning

        if (0 > minV && 0 >= maxV) // all negative > zero at end
        {
            zeroSweep = sweepF
        }
        else if (0 > minV && 0 < maxV) // zero somewhere in the middle
        {
            zeroSweep = (sweepF * abs(minF)) / (maxF - minF)
        }
        zeroF += zeroSweep
    }

    private var trackWidthF: Float = 50.0f
    var trackWidth: Float
        get() = trackWidthF
        set(v)
        {
            trackWidthF = v
            paintTrack.strokeWidth = trackWidthF
            paintTrackN.strokeWidth = trackWidthF
            invalidateTextPaintAndMeasurements(false)
        }

    private var bUseTrackColor: Boolean = true
    private var mTrackColor = Color.LTGRAY
    var trackColor: Int
        get() = mTrackColor
        set(v) {
            mTrackColor = v
            paintTrack.color = mTrackColor
            invalidateTextPaintAndMeasurements(false)
        }

    private var bUseTrackColorN: Boolean = true
    private var mTrackColorN = Color.LTGRAY
    var trackColorN: Int
        get() = mTrackColorN
        set(v) {
            mTrackColorN = v
            paintTrackN.color = mTrackColorN
            invalidateTextPaintAndMeasurements(false)
        }

    private var trackalpha:Int = 32
    var trackAlpha: Int
        get() = trackalpha
        set(v) {
            trackalpha = v

            val c1 = paintTrack.color
            //paintTrack.color = (c1 and -16777216) or (c1 and 16777215)
            paintTrack.color = (trackalpha shl 24) or (c1 and 16777215)

            val c2 = paintTrackN.color
            //paintTrackN.color = (c2 and -16777216) or (c2 and 16777215)
            paintTrackN.color = (trackalpha shl 24) or (c2 and 16777215)

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

    private var animateV: Boolean = true
    var animateValue: Boolean
        get() = animateV
        set(v)
        {
            animateV = v
        }

    private var aniDuration: Int = 400
    var maxAnimationDuration: Int
        get() = aniDuration
        set(v)
        {
            aniDuration = v
        }

    var showIntro: Boolean
        get() { return intro != introsteps.done }
        set(v)
        {
            intro = if (!v) introsteps.done else introsteps.tointro
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

    private fun animTrack()
    {
        val anims = AnimatorSet()
        val a = ObjectAnimator.ofFloat(this, "wake", 1f, 0f)
        a.startDelay = 250
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
        c.startDelay = 250
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
        invalidateTextPaintAndMeasurements(true)
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

        if (0 < zeroSweep)
            canvas.drawArc(rectTrack, startFromF, zeroSweep, false, paintTrackN)
        if (0 < sweepF - zeroSweep)
            canvas.drawArc(rectTrack, zeroF, sweepF - zeroSweep, false, paintTrack)

        if (introsteps.done != intro)
            return

        canvas.drawArc(rectMircury, zeroF, valueSweepF, false, paintMercury)

        subLabel.draw(canvas)
        mainLabel.draw(canvas)
        prefix.draw(canvas)
        suffix.draw(canvas)
    }
}

