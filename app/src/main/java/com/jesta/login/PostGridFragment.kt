package com.jesta.login

import android.os.Bundle
import android.view.*
import com.jesta.R


class PostGridFragment : androidx.fragment.app.Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment with the PostGrid theme
        val view = inflater.inflate(R.layout.jesta_post_grid_fragment, container, false)

        return view
    }
}