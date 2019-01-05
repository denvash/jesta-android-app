package com.jesta.data

data class Status(
    var missionID: String = MISSION_EMPTY_ID,
    var doerIDList: List<String> = ArrayList(),
    var isPoster: Boolean = false,
    var status: Int = RELATION_STATUS_INIT
)