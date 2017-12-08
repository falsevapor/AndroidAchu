package com.chepel.krug

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.widget.ProgressBar



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
        btnAni.setOnClickListener {
            val animation = ObjectAnimator.ofFloat(progrezz, "progress", 0.0f, 89.0f)
            animation.duration = 400 //in milliseconds
            animation.interpolator = FastOutSlowInInterpolator()
            animation.start()
        }
    }

}
