/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.ConnectMainViewPagerAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.models.BottomNavigationItem
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable

/**
 * Created by Thaddeus Dannar on 9/18/23.
 */
class UIUtil {


    companion object {
        fun getFontAwesomeDrawable(context: Context, iconId: Int): Drawable {
            return getFontAwesomeDrawable(context, false, iconId)
        }
        fun getFontAwesomeDrawable(context: Context, isSelected: Boolean, iconId: Int): Drawable {
            return FontDrawable(context,
                iconId,
                if (isSelected) Enums.FontAwesomeIconType.SOLID else Enums.FontAwesomeIconType.REGULAR)
                .withColor(
                    ContextCompat.getColor(context,
                        if (isSelected) R.color.connectPrimaryBlue else R.color.connectGrey10))
                .withSize(R.dimen.material_text_body1)
        }

        fun getIconFontString(context: Context, navItem: ConnectMainViewPagerAdapter.FeatureType): String?{
            return when (navItem) {
                ConnectMainViewPagerAdapter.FeatureType.Calls -> context.getString(R.string.fa_phone_alt)
                ConnectMainViewPagerAdapter.FeatureType.Voicemail -> context.getString(R.string.fa_comment_dots)
                ConnectMainViewPagerAdapter.FeatureType.Messaging -> context.getString(R.string.fa_video)
                ConnectMainViewPagerAdapter.FeatureType.Meetings -> context.getString(R.string.fa_user)
                ConnectMainViewPagerAdapter.FeatureType.Contacts -> context.getString(R.string.fa_user_circle)
                ConnectMainViewPagerAdapter.FeatureType.Rooms -> context.getString(R.string.fa_door_open)
                ConnectMainViewPagerAdapter.FeatureType.Chat -> context.getString(R.string.fa_comments_alt)
                ConnectMainViewPagerAdapter.FeatureType.More -> context.getString(R.string.fa_grid_2)
                ConnectMainViewPagerAdapter.FeatureType.Calendar -> context.getString(R.string.fa_calendar)
                else -> null
            }
        }

        fun getNavListItemDrawable(context: Context, navItem: BottomNavigationItem, isItemSelected: Boolean): Drawable? {
            return if(navItem.faIcon != null) navItem.faIcon?.let {
                getFontAwesomeDrawable(
                    context,
                    isItemSelected,
                    it
                )
            } else navItem.drawableIcon?.let {
                ContextCompat.getDrawable(context, it)
            }
        }

    }
}