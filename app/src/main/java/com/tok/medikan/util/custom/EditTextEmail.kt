package com.tok.medikan.util.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.tok.medikan.R
import com.google.android.material.textfield.TextInputEditText

class EditTextEmail : TextInputEditText {

    private var errorBackground: Drawable? = null
    private var defaultBackground: Drawable? = null
    private var isError: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isError) {
            errorBackground
        } else {
            defaultBackground
        }
    }

    private fun init() {
        errorBackground = ContextCompat.getDrawable(context, R.drawable.bg_edittext_error)
        defaultBackground = ContextCompat.getDrawable(context, R.drawable.bg_edittext_default)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val input = s.toString()
                if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    error = context.getString(R.string.error_email)
                    isError = true
                } else {
                    isError = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    error = context.getString(R.string.error_email)
                    isError = true
                } else {
                    isError = false
                }
            }
        })
    }
}