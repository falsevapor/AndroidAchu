package com.chepel.krug

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_today.*

class TodayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        btnOK.setOnClickListener { view ->
            Toast.makeText(this, "Good morning", Toast.LENGTH_SHORT).show()
        }
        //fab.setOnClickListener { view ->
        //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //            .setAction("Action", null).show()
        //}
    }

}
