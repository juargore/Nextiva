/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.view

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

/**
 * Created by Thaddeus Dannar on 10/16/23.
 */

class CustomSnackbar private constructor(
    parent: ViewGroup,
    content: View,
    addCloseButton: Boolean,
    contentViewCallback: com.google.android.material.snackbar.ContentViewCallback,
    snackStyle: SnackStyle
) : BaseTransientBottomBar<CustomSnackbar>(parent, content, contentViewCallback) {

    private var textView: TextView = content.findViewById(R.id.snackbar_text)
    private var closeButton: View = content.findViewById(R.id.snackbar_close)
    private var undoButton: View = content.findViewById(R.id.snackbar_undo)
    private var icon: FontTextView = content.findViewById(R.id.snackbar_icon)
    private var constraintLayout: ConstraintLayout = content.findViewById(R.id.custom_snackbar)
    private var defaultLinkTextColor: Int = R.color.white


    init {

        getView().apply {
            setBackgroundColor(0) // 0 means transparent, we need to remove background from rootview
            setPadding(0, 0, 0, 0)
        }
        content.setOnClickListener { dismiss() }

        if (!addCloseButton) {
            closeButton.visibility = View.GONE
        }

        when(snackStyle) {
            SnackStyle.Standard -> {
                constraintLayout.background = ContextCompat.getDrawable(context, R.drawable.rounded_snackbar) as GradientDrawable
                textView.typeface = ResourcesCompat.getFont(context, R.font.lato_regular)
                textView.setTextColor(ContextCompat.getColor(context, R.color.connectWhite))
                icon.setTextColor(context.getColor(R.color.connectSecondaryGrey))
            }
            SnackStyle.Error -> {
                constraintLayout.background = ContextCompat.getDrawable(context, R.drawable.rounded_error_snackbar) as GradientDrawable
                textView.typeface = ResourcesCompat.getFont(context, R.font.lato_bold)
                icon.setTextColor(context.getColor(R.color.connectPrimaryRed))
                textView.setTextColor(ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue))

            }
        }
    }

    fun setText(text: CharSequence, gravity: Int = Gravity.START): CustomSnackbar {
        textView.gravity = gravity
        textView.text = text
        return this
    }

    fun setLink(textToLink: String, linkTextColor: Int = defaultLinkTextColor, clickListener: View.OnClickListener): CustomSnackbar {
        val spannableText = SpannableString(textView.text)
        val startIndex = textView.text.indexOf(textToLink)
        if (startIndex != -1) {
            val clickableSpan = object : ClickableSpan() {
                override fun updateDrawState(tp: TextPaint) {
                    tp.isUnderlineText = false
                    tp.typeface = Typeface.DEFAULT_BOLD
                }
                override fun onClick(widget: View) {
                    clickListener.onClick(widget)
                    dismiss()
                }
            }
            spannableText.setSpan(
                    clickableSpan,
                    startIndex,
                    startIndex + textToLink.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableText.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, linkTextColor)), startIndex, startIndex + textToLink.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textView.text = spannableText
        textView.movementMethod = LinkMovementMethod.getInstance()
        return this
    }

    fun setCloseAction(clickListener: View.OnClickListener): CustomSnackbar {
        closeButton.visibility = View.VISIBLE
        closeButton.setOnClickListener {
            dismiss()
            clickListener.onClick(it)
        }
        return this
    }

    fun disableCloseButton(): CustomSnackbar {
        closeButton.setOnClickListener(null)
        closeButton.visibility = View.GONE
        return this
    }

    fun enableUndoAction(onUndoClicked: () -> Unit): CustomSnackbar {
        undoButton.visibility = View.VISIBLE
        undoButton.setOnClickListener {
            onUndoClicked()
            dismiss()
        }
        return this
    }

    fun setFontAwesomeIcon(fontAwesomeIcon: String){
        icon.setIcon(fontAwesomeIcon, Enums.FontAwesomeIconType.REGULAR)
        icon.visibility = View.VISIBLE
    }

    companion object {
        fun make(
            view: View,
            duration: Int,
            contentViewCallBack: com.google.android.material.snackbar.ContentViewCallback,
            addCloseButton: Boolean = true,
            snackStyle: SnackStyle = SnackStyle.Standard
        ): CustomSnackbar {
            val parent = findSuitableParent(view)
                    ?: throw IllegalArgumentException("No suitable parent found for the custom snackbar")

            val customSnackbarView = LayoutInflater.from(view.context)
                    .inflate(R.layout.custom_snackbar, parent, false)

            val customSnackbar = CustomSnackbar(
                parent,
                customSnackbarView,
                addCloseButton,
                contentViewCallBack,
                snackStyle
            )

            customSnackbar.duration = duration
            return customSnackbar
        }

        private fun findSuitableParent(view: View): ViewGroup? {
            var currentView: View? = view
            var fallback: ViewGroup? = null

            while (currentView != null) {
                if (currentView is ViewGroup) {
                    if (currentView.id == android.R.id.content) {
                        return currentView
                    } else if (fallback == null) {
                        fallback = currentView
                    }
                }
                val parent = currentView.parent
                currentView = if (parent is View) parent else null
            }

            return fallback
        }
    }
}

enum class SnackStyle {
    Standard, Error;

    companion object {
        fun fromBoolean(success: Boolean) : SnackStyle {
            return if(success) {
                Standard
            } else {
                Error
            }
        }
    }
}
