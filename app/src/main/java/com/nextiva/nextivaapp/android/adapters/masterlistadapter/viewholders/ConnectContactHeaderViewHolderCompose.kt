/*
 * Copyright (c) 2024 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.view.Gravity
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemConnectMessagesComposeBinding
import com.nextiva.nextivaapp.android.fragments.ConnectContactsListFragment
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

internal class ConnectContactHeaderViewHolderCompose(
    itemView: View,
    context: Context,
    masterListListener: MasterListListener
): BaseViewHolder<ConnectContactHeaderListItem>(itemView, context, masterListListener) {

    private val masterItemView: View
    private lateinit var composeView: ComposeView

    init {
        bindViews(itemView)
        masterItemView = itemView
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectMessagesComposeBinding.bind(view)
        composeView = binding.composeView
    }

    override fun bind(listItem: ConnectContactHeaderListItem) {
        removeItemViewFromParent()

        mListItem = listItem

        composeView.setContent {
            var count by remember { mutableIntStateOf(mListItem.currentCount ?: 0) }
            val isLoading by mListItem.isLoadingLiveData.observeAsState(initial = false)
            var isExpanded by remember { mutableStateOf(mListItem.isExpanded) }

            mListItem.updateCount = { count = it }

            LaunchedEffect(mListItem.isExpanded) {
                isExpanded = mListItem.isExpanded
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(id = R.dimen.general_horizontal_margin_large),
                            vertical = dimensionResource(id = R.dimen.general_horizontal_margin_medium)
                        )
                        .clickable {
                            if (mMasterListListener != null &&
                                mListItem != null &&
                                mListItem.isLoadingLiveData.value != true &&
                                !ConnectContactsListFragment.isLoadingOnFragment
                            ) {
                                mListItem.isExpanded = !mListItem.isExpanded
                                mListItem.isLoadingLiveData.value = true
                                mMasterListListener.onConnectContactHeaderListItemClicked(mListItem)
                            }
                        }
                ) {
                    Text(
                        text = mListItem.data.title.orEmpty().uppercase(),
                        style = TextStyle(
                            color = colorResource(id = R.color.connectSecondaryDarkBlue),
                            fontSize = dimensionResource(id = R.dimen.material_text_caption).value.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.lato_heavy)),
                        ),
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.general_view_xmedium))
                                    .padding(end = dimensionResource(id = R.dimen.general_horizontal_margin_xsmall)),
                                color = colorResource(id = R.color.connectPrimaryBlue),
                                strokeWidth = 2.dp
                            )
                        }

                        Text(
                            text = count.toString(),
                            color = colorResource(id = R.color.connectGrey10),
                            modifier = Modifier
                                .padding(end = dimensionResource(id = R.dimen.general_padding_large))
                        )

                        val chevronIcon = if (isExpanded) {
                            mContext.getString(R.string.fa_chevron_down)
                        } else {
                            mContext.getString(R.string.fa_chevron_right)
                        }

                        AndroidView(
                            modifier = Modifier.size(dimensionResource(id = R.dimen.general_view_small)),
                            factory = { context ->
                                FontTextView(context).apply {
                                    gravity = Gravity.CENTER
                                    backgroundTintList = context.getColorStateList(R.color.connectGrey03)
                                    setTextColor(context.getColor(R.color.connectGrey10))
                                    setIcon(chevronIcon, Enums.FontAwesomeIconType.SOLID)
                                }
                            },
                            update = { ftv: FontTextView ->
                                ftv.setIcon(chevronIcon, Enums.FontAwesomeIconType.SOLID)
                            }
                        )
                    }
                }

                if (!mListItem.isExpanded) {
                    Divider(
                        color = colorResource(id = R.color.connectGrey03),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.hairline_small))
                    )
                }
            }
        }
    }
}
