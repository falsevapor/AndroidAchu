package com.chepel.krug

import android.animation.*
import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dash.*
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.CardView
import kotlinx.android.synthetic.main.include_dash_topcards.*
import kotlinx.android.synthetic.main.include_dash_bigcards.*
import android.support.design.widget.TabLayout
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.view.animation.AnimationUtils




/**
 * A placeholder fragment containing a simple view.
 */
class DashboardFragment : Fragment()
{
    private val mDataSet: Array<String>? = null

    class CardDetailAdapter(val ctx:Context): RecyclerView.Adapter<CardDetailAdapter.ViewHolder>()
    {
        var mDataSet = ArrayList<DataBit>()
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
        {
            var separator:View? = null
            var mainLabel: TextView? = null
            var icon: ImageView? = null
            var valueLabel: TextView? = null
            var suffixLabel: TextView? = null

            init
            {
                icon = v.findViewById(R.id.detailIcon) as ImageView
                mainLabel = v.findViewById(R.id.detailLabel) as TextView
                valueLabel = v.findViewById(R.id.detailValue) as TextView
                suffixLabel = v.findViewById(R.id.detailSuffix) as TextView
                separator = v.findViewById(R.id.detailSeparator) as View
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder
        {
            // Create a new view.
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_detail_item, viewGroup, false)

            return ViewHolder(v)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int)
        {
            val dataBit = mDataSet[position]
            viewHolder.separator!!.visibility = if (0 < position) View.VISIBLE else View.INVISIBLE
            viewHolder.valueLabel!!.text = dataBit.valueForUI
            viewHolder.suffixLabel!!.text = ctx.getString(dataBit.suffixResID)
            viewHolder.mainLabel!!.text = ctx.getString(dataBit.nameResID)
            //viewHolder.icon!!.setImageResource(dataBit.iconResID)
            viewHolder.icon!!.setImageDrawable(dataBit.drawable)

            val animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in)
            val ad = 50 * position
            animation.startOffset = ad.toLong()//Provide delay here
            viewHolder.itemView.startAnimation(animation)
        }

        override fun getItemCount(): Int
        {
            return mDataSet.size
        }
    }

    //private var mGeoDataClient: GeoDataClient? = null
    //private var mPlaceDetectionClient: PlaceDetectionClient? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val ret = inflater.inflate(R.layout.fragment_dash, container, false)
        // Construct a GeoDataClient.
      //  mGeoDataClient = Places.getGeoDataClient(activity, null)

        // Construct a PlaceDetectionClient.
        //mPlaceDetectionClient = Places.getPlaceDetectionClient(activity, null)

        return ret
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    class ACardSide
    {
        var card:CardView? = null
        var progress:GaugeProgress? = null

        var titleArea:LinearLayout? = null
        var title:TextView? = null
        var value:TextView? = null
        var titleSuffix:TextView? = null

        var bounds = Rect(0,0,0,0)

        var titleSize = 0.0f
        var titleBounds = RectF(0f,0f,0f,0f)
        var valueSize = 0.0f
        var titleSuffixSize = 0.0f

        val noBounds:Boolean
            get() = bounds.isEmpty

        fun inflate(refCard:CardView, runtime:Boolean)
        {
            card = refCard
            progress = refCard.findViewById<GaugeProgress>(R.id.cardProgress)

            titleArea = refCard.findViewById<LinearLayout>(R.id.cardTitleArea)
            title = refCard.findViewById<TextView>(R.id.cardTitle)
            value = refCard.findViewById<TextView>(R.id.cardValue)
            titleSuffix = refCard.findViewById<TextView>(R.id.cardTitleSuffix)

            //progress!!.showIntro = runtime
            //progress!!.animateValue = runtime
            progress!!.valuePresenter = value!!
        }

        fun saveSizes()
        {
            titleSize =  title?.textSize ?: 0.0f
            valueSize =  value?.textSize ?: 0.0f
            titleSuffixSize =  titleSuffix?.textSize ?: 0.0f

            titleBounds.left = titleArea?.x ?: 0.0f
            titleBounds.top = titleArea?.y ?: 0.0f
        }
    }

    class ACard
    {
        var up:Boolean = false
        var small = ACardSide()
        var BIG = ACardSide()

        var details: RecyclerView? = null
        var detailsAdapter:CardDetailAdapter? = null

        var detailsArea:LinearLayout? = null

