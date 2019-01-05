package com.jesta.data

import com.ncapdevi.fragnav.FragNavController

const val nullString = "null"
const val nullInt = -1

const val BUNDLE_MISSION = "jesta-mission"
const val BUNDLE_ROOM_ID = "jesta-chat"
const val BUNDLE_RELATION_ID = "jesta-relation"
const val BUNDLE_MISSION_ID = "jesta-mission-id"


const val DIFFICULTY_EASY = "Easy"
const val DIFFICULTY_MEDIUM = "Medium"
const val DIFFICULTY_HARD = "Hard"

const val STATUS_POSTER = "Poster"
const val STATUS_DOER = "Doer"
const val STATUS_PENDING = "Pending"
const val STATUS_IN_PROGRESS = "In Progress"
const val STATUS_DONE = "Done"
const val STATUS_DECLINED = "Declined"

const val DEFAULT_AVATAR_URL = "https://i.imgur.com/HyHVdRC.png"

const val INDEX_DO_JESTA = FragNavController.TAB1
const val INDEX_ASK_JESTA = FragNavController.TAB2
const val INDEX_STATUS = FragNavController.TAB3
const val INDEX_SETTINGS = FragNavController.TAB4

const val RESULT_LOAD_IMAGE: Int = 1
const val REQUEST_STORAGE_PERMISSION = 100

const val MISSION_EMPTY_ID: String = nullString
const val MISSION_EMPTY_AUTHOR_ID: String = nullString
const val MISSION_EMPTY_AUTHOR_IMAGE: String = nullString
const val MISSION_EMPTY_TITLE: String = nullString
const val MISSION_EMPTY_DIFFICULTY: String = nullString
const val MISSION_EMPTY_DESCRIPTION: String = nullString
const val MISSION_EMPTY_IMAGE_URL: String = nullString
const val MISSION_EMPTY_LOCATION: String = nullString
const val MISSION_EMPTY_PAYMENT: Int = nullInt
const val MISSION_EMPTY_NUM_OF_PEOPLE: Int = nullInt
const val MISSION_EMPTY_DURATION: Int = nullInt
const val MISSION_EMPTY_DIAMONDS: Int = nullInt

const val USER_EMPTY_ID: String = nullString
const val USER_EMPTY_DISPLAY_NAME: String = nullString
const val USER_EMPTY_EMAIL: String = nullString
const val USER_EMPTY_PHOTO: String = nullString
const val USER_EMPTY_DIAMONDS: String = nullString

const val RELATION_EMPTY_ID: String = nullString
const val RELATION_EMPTY_POSTER_ID: String = nullString
const val RELATION_EMPTY_DOER_ID: String = nullString
const val RELATION_EMPTY_MISSION_ID: String = nullString

const val RELATION_STATUS_INIT = 0
const val RELATION_STATUS_IN_PROGRESS = 1
const val RELATION_STATUS_DONE = 2
const val RELATION_STATUS_USER_DECLINED = 3