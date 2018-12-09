package com.jesta.util

import android.content.res.Resources
import android.net.Uri
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jesta.R
import kotlinx.android.parcel.Parcelize
import java.io.BufferedReader
import java.util.ArrayList

data class Payment(val visible: Boolean, val price: Int)

@Parcelize
data class Mission(
//    var id: String = "emptyID",
    var difficulty: String = "emptyDifficulty",
    var description: String = "emptyDescription",
    var imageUrl: String = "emptyImageUrl",
    var payment: Int = -1,
    var numOfPeople: Int = -1,
    var duration: Int = -1,
    var location: String = "emptyLocation"
) : Parcelable {

    constructor(dbJesta: Map<String, String>) : this() {
//        id = dbJesta["id"]!!
        difficulty = dbJesta["difficulty"].toString()
        description = dbJesta["description"].toString()
        imageUrl = dbJesta["imageUrl"].toString()
        payment = dbJesta["payment"].toString().toInt()
        numOfPeople = dbJesta["numOfPeople"].toString().toInt()
        duration = dbJesta["duration"].toString().toInt()
        location = dbJesta["location"].toString()
    }

    companion object {
        fun initJestaCardList(resources: Resources): List<Mission> {
            val inputStream = resources.openRawResource(R.raw.jesta_posts)
            val jsonJestasString = inputStream.bufferedReader().use(BufferedReader::readText)
            val gson = Gson()
            val jestaListType = object : TypeToken<ArrayList<Mission>>() {}.type
            return gson.fromJson<List<Mission>>(jsonJestasString, jestaListType)
        }
    }
}

// TODO: tweak to use Post and not Mission
data class Post(val user: User, val mission: Mission) {

    companion object {
        fun initJestaCardList(resources: Resources): List<Post> {
            val inputStream = resources.openRawResource(R.raw.jesta_posts)
            val jsonJestasString = inputStream.bufferedReader().use(BufferedReader::readText)
            val gson = Gson()
            val jestaListType = object : TypeToken<ArrayList<Post>>() {}.type
            return gson.fromJson<List<Post>>(jsonJestasString, jestaListType)
        }
    }
}

data class User(val id: String, val email: String) {
    var displayName: String = "emptyName"
    var photoUrl: String = "emptyPhoto"
    var phoneNumber: String = "emptyNumber"
}
