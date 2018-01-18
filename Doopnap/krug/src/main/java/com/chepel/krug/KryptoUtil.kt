package com.chepel.krug

/**
 * Created by Maksim on 1/3/2018.
 */

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import android.util.Base64
import java.io.IOException


object KryptoUtil
{
    object Base64Utils
    {
        @Throws(IOException::class)
        fun base64Decode(property: String): ByteArray {
            return Base64.decode(property, Base64.NO_WRAP)
        }

        fun base64Encode(bytes: ByteArray): String {
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        }
    }

    val SYMMETRIC_ALGORITHM_BASENAME = "AES"
    val SYMMETRIC_ALGORITHM_NAME = SYMMETRIC_ALGORITHM_BASENAME + "/CBC/PKCS5Padding"

    private val PRIVATE_IVPSEC_KEY =   "LeY40viUq42rO1aFOw+z8j=="

    @Throws(NoSuchAlgorithmException::class)
    fun generateKey(): String
    {
        var kgen: KeyGenerator? = KeyGenerator.getInstance(SYMMETRIC_ALGORITHM_BASENAME)
        val secureRandom = SecureRandom()
        kgen!!.init(256, secureRandom)
        val key = kgen.generateKey()
        val bytes = key.encoded
        return Base64Utils.base64Encode(bytes)
    }

    @Throws(Exception::class)
    fun encrypt(password: String, encryptionKey: String): String
    {
        val decodedEncryptionKey = Base64Utils.base64Decode(encryptionKey)
        val cipher = Cipher.getInstance(SYMMETRIC_ALGORITHM_NAME)
        val key = SecretKeySpec(decodedEncryptionKey, SYMMETRIC_ALGORITHM_BASENAME)
        val ivspec = IvParameterSpec(Base64Utils.base64Decode(PRIVATE_IVPSEC_KEY))
        cipher.init(Cipher.ENCRYPT_MODE, key, ivspec)
        val value = cipher.doFinal(password.toByteArray())
        return Base64Utils.base64Encode(value)
    }

    @Throws(Exception::class)
    fun decrypt(encryptedPassword: String, encryptionKey: String): String
    {
        val decodedEncryptionKey = Base64Utils.base64Decode(encryptionKey)
        val decodedEncryptedPassword = Base64Utils.base64Decode(encryptedPassword)
        val cipher = Cipher.getInstance(SYMMETRIC_ALGORITHM_NAME)
        val ivspec = IvParameterSpec(Base64Utils.base64Decode(PRIVATE_IVPSEC_KEY))
        val key = SecretKeySpec(decodedEncryptionKey, SYMMETRIC_ALGORITHM_BASENAME)
        cipher.init(Cipher.DECRYPT_MODE, key, ivspec)
        val rawPassword = cipher.doFinal(decodedEncryptedPassword)
        return String(rawPassword)
    }
}
