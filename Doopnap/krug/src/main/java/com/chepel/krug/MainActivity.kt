package com.chepel.krug

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Button
import android.widget.Toast
import com.chepel.krug.dummy.CouponsContent
import com.chepel.krug.dummy.TipsContent

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.util.*
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.RatingBar


class MainActivity :
        AppCompatActivity(),
        RadarFragment.OnRadarInteractionListener,
        RewardsFragment.OnRewardsInteractionListener,
        CouponFragment.OnCouponInteractionListener,
        TipFragment.OnTipsInteractionListener
{
    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private val dash = DashboardFragment()
    private val radar = RadarFragment()
    private val tips = TipFragment()
    private val offers = CouponFragment()
    private val rewards = RewardsFragment()

    private var btns = mutableListOf<Button>()
    private val titles = intArrayOf(R.string.title_dashboard, R.string.title_radar, R.string.title_tips, R.string.title_offers, R.string.title_rewards)

    var runtime:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val finish = intent.getBooleanExtra("sign_out", false)
        if (finish)
        {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        runtime = intent.getBooleanExtra("runtime", false)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        btn_dash.isSelected = true

        btn_dash.setOnClickListener { onTab(0) }
        btn_radar.setOnClickListener { onTab(1) }
        btn_tips.setOnClickListener { onTab(2) }
        btn_offers.setOnClickListener { onTab(3) }
        btn_rewards.setOnClickListener { onTab(4) }

        //navigation1.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        viewpager.adapter = mSectionsPagerAdapter

        viewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                selectBottomTabButton(position)
            }
        })
    }

    override fun onStart() {
        super.onStart()

        btns.add(btn_dash)
        btns.add(btn_radar)
        btns.add(btn_tips)
        btns.add(btn_offers)
        btns.add(btn_rewards)
    }

    private var showGoodMorning:Boolean = true

    override fun onResume() {
        super.onResume()

        val me = My(this)
        me.load()

        val goodMorning = intent.getBooleanExtra("goodMorning", false)

        if (goodMorning && showGoodMorning)
        {
            val cal = Calendar.getInstance()
            val h = cal.get(Calendar.HOUR_OF_DAY)
            var greet = getString(R.string.hello)
            when (h)
            {
                in 4..10 -> greet = getString(R.string.goodMorning)
                in 11..13 -> greet = getString(R.string.goodDay)
                in 14..16 -> greet = getString(R.string.goodAfternoon)
                in 17..20 -> greet = getString(R.string.goodEvening)
            }

            Toast.makeText(this, greet, Toast.LENGTH_SHORT).show()
            //val intent = Intent(this,  TodayActivity::class.java)
            //startActivity(intent)
            showGoodMorning = false
            showTodayDialog()
        }
        else
            initUI("onResume")
    }

    private fun showTodayDialog()
    {
        val builder = AlertDialog.Builder(this)
        // Get the layout inflater
        val inflater = layoutInflater

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.activity_today, null))
        val d = builder.create()
        d.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        d.show()
        val btnOk = d.findViewById<Button>(R.id.btnOK)
        val btnSkip = d.findViewById<Button>(R.id.btn_skip)
        val btnReport = d.findViewById<Button>(R.id.btnReport)
        val rbEnergy = d.findViewById<RatingBar>(R.id.ratingBarEnergy)
        val rbWellness = d.findViewById<RatingBar>(R.id.ratingBarWellness)
        btnOk?.setOnClickListener { view ->
            Toast.makeText(this, "Thank you for report", Toast.LENGTH_SHORT).show()
            onTodayDialogResult(d, R.id.btnOK)
        }
        btnReport?.setOnClickListener { view ->
            onTodayDialogResult(d, R.id.btnReport)
            Toast.makeText(this, "Identify new symptoms", Toast.LENGTH_SHORT).show()
        }
        btnSkip?.setOnClickListener { view ->
            onTodayDialogResult(d, R.id.btn_skip)
        }
    }

    fun onTodayDialogResult(d:AlertDialog, n:Int)
    {
        val anims = AnimatorSet()
        val a = ValueAnimator.ofInt(0,1)
        a.duration = 450
        a.addListener(object : Animator.AnimatorListener
        {
            override fun onAnimationEnd(p0: Animator?)
            {
                when(n)
                {
                    R.id.btnOK -> onTodayReport()
                    R.id.btnReport -> onTodayNewSymptoms()
                }
                d.dismiss()
                initUI("onTodayDialogResult")
            }
            override fun onAnimationCancel(p0: Animator?) {
            }
            override fun onAnimationRepeat(p0: Animator?) {
            }
            override fun onAnimationStart(p0: Animator?) {
            }
        })
        anims.play(a)
        anims.start()
    }

    fun initUI(str:String)
    {
        dash.readyForLoad = true
        dash.updateValues(runtime, "act:initUI:"+str)
    }

    fun onTodayReport()
    {

    }

    fun onTodayNewSymptoms()
    {
        onShowCalibration()
    }


    fun selectBottomTabButton(n:Int)
    {
        if (0 > n || btns.size <= n)
            return
        for (btn in btns)
            btn.isSelected = false
        btns[n].isSelected = true

        the_title.text = getString(titles[n])
    }

    private fun onTab(n:Int)
    {
        selectBottomTabButton(n)
        viewpager.currentItem = n
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == R.id.action_calibrate)
        {
            return onShowCalibration()
        }
        if (item.itemId == android.R.id.home)
        {
            return onShowSettings()
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item)
    }

    private fun onShowSettings():Boolean
    {
        val intent = Intent(this,  SettingsActivity::class.java)
        intent.putExtra("Settings extra", "Logeen sukses")
        startActivity(intent)

        return true
    }

    private fun onShowCalibration():Boolean
    {
        val intent = Intent(this,  CalibrateActivity::class.java)
        startActivity(intent)

        return true
    }

    override fun onRewardsInteraction(uri: Uri) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRadarInteraction(uri: Uri) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTipsInteraction(item: TipsContent.Tip) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCouponInteraction(item: CouponsContent.Coupon) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> dash//DashboardFragment()
                1 -> radar
                2 -> tips
                3 -> offers
                4 -> rewards
                else -> dash
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 5
        }
    }
}
