package com.jesta.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Mission(
    var id: String = "emptyID",
    var authorId: String = "emptyID",
    var title: String = "emptyTitle",
    var difficulty: String = "emptyDifficulty",
    var description: String = "emptyDescription",
    var imageUrl: String = "emptyImageUrl",
    var payment: Int = -1,
    var numOfPeople: Int = -1,
    var duration: Int = -1,
    var location: String = "emptyLocation",
    var diamonds: Int = -1,
    var tags: List<String> = listOf("Tag1", "Tag2", "Tag3")
) : Parcelable {

    constructor(dbJesta: Map<String, String>) : this() {
        id = dbJesta["id"].toString()
        authorId = dbJesta["authorId"].toString()
        difficulty = dbJesta["difficulty"].toString()
        description = dbJesta["description"].toString()
        imageUrl = dbJesta["imageUrl"].toString()
        payment = dbJesta["payment"].toString().toInt()
        numOfPeople = dbJesta["numOfPeople"].toString().toInt()
        duration = dbJesta["duration"].toString().toInt()
        location = dbJesta["location"].toString()
        diamonds = dbJesta["diamonds"].toString().toInt()
        title = dbJesta["title"].toString()

        // TODO write initializer from JSON
//        tags = dbJesta["tags"]
    }

}