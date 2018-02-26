package com.chepel.krug

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_habits_intro.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HabitsIntroFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HabitsIntroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HabitsIntroFragment : Fragment() {
    interface OnFragmentInteractionListener {
        fun onStartSurvey()
        fun onHowWorks()
        fun onSkip()
        fun onDone()
    }

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_habits_intro, container, false)
        return v
    }

    override fun onStart() {
        super.onStart()
        btn_how_works.setOnClickListener {
            if(null != mListener)
                mListener!!.onHowWorks()
        }
        btnOK.setOnClickListener {
            if(null != mListener)
                mListener!!.onStartSurvey()
        }
        btn_skip.setOnClickListener {
            if(null != mListener)
                mListener!!.onSkip()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

}// Required empty public constructor
