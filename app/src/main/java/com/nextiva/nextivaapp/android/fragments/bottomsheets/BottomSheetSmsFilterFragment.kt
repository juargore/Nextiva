package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.BottomSheetMenuAdapter
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetSmsMenuDialogBinding
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.MessageListViewModel
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetSmsFilterFragment : BaseBottomSheetDialogFragment() {
    private lateinit var allLayout: ConstraintLayout
    private lateinit var allText: TextView
    private lateinit var allIcon: FontTextView
    private lateinit var recyclerView: RecyclerView
    private var menuListItems: ArrayList<BottomSheetMenuListItem> = arrayListOf()

    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel: MessageListViewModel by lazy {
        val owner = activity ?: this
        ViewModelProvider(owner)[MessageListViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userTeams = sessionManager.usersTeams
        val isTeamSmsLicenseEnabled = viewModel.isTeamSmsLicenseEnabled()
        val isTeamSmsEnabled = viewModel.isTeamSmsEnabled()

        for (item in userTeams) {
            item?.let {
                if (isTeamSmsLicenseEnabled || isTeamSmsEnabled) {
                    it.teamName?.let { teamName ->
                        it.teamId?.let { teamId ->
                            menuListItems.add(BottomSheetMenuListItem(teamName, R.string.fa_users, false, teamId))
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_sms_menu_dialog, container, false)
        view?.let { bindViews(view) }
        return view
    }

    private fun getTeamId(teamName: String): String? {
        val userTeams = sessionManager.usersTeams
        val team = userTeams.find { it.teamName == teamName }
        return team?.teamId

    }

    private fun bindViews(view: View) {
        val binding = BottomSheetSmsMenuDialogBinding.bind(view)

        allLayout = binding.bottomSheetSmsAllLayout
        allText = binding.bottomSheetSmsAllText
        allIcon = binding.bottomSheetSmsAllIcon
        recyclerView = binding.bottomSheetSmsFilterRecyclerView

        menuListItems.sortWith(compareBy { it.text })

        val adapter = BottomSheetMenuAdapter(menuListItems) { listItem ->
            getTeamId(listItem.text)?.let { teamId -> viewModel.selectedFilter = teamId}
            dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter


        when (viewModel.selectedFilter) {
            Enums.Platform.ConnectSmsFilter.ALL -> {
                allText.setTypeface(allText.typeface, Typeface.BOLD)
                allIcon.setIcon(R.string.fa_minus, Enums.FontAwesomeIconType.SOLID)
            }
            else -> {
                val itemToUpdate = menuListItems.indexOfFirst { it.itemId == viewModel.selectedFilter }
                if (itemToUpdate != -1) {
                    menuListItems[itemToUpdate].isSelected = true
                    menuListItems[itemToUpdate].iconType = Enums.FontAwesomeIconType.SOLID
                    adapter.notifyItemChanged(itemToUpdate)
                }

            }

        }

        allLayout.setOnClickListener {
            viewModel.selectedFilter = Enums.Platform.ConnectSmsFilter.ALL
            dismiss()
        }
    }
}
