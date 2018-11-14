package com.jesta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.jesta_login_fragment.*
import kotlinx.android.synthetic.main.jesta_login_fragment.view.*

class LoginFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.jesta_login_fragment, container, false)

        view.dev_button.setOnClickListener{
            (activity as NavigationHost).navigateTo(PostGridFragment(),false)
        }

        // Set an error if the password is less than 8 characters.
        view.next_button.setOnClickListener {
            if (!isPasswordValid(password_edit_text.text)) {
                password_text_input.error = getString(R.string.jesta_error_password)
            } else {
                password_text_input.error = null // Clear the error
                (activity as NavigationHost).navigateTo(PostGridFragment(), false) // Navigate to the next Fragment
            }
        }

        // Clear the error once more than 8 characters are typed.
        view.password_edit_text.setOnKeyListener { _, _, _ ->
            if (isPasswordValid(password_edit_text.text)) {
                password_text_input.error = null //Clear the error
            }
            false
        }
        return view
    }

    /*
    In reality, this will have more complex logic including, but not limited to, actual
    authentication of the username and password.
 */
    private fun isPasswordValid(text: Editable?): Boolean {
        return text != null && text.length >= 8
    }

}