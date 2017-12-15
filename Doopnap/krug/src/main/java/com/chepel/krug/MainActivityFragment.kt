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
        progrezz.value = 67f
        progrezz2.value = 89f
        btnAni0.setOnClickListener {
            progrezz.value = 50f
            progrezz2.value = 0f
        }
        btnAni38.setOnClickListener {
            progrezz.value = 60f
            progrezz2.value = 25f
        }
        btnAni62.setOnClickListener {
            progrezz.value = 75f
            progrezz2.value = 50f
        }
        btnAni89.setOnClickListener {
            progrezz.value = 90f
            progrezz2.value = 75f
        }
        btnAni100.setOnClickListener {
            progrezz.value = 100f
            progrezz2.value = 100f
        }
    }

}
