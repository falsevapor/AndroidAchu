package com.chepel.krug

import android.animation.*
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
import android.util.Log
import android.view.animation.AnimationUtils
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import kotlinx.android.synthetic.main.sickness_prediction.*
import kotlinx.android.synthetic.main.sleep_bank.*
import kotlinx.android.synthetic.main.health_zone.*
import kotlinx.android.synthetic.main.rewards_tracker.*
import android.support.design.widget.TabLayout




/**
 * A placeholder fragment containing a simple view.
 */
class DashboardFragment : Fragment() {

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

    override fun onStart() {
        super.onStart()

        var runtime = false
        if (null != activity)
            runtime = activity!!.intent.getBooleanExtra("runtime", false)

        progress_tl.animateValue = runtime
        progress_tr.animateValue = runtime
        progress_bl.animateValue = runtime
        progress_br.animateValue = runtime
        progress_tl.showIntro = runtime
        progress_tr.showIntro = runtime
        progress_bl.showIntro = runtime
        progress_br.showIntro = runtime

        card_tl.setOnClickListener { onOpenDetails(card_tl, cardTL, progress_tl, progressTL, false) }
        card_tr.setOnClickListener { onOpenDetails(card_tr, cardTR, progress_tr, progressTR, false) }
        card_bl.setOnClickListener { onOpenDetails(card_bl, cardBL, progress_bl, progressBL, true) }
        card_br.setOnClickListener { onOpenDetails(card_br, cardBR, progress_br, progressBR, true) }

        progress_tl.value = 51f
        progress_tr.value = 0f
        progress_bl.value = 77f
        progress_br.value = 14f

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

    private fun onOpenDetails(card:CardView, CARD:CardView, progress: GaugeProgress?, PROGRESS: GaugeProgress, up:Boolean)
    {
        val startBoundsI = Rect()
        val finalBoundsI = Rect()
        val startBounds = RectF()
        val finalBounds = RectF()
        val globalOffset = Point()

        val gc = Point()
        val GC = Point()
        val SBI = Rect()

        if (null != progress)
            PROGRESS.value = progress.value

        //card.getLocalVisibleRect(startBoundsI)
        card.getGlobalVisibleRect(startBoundsI, gc)
        //val cardcolor = card.cardBackgroundColor
        CARD.setCardBackgroundColor(card.cardBackgroundColor)
        //CARD.getLocalVisibleRect(finalBoundsI)
        CARD.getGlobalVisibleRect(SBI, GC)
        topContainer.getGlobalVisibleRect(finalBoundsI, globalOffset)

        Log.i("rectz", "start: $startBoundsI ($startBounds); end: $finalBoundsI($finalBounds); global pos: $globalOffset; CARD: $SBI; offset: $GC ")

        startBoundsI.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsI.offset(-globalOffset.x, -globalOffset.y)


        val lp = CARD.layoutParams as ViewGroup.MarginLayoutParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            finalBoundsI.offset(lp.marginStart, lp.topMargin)
            finalBoundsI.right -= lp.marginStart + lp.marginEnd
        }
        else
        {
            finalBoundsI.offset(lp.leftMargin, lp.topMargin)
            finalBoundsI.right -= lp.leftMargin + lp.rightMargin
        }
        finalBoundsI.bottom -= lp.topMargin + lp.bottomMargin

        startBounds.set(startBoundsI)
        finalBounds.set(finalBoundsI)

        Log.i("rectz", "start: $startBoundsI ($startBounds); end: $finalBoundsI($finalBounds); global pos: $globalOffset ")

        lp.width = startBoundsI.width()
        lp.height = startBoundsI.height()
        CARD.requestLayout()

        CARD.x = startBounds.left
        CARD.y = startBounds.top

        card.alpha = 0f
        CARD.visibility = View.VISIBLE
        CARD.alpha = 1f

        CARD.setOnClickListener { onCloseDetails(card, CARD, startBoundsI, finalBoundsI, up) }
        //return


        val anims = AnimatorSet()
        /*
        val aX = ObjectAnimator.ofFloat(CARD, View.X, startBounds.left, finalBounds.left)
        //a.interpolator = LinearOutSlowInInterpolator()
        //a.interpolator = FastOutSlowInInterpolator()
        aX.interpolator = FastOutSlowInInterpolator()

        val aY = ObjectAnimator.ofFloat(CARD, View.Y, startBounds.top, finalBounds.top)
        aY.interpolator = FastOutSlowInInterpolator()
        */
        val aX = ValueAnimator.ofFloat(startBounds.left, finalBounds.left)
        aX.interpolator = FastOutSlowInInterpolator()
        aX.addUpdateListener {
            val iw:Float = it.animatedValue as Float
            //Log.i("sizez", "x:$iw")
            CARD.x = iw
        }

        val aY = ValueAnimator.ofFloat(startBounds.top, finalBounds.top)
        aY.interpolator = FastOutSlowInInterpolator()
        aY.addUpdateListener {
            val ih:Float = it.animatedValue as Float
            //Log.i("sizez", "        y:$ih")
            CARD.y = ih
        }

        val aW = ValueAnimator.ofInt(startBoundsI.width(),finalBoundsI.width())
        aW.interpolator = FastOutSlowInInterpolator()
        aW.addUpdateListener {
            val iw:Int = it.animatedValue as Int
            //Log.i("sizez", "                w:$iw")
            lp.width = iw
            CARD.layoutParams = lp
            CARD.requestLayout()
        }

        val aH = ValueAnimator.ofInt(startBoundsI.height(),finalBoundsI.height())
        aH.interpolator = FastOutSlowInInterpolator()
        aH.addUpdateListener {
            val ih:Int = it.animatedValue as Int
            //Log.i("sizez", "                        h:$ih")
            lp.height = ih
            CARD.layoutParams = lp
            CARD.requestLayout()
        }

        if (up)
        {
            aX.duration = 275

            aY.startDelay = 75
            aY.duration = 300

            aW.duration = 275

            aH.startDelay = 75
            aH.duration = 300
        }
        else
        {
            aX.startDelay = 75
            aX.duration = 300

            aY.duration = 275

            aW.startDelay = 75
            aW.duration = 300

            aH.duration = 275
        }

        anims.play(aX).with(aY).with(aW).with(aH)
        if (null != progress)
        {
            val e = ValueAnimator.ofInt(progress.height, (progress.height * 1).toInt())
            e.duration = 375
            e.interpolator = FastOutSlowInInterpolator()
            e.addUpdateListener {
                PROGRESS.layoutParams.height = (it.animatedValue as Int)
                PROGRESS.requestLayout()
            }
            anims.play(e)
        }
        anims.start()

//        CARD.setOnClickListener { onCloseDetails(card, CARD, startBoundsI, finalBoundsI, up) }
    }

