package com.jesta.doJesta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jesta.R
import kotlinx.android.synthetic.main.fragment_bottomsheet.*

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottomsheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        do_jesta_navigation_view.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.nav_ask_jesta -> Toast.makeText(activity, "Navigate to ask Jesta", Toast.LENGTH_LONG).show()
                R.id.nav_profile -> Toast.makeText(activity, "Navigate to Profile page", Toast.LENGTH_LONG).show()
                R.id.nav_map -> Toast.makeText(activity, "Navigate to Map page", Toast.LENGTH_LONG).show()
                R.id.nav_status -> Toast.makeText(activity, "Navigate to Status page", Toast.LENGTH_LONG).show()
            }
            true
        }
    }
}
