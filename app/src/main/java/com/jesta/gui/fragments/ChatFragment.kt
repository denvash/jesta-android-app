@file:Suppress("LABEL_NAME_CLASH")

package com.jesta.gui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.data.chat.Author
import com.jesta.data.chat.Message
import com.jesta.utils.db.SysManager
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.jesta_main_activity.view.*
import java.util.*


class ChatFragment : Fragment() {
    private val sysManager = SysManager(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val adapter = MessagesListAdapter<Message>(sysManager.currentUserFromDB.id, null)
        view.messagesList.setAdapter(adapter)

        val currUser = sysManager.currentUserFromDB

        view.input.setInputListener {
            adapter.addToStart(
                Message(
                    "0",
                    Author(currUser.id,currUser.displayName,currUser.photoUrl),
                    Date(),
                    it.toString()
                    ),true)
            true
        }

        return view
    }
}
