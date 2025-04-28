package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetCallsMenuDialogBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetCallsFilterFragment(private val currentFilter: Int, val selectedFilter: (Int) -> Unit): BaseBottomSheetDialogFragment() {

    @Inject
    lateinit var dbManager: DbManager

    private lateinit var allLayout: ConstraintLayout
    private lateinit var allText: TextView
    private lateinit var allIcon: FontTextView
    private lateinit var allCount: TextView
    private lateinit var missedLayout: ConstraintLayout
    private lateinit var missedText: TextView
    private lateinit var missedIcon: FontTextView
    private lateinit var missedCount: TextView
    private lateinit var voicemailLayout: ConstraintLayout
    private lateinit var voicemailText: TextView
    private lateinit var voicemailIcon: FontTextView
    private lateinit var voicemailCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_calls_menu_dialog, container, false)
        view?.let { bindViews(view) }

        dbManager.unreadCallLogEntriesCount.observe(viewLifecycleOwner) {
            val count = it ?: 0
            missedCount.text = count.toString()
        }

        dbManager.newVoicemailCountLiveData.observe(viewLifecycleOwner) {
            val count = it ?: 0
            voicemailCount.text = count.toString()
        }

        dbManager.unreadCallLogAndVoicemailCount.observe(viewLifecycleOwner) {
            val count = it ?: 0
            allCount.text = count.toString()
        }

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetCallsMenuDialogBinding.bind(view)

        allLayout = binding.bottomSheetCallsAllLayout
        allText = binding.bottomSheetCallsAllText
        allIcon = binding.bottomSheetCallsAllIcon
        allCount = binding.bottomSheetCallsAllCount
        missedLayout = binding.bottomSheetCallsMissedLayout
        missedText = binding.bottomSheetCallsMissedText
        missedIcon = binding.bottomSheetCallsMissedIcon
        missedCount = binding.bottomSheetCallsMissedCount
        voicemailLayout = binding.bottomSheetCallsVoicemailLayout
        voicemailText = binding.bottomSheetCallsVoicemailText
        voicemailIcon = binding.bottomSheetCallsVoicemailIcon
        voicemailCount = binding.bottomSheetCallsVoicemailCount

        when (currentFilter) {
            Enums.Platform.ConnectCallsFilter.ALL -> {
                allText.setTypeface(allText.typeface, Typeface.BOLD)
                allIcon.setIcon(R.string.fa_phone_alt, Enums.FontAwesomeIconType.SOLID)
            }
            Enums.Platform.ConnectCallsFilter.MISSED -> {
                missedText.setTypeface(missedText.typeface, Typeface.BOLD)
                missedIcon.setIcon(R.string.fa_phone_alt, Enums.FontAwesomeIconType.SOLID)
            }
            Enums.Platform.ConnectCallsFilter.VOICEMAIL -> {
                voicemailText.setTypeface(voicemailText.typeface, Typeface.BOLD)
                voicemailIcon.setIcon(R.string.fa_voicemail, Enums.FontAwesomeIconType.SOLID)
            }
        }

        allLayout.setOnClickListener {
            selectedFilter(Enums.Platform.ConnectCallsFilter.ALL)
            dismiss()
        }
        missedLayout.setOnClickListener {
            selectedFilter(Enums.Platform.ConnectCallsFilter.MISSED)
            dismiss()
        }
        voicemailLayout.setOnClickListener {
            selectedFilter(Enums.Platform.ConnectCallsFilter.VOICEMAIL)
            dismiss()
        }
    }
}