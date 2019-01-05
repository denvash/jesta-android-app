package com.jesta.data.chat

import com.stfalcon.chatkit.commons.models.IUser

data class Author(
    var dbID: String,
    var displayName: String,
    var imageUrl: String
) : IUser {
    override fun getAvatar(): String {
        return imageUrl
    }

    override fun getName(): String {
        return displayName
    }

    override fun getId(): String {
        return dbID
    }
}