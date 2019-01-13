package com.jesta.utils.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class FocusableEditText(context: Context?, attrs: AttributeSet?) : TextInputEditText(context, attrs){
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) this.hideKeyboard()
    }
}