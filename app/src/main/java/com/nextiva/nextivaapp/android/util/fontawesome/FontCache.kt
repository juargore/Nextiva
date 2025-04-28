package com.nextiva.nextivaapp.android.util.fontawesome

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.FontAwesomeVersion
import java.util.Hashtable


object FontCache {
    private val fontCache: Hashtable<Int?, Typeface?>? = Hashtable()

    operator fun get(
        context: Context,
        @Enums.FontAwesomeIconType.Type iconType: Int,
        @Enums.FontAwesomeVersion.Type faVersion: Int
    ): Typeface? {
        val fontId = when (iconType) {
            Enums.FontAwesomeIconType.SOLID -> if (faVersion == FontAwesomeVersion.FA_V6) {
                R.font.fa_v6_solid_900
            } else {
                R.font.fa_solid_900
            }

            Enums.FontAwesomeIconType.BRAND -> if (faVersion == FontAwesomeVersion.FA_V6) {
                R.font.fa_v6_brands_400
            } else {
                R.font.fa_brands_400
            }

            Enums.FontAwesomeIconType.CUSTOM -> R.font.fa_custom
            else -> if (faVersion == FontAwesomeVersion.FA_V6) {
                R.font.fa_v6_regular_400
            } else {
                R.font.fa_regular_400
            }
        }

        var typeface: Typeface? = fontCache?.get(fontId)

        if (typeface == null) {
            typeface = try {
                ResourcesCompat.getFont(context, fontId)

            } catch (e: Exception) {
                return null
            }

            fontCache?.put(fontId, typeface)
        }

        return typeface
    }
}