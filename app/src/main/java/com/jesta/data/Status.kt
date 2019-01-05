package com.jesta.data

data class Status(
    val missionID: List<String>,
    val doerIDList: List<String>,
    val isPoster: Boolean,
    val status: String
)