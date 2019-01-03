package com.jesta.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Relation(
    var id: String = RELATION_EMPTY_ID,
    var doer_id: String = RELATION_EMPTY_USER_ID,
    var poster_id: String = RELATION_EMPTY_POSTER_ID,
    var jesta_id: String = RELATION_EMPTY_JESTA_ID,
    var status: Int = RELATION_STATUS_INIT
) : Parcelable