package com.chepel.krug

import android.animation.*
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.CardView


/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onStart() {
        super.onStart()

        card_tl!!.setOnClickListener { onOpenDetails(card_tl!!, cardTL!!, progress_tl, progressTL, false) }
        card_tr!!.setOnClickListener { onOpenDetails(card_tr!!, cardTR!!, progress_tr, progressTR, false) }
        card_bl!!.setOnClickListener { onOpenDetails(card_bl!!, cardBL!!, progress_bl, progressBL, true) }
        card_br!!.setOnClickListener { onOpenDetails(card_br!!, cardBR!!, progress_br, progressBR, true) }

        progress_tl.value = 67f
        progress_tr.value = 89f
        progress_bl.value = 77f
        progress_br.value = 14f
        btnAni0.setOnClickListener {
            progress_tl.value = 50f
            progress_tr.value = 0f
            progress_bl.value = 50f
            progress_br.value = 0f
        }
        btnAni38.setOnClickListener {
            progress_tl.value = 60f
            progress_tr.value = 25f
            progress_bl.value = 55f
            progress_br.value = 30f
        }
        btnAni62.setOnClickListener {
            progress_tl.value = 75f
            progress_tr.value = 50f
            progress_bl.value = 63f
            progress_br.value = 48f
        }
        btnAni89.setOnClickListener {
            progress_tl.value = 90f
            progress_tr.value = 75f
            progress_bl.value = 95f
            progress_br.value = 81f
        }
        btnAni100.setOnClickListener {
            progress_tl.value = 100f
            progress_tr.value = 100f
            progress_bl.value = 100f
            progress_br.value = 100f
        }
    }

    private fun onOpenDetails(card:CardView, CARD:CardView, progress: KrugProgress, PROGRESS: KrugProgress, up:Boolean)
    {
        val startBoundsI = Rect()
        val finalBoundsI = Rect()
        val startBounds = RectF()
        val finalBounds = RectF()
        val globalOffset = Point()

        PROGRESS.value = progress.value

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        card.getGlobalVisibleRect(startBoundsI)
        topContainer.getGlobalVisibleRect(finalBoundsI, globalOffset)
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

        CARD.x = startBounds.left
        CARD.y = startBounds.top
        lp.width = startBoundsI.width()
        lp.height = startBoundsI.height()
        CARD.layoutParams = lp
        CARD.requestLayout()

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        card.alpha = 0f
        CARD.visibility = View.VISIBLE

        val anims = AnimatorSet()
        val aX = ObjectAnimator.ofFloat(CARD, View.X, startBounds.left, finalBounds.left)
        //a.interpolator = LinearOutSlowInInterpolator()
        //a.interpolator = FastOutSlowInInterpolator()
        //aX.interpolator = FastOutSlowInInterpolator()
        aX.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
            }
            override fun onAnimationCancel(p0: Animator?) {
            }
            override fun onAnimationRepeat(p0: Animator?) {
            }
            override fun onAnimationStart(p0: Animator?) {
            }
        })

        val aY = ObjectAnimator.ofFloat(CARD, View.Y, startBounds.top, finalBounds.top)
        //aY.interpolator = FastOutSlowInInterpolator()

        val aW = ValueAnimator.ofInt(startBoundsI.width(),finalBoundsI.width())
        //aW.interpolator = FastOutSlowInInterpolator()
        aW.addUpdateListener {
            lp.width = (it.animatedValue as Int)
            CARD.layoutParams = lp
            CARD.requestLayout()
        }

        val aH = ValueAnimator.ofInt(startBoundsI.height(),finalBoundsI.height())
        //aH.interpolator = FastOutSlowInInterpolator()
        aH.addUpdateListener {
            lp.height = (it.animatedValue as Int)
            CARD.layoutParams = lp
            CARD.requestLayout()
        }

        val e = ValueAnimator.ofInt(progress.height,(progress.height * 1).toInt())
        e.duration = 375
        e.interpolator = FastOutSlowInInterpolator()
        e.addUpdateListener {
            PROGRESS.layoutParams.height = (it.animatedValue as Int)
            PROGRESS.requestLayout()
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

        anims.play(aX).with(aY).with(aW).with(aH).with(e)
        anims.start()

        CARD.setOnClickListener { onCloseDetails(card, CARD, startBoundsI, finalBoundsI, up) }
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
        //aX.interpolator = FastOutSlowInInterpolator()
        aX.addListener(object : Animator.AnimatorListener
        {
            override fun onAnimationEnd(p0: Animator?)
            {
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
        //aY.interpolator = FastOutSlowInInterpolator()

        val aW = ValueAnimator.ofInt(finalBoundsI.width(), startBoundsI.width())
        //aW.interpolator = FastOutSlowInInterpolator()
        aW.addUpdateListener {
            lp.width = (it.animatedValue as Int)
            CARD.layoutParams = lp
            CARD.requestLayout()
        }

        val aH = ValueAnimator.ofInt(finalBoundsI.height(), startBoundsI.height())
        //aH.interpolator = FastOutSlowInInterpolator()
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
