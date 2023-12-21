package com.capstoneproject.clothizeapp.client.ui.custom_view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.capstoneproject.clothizeapp.R
import com.google.android.material.textfield.TextInputEditText

class CustomEmailEditText : TextInputEditText {
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

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    setError(resources.getString(R.string.not_fill), null)
                } else if (!isEmailValid(s.toString())) {
                    setError(resources.getString(R.string.email_not_valid), null)
                } else {
                    error = null
                }

            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.

            }
        })
    }

    private fun isEmailValid(email: String): Boolean {
        // You can use a regular expression or other validation logic to check if the email is valid.
        // This is a simple example; you should use a more comprehensive email validation.
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}