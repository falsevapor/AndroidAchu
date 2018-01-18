package com.chepel.aachuapp.interfaces

/**
 * Created by maksim.chepel on 12/6/17.
 */
class User {
    enum class eGender
    {
        unknown,
        male,
        female
    }
    enum class eFrequency
    {
        unknown,
        no,
        rare,
        regular,
        yes,
    }
    enum class eBloodType
    {
        unknown,
        positiveO,
        negativeO,
        positiveA,
        negativeA,
        positiveB,
        negativeB,
        positiveAB,
        negativeAB,
    }
    var ID: String = ""
    var Email: String = ""
    var Password: String = ""
    var FirstName: String = ""
    var LastName: String = ""
    var Token: String = ""
    var YearOfBirth: Int = 1800
    var Gender: eGender = eGender.unknown
    var Height: Float = 0.toFloat()
    var Weight: Float = 0.toFloat()
    var HomeTown: String = ""

    var Smoke: eFrequency = eFrequency.unknown
    var Alcohol: eFrequency = eFrequency.unknown
    var Exercise: eFrequency = eFrequency.unknown
    var Stress: eFrequency = eFrequency.unknown
    var BloodType: eBloodType = eBloodType.unknown
}