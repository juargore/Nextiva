package com.nextiva.nextivaapp.android.core.notifications.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.core.notifications.models.HolidaySelection
import com.nextiva.nextivaapp.android.core.notifications.viewmodel.ObservedHolidayListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ObservedHolidayActivity : BaseActivity() {

    private lateinit var holidayListViewModel: ObservedHolidayListViewModel

    companion object {
        const val INTEN_EXTRA_HOLIDAY_LIST = "observedHolidaySelectedList"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        holidayListViewModel =
            ViewModelProvider(this)[ObservedHolidayListViewModel::class.java]
        val activity = this

        val holidayList = holidayListViewModel.getObservedHolidayList()

        val data: Intent? = intent
        data?.hasExtra(INTEN_EXTRA_HOLIDAY_LIST)?.let {
            val list = data.getStringExtra(INTEN_EXTRA_HOLIDAY_LIST)
            if (list == null || list.isEmpty()) {
                holidayListViewModel.observedHolidaySelectionList = holidayList.map { holiday ->
                    HolidaySelection(holiday, false)
                }.toList()
            } else {
                val gsonList: ArrayList<HolidaySelection> = Gson().fromJson(
                    list,
                    object :
                        com.google.common.reflect.TypeToken<ArrayList<HolidaySelection>>() {}.type
                )
                holidayListViewModel.observedHolidaySelectionList = gsonList
            }
        }

        setContentView(ComposeView(this).apply {
            setContent {
                ObservedHolidayScreenView(
                    selectedHolidayList = holidayListViewModel.observedHolidaySelectionList.toMutableList(),
                    holidayList = holidayList,
                    onBackButton = { activity.finish() },
                    observedHolidayAddListener = { holidayList ->

                        val returnIntent = Intent()
                        holidayList?.let {
                            returnIntent.putExtra(
                                INTEN_EXTRA_HOLIDAY_LIST, Gson().toJson(holidayList)
                            )
                        }
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                )
            }
        })
    }
}