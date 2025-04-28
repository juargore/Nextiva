package com.nextiva.nextivaapp.android.core.notifications.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.notifications.api.Holiday
import com.nextiva.nextivaapp.android.core.notifications.api.HolidayType
import com.nextiva.nextivaapp.android.core.notifications.models.HolidaySelection
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule
import com.nextiva.nextivaapp.android.core.notifications.viewmodel.CreateScheduleViewModel
import com.nextiva.nextivaapp.android.core.notifications.viewstate.DailyHoursScheduleViewState
import com.nextiva.nextivaapp.android.databinding.ActivityCreateScheduleBinding
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDialog
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.MenuUtil
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateScheduleActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var radioGroup: RadioGroup
    private lateinit var linearDailyHoursContainer: LinearLayout
    private lateinit var txtCancelCreateSchedule: TextView
    private lateinit var txtSaveSchedule: TextView
    private lateinit var txtScheduleNameValidation: TextView
    private lateinit var txtLabelScheduleName: TextView
    private lateinit var specifiedDatesTextView: TextView
    private lateinit var txtScheduleObservedHoliday: TextView
    private lateinit var editTxtScheduleName: EditText
    private lateinit var txtScheduleCustomDates: TextView


    private lateinit var viewModel: CreateScheduleViewModel

    private lateinit var someActivityResultLauncher: ActivityResultLauncher<Intent>

    private var specifiedDatesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.getStringExtra(INTENT_EXTRA_SPECIFIED_DATE)?.let { holidayString ->
                        viewModel.addHoliday(GsonUtil.getObject(Holiday::class.java, holidayString))
                    }
            }
        }
    private var customDatesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.getStringExtra(INTENT_EXTRA_CUSTOM_DATE)?.let { holidayString ->
                        viewModel.addHoliday(GsonUtil.getObject(Holiday::class.java, holidayString))
                    }
            }
        }

    companion object {
        const val SAME_HOURS_MONDAY_FRIDAY = 0
        const val CUSTOM_HOURS_DAY = 1
        const val INTENT_EXTRA_SCHEDULE_ID = "scheduleId"
        const val INTENT_EXTRA_HOLIDAY_LIST = "observedHolidaySelectedList"
        const val INTENT_EXTRA_ON_API_SUCCESS = "onAPiSuccess"
        const val INTENT_EXTRA_SPECIFIED_DATE = "specifiedDate"
        const val INTENT_EXTRA_CUSTOM_DATE = "customDate"

        fun newIntent(context: Context): Intent {
            return Intent(context, CreateScheduleActivity::class.java)
        }

        fun newIntent(context: Context, selectedSchedule: Schedule): Intent {
            val intent = Intent(context, CreateScheduleActivity::class.java)
            intent.putExtra(Constants.EXTRA_SCHEDULE, GsonUtil.getJSON(selectedSchedule))
            return intent
        }
    }

    private val mViewStateObserver =
        Observer<ArrayList<DailyHoursScheduleViewState>> { dailyHourScheduleViewStateList ->
            loadCheckBoxContainer(dailyHourScheduleViewStateList)
        }

    private val onApiSuccess = Observer<String?> { savedScheduleId ->
        val returnIntent = Intent()
        returnIntent.putExtra(INTENT_EXTRA_ON_API_SUCCESS, !savedScheduleId.isNullOrEmpty())
        returnIntent.putExtra(INTENT_EXTRA_SCHEDULE_ID, savedScheduleId)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private val mScheduleNameValidationObserver = Observer<String> { validationResult ->
        when (validationResult) {
            Enums.ScheduleValidationErrorType.EMPTY_SCHEDULE_NAME -> {
                updateScheduleNameErrorView(
                    application.getString(R.string.notification_create_schedule_validation_error_schedule_name_empty),
                    true
                )
            }
            Enums.ScheduleValidationErrorType.MAX_SCHEDULE_NAME_CHAR_LIMIT_REACHED -> {
                updateScheduleNameErrorView(
                    application.getString(R.string.notification_create_schedule_validation_error_schedule_name_max_limit_reached),
                    true
                )
            }
            Enums.ScheduleValidationErrorType.SCHEDULE_NAME_IN_USE -> {
                updateScheduleNameErrorView(
                    application.getString(R.string.notification_create_schedule_validation_error_schedule_name_in_use),
                    true
                )
            }
            Enums.ScheduleValidationErrorType.SCHEDULE_VALIDATION_ERROR_NONE -> {
                updateScheduleNameErrorView("", false)
                if (viewModel.hasValidScheduleTimes) {
                    txtSaveSchedule.apply {
                        text = getString(R.string.notification_create_schedule_txt_saving)
                        isClickable = false
                    }
                    viewModel.makeScheduleRequest(editTxtScheduleName.text.toString())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[CreateScheduleViewModel::class.java]
        intent.getStringExtra(Constants.EXTRA_SCHEDULE)?.let { scheduleGson ->
            viewModel.selectedSchedule = GsonUtil.getObject(Schedule::class.java, scheduleGson)

            viewModel.selectedSchedule?.holidays?.forEach { holiday ->
                when (holiday.type) {
                    HolidayType.SPECIFIC.toString(), HolidayType.CUSTOM.toString() -> viewModel.addHoliday(holiday.toHoliday())
                    HolidayType.OBSERVED.toString() -> viewModel.addHolidaySelectionItem(holiday.toHoliday())
                }
            }
        }

        setContentView(bindViews())
        setUpToolBar()
        setOnClicks()

        if (viewModel.selectedSchedule != null) {
            editTxtScheduleName.setText(viewModel.selectedSchedule?.name)

            if (viewModel.selectedSchedule?.monToFri == true) {
                radioGroup.check(R.id.radio_button_schedule_mon_fri)

            } else {
                radioGroup.check(R.id.radio_button_schedule_custom_hours)
            }

        } else {
            radioGroup.check(R.id.radio_button_schedule_mon_fri)
        }

        viewModel.dailyHoursScheduleViewStateList.observe(this, mViewStateObserver)
        viewModel.onSaveSuccess.observe(this, onApiSuccess)
        viewModel.scheduleNameValidationResult.observe(this, mScheduleNameValidationObserver)
        viewModel.scheduleDeletedLiveData.observe(this) { success ->
            if (success) {
                onBackPressedDispatcher.onBackPressed()
            }
        }
        viewModel.getObservedHolidayList()

        someActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
                val data = result.data ?: return@registerForActivityResult
                val list = data.getStringExtra(INTENT_EXTRA_HOLIDAY_LIST)
                list?.let {
                    val holidayList: ArrayList<HolidaySelection> = Gson().fromJson(
                        list,
                        object : com.google.common.reflect.TypeToken<ArrayList<HolidaySelection>>() {}.type
                    )
                    viewModel.updateHolidaySelectionList(holidayList)
                }

            }
    }

    private fun setUpScheduledHolidaysView(binding: ActivityCreateScheduleBinding) {
        binding.composeScheduledHolidaysView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    this@CreateScheduleActivity
                )
            )
            setContent {
                MaterialTheme {
                    ScheduleHolidayView(viewModel, Modifier)
                }
            }
        }
    }

    private fun setOnClicks() {

        txtScheduleObservedHoliday.setOnClickListener {
            val observedHolidayList = viewModel.observedHolidaySelectionList.value
            val intent = Intent(this, ObservedHolidayActivity::class.java).apply {
                val gson = Gson()
                val jsonString = gson.toJson(observedHolidayList)
                putExtra(INTENT_EXTRA_HOLIDAY_LIST, jsonString)
            }
            someActivityResultLauncher.launch(intent)
        }

        txtCancelCreateSchedule.setOnClickListener {
            if (viewModel.selectedSchedule != null) {
                BottomSheetDialog(
                    getString(R.string.notification_create_schedule_discard_title),
                    getString(R.string.notification_create_schedule_discard_message),
                    getString(R.string.notification_create_schedule_discard_primary),
                    getString(R.string.notification_create_schedule_discard_go_back),
                    ContextCompat.getColor(this, R.color.connectPrimaryBlue),
                    ContextCompat.getColor(this, R.color.connectPrimaryBlue),
                    ContextCompat.getColor(this, R.color.connectWhite)
                ) {
                    this.finish()
                }.show(supportFragmentManager, null)

            } else {
                this.finish()
            }
        }

        radioGroup.setOnCheckedChangeListener { _, _ ->
            val selectedOption = radioGroup.indexOfChild(
                radioGroup.findViewById(radioGroup.checkedRadioButtonId)
            )
            viewModel.initializeViewStateList(selectedOption)
            when (selectedOption) {
                SAME_HOURS_MONDAY_FRIDAY -> {
                    loadSameHoursScheduleContainer(viewModel.getSameHourScheduleViewStateList())
                }
                CUSTOM_HOURS_DAY -> {
                    loadCheckBoxContainer(viewModel.getCustomHoursScheduleViewStateList())
                }
            }
        }

        txtSaveSchedule.setOnClickListener {
            val scheduleName = editTxtScheduleName.text.toString()
            viewModel.validateScheduleName(scheduleName)
        }

        specifiedDatesTextView.setOnClickListener {
            specifiedDatesLauncher.launch(SpecifiedDatesActivity.newIntent(this))
        }

        txtScheduleCustomDates.setOnClickListener {
            customDatesLauncher.launch(CustomDateActivity.newIntent(this))
        }
    }

    private fun loadSameHoursScheduleContainer(customHoursScheduleViewStateList: ArrayList<DailyHoursScheduleViewState>) {
        linearDailyHoursContainer.removeAllViews()

        customHoursScheduleViewStateList.forEach { dailyHoursScheduleViewState ->
            linearDailyHoursContainer.addView(CustomHourScheduleView(this).apply {
                viewState = dailyHoursScheduleViewState
            })
        }
    }

    private fun updateScheduleNameErrorView(
        scheduleNameErrorString: String, shouldShowError: Boolean
    ) {
        txtScheduleNameValidation.text = scheduleNameErrorString
        txtScheduleNameValidation.visibility = if (shouldShowError) View.VISIBLE else View.GONE
        editTxtScheduleName.background = AppCompatResources.getDrawable(
            this,
            if (shouldShowError) R.drawable.connect_text_error_input_background else R.drawable.connect_text_input_background
        )
        txtLabelScheduleName.setTextColor(
            ContextCompat.getColor(
                this, if (shouldShowError) R.color.connectSecondaryRed else R.color.connectGrey10
            )
        )
    }


    private fun loadCheckBoxContainer(customHoursScheduleViewStateList: ArrayList<DailyHoursScheduleViewState>) {
        linearDailyHoursContainer.removeAllViews()

        customHoursScheduleViewStateList.forEach { dailyHoursScheduleViewState ->
            linearDailyHoursContainer.addView(CustomHourScheduleView(this).apply {
                viewState = dailyHoursScheduleViewState
            })
        }

    }

    private fun setUpToolBar() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setStatusBarColor(ContextCompat.getColor(this, R.color.connectGrey01))

        setSupportActionBar(toolbar)
        title = if (viewModel.selectedSchedule != null) {
            getString(R.string.notification_schedules_edit_toolbar_title)

        } else {
            getString(R.string.notification_schedules_create_toolbar_title)
        }

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.connectSecondaryDarkBlue))
        toolbar.navigationIcon =
            FontDrawable(this, R.string.fa_arrow_left, Enums.FontAwesomeIconType.REGULAR)
                .withColor(ContextCompat.getColor(this, R.color.connectGrey09))
                .withSize(R.dimen.material_text_title)

        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun txtScheduleCustomDatesOnClickListener(): (View) -> Unit {
        return { ContextCompat.startActivity(this.applicationContext, CustomDateActivity.newIntent(this.applicationContext), null) }
    }

    fun bindViews(): View {
        val binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        toolbar = binding.connectCreateScheduleToolBar
        radioGroup = binding.radioGroupsScheduleDailyHours
        this.linearDailyHoursContainer = binding.linearHoursBreaksContainer
        txtCancelCreateSchedule = binding.txtScheduleCancel
        txtSaveSchedule = binding.txtScheduleSave
        txtScheduleNameValidation = binding.editTxtValidationMessage
        txtLabelScheduleName = binding.labelScheduleName
        txtScheduleObservedHoliday = binding.txtScheduleObservedHoliday
        editTxtScheduleName = binding.editScheduleName
        specifiedDatesTextView = binding.txtScheduleSpecifiedDates
        txtScheduleCustomDates = binding.txtScheduleCustomDates

        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.lato_bold)
        binding.labelScheduleName.typeface = typeface
        binding.txtScheduleSave.typeface = typeface
        binding.txtScheduleCancel.typeface = typeface

        setUpScheduledHolidaysView(binding)

        editTxtScheduleName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                updateScheduleNameErrorView(
                    "",
                    false
                )
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // This method is called to notify that the text has changed
                // Here, you can edit the text in newText as desired
            }

            override fun afterTextChanged(s: Editable) {
                // This method is called to notify that the text has been changed
            }
        })

        overrideEdgeToEdge(binding.root)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_schedule, menu)

        MenuUtil.setMenuContentDescriptions(menu)
        menu.findItem(R.id.menu_schedule_delete)?.icon =
            FontDrawable(this, R.string.fa_trash_alt, Enums.FontAwesomeIconType.REGULAR)
                .withColor(ContextCompat.getColor(this, R.color.connectGrey09))
                .withSize(R.dimen.font_awesome_menu_icon)

        if (!intent.hasExtra(Constants.EXTRA_SCHEDULE)) {
            menu.removeItem(R.id.menu_schedule_delete)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_schedule_delete -> {
                BottomSheetDialog(
                    getString(R.string.notification_schedules_delete_title),
                    getString(R.string.notification_schedules_delete_message),
                    getString(R.string.notification_schedules_delete_confirm),
                    getString(R.string.general_cancel),
                    ContextCompat.getColor(this, R.color.connectPrimaryRed),
                    ContextCompat.getColor(this, R.color.connectPrimaryBlue),
                    ContextCompat.getColor(this, R.color.connectWhite)
                ) {
                    viewModel.deleteSchedule()
                }.show(supportFragmentManager, null)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}