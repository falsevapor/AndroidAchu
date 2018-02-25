package com.chepel.krug

import android.content.Intent
import android.net.Uri
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
import android.widget.Toast
import com.chepel.krug.HabitsIntroFragment.OnFragmentInteractionListener

import kotlinx.android.synthetic.main.activity_habits.*
import kotlinx.android.synthetic.main.fragment_habits.view.*

class HabitsActivity : AppCompatActivity(), OnFragmentInteractionListener {

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
        HabitsStepFragment.texts.add(HabitsStepFragment.Companion.Text(getString(R.string.habit_title_1), getString(R.string.habit_slogan_1), getString(R.string.next)))
        HabitsStepFragment.texts.add(HabitsStepFragment.Companion.Text(getString(R.string.habit_title_2), getString(R.string.habit_slogan_2), getString(R.string.next)))
        HabitsStepFragment.texts.add(HabitsStepFragment.Companion.Text(getString(R.string.habit_title_3), getString(R.string.habit_slogan_3), getString(R.string.next)))
        HabitsStepFragment.texts.add(HabitsStepFragment.Companion.Text(getString(R.string.habit_title_4), getString(R.string.habit_slogan_4), getString(R.string.done)))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habits)

        setSupportActionBar(habits_toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
    }

    override fun onStartSurvey() {
        //val intent = Intent(this,  MainActivity::class.java)
        //intent.putExtra("runtime", true)
        //startActivity(intent)
        //finish()
    }
    override fun onHowWorks() {
        /*
        val intent = Intent(this,  MainActivity::class.java)
        intent.putExtra("runtime", true)
        startActivity(intent)
        finish()*/
    }

    override fun onSkip() {
        val intent = Intent(this,  MainActivity::class.java)
        intent.putExtra("runtime", true)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //if (id == R.id.action_settings) {
        //    return true
        //}

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
            val nsteps = HabitsStepFragment.texts.size
            val rootView = inflater.inflate(R.layout.fragment_habits, container, false)
            val text = HabitsStepFragment.texts.get(step)
            rootView.stepTitle.text = text.title
            rootView.stepSlogan.text = text.slogan
            rootView.btnOK.text = text.next
            rootView.step.text = "$step"
            rootView.steps.text = "/$nsteps"
            rootView.btnOK.setOnClickListener { onNext(step) }

            return rootView
        }

        private fun onNext(position:Int)
        {

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
