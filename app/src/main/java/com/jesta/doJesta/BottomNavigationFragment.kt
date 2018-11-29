package com.jesta.doJesta

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jesta.R
import com.jesta.askJesta.AskJestaActivity
import com.jesta.map.MapActivity
import com.jesta.profile.ProfileActivity
import com.jesta.status.StatusActivity
import kotlinx.android.synthetic.main.fragment_bottomsheet.*

class BottomNavigationFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottomsheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        do_jesta_navigation_view.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.nav_ask_jesta -> {
                    val intent = Intent(context, AskJestaActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_do_jesta -> {
                    val intent = Intent(context, DoJestaActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_map -> {
                    val intent = Intent(context, MapActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_status -> {
                    val intent = Intent(context, StatusActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }
}
