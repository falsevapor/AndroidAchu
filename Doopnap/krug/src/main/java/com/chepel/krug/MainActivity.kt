package com.chepel.krug

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
import com.chepel.krug.dummy.CouponsContent
import com.chepel.krug.dummy.TipsContent

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

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

    //val btns = arrayOf(btn_dash, btn_radar, btn_tips,  btn_offers, btn_rewards)

    /*
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_dashboard -> {
                viewpager.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tips -> {
                viewpager.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_offers -> {
                viewpager.currentItem = 2
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_radar -> {
                viewpager.currentItem = 3
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_rewards -> {
                viewpager.currentItem = 3
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    */

    var btns = mutableListOf<Button>()
    val titles = intArrayOf(R.string.title_dashboard, R.string.title_radar, R.string.title_tips, R.string.title_offers, R.string.title_rewards)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val finish = intent.getBooleanExtra("sign_out", false)
        if (finish)
        {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

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

        /*
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        */
    }

    override fun onStart() {
        super.onStart()

        btns.add(btn_dash)
        btns.add(btn_radar)
        btns.add(btn_tips)
        btns.add(btn_offers)
        btns.add(btn_rewards)
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

    fun onTab(n:Int)
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

    fun onShowSettings():Boolean
    {
        val intent = Intent(this,  SettingsActivity::class.java)
        intent.putExtra("Settings extra", "Logeen sukses")
        startActivity(intent)

        return true
    }

    fun onShowCalibration():Boolean
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

    override fun onCoupunInteraction(item: CouponsContent.Coupon) {
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
