package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import java.util.Objects

class OneActiveCallButtonView: ConstraintLayout {
    private val attributeNotSet = -1

    private lateinit var iconSwitcher: TextSwitcher
    private lateinit var firstIconView: FontTextView
    private lateinit var secondIconView: FontTextView
    private lateinit var textSwitcher: TextSwitcher
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var primaryIcon: Int = -1
    private var secondaryIcon: Int = -1
    private var currentIcon: Int? = -1

    private var wasSelected = false

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
        Objects.requireNonNull(inflater).inflate(R.layout.view_one_active_call_button, this, true)

        iconSwitcher = findViewById(R.id.active_call_icon_switcher)
        firstIconView = findViewById(R.id.active_call_first_font_text_view)
        secondIconView = findViewById(R.id.active_call_second_font_text_view)
        textSwitcher = findViewById(R.id.active_call_button_text_switcher)
        firstTextView = findViewById(R.id.active_call_button_first_text_view)
        secondTextView = findViewById(R.id.active_call_button_second_text_view)

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ActiveCallButtonView, 0, 0)

        try {
            primaryIcon = typedArray.getResourceId(R.styleable.ActiveCallButtonView_primaryIcon, attributeNotSet)
            secondaryIcon = typedArray.getResourceId(R.styleable.ActiveCallButtonView_secondaryIcon, attributeNotSet)

            firstIconView.rotation = typedArray.getFloat(R.styleable.ActiveCallButtonView_iconRotation, 0F)

            setIcon(primaryIcon)
            setIconSwitcherBackground(typedArray.getResourceId(R.styleable.ActiveCallButtonView_iconBackground, attributeNotSet))
            setText(typedArray.getResourceId(R.styleable.ActiveCallButtonView_text, attributeNotSet))
            setIconTint(typedArray.getResourceId(R.styleable.ActiveCallButtonView_iconTint, attributeNotSet))
            setTextColor(typedArray.getResourceId(R.styleable.ActiveCallButtonView_subTextColor, attributeNotSet))
            setImageContentDescription(typedArray.getResourceId(R.styleable.ActiveCallButtonView_contentDescription, attributeNotSet))
            setIconAnimationIn(typedArray.getResourceId(R.styleable.ActiveCallButtonView_imageAnimationIn, attributeNotSet))
            setIconAnimationOut(typedArray.getResourceId(R.styleable.ActiveCallButtonView_imageAnimationOut, attributeNotSet))
            setTextAnimationIn(typedArray.getResourceId(R.styleable.ActiveCallButtonView_textAnimationIn, attributeNotSet))
            setTextAnimationOut(typedArray.getResourceId(R.styleable.ActiveCallButtonView_textAnimationOut, attributeNotSet))

        } finally {
            typedArray.recycle()
        }

        setContentDescription(context)
    }

    private fun setIconSwitcherBackground(@DrawableRes background: Int) {
        if (background != attributeNotSet) {
            iconSwitcher.background = AppCompatResources.getDrawable(context, background)
        }
    }

    private fun setText(text: String) {
        if (!TextUtils.equals((textSwitcher.currentView as TextView).text, text)) {
            textSwitcher.setCurrentText(text)
        }
    }

    private fun setText(@StringRes resourceId: Int) {
        if (resourceId != attributeNotSet) {
            setText(context.getText(resourceId).toString())
        }
    }

    private fun setIcon(@StringRes fontIcon: Int) {
        (iconSwitcher.currentView as FontTextView).let { iconView ->
            if (fontIcon != attributeNotSet && iconView.tag != context.getString(fontIcon)) {
                val iconToUse = if (fontIcon != primaryIcon && fontIcon != secondaryIcon) {
                    fontIcon
                } else {
                    if (isSelected) secondaryIcon else primaryIcon
                }

                val iconType = when (iconToUse) {
                    R.string.fa_custom_dialer -> Enums.FontAwesomeIconType.CUSTOM
                    R.string.fa_bluetooth_b -> Enums.FontAwesomeIconType.BRAND
                    else -> Enums.FontAwesomeIconType.SOLID
                }

                iconView.setIcon(iconToUse, iconType)
                iconView.tag = context.getString(iconToUse)

                currentIcon = iconToUse
            }
        }
    }

    fun setIconBackgroundResource(@DrawableRes drawableRes: Int) {
        iconSwitcher.setBackgroundResource(drawableRes)
    }

    fun setIconTint(@ColorRes iconTintColor: Int) {
        if (iconTintColor != attributeNotSet) {
            setTintOnImageView(firstIconView, iconTintColor)
            setTintOnImageView(secondIconView, iconTintColor)
        }
    }

    private fun setTintOnImageView(fontTextView: FontTextView, @ColorRes imageTintColor: Int) {
        if (imageTintColor != attributeNotSet) {
            fontTextView.setTextColor(ContextCompat.getColor(context, imageTintColor))
        }
    }

    private fun setTextColor(@ColorRes textTintColor: Int) {
        if (textTintColor != attributeNotSet) {
            setColorOnTextView(firstTextView, textTintColor)
            setColorOnTextView(secondTextView, textTintColor)
        }
    }

    private fun setColorOnTextView(textView: TextView, textTintColor: Int) {
        if (textTintColor != attributeNotSet) {
            textView.setTextColor(textTintColor)
        }
    }

    private fun setImageContentDescription(@StringRes contentDescriptionResource: Int) {
        if (contentDescriptionResource != attributeNotSet) {
            setFirstImageContentDescription(contentDescriptionResource)
        }
    }

    private fun setFirstImageContentDescription(@StringRes contentDescriptionResource: Int) {
        if (contentDescriptionResource != attributeNotSet) {
            setContentDescriptionOnFontTextView(firstIconView, context.getText(contentDescriptionResource))
        }
    }

    private fun setContentDescriptionOnFontTextView(fontTextView: FontTextView, contentDescription: CharSequence) {
        fontTextView.contentDescription = contentDescription
    }

    private fun setTextAnimationIn(@AnimRes animationInResource: Int) {
        if (animationInResource != attributeNotSet) {
            setTextAnimationIn(textSwitcher, animationInResource)
        }
    }

    private fun setTextAnimationIn(textSwitcher: TextSwitcher, @AnimRes animationInResource: Int) {
        if (animationInResource != attributeNotSet) {
            setAnimationInOnTextSwitcher(textSwitcher, animationInResource)
        }
    }

    private fun setTextAnimationOut(@AnimRes animationOutResource: Int) {
        if (animationOutResource != attributeNotSet) {
            setAnimationInOnTextSwitcher(textSwitcher, animationOutResource)
        }
    }

    private fun setAnimationInOnTextSwitcher(textSwitcher: TextSwitcher, @AnimRes animationResource: Int) {
        if (animationResource != attributeNotSet) {
            textSwitcher.setInAnimation(context, animationResource)
        }
    }

    private fun setIconAnimationIn(@AnimRes animationInResource: Int) {
        if (animationInResource != attributeNotSet) {
            try {
                setAnimationInOnIconSwitcher(iconSwitcher, animationInResource)
            } catch (e: Resources.NotFoundException) {
                LogUtil.d("Animation not found: $e")
            }
        }
    }

    private fun setIconAnimationOut(@AnimRes animationOutResource: Int) {
        if (animationOutResource != attributeNotSet) {
            try {
                setAnimationInOnIconSwitcher(iconSwitcher, animationOutResource)
            } catch (e: Resources.NotFoundException) {
                LogUtil.d("Animation not found: $e")
            }
        }
    }

    private fun setAnimationInOnIconSwitcher(iconSwitcher: TextSwitcher, @AnimRes animationResource: Int) {
        if (animationResource != attributeNotSet) {
            iconSwitcher.setInAnimation(context, animationResource)
        }
    }

    private fun switchIcon(@StringRes icon: Int) {
        if (iconSwitcher.tag != icon) {
            setIcon(icon)
            iconSwitcher.tag = context.getString(icon)
        }
    }

    private fun switchText(text: String) {
        if (textSwitcher.tag != text) {
            textSwitcher.setText(text)
            textSwitcher.tag = text
        }
    }

    fun switchIconText(@StringRes icon: Int, text: String) {
        switchIcon(icon)
        switchText(text)
    }

    private fun disableEnableControls(enable: Boolean, vg: ViewGroup) {
        for (i in 0 until vg.childCount) {
            val child = vg.getChildAt(i)
            child.isEnabled = enable
            if (child is ViewGroup) {
                disableEnableControls(enable, child)
            }
        }
    }

    fun setContentDescription(context: Context) {
        val contentDescription = context.getString(getContentDescriptionStringId())
        setContentDescription(contentDescription)
        iconSwitcher.contentDescription = context.getString(R.string.active_call_image_switcher_content_description, contentDescription)
        firstIconView.contentDescription = context.getString(R.string.active_call_first_image_view_content_description, contentDescription)
        secondIconView.contentDescription = context.getString(R.string.active_call_second_image_view_content_description, contentDescription)
        textSwitcher.contentDescription = context.getString(R.string.active_call_text_switcher_content_description, contentDescription)
        firstTextView.contentDescription = context.getString(R.string.active_call_first_text_view_content_description, contentDescription)
        secondTextView.contentDescription = context.getString(R.string.active_call_second_text_view_content_description, contentDescription)
        disableEnableControls(this.isEnabled, this)
    }

    private fun getContentDescriptionStringId(): Int {
        when (id) {
            R.id.active_call_mute_button -> return R.string.active_call_mute_button_content_description
            R.id.active_call_hold_button -> return R.string.active_call_hold_button_content_description
            R.id.active_call_speaker_button -> return R.string.active_call_speaker_button_content_description
            R.id.active_call_keypad_button -> return R.string.active_call_keypad_button_content_description
            R.id.active_call_video_add_participant_button -> return if (currentIcon == R.string.fa_user_plus) R.string.active_call_add_participants_button_content_description else R.string.active_call_video_button_content_description
            R.id.active_call_new_call_swap_button -> return if (currentIcon == R.string.fa_exchange) R.string.active_call_swap_call_button_content_description else R.string.active_call_new_call_button_content_description
            R.id.active_call_end_call_button -> return R.string.active_call_end_call_button_content_description
            R.id.active_call_more_button -> return R.string.active_call_more_button_content_description
            R.id.active_call_transfer_complete_button -> return R.string.active_call_complete_transfer_button_content_description
            R.id.active_call_transfer_end_button -> return R.string.active_call_end_transfer_button_content_description
        }
        return -1
    }

    fun updateSelection(selected: Boolean) {
        isSelected = selected

        if (selected != wasSelected) {
            setIcon(if (selected) secondaryIcon else primaryIcon)
        }
        wasSelected = selected
    }
}