    private fun onCloseDetails(card:CardView, CARD:CardView, startBoundsI:Rect, finalBoundsI:Rect, up:Boolean)
    {
        val startBounds = RectF()
        val finalBounds = RectF()
        val lp = CARD.layoutParams as ViewGroup.MarginLayoutParams

        startBounds.set(startBoundsI)
        finalBounds.set(finalBoundsI)

        val anims = AnimatorSet()
        val aX = ObjectAnimator.ofFloat(CARD, View.X, finalBounds.left, startBounds.left)
        //a.interpolator = LinearOutSlowInInterpolator()
        //a.interpolator = FastOutSlowInInterpolator()
        aX.interpolator = FastOutSlowInInterpolator()
        aX.addListener(object : Animator.AnimatorListener
        {
            override fun onAnimationEnd(p0: Animator?)
            {
                //card.visibility = View.INVISIBLE
                card.alpha = 1f
                CARD.visibility = View.GONE
            }
            override fun onAnimationCancel(p0: Animator?) {
            }
            override fun onAnimationRepeat(p0: Animator?) {
            }
            override fun onAnimationStart(p0: Animator?) {
            }
        })

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

        /*
        val e = ValueAnimator.ofInt(progress.height,(progress.height * 1).toInt())
        e.duration = 375
        e.interpolator = FastOutSlowInInterpolator()
        e.addUpdateListener {
            PROGRESS.layoutParams.height = (it.animatedValue as Int)
            PROGRESS.requestLayout()
        }
        */
        if (up)
        {
            aX.startDelay = 75
            aX.duration = 300

            aY.duration = 275

            aW.startDelay = 75
            aW.duration = 300

            aH.duration = 275
        }
        else
        {
            aX.duration = 275

            aY.startDelay = 75
            aY.duration = 300

            aW.duration = 275

            aH.startDelay = 75
            aH.duration = 300
        }

        anims.play(aX).with(aY).with(aW).with(aH)/*.with(e)*/
        anims.start()
    }
}
