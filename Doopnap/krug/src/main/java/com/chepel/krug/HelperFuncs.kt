package com.chepel.krug

import android.content.Context
import android.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Maksim on 1/7/2018.
 */

class My(_context: Context)
{
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

    enum class EPrefSection(val bit:Int)
    {
        Credentials(1),
        General(2),
        MyInfo(4),
        Habits(8),
        Notifications(16),
        Stats(32),
    }


    private val context:Context = _context

    var a: EAuthenticator = EAuthenticator.Achu
    var uid = ""
    var email = ""
    var xtra = ""
    private var key = ""
    private var keykey = ""

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

    fun load(userID:String = "", section:Int = Int.MAX_VALUE):My
    {
        val p = PreferenceManager.getDefaultSharedPreferences(context)

        if (0 < (section and EPrefSection.Credentials.bit))
        {
            uid = if (userID.isEmpty()) p.getString(mykey(context.getString(R.string.opts_uid)), uid) else userID
            xtra = p.getString(mykey(context.getString(R.string.opts_xtra)), xtra)
            email = p.getString(mykey(context.getString(R.string.opts_email)), email)
            key = p.getString(mykey(context.getString(R.string.opts_key)), key)
            a = EAuthenticator.valueOf(p.getString(mykey(context.getString(R.string.opts_authenticator)), a.name))

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
        }
        if (0 < (section and EPrefSection.General.bit))
        {
        }
        if (0 < (section and EPrefSection.MyInfo.bit))
        {
            email = p.getString(mykey(context.getString(R.string.opts_email)), email)
            first_name = p.getString(mykey(context.getString(R.string.opts_name)), first_name)
            last_name = p.getString(mykey(context.getString(R.string.opts_lastname)), last_name)
            sex = ESex.valueOf(p.getString(mykey(context.getString(R.string.opts_sex)), sex.name))
            age = p.getInt(mykey(context.getString(R.string.opts_age)), age)
            height = p.getFloat(mykey(context.getString(R.string.opts_height)), height.toFloat()).toDouble()
            weight = p.getFloat(mykey(context.getString(R.string.opts_weight)), weight.toFloat()).toDouble()
        }
        if (0 < (section and EPrefSection.Habits.bit))
        {
        }
        if (0 < (section and EPrefSection.Notifications.bit))
        {
        }
        if (0 < (section and EPrefSection.Stats.bit))
        {
            loginCounter = p.getInt(mykey(context.getString(R.string.opts_loginCounter)), loginCounter)
            val tmp = p.getString(mykey(context.getString(R.string.opts_lastLogin)), "")

            val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            try
            {
                loginLast = fmt.parse(tmp)
            } catch (e: Exception) {}
        }
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

    fun canLogin():Boolean
    {
        when (a)
        {
            EAuthenticator.Achu -> { return uid.isNotEmpty() && Key.isNotEmpty() }
        }
        return false
    }

    fun signOut()
    {
        xtra = ""
        key = ""
        save(EPrefSection.Credentials.bit)
    }

    fun forget()
    {
        val p = PreferenceManager.getDefaultSharedPreferences(context).edit()
        p.remove(context.getString(R.string.opts_uid))
        p.remove(mykey(context.getString(R.string.opts_email)))
        p.remove(mykey(context.getString(R.string.opts_xtra)))
        p.remove(mykey(context.getString(R.string.opts_authenticator)))
        p.remove(mykey(context.getString(R.string.opts_key)))
        p.remove(mykey(context.getString(R.string.opts_email)))
        p.remove(mykey(context.getString(R.string.opts_name)))
        p.remove(mykey(context.getString(R.string.opts_lastname)))
        p.remove(mykey(context.getString(R.string.opts_sex)))
        p.remove(mykey(context.getString(R.string.opts_age)))
        p.remove(mykey(context.getString(R.string.opts_height)))
        p.remove(mykey(context.getString(R.string.opts_weight)))
        p.remove(mykey(context.getString(R.string.opts_loginCounter)))
        p.remove(mykey(context.getString(R.string.opts_lastLogin)))
        p.apply()
        clean()
    }

    fun save(section:Int = Int.MAX_VALUE)
    {
        val p = PreferenceManager.getDefaultSharedPreferences(context).edit()
        if (0 < (section and EPrefSection.Credentials.bit))
        {
            p.putString(context.getString(R.string.opts_uid), uid)
            p.putString(mykey(context.getString(R.string.opts_email)), email)
            p.putString(mykey(context.getString(R.string.opts_xtra)), xtra)
            p.putString(mykey(context.getString(R.string.opts_authenticator)), a.name)
            p.putString(mykey(context.getString(R.string.opts_key)), key)
        }
        if (0 < (section and EPrefSection.General.bit))
        {
        }
        if (0 < (section and EPrefSection.MyInfo.bit))
        {
            p.putString(mykey(context.getString(R.string.opts_email)), email)
            p.putString(mykey(context.getString(R.string.opts_name)), first_name)
            p.putString(mykey(context.getString(R.string.opts_lastname)), last_name)
            p.putString(mykey(context.getString(R.string.opts_sex)), sex.name)
            p.putInt(mykey(context.getString(R.string.opts_age)), age)
            p.putFloat(mykey(context.getString(R.string.opts_height)), height.toFloat())
            p.putFloat(mykey(context.getString(R.string.opts_weight)), weight.toFloat())
        }
        if (0 < (section and EPrefSection.Habits.bit))
        {
        }
        if (0 < (section and EPrefSection.Notifications.bit))
        {
        }
        if (0 < (section and EPrefSection.Stats.bit))
        {
            p.putInt(mykey(context.getString(R.string.opts_loginCounter)), loginCounter)

            var s = ""
            val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            try
            {
                s = fmt.format(loginLast)
            } catch (e: Exception) {s = ""}

            p.putString(mykey(context.getString(R.string.opts_lastLogin)), s)
        }

        p.apply()
    }
}

