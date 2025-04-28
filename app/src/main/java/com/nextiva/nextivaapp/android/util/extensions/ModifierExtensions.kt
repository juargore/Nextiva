package com.nextiva.nextivaapp.android.util.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

fun Modifier.contentDescription(contentDescription: String): Modifier {
    return this.semantics {
        this.contentDescription = contentDescription
    }
}