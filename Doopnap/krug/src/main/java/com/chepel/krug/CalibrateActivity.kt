package com.chepel.krug

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_calibrate.*
import kotlinx.android.synthetic.main.content_calibrate.*

class CalibrateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibrate)

        setSupportActionBar(calibration_toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        btnOK.setOnClickListener { view ->
            onAccept()
        }

        symp1.setOnClickListener{ onTapSymptom(symp1, chk1) }
        symp2.setOnClickListener{ onTapSymptom(symp2, chk2) }
        symp3.setOnClickListener{ onTapSymptom(symp3, chk3) }
        symp4.setOnClickListener{ onTapSymptom(symp4, chk4) }
        symp5.setOnClickListener{ onTapSymptom(symp5, chk5) }
        symp6.setOnClickListener{ onTapSymptom(symp6, chk6) }
        symp7.setOnClickListener{ onTapSymptom(symp7, chk7) }
        symp8.setOnClickListener{ onTapSymptom(symp8, chk8) }
        symp9.setOnClickListener{ onTapSymptom(symp9, chk9) }
    }

    fun onTapSymptom(card: CardView, fab: FloatingActionButton)
    {
        card.isSelected = !card.isSelected

        if (card.isSelected)
        {
            fab.visibility = View.VISIBLE
            val anims = AnimatorSet()
            val a = ObjectAnimator.ofFloat(fab, "alpha", 0f, 1f)
            val x = ObjectAnimator.ofFloat(card, "scaleX", 1f, 0.9f)
            val y = ObjectAnimator.ofFloat(card, "scaleY", 1f, 0.9f)
            val o = ObjectAnimator.ofFloat(card, "alpha", 1f, 0.5f)
            a.duration = 250
            x.duration = 250
            y.duration = 250
            o.duration = 250
            a.interpolator = FastOutSlowInInterpolator()
            x.interpolator = FastOutSlowInInterpolator()
            y.interpolator = FastOutSlowInInterpolator()
            o.interpolator = FastOutSlowInInterpolator()
            anims.play(a).with(x).with(y).with(o)
            anims.start()
        }
        else
        {
            val anims = AnimatorSet()
            val a = ObjectAnimator.ofFloat(fab, "alpha", 1f, 0f)
            val x = ObjectAnimator.ofFloat(card, "scaleX", 0.9f, 1f)
            val y = ObjectAnimator.ofFloat(card, "scaleY", 0.9f, 1f)
            val o = ObjectAnimator.ofFloat(card, "alpha", 0.5f, 1f)
            a.duration = 250
            x.duration = 250
            y.duration = 250
            o.duration = 250
            a.interpolator = FastOutSlowInInterpolator()
            x.interpolator = FastOutSlowInInterpolator()
            y.interpolator = FastOutSlowInInterpolator()
            o.interpolator = FastOutSlowInInterpolator()
            a.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(p0: Animator?) {
                    fab.visibility = View.GONE
                }
                override fun onAnimationCancel(p0: Animator?) {
                }
                override fun onAnimationRepeat(p0: Animator?) {
                }
                override fun onAnimationStart(p0: Animator?) {
                }
            })
            anims.play(a).with(x).with(y).with(o)
            anims.start()
        }
    }

    fun onAccept()
    {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == android.R.id.home)
        {
            finish()
            return true
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item)
    }

}
