package com.nextiva.nextivaapp.android.core.common

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.NextivaContact

class UiUtil {

    companion object {

        fun getAvatarInfoList(
            contactList: List<NextivaContact?>,
            teams: List<SmsTeam>?
        ): ArrayList<AvatarInfo> {
            val avatarInfoList: ArrayList<AvatarInfo> = ArrayList()
            val teamsAvatarInfoList: ArrayList<AvatarInfo> = ArrayList()

            contactList.forEach { contact ->
                contact?.avatarInfo?.let { avatarInfo ->
                    avatarInfo.size = AvatarInfo.SIZE_LARGE
                    avatarInfoList.add(avatarInfo)
                }
            }

            teams?.map { it.avatarInfo }?.forEach { avatarInfo ->
                avatarInfo.size = AvatarInfo.SIZE_LARGE
                teamsAvatarInfoList.add(avatarInfo)
            }

            teamsAvatarInfoList.sortWith(avatarInfoComparator)
            avatarInfoList.sortWith(avatarInfoComparator)

            avatarInfoList.addAll(teamsAvatarInfoList)

            return avatarInfoList
        }

        fun getUiName(context: Context, contactList: List<NextivaContact?>, teams: List<SmsTeam>?): String {
            val uiNameList: ArrayList<String> = ArrayList()
            val teamsUiNameList: ArrayList<String> = ArrayList()
            val uiNameStringList: ArrayList<String> = ArrayList()

            contactList.forEach { contact ->
                uiNameList.add(
                    contact?.uiName
                        ?: context.getString(R.string.connect_contact_details_unknown_contact)
                )
            }

            teams?.forEach { team ->
                (team.teamName ?: team.teamPhoneNumber)?.let { teamsUiNameList.add(it) }
            }

            teamsUiNameList.sortWith(uiNameComparator)
            uiNameList.sortWith(uiNameComparator)

            uiNameList.addAll(teamsUiNameList)

            when (uiNameList.size) {
                0, 1, 2, 3 -> uiNameList.forEach { uiNameStringList.add(it) }
                else -> {
                    uiNameStringList.add(uiNameList.first())
                    uiNameStringList.add(uiNameList[1])
                    uiNameStringList.add(
                        context.getString(
                            R.string.bottom_sheet_sms_details_overflow,
                            uiNameList.size - 2
                        )
                    )
                }
            }

            return uiNameStringList.joinToString(separator = ", ")
        }

        private val avatarInfoComparator = Comparator<AvatarInfo> { a, b ->
            when {
                a.displayName?.firstOrNull()?.isLetter() == false && b.displayName?.firstOrNull()
                    ?.isLetter() == true -> -1
                a.displayName?.firstOrNull()?.isLetter() == true && b.displayName?.firstOrNull()
                    ?.isLetter() == false -> 1
                else -> {
                    val compare = (a.displayName ?: "").compareTo(b.displayName ?: "", true)
                    when {
                        compare < 0 -> -1
                        compare > 0 -> 1
                        else -> 0
                    }
                }
            }
        }

        private val uiNameComparator = Comparator<String> { a, b ->
            when {
                a.firstOrNull()?.isLetter() == false && b.firstOrNull()?.isLetter() == true -> -1
                a.firstOrNull()?.isLetter() == true && b.firstOrNull()?.isLetter() == false -> 1
                else -> {
                    val compare = (a ?: "").compareTo(b ?: "", true)
                    when {
                        compare < 0 -> -1
                        compare > 0 -> 1
                        else -> 0
                    }
                }
            }
        }

        fun <T> LiveData<T>.toMutableLiveData(): MutableLiveData<T> {
            val mediatorLiveData = MediatorLiveData<T>()
            mediatorLiveData.addSource(this) {
                mediatorLiveData.value = it
            }
            return mediatorLiveData
        }
    }
}