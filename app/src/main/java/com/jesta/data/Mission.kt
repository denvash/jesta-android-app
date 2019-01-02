package com.jesta.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Mission(
    var id: String = MISSION_EMPTY_ID,
    var authorId: String = MISSION_EMPTY_AUTHOR_ID,
    var authorImage: String = MISSION_EMPTY_AUTHOR_IMAGE,
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
) : Parcelable