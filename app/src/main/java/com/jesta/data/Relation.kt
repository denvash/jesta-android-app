package com.jesta.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Relation(
    var id: String = RELATION_EMPTY_ID,
    var doerList: ArrayList<String> = ArrayList(),
    var posterID: String = RELATION_EMPTY_POSTER_ID,
    var missionID: String = RELATION_EMPTY_MISSION_ID,
    var chosenID: String = RELATION_EMPTY_CHOSEN_ID,
    var status: Int = RELATION_STATUS_INIT
) : Parcelable