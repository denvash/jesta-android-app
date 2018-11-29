package com.jesta.doJesta

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class JestaCardGridItemDecoration internal constructor(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        outRect.left = space
        outRect.right = space
        outRect.bottom = 2*space

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space
        } else {
            outRect.top = 0
        }
    }
}
