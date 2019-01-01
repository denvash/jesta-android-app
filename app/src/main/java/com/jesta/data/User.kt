package com.jesta.data

import com.google.firebase.auth.FirebaseUser

data class User(
    var id: String = "emptyID",
    var email: String = "emptyEmail",
    var displayName: String = "emptyName",
    var photoUrl: String = "emptyPhoto",
    var phoneNumber: String = "emptyNumber"
) {

    constructor(firebaseUser: FirebaseUser) : this() {
        id = firebaseUser.uid
        email = firebaseUser.email.toString()
        photoUrl = firebaseUser.photoUrl.toString()
        displayName = firebaseUser.displayName.toString()
        phoneNumber = firebaseUser.phoneNumber.toString()
    }

    constructor(dbUser: Map<String, String>) : this() {
        id = dbUser["id"].toString()
        email = dbUser["email"].toString()
        photoUrl = dbUser["photoUrl"].toString()
        displayName = dbUser["displayName"].toString()
        phoneNumber = dbUser["phoneNumber"].toString()
    }
}