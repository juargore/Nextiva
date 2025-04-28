package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.LiveDataDatabaseObserver
import com.nextiva.nextivaapp.android.constants.Constants.Contacts.Aliases.XBERT_ALIASES
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ListItemConnectContactBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.fragments.ConnectContactsListFragment
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern


class ConnectContactViewHolder (
    itemView: View,
    context: Context,
    masterListListener: MasterListListener,
    private val dbManager: DbManager?,
    private val sessionManager: SessionManager
) : BaseViewHolder<ConnectContactListItem>(itemView, context, masterListListener), View.OnClickListener, View.OnLongClickListener, LiveDataDatabaseObserver {

    private val masterItemView: View

    private var presenceLiveData : LiveData<NextivaContact>? = null

    private lateinit var avatarView: AvatarView
    private lateinit var name: TextView
    private lateinit var labelBlocked: TextView
    private lateinit var sharedIcon: FontTextView
    private lateinit var favoriteIcon: FontTextView
    private lateinit var statusText: TextView
    private lateinit var searchMatch: TextView
    private lateinit var importIcon: FontTextView
    private lateinit var importText: TextView

    private val presenceObserver = Observer<NextivaContact?> { contact ->
        contact?.let {
            if (it.userId == mListItem.strippedContact?.contactTypeId || it.userId == mListItem.nextivaContact?.userId) {
                it.avatarInfo.presence?.let { presence ->
                    statusText.text = presence.humanReadablePresenceText
                }
                val avatarInfo: AvatarInfo = it.avatarInfo
                avatarInfo.setIsConnect(true)

                if(contact.aliases?.lowercase(Locale.ROOT)?.contains(XBERT_ALIASES) == true)
                    avatarInfo.iconResId = R.drawable.xbert_avatar
                else
                    avatarInfo.iconResId = setIconBasedOnContactType(contact.contactType)

                avatarView.setAvatar(avatarInfo)
            }
        }
    }

    private val setSelected: () -> Unit = {
        mListItem.importState?.let {
            if (mListItem.importState != Enums.Platform.ConnectContactListItemImportState.IMPORTED) {
                mListItem.importState = Enums.Platform.ConnectContactListItemImportState.SELECTED
                setImportStateUi()
            }
        }
    }

    constructor(
        parent: ViewGroup,
        context: Context,
        masterListListener: MasterListListener,
        dbManager: DbManager,
        sessionManager: SessionManager,
    ) : this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_connect_contact, parent, false),
        context, masterListListener, dbManager, sessionManager)


    init {
        bindViews(itemView)
        masterItemView = itemView
        masterItemView.setOnClickListener(this)
        masterItemView.setOnLongClickListener(this)
        favoriteIcon.setOnClickListener(this)
    }

    override fun bind(listItem: ConnectContactListItem) {
        removeItemViewFromParent()

        mListItem = listItem
        val contactType = listItem.strippedContact?.contactType ?: listItem.nextivaContact?.contactType

        (listItem.strippedContact?.avatarInfo ?: listItem.nextivaContact?.avatarInfo)?.let {
            avatarView.setAvatar(it)
        }
        name.text = if (listItem.showSelfIndicator) {
            "${listItem.strippedContact?.uiName ?: listItem.nextivaContact?.uiName} ${
                mContext.getString(
                    R.string.bottom_sheet_sms_details_self_indicator
                )
            }"

        } else {
            listItem.strippedContact?.uiName ?: listItem.nextivaContact?.uiName
        }

        if (listItem.isBlocked) {
            name.setTextColor(ContextCompat.getColor(mContext, R.color.connectGreyDisabled))
            labelBlocked.visibility = View.VISIBLE
        } else {
            name.setTextColor(ContextCompat.getColor(mContext, R.color.connectSecondaryDarkBlue))
            labelBlocked.visibility = View.GONE
        }

        if (listItem.nextivaContact?.contactType != Enums.Contacts.ContactTypes.LOCAL) {
            if (listItem.showIcons) {
                setFavoriteUI((listItem.strippedContact?.favorite ?: listItem.nextivaContact?.isFavorite) == true)

            } else {
                favoriteIcon.visibility = View.GONE
                sharedIcon.visibility = View.GONE
            }

        } else {
            favoriteIcon.visibility = View.GONE
            sharedIcon.visibility = View.GONE
        }

        if (listItem.isForSearchInNewMessage) {
            masterItemView.layoutParams.height = mContext.resources.getDimension(R.dimen.connect_contact_search_height).toInt()
            avatarView.layoutParams.height = mContext.resources.getDimension(R.dimen.avatar_connect_size_search).toInt()
            avatarView.layoutParams.width = mContext.resources.getDimension(R.dimen.avatar_connect_size_search).toInt()
            statusText.setTypeface(null, Typeface.NORMAL)
        }

        setImportStateUi()
        listItem.setSelected = setSelected

        sharedIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectGrey08))

        when (listItem.strippedContact?.contactType ?: listItem.nextivaContact?.contactType) {
            Enums.Contacts.ContactTypes.CONNECT_PERSONAL -> {
                sharedIcon.setIcon(R.string.fa_lock, Enums.FontAwesomeIconType.SOLID)
                sharedIcon.visibility = View.VISIBLE
            }
            else -> {
                sharedIcon.text = null
                sharedIcon.visibility = View.GONE
            }
        }

        if (!listItem.searchTerm.isNullOrEmpty() && !listItem.nextivaContact?.uiName.isNullOrEmpty()) {
            statusText.visibility = View.GONE

            listItem.searchTerm?.let { searchTerm ->
                var searchMatchSet = false

                if (listItem.nextivaContact?.uiName?.lowercase(Locale.ROOT)?.contains(searchTerm.lowercase(Locale.ROOT)) == true) {
                    searchMatchSet = true
                    name.text = getText(name.text.toString(), searchTerm)

                    if (listItem.isBlocked) {
                        name.setTextColor(ContextCompat.getColor(mContext, R.color.connectGreyDisabled))
                        labelBlocked.visibility = View.VISIBLE
                    } else {
                        name.setTextColor(ContextCompat.getColor(mContext, R.color.connectSecondaryDarkBlue))
                        labelBlocked.visibility = View.GONE
                    }
                }

                if (!searchMatchSet) {
                    var check = listItem.nextivaContact?.getSearchMatchText(searchTerm.lowercase(Locale.ROOT), true)
                    if (listItem.isForSearchInNewMessage) { check = check?.substringBefore('x') }
                    searchMatch.visibility = View.VISIBLE
                    searchMatch.text = getText(check ?: "", searchTerm)

                } else {
                    searchMatch.visibility = View.GONE
                }
            }

        } else {
            searchMatch.visibility = View.GONE
        }

        statusText.visibility = View.GONE

        (listItem.strippedContact?.presence ?: listItem.nextivaContact?.presence)?.let {
            if (searchMatch.visibility == View.GONE) {
                statusText.visibility = View.VISIBLE
                statusText.text = it.humanReadablePresenceText
            }
        }


        (listItem.strippedContact?.avatarInfo ?: listItem.nextivaContact?.avatarInfo)?.let {
            it.fontAwesomeIconResId = R.string.fa_user
            it.setIsConnect(true)

            contactType?.let { type ->
                it.iconResId = setIconBasedOnContactType(type)
            }
            avatarView.setAvatar(it)
        }

        setContentDescriptions()
    }

    private fun setIconBasedOnContactType(contactType: Int): Int {
        var iconId = 0
        when (contactType) {
            Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW -> {
                iconId = R.drawable.avatar_callflow
            }
            Enums.Contacts.ContactTypes.CONNECT_TEAM -> {
                iconId = R.drawable.avatar_team
            }
            Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS -> {
                iconId = R.drawable.avatar_callcenter
            }
        }
        return iconId
    }

    private fun setImportStateUi() {
        when (mListItem.importState) {
            Enums.Platform.ConnectContactListItemImportState.UNSELECTED -> {
                importIcon.visibility = View.VISIBLE
                importText.visibility = View.GONE
                importIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectWhite))
                importIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.connectGrey03))
            }
            Enums.Platform.ConnectContactListItemImportState.SELECTED -> {
                importIcon.visibility = View.VISIBLE
                importText.visibility = View.GONE
                importIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectWhite))
                importIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.connectSecondaryBlue))
            }
            Enums.Platform.ConnectContactListItemImportState.IMPORTED -> {
                importIcon.visibility = View.VISIBLE
                importText.visibility = View.VISIBLE
                importIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectPrimaryGreen))
                importIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.connectPrimaryGreen))
            }
            else -> {
                importIcon.visibility = View.GONE
                importText.visibility = View.GONE
            }
        }
    }

    private fun setFavoriteUI(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteIcon.setIcon(R.string.fa_star, Enums.FontAwesomeIconType.SOLID)
            favoriteIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectPrimaryYellow))
        } else {
            favoriteIcon.setIcon(R.string.fa_star, Enums.FontAwesomeIconType.REGULAR)
            favoriteIcon.setTextColor(ContextCompat.getColor(mContext, R.color.connectGrey08))
        }
    }

    fun getText(input: String, searchTerm: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder(input)
        val pattern: Pattern = Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(input)

        while (matcher.find()) {
            builder.setSpan(BackgroundColorSpan(ContextCompat.getColor(mContext, R.color.connectSecondaryYellow)),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }

        return builder
    }

    private fun bindViews(view: View) {
        val binding = ListItemConnectContactBinding.bind(view)

        avatarView = binding.listItemConnectContactAvatarView
        name = binding.listItemConnectContactName
        labelBlocked = binding.listItemConnectContactBlockedLabel
        sharedIcon = binding.listItemConnectContactSharedIcon
        favoriteIcon = binding.listItemConnectContactFavoriteIcon
        searchMatch = binding.listItemConnectContactSearchMatch
        importIcon = binding.listItemConnectContactImportIcon
        importText = binding.listItemConnectContactImportText
        statusText = binding.listItemConnectContactPresenceStatus
    }

    private fun setContentDescriptions() {
        masterItemView.contentDescription = mContext.getString(R.string.connect_contact_list_item_content_description, name.text)
        name.contentDescription = mContext.getString(R.string.connect_contact_list_item_name_content_description, name.text)
        avatarView.contentDescription = mContext.getString(R.string.connect_contact_list_item_avatar_content_description, name.text)
        sharedIcon.contentDescription = mContext.getString(R.string.connect_contact_list_item_shared_icon_content_description, name.text)
        favoriteIcon.contentDescription = mContext.getString(R.string.connect_contact_list_item_favorite_icon_content_description,
                name.text,
                mListItem.nextivaContact?.isFavorite.toString())
    }

    override fun onClick(view: View?) {
        if (!ConnectContactsListFragment.isLoadingOnFragment) {
            when (view?.id) {
                favoriteIcon.id -> {
                    mMasterListListener?.onConnectContactFavoriteIconClicked(mListItem)
                    (mListItem.strippedContact?.favorite ?: mListItem.nextivaContact?.isFavorite)?.let { isFavorite ->
                        setFavoriteUI(!isFavorite)
                        setContentDescriptions()
                    }
                }
                else -> {
                    mListItem.importState?.let {
                        mListItem.importState = when (it) {
                            Enums.Platform.ConnectContactListItemImportState.SELECTED -> Enums.Platform.ConnectContactListItemImportState.UNSELECTED
                            Enums.Platform.ConnectContactListItemImportState.UNSELECTED -> Enums.Platform.ConnectContactListItemImportState.SELECTED
                            else -> mListItem.importState
                        }

                        setImportStateUi()
                    }

                    mMasterListListener?.onConnectContactListItemClicked(mListItem)
                }
            }
        }
    }

    override fun addObservers() {
        (mListItem.strippedContact?.contactTypeId ?: mListItem.nextivaContact?.userId)?.let { userId ->
            removeObservers()
            presenceLiveData = dbManager?.getContactLiveData(userId)?.apply { observeForever(presenceObserver)}
        }
    }

    override fun removeObservers() {
        presenceLiveData?.removeObserver(presenceObserver)
        presenceLiveData = null
    }

    override fun onLongClick(view: View?): Boolean {
        mMasterListListener?.onConnectContactListItemLongClicked(mListItem)
        return true
    }
}