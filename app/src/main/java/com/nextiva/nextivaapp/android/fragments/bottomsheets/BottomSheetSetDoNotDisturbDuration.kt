package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetSetDoNotDisturbDurationBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.util.extensions.dayWithSuffix
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetSetDoNotDisturbDuration : BaseBottomSheetDialogFragment() {
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var presenceRepository: PresenceRepository
    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var calendarManager: CalendarManager

    private val formatterManager = FormatterManager.getInstance()

    private lateinit var dndDoneTxt: TextView
    private lateinit var dndDurationRadioGroup: RadioGroup
    private lateinit var datePicker: TextView
    private lateinit var timePicker: TextView
    private lateinit var customLayout: LinearLayout
    private lateinit var customButton: RadioButton

    private var selectedDate = LocalDateTime.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_set_do_not_disturb_duration, container, false)
        view?.let { bindViews(view) }
        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetSetDoNotDisturbDurationBinding.bind(view)
        dndDoneTxt = binding.bottomSheetDndDoneText
        dndDurationRadioGroup = binding.radioGroupDndDuration
        datePicker = binding.bottomSheetDoNotDisturbDurationDatePicker
        timePicker = binding.bottomSheetDoNotDisturbDurationTimePicker
        customLayout = binding.bottomSheetDoNotDisturbDurationCustomLayout
        customButton = binding.radioButtonDndDurationCustom

        dndDoneTxt.setOnClickListener {
            val (dndExpiresAtDateTime, dndDuration) = getDndDurationPair()
            presenceRepository.setPresence(
                Enums.Contacts.ConnectPresenceStates.DND,
                arguments?.getString(Constants.PRESENCE_STATUS_TEXT),
                dndExpiresAtDateTime,
                if (dndDuration == -1) null else dndDuration
            )
            this.dismiss()
        }
        customButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedDate = LocalDateTime.now()
                setDateText()
                setTimeText()
                customLayout.visibility = View.VISIBLE

            } else {
                customLayout.visibility = View.GONE
            }
        }

        datePicker.setOnClickListener {
            val dialog = DatePickerDialog(requireActivity(), { _, year, month, day ->
                selectedDate = selectedDate.withYear(year)
                    .withDayOfMonth(day)
                    .withMonth(month.plus(1))
                setDateText()

            }, selectedDate.year, selectedDate.month.ordinal, selectedDate.dayOfMonth)
            dialog.datePicker.minDate = System.currentTimeMillis()
            dialog.show()
        }

        timePicker.setOnClickListener {
            val dialog = TimePickerDialog(requireActivity(), { _, hour, minute ->
                val currentTime = LocalDateTime.now()
                var isTimeEarlierThanNow = false

                if (selectedDate.monthValue == currentTime.monthValue &&
                        selectedDate.year == currentTime.year &&
                        selectedDate.dayOfMonth == currentTime.dayOfMonth) {
                    isTimeEarlierThanNow = selectedDate.hour < currentTime.hour || (selectedDate.hour == currentTime.hour && selectedDate.minute < currentTime.minute)
                }

                selectedDate = selectedDate.withHour(if (isTimeEarlierThanNow) currentTime.hour else hour)
                    .withMinute(if (isTimeEarlierThanNow) currentTime.minute else minute)
                setTimeText()

            }, selectedDate.hour, selectedDate.minute, false)
            dialog.show()
        }
    }

    private fun getDndDurationPair(): Pair<String?, Int?> {
        val checkedIndex = dndDurationRadioGroup.indexOfChild(
            dndDurationRadioGroup.findViewById(dndDurationRadioGroup.checkedRadioButtonId)
        )

        var dndDuration = -1
        var dndExpiresAtDateTime: String? = null
        when (checkedIndex) {
            Enums.DndDurationType.DND_DURATION_30_MINUTE -> {
                dndDuration = 30
            }
            Enums.DndDurationType.DND_DURATION_1_HOUR -> {
                dndDuration = 60
            }
            Enums.DndDurationType.DND_DURATION_6_HOUR -> {
                dndDuration = 360
            }
            Enums.DndDurationType.DND_DURATION_DAY -> {
                dndExpiresAtDateTime = getLastTimeOfTheDay()
            }
            Enums.DndDurationType.DND_DURATION_CUSTOM -> {
                dndExpiresAtDateTime = getDateStringFromSelectedDate()
            }
        }

        return Pair(dndExpiresAtDateTime, dndDuration)
    }

    private fun setDateText() {
        datePicker.text = getString(R.string.set_do_not_disturb_bottom_sheet_duration_date,
            selectedDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            selectedDate.dayOfMonth.dayWithSuffix(),
            selectedDate.year.toString())
    }

    private fun setTimeText() {
        timePicker.text = formatterManager.formatHHmm(requireContext(), selectedDate.atZone(ZoneId.systemDefault()).toInstant())
            .replace(":", " : ")
    }

    private fun getDateStringFromSelectedDate(): String {
        return selectedDate.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toString()
    }

    private fun getLastTimeOfTheDay(): String? {
        return try {
            val dateString = SimpleDateFormat("yyyy-MM-dd").format(Date())
            val endOfDayTime = LocalDateTime.of(LocalDate.parse(dateString), LocalTime.MAX)
                .truncatedTo(ChronoUnit.MILLIS)
            endOfDayTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC)
                .toString()
        } catch (e: java.lang.Exception) {
            null
        }
    }
}