        fun inflate(ctx:Context, card:CardView, CARD:CardView, runtime:Boolean)
        {
            detailsAdapter = CardDetailAdapter(ctx)
            small.inflate(card, runtime)
            BIG.inflate(CARD, false)
            details = CARD.findViewById<RecyclerView>(R.id.cardDetails)
            detailsArea = CARD.findViewById<LinearLayout>(R.id.cardDetailsArea)
            details!!.adapter = detailsAdapter
        }

        var databits = ArrayList<DataBit>()
    }

    var HEART = ACard()
    var ACTIVITY = ACard()
    var SLEEP = ACard()
    var WEATHER = ACard()

    fun setupCard(theCard:ACard, card:CardView, CARD:CardView, runtime:Boolean, up:Boolean)
    {
        theCard.up = up
        theCard.databits.clear()
        theCard.inflate(activity!!, card, CARD, runtime)
        card.setOnClickListener { onOpenDetails(theCard) }
    }

    private fun forceProgressValue(runtime:Boolean, progress:GaugeProgress, value:Float)
    {
        progress.showIntro = false
        progress.animateValue = false
        progress.value = 0f
        progress.showIntro = runtime
        progress.animateValue = runtime
        progress.value = value
    }

    fun updateValues(runtime:Boolean, str:String)
    {
        if (!readyForLoad || !isResumed)
            return

        forceProgressValue(runtime, HEART.small.progress!!, 51f)
        forceProgressValue(runtime, ACTIVITY.small.progress!!, 71f)
        forceProgressValue(runtime, SLEEP.small.progress!!, 3f)
        forceProgressValue(runtime, WEATHER.small.progress!!, 14f)
    }

