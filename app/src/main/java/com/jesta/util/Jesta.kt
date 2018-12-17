package com.jesta.util

import android.content.res.Resources
import android.net.Uri
import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jesta.R
import kotlinx.android.parcel.Parcelize
import java.io.BufferedReader
import java.util.ArrayList

data class Payment(val visible: Boolean, val price: Int)

@Parcelize
data class Mission(
    var id: String = "emptyID",
    var difficulty: String = "emptyDifficulty",
    var description: String = "emptyDescription",
    var imageUrl: String = "emptyImageUrl",
    var payment: Int = -1,
    var numOfPeople: Int = -1,
    var duration: Int = -1,
    var location: String = "emptyLocation",
    var diamonds: Int = -1,
    var tags: List<String> = listOf("Tag1","Tag2")
) : Parcelable {

    constructor(dbJesta: Map<String, String>) : this() {
        id = dbJesta["id"].toString()
        difficulty = dbJesta["difficulty"].toString()
        description = dbJesta["description"].toString()
        imageUrl = dbJesta["imageUrl"].toString()
        payment = dbJesta["payment"].toString().toInt()
        numOfPeople = dbJesta["numOfPeople"].toString().toInt()
        duration = dbJesta["duration"].toString().toInt()
        location = dbJesta["location"].toString()

        // TODO add diamonds prop
//        diamonds = dbJesta["diamonds"].toString().toInt()

        // TODO write initializer from JSON
//        tags = dbJesta["tags"]
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

data class User(
    var id: String = "emptyID",
    var email: String = "emptyEmail",
    var displayName: String = "emptyName",
    var photoUrl: String = "emptyPhoto",
    var phoneNumber: String = "emptyNumber"
) {

    constructor(firebaseUser: FirebaseUser) : this() {
        id = firebaseUser.uid
        email = firebaseUser.email.toString()
        photoUrl = firebaseUser.photoUrl.toString()
        displayName = firebaseUser.displayName.toString()
        phoneNumber = firebaseUser.phoneNumber.toString()
    }

    constructor(dbUser: Map<String, String>) : this() {
        id = dbUser["id"].toString()
        email = dbUser["email"].toString()
        photoUrl = dbUser["photoUrl"].toString()
        displayName = dbUser["displayName"].toString()
        phoneNumber = dbUser["phoneNumber"].toString()
    }
}
