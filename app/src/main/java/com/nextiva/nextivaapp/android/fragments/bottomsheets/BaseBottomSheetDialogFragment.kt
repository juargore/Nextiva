package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.app.Dialog
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallHistoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatConversationListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactCategoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FeatureFlagListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SimpleBaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.ChatMessage
import com.nextiva.nextivaapp.android.models.SmsMessage
import com.nextiva.nextivaapp.android.util.ApplicationUtil
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@AndroidEntryPoint
open class BaseBottomSheetDialogFragment(private val nestedScrollViewId: Int?, private var tintedAppBar: Boolean) : BottomSheetDialogFragment(), MasterListListener {

    @Inject
    lateinit var settingsManager: SettingsManager

    var showFullHeight = false

    val compositeDisposable = CompositeDisposable()

    constructor() : this(null, false)
    constructor(tintedAppBar: Boolean) : this(null, tintedAppBar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, if (tintedAppBar) {
            R.style.BottomSheetDialogTinted
        } else {
            R.style.BottomSheetDialog
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        dialog.window?.let { window ->
            WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
                controller.isAppearanceLightNavigationBars = !ApplicationUtil.isNightModeEnabled(requireActivity(), settingsManager)
                window.navigationBarColor = ContextCompat.getColor(requireActivity(), R.color.connectWhite)
            }
        }

        dialog.setOnShowListener { dialogInterface ->
            if (showFullHeight) {
                val bottomSheetDialog = dialogInterface as BottomSheetDialog
                setupFullHeight(bottomSheetDialog)

            } else {
                compensateForNavigationBarWhenScrolling()
            }
        }

        return dialog
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet as View)
        val layoutParams = bottomSheet.layoutParams
        if (layoutParams != null) {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun adjustHeight(bottomSheetDialog: BottomSheetDialog){
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet as View)
        val layoutParams = bottomSheet.layoutParams
        if (layoutParams != null) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    protected fun collapseToHeight(bottomSheetDialog: BottomSheetDialog, height: Int, state: Int) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet as View)
        val layoutParams = bottomSheet.layoutParams
        if (layoutParams != null) {
            layoutParams.height = height
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = state
    }

    protected fun setDialogState(bottomSheetDialog: BottomSheetDialog, state: Int) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet as View)
        behavior.state = state
    }

    private fun compensateForNavigationBarWhenScrolling() {
        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { sheetView ->
            if (getWindowHeight() == sheetView.height) {
                nestedScrollViewId?.let { scrollViewId ->
                    dialog?.findViewById<NestedScrollView>(scrollViewId)?.let { scrollView ->
                        (scrollView.layoutParams as? ViewGroup.MarginLayoutParams)?.let { layoutParams ->
                            layoutParams.bottomMargin = getNavigationBarHeight()
                            scrollView.layoutParams = layoutParams
                        }
                    }
                }
            }
        }
    }

    private fun getNavigationBarHeight(): Int {
        val resources: Resources = requireContext().resources

        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    private fun getStatusBarHeight(): Int {
        val resources: Resources = requireContext().resources

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    private fun getWindowHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

            windowMetrics.bounds.height() - insets.top - insets.bottom

        } else {
            val displayMetrics = DisplayMetrics()
            val display = requireActivity().windowManager.defaultDisplay
            display.getRealMetrics(displayMetrics)

            displayMetrics.heightPixels - getNavigationBarHeight() - getStatusBarHeight()
        }
    }

    override fun onCallHistoryListItemClicked(listItem: CallHistoryListItem) {}
    override fun onCallHistoryListItemLongClicked(listItem: CallHistoryListItem) {}
    override fun onCallHistoryCallButtonClicked(listItem: CallHistoryListItem) {}
    override fun onContactHeaderListItemClicked(listItem: HeaderListItem) {}
    override fun onContactHeaderListItemLongClicked(listItem: HeaderListItem) {}
    override fun onContactListItemClicked(listItem: ContactListItem) {}
    override fun onContactListItemLongClicked(listItem: ContactListItem) {}
    override fun onDetailItemViewListItemClicked(listItem: DetailItemViewListItem) {}
    override fun onDetailItemViewListItemLongClicked(listItem: DetailItemViewListItem) {}
    override fun onDetailItemViewListItemAction1ButtonClicked(listItem: DetailItemViewListItem) {}
    override fun onDetailItemViewListItemAction2ButtonClicked(listItem: DetailItemViewListItem) {}
    override fun onChatConversationItemClicked(listItem: ChatConversationListItem) {}
    override fun onChatConversationItemLongClicked(listItem: ChatConversationListItem) {}
    override fun onResendFailedChatMessageClicked(listItem: SimpleBaseListItem<ChatMessage>) {}
    override fun onResendFailedSmsMessageClicked(listItem: SimpleBaseListItem<SmsMessage>) {}
    override fun onChatMessageListItemDatetimeVisibilityToggled(listItem: SimpleBaseListItem<ChatMessage>) {}
    override fun onVoicemailCallButtonClicked(listItem: VoicemailListItem) {}
    override fun onVoicemailReadButtonClicked(listItem: VoicemailListItem) {}
    override fun onVoicemailDeleteButtonClicked(listItem: VoicemailListItem) {}
    override fun onVoicemailContactButtonClicked(listItem: VoicemailListItem) {}
    override fun onVoicemailSmsButtonClicked(listItem: VoicemailListItem) {}
    override fun onSmsConversationItemClicked(listItem: MessageListItem) {}
    override fun onSmsMessageListItemDatetimeVisibilityToggled(listItem: SmsMessageListItem) {}
    override fun onConnectContactHeaderListItemClicked(listItem: ConnectContactHeaderListItem) {}
    override fun onConnectContactFavoriteIconClicked(listItem: ConnectContactListItem) {}
    override fun onPositiveRatingItemClicked(voicemailListItem: VoicemailListItem) {}
    override fun onNegativeRatingItemClicked(voicemailListItem: VoicemailListItem) {}
    override fun onConnectContactDetailHeaderListItemClicked(listItem: ConnectContactDetailHeaderListItem) {}
    override fun onConnectContactDetailListItemClicked(listItem: ConnectContactDetailListItem) {}
    override fun onConnectContactCategoryItemClicked(listItem: ConnectContactCategoryListItem) {}
    override fun onConnectContactListItemClicked(listItem: ConnectContactListItem) {}
    override fun onConnectHomeListItemClicked(listItem: ConnectHomeListItem) {}
    override fun onFeatureFlagListItemChecked(listItem: FeatureFlagListItem) {}
    override fun onConnectContactListItemLongClicked(listItem: ConnectContactListItem) {}
    override fun onDialogContactActionHeaderListItemClicked(listItem: DialogContactActionHeaderListItem) {}
    override fun onDialogContactActionListItemClicked(listItem: DialogContactActionListItem) {}
    override fun onDialogContactActionDetailListItemClicked(listItem: DialogContactActionDetailListItem) {}
}