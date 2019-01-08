package com.jesta.gui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.gui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_login_create_account.view.*

class LoginCreateAccountFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_login_create_account, container, false)
        val instance = MainActivity.instance

        view.jesta_login_create_button_back.setOnClickListener {
            instance.fragNavController.popFragment()
        }
        return view
    }
}