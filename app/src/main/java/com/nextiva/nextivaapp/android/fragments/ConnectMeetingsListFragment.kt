package com.nextiva.nextivaapp.android.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.FragmentConnectMeetingsListBinding
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetScheduledMeetings
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.util.MenuUtil
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.viewmodels.ConnectMeetingsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConnectMeetingsListFragment(private val searchViewFocusChangeCallback: ((Boolean) -> Unit)?) : GeneralRecyclerViewFragment(), SearchView.OnQueryTextListener {

    constructor() : this(null)

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val viewModel: ConnectMeetingsListViewModel by lazy {
        val owner = activity ?: this
        ViewModelProvider(owner)[ConnectMeetingsListViewModel::class.java]
    }

    var handler: Handler = Handler(Looper.getMainLooper())
    var runnable: Runnable? = null
    var delay = Constants.ONE_MINUTE_IN_MILLIS * 1 // 1 minutes

    private val allListItemObserver = Observer<ArrayList<BaseListItem>> { listItems ->
        mAdapter.updateList(listItems)
    }

    private val scrollToTopObserver = Observer<Int> {
        scrollToTop()
    }

    private val apiSuccessEventObserver = Observer<Boolean> { success ->
        if (success)
            showRecyclerView()
        else
            showEmptyState()
    }

    private val launchLoopObserver = Observer<Boolean> { launchLoop ->
        if(launchLoop) {
            handler.postDelayed(Runnable {
                viewModel.fetchTodayMeetings {
                    stopRefreshing()
                    runnable?.let { runnable -> handler.postDelayed(runnable, delay) }
                }
            }.also { runnable = it }, delay)
        } else {
            clearLoop()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSwipeRefreshLayout?.isEnabled = false
        fetchItemList(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.let { bindViews(it) }
        //it may not be necessary when the DB is implemented
        (mRecyclerView.itemAnimator as SimpleItemAnimator).changeDuration = 0

        viewModel.meetingsMutableLiveData.observe(viewLifecycleOwner, allListItemObserver)
        viewModel.apiSuccessLiveData.observe(viewLifecycleOwner, apiSuccessEventObserver)
        viewModel.launchLoop.observe(viewLifecycleOwner, launchLoopObserver)
        viewModel.scrollToTop.observe(viewLifecycleOwner,scrollToTopObserver)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewAddScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(mAdapter.itemCount > 0){
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    val totalItem = linearLayoutManager!!.itemCount
                    val lastVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                    if (totalItem <= (lastVisible + 10)) {
                        viewModel.getMoreMeetings()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        viewModel.setUiVisible(true)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setUiVisible(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        runnable = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        if (isVisible) {
            menu.clear()
            inflater.inflate(R.menu.menu_scheduled_meetings_list, menu)
            MenuUtil.setMenuContentDescriptions(menu)

            activity?.let { activity ->
                menu.findItem(R.id.meetings_list_action_scheduled_meetings)?.icon = FontDrawable(activity,
                        R.string.fa_calendar,
                        Enums.FontAwesomeIconType.REGULAR)
                        .withColor(ContextCompat.getColor(activity, R.color.connectGrey09))
                        .withSize(R.dimen.material_text_title)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (activity == null) {
            return super.onOptionsItemSelected(item)
        }

        when (item.itemId) {
            R.id.meetings_list_action_scheduled_meetings -> {
                BottomSheetScheduledMeetings().show(requireActivity().supportFragmentManager, null)
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun bindViews(view: View) {
        val binding = FragmentConnectMeetingsListBinding.bind(view)
    }

    private fun clearLoop(){
        runnable?.let { handler.removeCallbacks(it) }
        runnable = null
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_connect_meetings_list
    }

    override fun getRecyclerViewId(): Int {
        return R.id.connect_meeting_list_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.CONNECT_MEETINGS_LIST
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return R.id.connect_meeting_list_swipe_refresh_layout
    }

    override fun getConnectEmptyStateViewId(): Int {
        return R.id.connect_meeting_list_empty_state_view
    }

    override fun getConnectEmptySearchResultsViewId(): Int {
        return R.id.connect_meeting_list_no_results_empty_state_view
    }

    override fun fetchItemList(forceRefresh: Boolean) {
        viewModel.initialFetchMeetings {
            mSwipeRefreshLayout?.isEnabled = true
            stopRefreshing()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}