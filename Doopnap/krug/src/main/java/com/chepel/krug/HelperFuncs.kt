package com.chepel.krug

import android.content.Context
import android.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Maksim on 1/7/2018.
 */

enum class EAuthenticator
{
    Achu,
    Google,
    Facebook,
    Amazon,
    Twitter,
}

enum class ESex
{
    Unk,
    M,
    W,
    O,
}

class My(_context: Context)
{
    private val context:Context = _context

    var a: EAuthenticator = EAuthenticator.Achu
    var uid = ""
    var email = ""
    var xtra = ""
    private var key = ""

    var first_name = ""
    var last_name = ""

    var sex:ESex = ESex.Unk
    var age = 0
    var height: Double = 0.0
    var weight: Double = 0.0

    var loginLast = Date()
    var loginCounter = 0

    fun mykey(str:String):String
    {
        return uid + ":" + str
    }

    fun load(userID:String)
    {
        val p = PreferenceManager.getDefaultSharedPreferences(context)
        uid = userID //p.getString(mykey(context.getString(R.string.opts_uid)), uid)
        xtra = p.getString(mykey(context.getString(R.string.opts_xtra)), xtra)
        email = p.getString(mykey(context.getString(R.string.opts_email)), email)
        key = p.getString(mykey(context.getString(R.string.opts_key)), key)
        a = EAuthenticator.valueOf(p.getString(mykey(context.getString(R.string.opts_authenticator)), a.name))

        first_name = p.getString(mykey(context.getString(R.string.opts_name)), first_name)
        last_name = p.getString(mykey(context.getString(R.string.opts_lastname)), last_name)

        sex = ESex.valueOf(p.getString(mykey(context.getString(R.string.opts_sex)), sex.name))
        age = p.getInt(mykey(context.getString(R.string.opts_age)), age)
        height = p.getFloat(mykey(context.getString(R.string.opts_height)), height.toFloat()).toDouble()
        weight = p.getFloat(mykey(context.getString(R.string.opts_weight)), weight.toFloat()).toDouble()

        loginCounter = p.getInt(mykey(context.getString(R.string.opts_loginCounter)), loginCounter)
        val tmp = p.getString(mykey(context.getString(R.string.opts_lastLogin)), "")

        val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        try
        {
            loginLast = fmt.parse(tmp)
        } catch (e: Exception) {}

        val s = context.getString(R.string.opts_key_key)
        keykey = p.getString(s, "")
        try
        {
            if (keykey.isEmpty())
            {
                keykey = KryptoUtil.generateKey()
                p.edit().putString(s, keykey).apply()
            }
        } catch (e:Exception){ }
        return this
    }

    fun clean()
    {
        uid = ""
        email = ""
        xtra = ""
        key = ""

        first_name = ""
        last_name = ""

        sex = ESex.Unk
        age = 0
        height = 0.0
        weight = 0.0

        loginLast = Date()
        loginCounter = 0
    }
}

class Credentials(_context: Context)
{
    var a: EAuthenticator = EAuthenticator.Achu
    var uid:String = ""
    var email:String = ""
    var xtra:String = ""
    private var key:String = ""
    private var keykey:String = ""
    val context:Context = _context

    var Key: String
        get()
        {
            if (a != EAuthenticator.Achu)
                return key

            try
            {
                if (keykey.isNotEmpty())
                {
                    return KryptoUtil.decrypt(key, keykey)
                }
            } catch (e:Exception){ }
            return ""
        }
        set(v)
        {
            if (a != EAuthenticator.Achu)
            {
                key = v
                return
            }
            try
            {
                if (keykey.isNotEmpty())
                {
                    key = KryptoUtil.encrypt(v, keykey)
                }
            } catch (e:Exception){ key = ""}
        }

    fun readPreferences():Credentials
    {
        val p = PreferenceManager.getDefaultSharedPreferences(context)
        uid = p.getString(context.getString(R.string.opts_uid), uid)
        xtra = p.getString(context.getString(R.string.opts_xtra), xtra)
        email = p.getString(context.getString(R.string.opts_email), email)
        key = p.getString(context.getString(R.string.opts_key), key)
        a = EAuthenticator.valueOf(p.getString(context.getString(R.string.opts_authenticator), a.name))

        val s = context.getString(R.string.opts_key_key)
        keykey = p.getString(s, "")
        try
        {
            if (keykey.isEmpty())
            {
                keykey = KryptoUtil.generateKey()
                p.edit().putString(s, keykey).apply()
            }
        } catch (e:Exception){ }
        return this
    }

    fun writePreferences():Credentials
    {
        val p = PreferenceManager.getDefaultSharedPreferences(context).edit()
        p.putString(context.getString(R.string.opts_uid), uid)
        p.putString(context.getString(R.string.opts_email), email)
        p.putString(context.getString(R.string.opts_xtra), xtra)
        p.putString(context.getString(R.string.opts_authenticator), a.name)
        p.putString(context.getString(R.string.opts_key), key)
        p.apply()
        return this
    }

    fun signOut():Credentials
    {
        xtra = ""
        key = ""
        return writePreferences()
    }

    fun forget():Credentials
    {
        a = EAuthenticator.Achu
        uid = ""
        email = ""
        xtra = ""
        key = ""
        return writePreferences()
    }

    fun canLogin():Boolean
    {
        when (a)
        {
            EAuthenticator.Achu -> { return uid.isNotEmpty() && Key.isNotEmpty() }
        }
        return false
    }
}
