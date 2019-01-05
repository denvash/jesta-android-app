package com.jesta.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Relation(
    var id: String = RELATION_EMPTY_ID,
    var doerID: String = RELATION_EMPTY_DOER_ID,
    var posterID: String = RELATION_EMPTY_POSTER_ID,
    var missionID: String = RELATION_EMPTY_MISSION_ID,
    var status: Int = RELATION_STATUS_INIT
) : Parcelable