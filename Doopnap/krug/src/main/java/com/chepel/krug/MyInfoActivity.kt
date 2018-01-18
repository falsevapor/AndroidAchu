package com.chepel.krug

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_my_info.*
import kotlinx.android.synthetic.main.content_my_info.*
import android.content.DialogInterface
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import java.util.*
import android.app.Activity
import android.preference.PreferenceManager
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView


class MyInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_info)

        setSupportActionBar(myinfo_toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        btn_ok.setOnClickListener { view ->
            save()
            StartMainActivity()
        }

        btn_skip.setOnClickListener { StartMainActivity() }

        my_sex.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> if (hasFocus) onSex() }
        my_sex.setOnClickListener { onSex() }

        my_age.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> if (hasFocus) onAge() }
        my_age.setOnClickListener { onAge() }

        my_weight.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> if (hasFocus) onWeight() }
        my_weight.setOnClickListener { onWeight() }

        my_height.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> if (hasFocus) onHeight() }
        my_height.setOnClickListener { onHeight() }

        switch_units.setOnCheckedChangeListener { bv, isChecked -> onUnits(isChecked) }
    }

    override fun onResume() {
        super.onResume()

        readPrefs()

        val register = intent.getBooleanExtra("register", false)
        val vis = if (register) View.VISIBLE else View.GONE
        btn_skip.visibility = vis
        btn_ok.visibility = vis

        lbl_units.visibility = vis
        lbl_imperial.visibility = vis
        switch_units.visibility = vis
        lbl_metric.visibility = vis
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == android.R.id.home)
        {
            if (!intent.getBooleanExtra("register", false))
            {
                save()
            }
            finish()
            return true
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item)
    }

    fun StartMainActivity()
    {
        val intent = Intent(this,  MainActivity::class.java)
        intent.putExtra("Login extra", "Logeen sukses")
        startActivity(intent)
        finish()
    }

    fun readPrefs()
    {
        val p = PreferenceManager.getDefaultSharedPreferences(this)
        switch_units.isChecked = ("0" == p.getString(getString(R.string.opts_units), "0"))
    }

    fun save()
    {
        val p = PreferenceManager.getDefaultSharedPreferences(this).edit()
        p.putString(getString(R.string.opts_units), if (switch_units.isChecked) "0" else "1")
        p.apply()
    }

    fun onSex()
    {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)

        val dlg = AlertDialog.Builder(this)
        dlg.setTitle(R.string.prompt_sex)
                .setItems(R.array.genders, DialogInterface.OnClickListener { dialog, which ->
                    val genders = resources.getStringArray(R.array.genders)
                    my_sex.setText(genders[which])
                    // The 'which' argument contains the index position
                    // of the selected item
                })

        dlg.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, id ->

        })
        dlg.create().show()
    }

    private fun onAge()
    {
        val dlg = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_picknumber, null)
        dlg.setTitle(R.string.prompt_age)
                .setView(dialogView)

        val num = dialogView.findViewById<NumberPicker>(R.id.the_number)
        val thisyear = Calendar.getInstance()[Calendar.YEAR]
        num!!.wrapSelectorWheel = false
        num.minValue = 1900
        num.maxValue = thisyear
        val def = my_age.text.toString()
        num.value = if (def.isEmpty()) thisyear else def.toInt()

        dlg.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, id ->
            my_age.setText(num.value.toString())// User clicked OK button
        })
        dlg.create().show()
    }

    private fun onWeight()
    {
        val metrik = switch_units.isChecked
        val dlg = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_picknumber, null)
        dlg.setTitle(R.string.prompt_weight)
                .setView(dialogView)

        val num = dialogView.findViewById<NumberPicker>(R.id.the_number)
        num!!.wrapSelectorWheel = false
        num.minValue = 0
        num.maxValue = 5000

        var x:Double = 0.0
        if (my_weight.getTag(R.string.numtag) is Double)
        {
            x = my_weight.getTag(R.string.numtag) as Double
        }
        if (!metrik)
            x /= 0.453592
        num.value = x.toInt()

        val suff = dialogView.findViewById<TextView>(R.id.num_suffix)
        suff!!.text = if (metrik) getString(R.string.kg) else getString(R.string.lb)

        dlg.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, id ->
            setWeight(num.value)
        })
        dlg.create().show()
    }


    fun setWeight(w:Int)
    {
        val metrik = switch_units.isChecked
        val s = "$w " + if (metrik) getString(R.string.kg) else getString(R.string.lb)
        my_weight.setText(s)

        var f:Double = w.toDouble()
        if (!metrik)
            f *= 0.453592
        my_weight.setTag(R.string.numtag, f)
    }

    fun setHeight(H:Int, h:Int)
    {
        val metrik = switch_units.isChecked

        var s = "$H "
        if (metrik)
            s += getString(R.string.m) + " $h " + getString(R.string.cm)
        else
            s += getString(R.string.ft) + " $h " + getString(R.string.in4)
        my_height.setText(s)

        val m = if (metrik) 100 else 12
        val Hh = H * m + h

        var f:Double = Hh.toDouble()
        if (!metrik)
            f *= 2.54
        my_height.setTag(R.string.numtag, f)
    }

    private fun onHeight()
    {
        val metrik = switch_units.isChecked
        val dlg = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_pick2numbers, null)
        dlg.setTitle(R.string.prompt_height)
                .setView(dialogView)

        val num = dialogView.findViewById<NumberPicker>(R.id.the_number)
        num!!.wrapSelectorWheel = false
        num.minValue = 0
        num.maxValue = 3

        var x:Double = 0.0
        if (my_height.getTag(R.string.numtag) is Double)
        {
            x = my_height.getTag(R.string.numtag) as Double
        }
        if (!metrik)
            x /= 2.54

        val X:Int = x.toInt()
        val m:Int = if (metrik) 100 else 12
        val a:Int = X / m
        val b:Int = X - (a * m)

        num.value = a

        val num2 = dialogView.findViewById<NumberPicker>(R.id.the_number_fraction)
        num2!!.wrapSelectorWheel = false
        num2.minValue = 0
        num2.maxValue = 99
        num2.value = b

        val suff = dialogView.findViewById<TextView>(R.id.num_suffix)
        suff!!.text = if (metrik) getString(R.string.m) else getString(R.string.ft)
        val post = dialogView.findViewById<TextView>(R.id.num_postfix)
        post!!.text = if (metrik) getString(R.string.cm) else getString(R.string.in4)

        dlg.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, id ->
            setHeight(num.value, num2.value)
        })
        dlg.create().show()
    }

    fun onUnits(metrik:Boolean)
    {
        if (my_weight.getTag(R.string.numtag) is Double)
        {
            var w:Double = my_weight.getTag(R.string.numtag) as Double
            if (!metrik)
                w /= 0.453592
            val n:Int = w.toInt()
            val s = "$n " + if (metrik) getString(R.string.kg) else getString(R.string.lb)
            my_weight.setText(s)
        }
        if (my_height.getTag(R.string.numtag) is Double)
        {
            var Hh:Double = my_height.getTag(R.string.numtag) as Double
            if (!metrik)
                Hh /= 2.54

            val HHhh:Int = Hh.toInt()
            val m:Int = if (metrik) 100 else 12
            val H:Int = HHhh / m
            val h:Int = HHhh - (H * m)

            var s = "$H "
            if (metrik)
                s += getString(R.string.m) + " $h " + getString(R.string.cm)
            else
                s += getString(R.string.ft) + " $h " + getString(R.string.in4)
            my_height.setText(s)
        }
    }
}
