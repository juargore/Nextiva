/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.models

import com.nextiva.nextivaapp.android.adapters.ConnectMainViewPagerAdapter
import com.nextiva.nextivaapp.android.fragments.GeneralRecyclerViewFragment

/**
 * Created by Thaddeus Dannar on 9/13/23.
 */

data class BottomNavigationItem (
    var itemId: ConnectMainViewPagerAdapter.FeatureType,
    var title: String,
    var fragment: GeneralRecyclerViewFragment? = null,
    var faIcon: Int? = null,
    var drawableIcon: Int? = null,
    )  : java.io.Serializable