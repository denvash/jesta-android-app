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

data class Payment (val visible: Boolean, val price: Int)

@Parcelize
data class Mission(
    val difficulty: String,
    val description: String,
    val imageUrl: String,
    val payment: Int,
    val numOfPeople: Int,
    val duration: Int,
    val location: String
) : Parcelable {
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