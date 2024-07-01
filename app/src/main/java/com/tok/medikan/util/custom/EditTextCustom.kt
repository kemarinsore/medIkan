package com.tok.medikan.util.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.tok.medikan.R
import com.google.android.material.textfield.TextInputEditText

class EditTextCustom : TextInputEditText {

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
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val input = s.toString()
                isError = if (input.length < 1) {
                    setError(context.getString(R.string.error_field), null)
                    true
                } else {
                    false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                isError = if (input.length < 1) {
                    setError(context.getString(R.string.error_field), null)
                    true
                } else {
                    false
                }
            }
        })
    }
}