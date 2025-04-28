package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.nextiva.nextivaapp.android.R

class ConnectTextInputView: LinearLayout {
    private lateinit var title: TextView
    private lateinit var editText: EditText

    val text: String
    get() { return editText.text.toString() }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_connect_text_input, this, true)
        title = findViewById(R.id.connect_text_input_title)
        editText = findViewById(R.id.connect_text_input_edit_text)

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ConnectTextInputView, 0, 0)
            title.text = ta.getString(R.styleable.ConnectTextInputView_text_input_title)
            editText.hint = ta.getString(R.styleable.ConnectTextInputView_text_input_hint)

            if (ta.getInt(R.styleable.ConnectTextInputView_text_input_max_length, -1) != -1) {
                editText.filters = arrayOf<InputFilter>(LengthFilter(ta.getInt(R.styleable.ConnectTextInputView_text_input_max_length, 0)))
            }

            ta.recycle()
        }
    }

    fun setTextWatcher(watcher: TextWatcher) {
        editText.addTextChangedListener(watcher)
    }

    fun setText(text: String) {
        editText.setText(text)
    }

    fun setInputType(inputType: Int) {
        editText.inputType = inputType
    }
}