package com.jesta.data.chat

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser

import java.util.Date

data class Message(
    var dbID: String,
    var author: Author,
    var date: Date,
    var msg: String
) : IMessage {
    override fun getId(): String {
        return dbID
    }

    override fun getCreatedAt(): Date {
        return date
    }

    override fun getUser(): IUser {
        return author
    }

    override fun getText(): String {
        return msg
    }
}
