package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.NextivaApplication
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DesignSystemListItem
import com.nextiva.nextivaapp.android.constants.Enums
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DesignSystemUtilityViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

    private var baseListItemsLiveData: MutableLiveData<ArrayList<BaseListItem>?> = MutableLiveData()
    private var selection = Enums.DesignSystemResourceType.COLOR

    fun selectedTab(tab: Int) {
        selection = tab
        loadListItems()
    }

    fun loadListItems() {
        when (selection) {
            Enums.DesignSystemResourceType.FONT -> {
                loadFontItems()
            }
            else -> {
                loadColorItems()
            }
        }
    }

    fun getListItemLiveData(): LiveData<ArrayList<BaseListItem>?> {
        return baseListItemsLiveData
    }

    private fun loadFontItems() {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        addFontSection("",
            intArrayOf(
                R.style.DS_Body1,
                R.style.DS_Body2,
                R.style.DS_Body2Heavy,
                R.style.DS_Caption1,
                R.style.DS_Caption1Heavy,
                R.style.DS_Caption2,
                R.style.DS_Caption2Italic,
                R.style.DS_H5,
                R.style.DS_H6,
                R.style.DS_Overline
            ), listItems)

        baseListItemsLiveData.value = listItems
    }

    private fun loadColorItems() {
        val listItems: ArrayList<BaseListItem> = ArrayList()

        addSection("Primary Colors",
            intArrayOf(
                R.color.ltGrey_55,
                R.color.ltGrey,
                R.color.mediumGrey,
                R.color.coolGray_50,
                R.color.grey,
                R.color.grey03,
                R.color.grey10,
                R.color.secondaryLightGrey,
                R.color.dkGrey,
                R.color.darkerGray,
                R.color.darkerGray_55,
                R.color.disabledTextGrey,
                R.color.backgroundGrey,
                R.color.secondaryDarkBlue,
                R.color.secondaryLtGrey,
                R.color.seekBackgroundGrey), listItems)

        addSection("Avatar Colors",
            intArrayOf(
                R.color.avatarBackground,
                R.color.avatarOnlinePresence,
                R.color.avatarAwayPresence,
                R.color.avatarBusyPresence,
                R.color.avatarOfflinePresence,
                R.color.avatarPendingPresence
            ), listItems)

        addSection("Group Header List Item",
            intArrayOf(
                R.color.groupHeaderBackground
            ), listItems)

        addSection("Text Colors",
            intArrayOf(
                R.color.contactListItemSubtitle,
                R.color.contactListItemSubtitleDark,
                R.color.smsMessageListItemSubtitle,
                R.color.smsMessageListItemtitle,
                R.color.voicemailListItemSubtitle,
                R.color.colorDialerText
            ), listItems)

        addSection("Divider Colors",
            intArrayOf(
                R.color.listItemDividerLight,
                R.color.listItemDividerDark
            ), listItems)

        addSection("Tabbar Colors",
            intArrayOf(
                R.color.tabbarChatStroke,
                R.color.chatChipBarBackgroundColor,
                R.color.menuButtonFillColor,
                R.color.darkModeMenuBar
            ), listItems)

        addSection("Primary Colors",
                intArrayOf(R.color.connectPrimaryBlue,
                        R.color.connectPrimaryLightBlue,
                        R.color.connectPrimaryGrey,
                        R.color.connectPrimaryGreen,
                        R.color.connectPrimaryRed,
                        R.color.connectPrimaryLightRed,
                        R.color.connectPrimaryYellow,
                        R.color.connectPrimaryOrange), listItems)

        addSection("Secondary Colors",
                intArrayOf(R.color.connectSecondaryGrey,
                        R.color.connectSecondaryBlue,
                        R.color.connectSecondaryLightBlue,
                        R.color.connectSecondaryBrightBlue,
                        R.color.connectSecondaryDarkBlue,
                        R.color.connectSecondaryRed,
                        R.color.connectSecondaryYellow), listItems)

        addSection("Grey Colors",
                intArrayOf(R.color.connectWhite,
                        R.color.connectGrey01,
                        R.color.connectGrey02,
                        R.color.connectGrey03,
                        R.color.connectGrey08,
                        R.color.connectGrey09,
                        R.color.connectGrey10), listItems)

        baseListItemsLiveData.value = listItems
    }

    fun addSection(title: String, colors: IntArray, listItems: ArrayList<BaseListItem>) {
        val application = application as NextivaApplication
        listItems.add(DesignSystemListItem(title, 0, Enums.DesignSystemResourceType.SECTION))
        for (color in colors) {
            val title = application.resources.getResourceEntryName(color)
            listItems.add(DesignSystemListItem(title, color, Enums.DesignSystemResourceType.COLOR))
        }
    }

    fun addFontSection(title: String, styles: IntArray, listItems: ArrayList<BaseListItem>) {
        val application = application as NextivaApplication
        listItems.add(DesignSystemListItem(title, 0, Enums.DesignSystemResourceType.SECTION))
        for (style in styles) {
            val title = application.resources.getResourceEntryName(style)
            listItems.add(DesignSystemListItem(title, style, Enums.DesignSystemResourceType.FONT))
        }
    }
}