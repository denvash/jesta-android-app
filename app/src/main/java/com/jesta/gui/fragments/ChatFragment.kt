@file:Suppress("LABEL_NAME_CLASH")

package com.jesta.gui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.data.BUNDLE_ROOM_ID
import com.jesta.data.chat.ChatManager
import com.jesta.data.chat.Message
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.db.SysManager
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.view_message_input.view.*


class ChatFragment : Fragment() {
    companion object {
        private val TAG = ChatFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(roomID: String) = ChatFragment().apply {
            arguments = Bundle().apply {
                putString(BUNDLE_ROOM_ID, roomID)
            }
        }
    }

    private lateinit var chatID: String

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getString(BUNDLE_ROOM_ID)?.let {
            chatID = it
        }
    }

    private val sysManager = SysManager(MainActivity.instance)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val currUser = sysManager.currentUserFromDB

        // Image loader
        val imageLoader = ImageLoader { imageView, url, _ -> Picasso.get().load(url).into(imageView) }

        val adapter = MessagesListAdapter<Message>(sysManager.currentUserFromDB.id, imageLoader)
        view.messagesList.setAdapter(adapter)

        val chatManger = ChatManager(MainActivity.instance)

        chatManger.subscribeToChatRoom(chatID).addOnCompleteListener { subscribeTask ->
            if (!subscribeTask.isSuccessful) {
                //todo some error
                return@addOnCompleteListener
            }

            chatManger.getMessagesByRoomId(chatID).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    //todo some error
                    return@addOnCompleteListener
                }

                view.attachmentButton.isEnabled = false

                val messagesHistory = (task.result as MutableList<*>).filterIsInstance<Message>()

                view.chat_progress_bar.visibility = View.INVISIBLE
                adapter.addToEnd(messagesHistory, true)
                chatManger.listenForChatUpdateAdapter(adapter, chatID, messagesHistory)

                view.jesta_chat_input.setInputListener {
                    chatManger.sendMessage(context, chatID, currUser, it.toString())
                    true
                }

                view.attachmentButton.setOnClickListener {
                    view.attachmentButton.isEnabled = false
                }
            }
        }

        return view
    }
}
