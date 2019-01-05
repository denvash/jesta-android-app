package com.jesta.data

import com.ncapdevi.fragnav.FragNavController
import java.util.*
import kotlin.collections.HashMap

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

const val AVATAR_BEAVER   = "https://i.imgur.com/wahDm7F.png"
const val AVATAR_DUCK     = "https://i.imgur.com/FXRwG9D.png"
const val AVATAR_ELEPHANT = "https://i.imgur.com/8vL5cNZ.png"
const val AVATAR_LION     = "https://i.imgur.com/QGTMABv.png"
const val AVATAR_MONKEY   = "https://i.imgur.com/jUe1gX7.png"
const val AVATAR_PIG      = "https://i.imgur.com/DBbhSGo.png"
const val AVATAR_PINGUIN  = "https://i.imgur.com/7Zzi8DW.png"
const val AVATAR_SEAL     = "https://i.imgur.com/5H4Dtwz.png"
const val AVATAR_BEAR     = "https://i.imgur.com/zULe6CC.png"
const val AVATAR_ZEBRA    = "https://i.imgur.com/ha8OvLA.png"
const val AVATAR_CAT      = "https://i.imgur.com/u0dDgE0.png"
const val AVATAR_CHICK    = "https://i.imgur.com/jNxnoBI.png"
const val AVATAR_CHICKEN  = "https://i.imgur.com/DQrVmzU.png"
const val AVATAR_COW      = "https://i.imgur.com/md0ASx3.png"
const val AVATAR_DOG      = "https://i.imgur.com/aB27FR4.png"
const val AVATAR_DONKEY   = "https://i.imgur.com/qhDPhjf.png"

val avatarDict: HashMap<Int, String> = hashMapOf(0 to AVATAR_BEAVER, 1 to AVATAR_DUCK,
    2 to AVATAR_ELEPHANT, 3 to AVATAR_LION, 4 to AVATAR_MONKEY, 5 to AVATAR_PIG, 6 to AVATAR_PINGUIN,
    7 to AVATAR_SEAL, 8 to AVATAR_BEAR, 9 to AVATAR_ZEBRA, 10 to AVATAR_CAT, 11 to AVATAR_CHICK,
    12 to AVATAR_CHICKEN, 13 to AVATAR_COW, 14 to AVATAR_DOG, 15 to AVATAR_DONKEY)

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