    override fun onStart() {
        super.onStart()

        var runtime = false
        if (null != activity)
            runtime = activity!!.intent.getBooleanExtra("runtime", false)

        setupCard(HEART, card_tl, cardTL, runtime, false)
        setupCard(ACTIVITY,  card_tr, cardTR, runtime, false)
        setupCard(SLEEP, card_bl, cardBL, runtime, true)
        setupCard(WEATHER, card_br, cardBR, runtime, true)

        btn_topTip.setOnClickListener {onNextTopCard()}
        btn_topDeal.setOnClickListener {onNextTopCard()}

        btn_topHome.setOnClickListener {onPrevTopCard()}
        btn_topTipBack.setOnClickListener {onPrevTopCard()}

        topDots.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                if (position < viewpagerHot.displayedChild)
                {
                    viewpagerHot.setInAnimation(activity, R.anim.left_in)
                    viewpagerHot.setOutAnimation(activity, R.anim.right_out)
                    viewpagerHot.displayedChild = position
                }
                if (position > viewpagerHot.displayedChild)
                {
                    viewpagerHot.setInAnimation(activity, R.anim.right_in)
                    viewpagerHot.setOutAnimation(activity, R.anim.left_out)
                    viewpagerHot.displayedChild = position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val ctx = activity!!
        HEART.databits.add(DataBit(ctx, 0, R.string.card_heart_rate, 0, R.string.card_suffix_match, R.drawable.ic_heart_rate, 55.0))
        HEART.databits.add(DataBit(ctx, 0, R.string.card_blood_pressure, 0, R.string.card_suffix_match, R.drawable.ic_blood_pressure, 77.0))
        HEART.databits.add(DataBit(ctx,0, R.string.card_blood_oxygen, 0, R.string.card_suffix_match, R.drawable.ic_blood_oxygen, 67.0))

        ACTIVITY.databits.add(DataBit(ctx,0, R.string.card_activity_steps, 0, R.string.card_suffix_match, R.drawable.ic_steps, 72.0))
        ACTIVITY.databits.add(DataBit(ctx,0, R.string.card_activity_active, 0, R.string.card_suffix_match, R.drawable.ic_active, 62.0))
        ACTIVITY.databits.add(DataBit(ctx,0, R.string.card_activity_inactive, 0, R.string.card_suffix_match, R.drawable.ic_inactive, 52.0))

        SLEEP.databits.add(DataBit(ctx,0, R.string.card_sleep_bedtime, 0, R.string.card_suffix_match, R.drawable.ic_bed_time, 72.0))
        SLEEP.databits.add(DataBit(ctx,0, R.string.card_sleep_waketime, 0, R.string.card_suffix_match, R.drawable.ic_wake_time, 62.0))
        SLEEP.databits.add(DataBit(ctx,0, R.string.card_sleep_total, 0, R.string.card_suffix_match, R.drawable.ic_total_sleep, 52.0))
        SLEEP.databits.add(DataBit(ctx,0, R.string.card_sleep_quality, 0, R.string.card_suffix_match, R.drawable.ic_sleep_quality, 58.0))

        WEATHER.databits.add(DataBit(ctx,0, R.string.card_weather_temperature, 0, R.string.card_suffix_match, R.drawable.ic_temp, 80.0))
        WEATHER.databits.add(DataBit(ctx,0, R.string.card_weather_pressure, 0, R.string.card_suffix_match, R.drawable.ic_pressure, 72.0))
        WEATHER.databits.add(DataBit(ctx,0, R.string.card_weather_humidity, 0, R.string.card_suffix_match, R.drawable.ic_humidity, 88.0))
        WEATHER.databits.add(DataBit(ctx,0, R.string.card_weather_pollution, 0, R.string.card_suffix_match, R.drawable.ic_air_pollution, 79.0))
    }

    var readyForLoad:Boolean = false

    override fun onResume() {
        super.onResume()

        var runtime = false
        if (null != activity)
            runtime = activity!!.intent.getBooleanExtra("runtime", false)
        updateValues(runtime, "frag:onResume")
    }

    fun onNextTopCard()
    {
        viewpagerHot.setInAnimation(activity, R.anim.right_in)
        viewpagerHot.setOutAnimation(activity, R.anim.left_out)
        viewpagerHot.showNext()
        var dotindex = topDots.selectedTabPosition + 1
        if (dotindex < topDots.tabCount)
            topDots.getTabAt(dotindex)!!.select()
    }

    fun onPrevTopCard()
    {
        viewpagerHot.setInAnimation(activity, R.anim.left_in)
        viewpagerHot.setOutAnimation(activity, R.anim.right_out)
        viewpagerHot.showPrevious()
        var dotindex = topDots.selectedTabPosition - 1
        if (-1 < dotindex)
            topDots.getTabAt(dotindex)!!.select()
    }

    private fun onOpenDetails(theCard:ACard)
    {
        val card = theCard.small.card
        val CARD = theCard.BIG.card
        val progress = theCard.small.progress
        val PROGRESS = theCard.BIG.progress
        val startBounds = RectF()
        val finalBounds = RectF()
        val globalOffset = Point()

        val gc = Point()

        if (null != progress && null != PROGRESS)
            PROGRESS.value = progress.value

        if (null != CARD && null != card)
        {
            val LP = CARD.layoutParams as ViewGroup.MarginLayoutParams

            if (theCard.small.noBounds && theCard.BIG.noBounds)
            {
                card.getGlobalVisibleRect(theCard.small.bounds, gc)
                CARD.setCardBackgroundColor(card.cardBackgroundColor)
                topContainer.getGlobalVisibleRect(theCard.BIG.bounds, globalOffset)

                theCard.small.bounds.offset(-globalOffset.x, -globalOffset.y)
                theCard.BIG.bounds.offset(-globalOffset.x, -globalOffset.y)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    theCard.BIG.bounds.offset(LP.marginStart, LP.topMargin)
                    theCard.BIG.bounds.right -= LP.marginStart + LP.marginEnd
                } else {
                    theCard.BIG.bounds.offset(LP.leftMargin, LP.topMargin)
                    theCard.BIG.bounds.right -= LP.leftMargin + LP.rightMargin
                }
                theCard.BIG.bounds.bottom -= LP.topMargin + LP.bottomMargin

                theCard.small.saveSizes()
                theCard.BIG.saveSizes()
            }

            val startBoundsI = theCard.small.bounds
            val finalBoundsI = theCard.BIG.bounds

            startBounds.set(startBoundsI)
            finalBounds.set(finalBoundsI)

            LP.width = startBoundsI.width()
            LP.height = startBoundsI.height()
            CARD.requestLayout()

            CARD.x = startBounds.left
            CARD.y = startBounds.top

            card.alpha = 0f
            CARD.visibility = View.VISIBLE
            CARD.alpha = 1f

            theCard.detailsArea!!.visibility = View.VISIBLE
            theCard.detailsArea!!.alpha = 0f

            CARD.setOnClickListener { onCloseDetails(theCard) }
            //return

            val anims = AnimatorSet()
            val aX = ObjectAnimator.ofFloat(CARD, "x", startBounds.left, finalBounds.left)
            aX.interpolator = FastOutSlowInInterpolator()

            val aY = ObjectAnimator.ofFloat(CARD, "y", startBounds.top, finalBounds.top)
            aY.interpolator = FastOutSlowInInterpolator()

            val aW = ValueAnimator.ofInt(startBoundsI.width(), finalBoundsI.width())
            aW.interpolator = FastOutSlowInInterpolator()
            aW.addUpdateListener {
                LP.width = it.animatedValue as Int
                CARD.layoutParams = LP
                CARD.requestLayout()
            }

            val aH = ValueAnimator.ofInt(startBoundsI.height(), finalBoundsI.height())
            aH.interpolator = FastOutSlowInInterpolator()
            aH.addUpdateListener {
                LP.height = it.animatedValue as Int
                CARD.layoutParams = LP
                CARD.requestLayout()
            }

            val aE = ObjectAnimator.ofFloat(CARD, "cardElevation", 0.0f, 8.0f)
            aE.duration = 375

            val aT1 = ValueAnimator.ofFloat(theCard.small.titleSize, theCard.BIG.titleSize)
            aT1.addUpdateListener {
                theCard.BIG.title!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float)
            }
            val aT2 = ValueAnimator.ofFloat(theCard.small.valueSize, theCard.BIG.valueSize)
            aT2.addUpdateListener {
                theCard.BIG.value!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float)
            }
            val aT3 = ValueAnimator.ofFloat(theCard.small.titleSuffixSize, theCard.BIG.titleSuffixSize)
            aT3.addUpdateListener {
                theCard.BIG.titleSuffix!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float)
            }
            val aTX = ObjectAnimator.ofFloat(theCard.BIG.titleArea, "x", theCard.small.titleBounds.left, theCard.BIG.titleBounds.left)
            val aTY = ObjectAnimator.ofFloat(theCard.BIG.titleArea, "y", theCard.small.titleBounds.top, theCard.BIG.titleBounds.top)
            aT1.interpolator = FastOutLinearInInterpolator()
            aT2.interpolator = FastOutLinearInInterpolator()
            aT3.interpolator = FastOutLinearInInterpolator()
            aTX.interpolator = FastOutLinearInInterpolator()
            aTY.interpolator = FastOutLinearInInterpolator()
            aT1.duration = 275
            aT2.duration = 275
            aT3.duration = 275
            aTX.duration = 275
            aTY.duration = 275

            val aD = ObjectAnimator.ofFloat(theCard.detailsArea, "alpha", 0.0f, 1.0f)
            aD.interpolator = FastOutLinearInInterpolator()
            aD.duration = 375

            if (theCard.up) {
                aX.duration = 275

                aY.startDelay = 75
                aY.duration = 300

                aW.duration = 275

                aH.startDelay = 75
                aH.duration = 300
            } else {
                aX.startDelay = 75
                aX.duration = 300

                aY.duration = 275

                aW.startDelay = 75
                aW.duration = 300

                aH.duration = 275
            }

            theCard.detailsAdapter!!.mDataSet.clear()

            val aDD = ValueAnimator.ofInt(0, 1)
            aDD.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(p0: Animator?) {
                    var i = 0
                    for (db in theCard.databits)
                    {
                        theCard.detailsAdapter!!.mDataSet.add(db)
                        theCard.detailsAdapter!!.notifyItemInserted(i)
                        i++
                    }
                    theCard.detailsAdapter!!.notifyItemRangeInserted(0, theCard.databits.size)
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })
            aDD.duration = 1
            aDD.startDelay = 300

            anims.play(aX).with(aE).with(aY).with(aW).with(aH).with(aT1).with(aT2).with(aT3).with(aTX).with(aTY).with(aD).with(aDD)
            //anims.play(aDD).after(aH)
            if (null != progress && null != PROGRESS)
            {
                PROGRESS.layoutParams.width = progress.width
                PROGRESS.layoutParams.height = progress.height
                PROGRESS.requestLayout()

                val e = ValueAnimator.ofInt(progress.height, PROGRESS.height)
                e.duration = 300
                e.interpolator = FastOutSlowInInterpolator()
                e.addUpdateListener {
                    PROGRESS.layoutParams.width = (it.animatedValue as Int)
                    PROGRESS.layoutParams.height = (it.animatedValue as Int)
                    PROGRESS.requestLayout()
                }
                anims.play(e)
            }
            anims.start()
        }
    }

    private fun onCloseDetails(theCard:ACard)
    {
        val card = theCard.small.card
        val CARD = theCard.BIG.card
        val progress = theCard.small.progress
        val PROGRESS = theCard.BIG.progress

        val startBounds = RectF()
        val finalBounds = RectF()

        if (null != CARD && null != card)
        {
            val startBoundsI = theCard.small.bounds
            val finalBoundsI = theCard.BIG.bounds
            val lp = CARD.layoutParams as ViewGroup.MarginLayoutParams

            var PW = 0
            var PH = 0
            if (null != PROGRESS)
            {
                PW = PROGRESS.width
                PH = PROGRESS.height
            }

            startBounds.set(startBoundsI)
            finalBounds.set(finalBoundsI)

            val anims = AnimatorSet()
            val aX = ObjectAnimator.ofFloat(CARD, View.X, finalBounds.left, startBounds.left)
            aX.interpolator = FastOutSlowInInterpolator()

            val aY = ObjectAnimator.ofFloat(CARD, View.Y, finalBounds.top, startBounds.top)
            aY.interpolator = FastOutSlowInInterpolator()

            val aW = ValueAnimator.ofInt(finalBoundsI.width(), startBoundsI.width())
            aW.interpolator = FastOutSlowInInterpolator()
            aW.addUpdateListener {
                lp.width = (it.animatedValue as Int)
                CARD.layoutParams = lp
                CARD.requestLayout()
            }

            val aH = ValueAnimator.ofInt(finalBoundsI.height(), startBoundsI.height())
            aH.interpolator = FastOutSlowInInterpolator()
            aH.addUpdateListener {
                lp.height = (it.animatedValue as Int)
                CARD.layoutParams = lp
                CARD.requestLayout()
            }

            val aE = ObjectAnimator.ofFloat(CARD, "cardElevation", 8.0f, 0.0f)
            aE.duration = 375

            val aT1 = ValueAnimator.ofFloat(theCard.BIG.titleSize, theCard.small.titleSize)
            aT1.addUpdateListener {
                theCard.BIG.title!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float)
            }
            val aT2 = ValueAnimator.ofFloat(theCard.BIG.valueSize, theCard.small.valueSize)
            aT2.addUpdateListener {
                theCard.BIG.value!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float)
            }
            val aT3 = ValueAnimator.ofFloat(theCard.BIG.titleSuffixSize, theCard.small.titleSuffixSize)
            aT3.addUpdateListener {
                theCard.BIG.titleSuffix!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float)
            }
            val aTX = ObjectAnimator.ofFloat(theCard.BIG.titleArea, "x", theCard.BIG.titleBounds.left, theCard.small.titleBounds.left)
            val aTY = ObjectAnimator.ofFloat(theCard.BIG.titleArea, "y", theCard.BIG.titleBounds.top, theCard.small.titleBounds.top)
            aT1.interpolator = FastOutLinearInInterpolator()
            aT2.interpolator = FastOutLinearInInterpolator()
            aT3.interpolator = FastOutLinearInInterpolator()
            aTX.interpolator = FastOutLinearInInterpolator()
            aTY.interpolator = FastOutLinearInInterpolator()
            aT1.duration = 275
            aT2.duration = 275
            aT3.duration = 275
            aTX.duration = 275
            aTY.duration = 275

            val aD = ObjectAnimator.ofFloat(theCard.detailsArea, "alpha", 1.0f, 0.0f)
            aD.interpolator = LinearOutSlowInInterpolator()
            aD.duration = 375

            if (theCard.up) {
                aX.startDelay = 75
                aX.duration = 300

                aY.duration = 275

                aW.startDelay = 75
                aW.duration = 300

                aH.duration = 275
            } else {
                aX.duration = 275

                aY.startDelay = 75
                aY.duration = 300

                aW.duration = 275

                aH.startDelay = 75
                aH.duration = 300
            }

            anims.play(aX).with(aE).with(aY).with(aW).with(aH).with(aT1).with(aT2).with(aT3).with(aTX).with(aTY).with(aD)
            if (null != progress && null != PROGRESS) {
                val e = ValueAnimator.ofInt(PH, progress.height)
                e.duration = 375
                e.interpolator = FastOutSlowInInterpolator()
                e.addUpdateListener {
                    PROGRESS.layoutParams.width = (it.animatedValue as Int)
                    PROGRESS.layoutParams.height = (it.animatedValue as Int)
                    PROGRESS.requestLayout()
                }
                e.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(p0: Animator?) {
                        theCard.detailsAdapter!!.mDataSet.clear()
                        theCard.detailsAdapter!!.notifyItemRangeRemoved(0,theCard.databits.size)
                        card.alpha = 1f
                        CARD.visibility = View.INVISIBLE
                        PROGRESS.layoutParams.width = PW
                        PROGRESS.layoutParams.height = PH
                        PROGRESS.requestLayout()
                        theCard.detailsArea!!.visibility = View.INVISIBLE
                        theCard.detailsArea!!.alpha = 0f
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }
                })
                anims.play(e)
            }
            anims.start()
        }
    }
}
