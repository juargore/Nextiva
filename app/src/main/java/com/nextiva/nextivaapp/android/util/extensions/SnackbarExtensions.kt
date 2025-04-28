package com.nextiva.nextivaapp.android.util.extensions

import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable

fun Snackbar.withFontAwesomeDrawable(drawable: FontDrawable?): Snackbar {
    (view.findViewById(com.google.android.material.R.id.snackbar_text) as? TextView)?.let { textView ->
        drawable?.let {
            textView.setCompoundDrawablesWithIntrinsicBounds(it.withSize(R.dimen.material_text_subhead), null, null, null)
            textView.compoundDrawablePadding = context.resources.getDimensionPixelOffset(R.dimen.general_padding_xmedium)
        }
    }

    return this
}