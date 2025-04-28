package com.nextiva.nextivaapp.android.core.common.ui

import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.fragments.ConnectCallsFragment
import com.nextiva.nextivaapp.android.models.CallTabItem

@Composable
internal fun TabListComposeView(tabsList: List<CallTabItem>, currentTabId: ConnectCallsFragment.CallTabs, onTabChanged: (Int) -> Unit) {
    var state by remember { mutableStateOf(currentTabId) }
    TabRow(
        selectedTabIndex = state.id,
        backgroundColor = colorResource(id = R.color.connectGrey01),
        divider = {},
        indicator = {}
    ) {

        tabsList.forEachIndexed { index, _ ->

            RoundedTabItem(tab= tabsList[index],index = index, state= state.id, maxBadgeCharLimit = ConnectCallsFragment.MAX_BADGE_CHAR_LIMIT){ tabIndex ->
                state = index.let { ConnectCallsFragment.CallTabs.values()[it] }
                onTabChanged.invoke(tabIndex)
            }

        }
    }
}