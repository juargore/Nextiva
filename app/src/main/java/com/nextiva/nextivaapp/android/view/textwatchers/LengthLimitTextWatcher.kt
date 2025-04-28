package com.nextiva.nextivaapp.android.view.textwatchers

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class LengthLimitTextWatcher(private val editText: EditText, val maxLength: Int): TextWatcher {

    private var oldText = editText.text.toString()

    override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val newText = s?.toString() ?: ""

        if (newText.toByteArray(Charsets.UTF_8).size <= maxLength) {
            oldText = newText
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val newText = s?.toString() ?: ""

        if (newText.toByteArray(Charsets.UTF_8).size > maxLength) {
            editText.removeTextChangedListener(this)
            editText.setText(oldText)
            editText.setSelection(oldText.length)
            editText.addTextChangedListener(this)
        }
    }

    override fun afterTextChanged(s: Editable?) = Unit
}