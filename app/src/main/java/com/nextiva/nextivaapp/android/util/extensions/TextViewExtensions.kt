package com.nextiva.nextivaapp.android.util.extensions

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.util.LinkifyCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.models.NextivaContact
import java.util.regex.Pattern

fun TextView.makeLinkable() {
    val pattern = Pattern.compile("""(\+?[0-9]{1,3}? ?-?\(?[0-9]{1,3}\)? ?-?[0-9]{3,5} ?-?[0-9]{4}( ?-?[0-9]{3})?)""", Pattern.CASE_INSENSITIVE)
    LinkifyCompat.addLinks(this, Linkify.PHONE_NUMBERS or Linkify.EMAIL_ADDRESSES or Linkify.WEB_URLS)
    LinkifyCompat.addLinks(this, pattern, "tel://", null, null, null)
}

fun TextView.setUnderlinedText(text: String?) {
    text?.let {
        val spannableString = SpannableString(it)
        spannableString.setSpan(UnderlineSpan(), 0, it.length, 0)
        this.text = spannableString

    } ?: run {
        this.text = null
    }
}

fun TextView.setContactTypeLabel(contact: NextivaContact?) {
    setPadding(resources.getDimension(R.dimen.general_padding_small).toInt(),
            resources.getDimension(R.dimen.general_padding_xxsmall).toInt(),
            resources.getDimension(R.dimen.general_padding_small).toInt(),
            resources.getDimension(R.dimen.general_padding_xxsmall).toInt())

    when (contact?.contactType) {
        Enums.Contacts.ContactTypes.CONNECT_USER -> {
            visibility = View.VISIBLE
            text = context.getString(R.string.connect_contact_details_teammate_label)
            setTextColor(ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue))
            background.setTint(ContextCompat.getColor(context, R.color.connectGrey03))
        }
        Enums.Contacts.ContactTypes.CONNECT_SHARED,
        Enums.Contacts.ContactTypes.CONNECT_PERSONAL -> {
            visibility = View.VISIBLE
            text = context.getString(R.string.connect_contact_details_business_label)
            setTextColor(ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue))
            background.setTint(ContextCompat.getColor(context, R.color.connectSecondaryYellow))
        }
        Enums.Contacts.ContactTypes.LOCAL -> {
            visibility = View.GONE
        }
        Enums.Contacts.ContactTypes.CONNECT_TEAM -> {
            visibility = View.VISIBLE
            text = context.getString(R.string.connect_contact_details_teams_label)
            setTextColor(ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue))
            background.setTint(ContextCompat.getColor(context, R.color.connectGrey03))
        }
        Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW -> {
            visibility = View.VISIBLE
            text = context.getString(R.string.connect_contact_details_call_flow_label)
            setTextColor(ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue))
            background.setTint(ContextCompat.getColor(context, R.color.connectGrey03))

        }
        Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS -> {
            visibility = View.VISIBLE
            text = context.getString(R.string.connect_contact_details_call_center_label)
            setTextColor(ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue))
            background.setTint(ContextCompat.getColor(context, R.color.connectGrey03))
        }
        else -> {
            visibility = View.VISIBLE
            text = context.getString(R.string.connect_contact_details_unsaved_label)
            setTextColor(ContextCompat.getColor(context, R.color.connectWhite))
            background.setTint(ContextCompat.getColor(context, R.color.connectGrey10))
        }
    }
}

fun TextView.setBlockedContactTypeLabel() {
    setPadding(
        resources.getDimension(R.dimen.general_padding_small).toInt(),
        resources.getDimension(R.dimen.general_padding_xxsmall).toInt(),
        resources.getDimension(R.dimen.general_padding_small).toInt(),
        resources.getDimension(R.dimen.general_padding_xxsmall).toInt()
    )

    visibility = View.VISIBLE
    text = context.getString(R.string.connect_contact_details_blocked_label)
    setTextColor(ContextCompat.getColor(context, R.color.connectSecondaryRed))
    background?.setTint(ContextCompat.getColor(context, R.color.surfaceError))
}
