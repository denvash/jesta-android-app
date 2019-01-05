@file:Suppress("LABEL_NAME_CLASH")

package com.jesta.gui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.data.BUNDLE_CHAT_ROOM
import com.jesta.data.chat.Author
import com.jesta.data.chat.ChatManager
import com.jesta.data.chat.ChatMessage
import com.jesta.data.chat.Message
import com.jesta.utils.db.SysManager
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.util.*


class ChatFragment : Fragment() {
    companion object {
        private val TAG = ChatFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(chatID: String) = ChatFragment().apply {
            arguments = Bundle().apply {
                putString(BUNDLE_CHAT_ROOM, chatID)
            }
        }
    }

    private lateinit var chatID: String

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getString(BUNDLE_CHAT_ROOM)?.let {
            chatID = it
        }
    }

    private val sysManager = SysManager(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val currUser = sysManager.currentUserFromDB

        // Image loader
        val imageLoader = ImageLoader { imageView, url, _ -> Picasso.get().load(url).into(imageView) }

        val adapter = MessagesListAdapter<Message>(sysManager.currentUserFromDB.id, imageLoader)
        view.messagesList.setAdapter(adapter)

        val chatManger = ChatManager()

        // todo dennis you should to animation here while waiting for subscription
        chatManger.subscribeToChatRoom(chatID).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                //todo some error
                return@addOnCompleteListener
            }

            val messagesLiveSnapShot = arrayListOf<ChatMessage>()
            chatManger.listenForIncomingMessages(adapter, chatID, messagesLiveSnapShot)

            view.input.setInputListener {
                chatManger.sendMessage(context, chatID, currUser, it.toString())
//            adapter.addToStart(
//                Message(
//                    chatID,
//                    Author(currUser.id, currUser.displayName, currUser.photoUrl),
//                    Date(),
//                    it.toString()
//                ), true
//            )
                true
            }
        }


        return view
    }
}
