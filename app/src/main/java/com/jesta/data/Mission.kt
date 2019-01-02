package com.jesta.data

import android.os.Parcelable
import com.google.gson.reflect.TypeToken
import com.jesta.gui.activities.MainActivity.Companion.gson
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Mission(
    var id: String = MISSION_EMPTY_ID,
    var authorId: String = MISSION_EMPTY_AUTHOR_ID,
    var title: String = MISSION_EMPTY_TITLE,
    var difficulty: String = MISSION_EMPTY_DIFFICULTY,
    var description: String = MISSION_EMPTY_DESCRIPTION,
    var imageUrl: String = MISSION_EMPTY_IMAGE_URL,
    var payment: Int = MISSION_EMPTY_PAYMENT,
    var numOfPeople: Int = MISSION_EMPTY_NUM_OF_PEOPLE,
    var duration: Int = MISSION_EMPTY_DURATION,
    var location: String = MISSION_EMPTY_LOCATION,
    var diamonds: Int = MISSION_EMPTY_DIAMONDS,
    var tags: List<String> = emptyList(),
    var isAvailable: Boolean = true
) : Parcelable {

//    constructor(dbJesta: Map<String, String>) : this() {
//        id = dbJesta[MISSION_ID].toString()
//        authorId = dbJesta[MISSION_AUTHOR_ID].toString()
//        difficulty = dbJesta[MISSION_DIFFICULTY].toString()
//        description = dbJesta[MISSION_DESCRIPTION].toString()
//        imageUrl = dbJesta[MISSION_IMAGE_URL].toString()
//        payment = dbJesta[MISSION_PAYMENT].toString().toInt()
//        numOfPeople = dbJesta[MISSION_NUM_OF_PEOPLE].toString().toInt()
//        duration = dbJesta[MISSION_DURATION].toString().toInt()
//        location = dbJesta[MISSION_LOCATION].toString()
//        diamonds = dbJesta[MISSION_DIAMONDS].toString().toInt()
//        title = dbJesta[MISSION_TITLE].toString()
//        tags = if (dbJesta[MISSION_TAGS] == null) emptyList() else gson.fromJson(dbJesta[MISSION_TAGS], object :
//            TypeToken<List<String>>() {}.type)
//        isAvailable = gson.fromJson(dbJesta[MISSION_IS_AVAILABLE], object :
//            TypeToken<Boolean>() {}.type)
//    }

}