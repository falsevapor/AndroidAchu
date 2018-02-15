package com.chepel.krug

import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.activity_habits.*

class HabitsActivity : AppCompatActivity() {

    private val intro = HabitsIntroFragment()

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        HabitsStepFragment.texts.add(HabitsStepFragment.Companion.Text(getString(R.string.)))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habits)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_habits, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm)
    {
        override fun getItem(position: Int): Fragment
        {
            if (0 == position)
            {
                return intro
            }

            return HabitsStepFragment.newInstance(position)
        }

        override fun getCount(): Int
        {
            return 5
        }
    }

    class HabitsStepFragment: Fragment()
    {
        var step:Int = 0
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
        {
            val rootView = inflater.inflate(R.layout.fragment_habits, container, false)
            //rootView.section_label.text = getString(R.string.section_format, arguments.getInt(ARG_SECTION_NUMBER))

            return rootView
        }

        companion object
        {
            class Text (var title:String, var slogan:String, var next:String)
            {
            }
            var texts = ArrayList<Text>()

            fun newInstance(position: Int): HabitsStepFragment {
                val fragment = HabitsStepFragment()
                fragment.step = position
                return fragment
            }
        }
    }
}
