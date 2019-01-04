package com.jesta.data

import com.google.firebase.auth.FirebaseUser

data class User(
    var id : String = USER_EMPTY_ID,
    var displayName: String = USER_EMPTY_DISPLAY_NAME,
    var email: String = USER_EMPTY_EMAIL,
    var photoUrl: String = USER_EMPTY_PHOTO,
    var diamonds: String = USER_EMPTY_DIAMONDS,
    var lastMission: Mission = Mission(id= MISSION_EMPTY_ID)
) {

    constructor(firebaseUser: FirebaseUser) : this() {
        id = firebaseUser.uid
        email = firebaseUser.email.toString()
        photoUrl = firebaseUser.photoUrl.toString()
        displayName = firebaseUser.displayName.toString()
    }
}