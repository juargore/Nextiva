package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.util.AttributeSet
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceViewHolder
import com.nextiva.nextivaapp.android.R

class CheckBoxPreferenceWithContentDescription : CheckBoxPreference {
    var contentDescription: String? = null

    constructor(cont: Context) : super(cont)
    constructor(cont: Context, attrs: AttributeSet) : super(cont, attrs) {
        setContentDescription(attrs)
    }

    constructor(cont: Context, attrs: AttributeSet, defStyleAttr: Int) : super(cont, attrs, defStyleAttr) {
        setContentDescription(attrs)
    }

    constructor(cont: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(cont, attrs, defStyleAttr, defStyleRes) {
        setContentDescription(attrs)
    }

    private fun setContentDescription(attrs: AttributeSet) {
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.ContentDescription)
        contentDescription = styledAttrs.getString(R.styleable.ContentDescription_contentDescription)
        styledAttrs.recycle()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        holder.findViewById(android.R.id.checkbox)?.contentDescription = contentDescription
        super.onBindViewHolder(holder)
    }
}