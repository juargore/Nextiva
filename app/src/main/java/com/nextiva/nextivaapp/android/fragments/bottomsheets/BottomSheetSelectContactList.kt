package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.ConnectMainActivity
import com.nextiva.nextivaapp.android.CreateBusinessContactActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.PagedConnectListAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetContactListBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.view.ConnectSearchView
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetSelectContactListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetSelectContactList(val contactTypes: IntArray,
                                   private val phoneNumberToAdd: String?,
                                   val parent: BaseBottomSheetDialogFragment?) : BaseBottomSheetDialogFragment(tintedAppBar = true), SearchView.OnQueryTextListener {

    constructor() : this(
        contactTypes = intArrayOf(
            Enums.Contacts.ContactTypes.CONNECT_SHARED,
            Enums.Contacts.ContactTypes.CONNECT_PERSONAL
        ),
        phoneNumberToAdd = null,
        parent = null
    )

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var avatarManager: AvatarManager

    @Inject
    lateinit var nextivaMediaPlayer: NextivaMediaPlayer


    private lateinit var viewModel: BottomSheetSelectContactListViewModel
    private var pagedAdapter: PagedConnectListAdapter? = null

    private lateinit var searchView: ConnectSearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var backIcon: RelativeLayout
    private lateinit var cancelIcon: RelativeLayout

    private val isSearchingObserver = Observer<Boolean> { isSearching ->
        (activity as? ConnectMainActivity)?.setSearching(isSearching)
    }

    private val allListItemObserver = Observer<PagingData<BaseListItem>> { listItems ->
        pagedAdapter?.submitData(viewLifecycleOwner.lifecycle, listItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFullHeight = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_contact_list, container, false)
        view?.let { bindViews(view) }
        viewModel = ViewModelProvider(requireActivity())[BottomSheetSelectContactListViewModel::class.java]
        viewModel.getIsSearchingLiveData().observe(viewLifecycleOwner, isSearchingObserver)

        recyclerView.addItemDecoration(DividerItemDecoration(context, 0))
        pagedAdapter = PagedConnectListAdapter(requireActivity(), this, calendarManager, dbManager, sessionManager, avatarManager, settingsManager, nextivaMediaPlayer)
        recyclerView.adapter = pagedAdapter

        viewModel.contactTypes = contactTypes
        viewModel.contactTypeLiveData.observe(viewLifecycleOwner, allListItemObserver)

        pagedAdapter?.addLoadStateListener {
            when (it.refresh) {
                //is LoadState.Loading -> if (viewModel.isCurrentlySearching) startRefreshing()
                is LoadState.NotLoading, is LoadState.Error -> {
                    //if (viewModel.isCurrentlySearching) stopRefreshing()

                    if (viewModel.isCurrentlySearching || viewModel.finishedSearching) {
                        recyclerView.scrollToPosition(0)
                    }
                }
                is LoadState.Loading -> {
                    // ignored
                }
            }
        }

        return view
    }

    fun bindViews(view: View) {
        val binding = BottomSheetContactListBinding.bind(view)

        searchView = binding.bottomSheetContactListSearchView
        recyclerView = binding.bottomSheetContactListRecyclerView
        backIcon = binding.backArrowInclude.backArrowView
        cancelIcon = binding.cancelIconInclude.closeIconView

        backIcon.setOnClickListener {
            resetSetSearching()
            dismiss()
        }
        cancelIcon.setOnClickListener {
            resetSetSearching()
            dismiss()
        }

        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { viewModel.onSearchTermUpdated(it) }
        pagedAdapter?.refresh()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { viewModel.onSearchTermUpdated(newText) }
        pagedAdapter?.refresh()
        return false
    }

    override fun onConnectContactListItemClicked(listItem: ConnectContactListItem) {
        super.onConnectContactListItemClicked(listItem)
        listItem.nextivaContact?.let {
            hideKeyboard()
            startCreateContactActivity(it)
            resetSetSearching()
            dismiss()
        }
    }

    private fun startCreateContactActivity(nextivaContact: NextivaContact) {
        requireActivity().startActivity(CreateBusinessContactActivity.newIntent(requireActivity(), nextivaContact, phoneNumberToAdd))
    }

    override fun onPause() {
        super.onPause()
        resetSetSearching()
    }

    private fun resetSetSearching() {
        searchView.resetSearchView()
        (activity as? ConnectMainActivity)?.setSearching(false)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
