package com.jesta.data

data class Status(
    val missionID: String,
    val doerIDList: List<String>,
    val isPoster: Boolean,
    val status: String
)