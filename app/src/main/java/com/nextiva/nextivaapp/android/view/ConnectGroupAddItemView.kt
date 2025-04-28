package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.content.ContextWrapper
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.GroupAddItemAdapter
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.Address
import com.nextiva.nextivaapp.android.db.model.DbDate
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.db.model.SocialMediaAccount
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDeleteConfirmation
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetMenu
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectAddressType
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectEmailType
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectPhoneType
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectSocialMediaType
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import java.util.Locale

class ConnectGroupAddItemView : LinearLayout {
    enum class ItemType { PHONE, EMAIL, DATE, SOCIAL, ADDRESS }

    private lateinit var itemType: ItemType

    private lateinit var recyclerView: RecyclerView
    private lateinit var icon: FontTextView
    private lateinit var textView: TextView

    // Pulled from source code from the MediaRouter in the official support library
    private val activity: FragmentActivity?
        get() {
            var context = context
            while (context is ContextWrapper) {
                if (context is FragmentActivity) {
                    return context
                }

                context = context.baseContext
            }
            return null
        }

    private var removeItemCallback: (ConnectAddItemView) -> Unit = { view ->
        activity?.let { activity ->
            BottomSheetDeleteConfirmation.newInstance {
                view.resetValues()
            }.show(activity.supportFragmentManager, null)
        }
    }

    private var typeSelectionCallback: (ConnectAddItemView) -> Unit = { view ->
        activity?.let { activity ->
            view.item?.let { item ->
                BottomSheetMenu(getTypeListItems(itemType, item)) {
                    (recyclerView.adapter as? GroupAddItemAdapter)?.let { adapter ->
                        when (view.itemType) {
                            ItemType.PHONE -> {
                                when (it.text) {
                                    ConnectPhoneType.Work.name -> adapter.updateItemType(ConnectPhoneType.Work.numericType, view)
                                    ConnectPhoneType.Mobile.name -> adapter.updateItemType(ConnectPhoneType.Mobile.numericType, view)
                                    ConnectPhoneType.Home.name -> adapter.updateItemType(ConnectPhoneType.Home.numericType, view)
                                    else -> adapter.updateItemType(ConnectPhoneType.Other.numericType, view)
                                }
                            }

                            ItemType.EMAIL -> {
                                when (it.text) {
                                    ConnectEmailType.Work.name -> adapter.updateItemType(ConnectEmailType.Work.numericType, view)
                                    ConnectEmailType.Home.name -> adapter.updateItemType(ConnectEmailType.Home.numericType, view)
                                    else -> adapter.updateItemType(ConnectEmailType.Other.numericType, view)
                                }
                            }

                            ItemType.DATE -> {
                                when (it.text) {
                                    context.getString(R.string.connect_create_contact_birthday_date_type) -> adapter.updateItemType(
                                        Enums.Contacts.DateType.BIRTH,
                                        view
                                    )
                                    context.getString(R.string.connect_create_contact_next_contact_date_type) -> adapter.updateItemType(
                                        Enums.Contacts.DateType.NEXT_CONTACT,
                                        view
                                    )
                                    context.getString(R.string.connect_create_contact_sign_up_date_type) -> adapter.updateItemType(
                                        Enums.Contacts.DateType.SIGN_UP,
                                        view
                                    )
                                    context.getString(R.string.connect_create_contact_cancel_date_type) -> adapter.updateItemType(
                                        Enums.Contacts.DateType.CANCEL,
                                        view
                                    )
                                    else -> {}
                                }
                            }

                            ItemType.SOCIAL -> {
                                when (it.text) {
                                    ConnectSocialMediaType.Facebook.name -> adapter.updateItemType(
                                        ConnectSocialMediaType.Facebook.numericType,
                                        view
                                    )

                                    ConnectSocialMediaType.LinkedIn.name -> adapter.updateItemType(
                                        ConnectSocialMediaType.LinkedIn.numericType,
                                        view
                                    )

                                    ConnectSocialMediaType.Instagram.name -> adapter.updateItemType(
                                        ConnectSocialMediaType.Instagram.numericType,
                                        view
                                    )

                                    ConnectSocialMediaType.Twitter.name -> adapter.updateItemType(
                                        ConnectSocialMediaType.Twitter.numericType,
                                        view
                                    )

                                    ConnectSocialMediaType.Telegram.name -> adapter.updateItemType(
                                        ConnectSocialMediaType.Telegram.numericType,
                                        view
                                    )

                                    else -> adapter.updateItemType(
                                        ConnectSocialMediaType.Other.numericType,
                                        view
                                    )
                                }
                            }

                            ItemType.ADDRESS -> {
                                when (it.text) {
                                    ConnectAddressType.Work.name -> adapter.updateItemType(
                                        ConnectAddressType.Work.numericType,
                                        view
                                    )

                                    ConnectAddressType.Home.name -> adapter.updateItemType(
                                        ConnectAddressType.Home.numericType,
                                        view
                                    )

                                    ConnectAddressType.Billing.name -> adapter.updateItemType(
                                        ConnectAddressType.Billing.numericType,
                                        view
                                    )

                                    ConnectAddressType.Shipping.name -> adapter.updateItemType(
                                        ConnectAddressType.Shipping.numericType,
                                        view
                                    )

                                    else -> adapter.updateItemType(
                                        ConnectAddressType.Other.numericType,
                                        view
                                    )
                                }
                            }

                            else -> R.string.general_unavailable
                        }
                    }

                }.show(activity.supportFragmentManager, null)
            }
        }
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_connect_group_add_item, this, true)
        icon = findViewById(R.id.view_connect_add_item_icon)
        textView = findViewById(R.id.view_connect_add_item_text)
        recyclerView = findViewById(R.id.recyclerView)

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ConnectGroupAddItemView, 0, 0)
            itemType = ItemType.values()[ta.getInt(R.styleable.ConnectGroupAddItemView_add_item_type, 0)]
            ta.recycle()
        }

        textView.text = when (itemType) {
            ItemType.PHONE -> context.getString(R.string.connect_create_contact_add_phone)
            ItemType.EMAIL -> context.getString(R.string.connect_create_contact_add_email)
            ItemType.DATE -> context.getString(R.string.connect_create_contact_add_date)
            ItemType.SOCIAL -> context.getString(R.string.connect_create_contact_add_social_profile)
            ItemType.ADDRESS -> context.getString(R.string.connect_create_contact_add_address)
        }

        textView.contentDescription = textView.text
        icon.contentDescription = context.getString(R.string.connect_group_add_item_add_icon_content_description, textView.text)
    }

    private fun getTypeListItems(itemType: ItemType, item: Int): ArrayList<BottomSheetMenuListItem> {
        val listItems: ArrayList<BottomSheetMenuListItem> = ArrayList()

        return when (itemType) {
            ItemType.PHONE -> {
                listItems.apply {
                    add(BottomSheetMenuListItem(ConnectPhoneType.Mobile.name))
                    add(BottomSheetMenuListItem(ConnectEmailType.Work.name))
                    add(BottomSheetMenuListItem(ConnectEmailType.Home.name))
                    add(BottomSheetMenuListItem(ConnectEmailType.Other.name))
                }
            }
            ItemType.EMAIL -> {
                listItems.apply {
                    add(BottomSheetMenuListItem(ConnectEmailType.Work.name))
                    add(BottomSheetMenuListItem(ConnectEmailType.Home.name))
                    add(BottomSheetMenuListItem(ConnectEmailType.Other.name))
                }
            }
            ItemType.DATE -> {
                (recyclerView.adapter as? GroupAddItemAdapter)?.let { adapter ->
                    val set = mutableSetOf<Int>().apply {
                        adapter.items.forEach {
                            (it as? DbDate)?.type?.let { type -> add(type) }
                        }
                    }
                    if (item == Enums.Contacts.DateType.BIRTH || !set.contains(Enums.Contacts.DateType.BIRTH )) {
                        listItems.add(BottomSheetMenuListItem(context.getString(R.string.connect_create_contact_birthday_date_type)))
                    }
                    if (item == Enums.Contacts.DateType.SIGN_UP || !set.contains(Enums.Contacts.DateType.SIGN_UP )) {
                        listItems.add(BottomSheetMenuListItem(context.getString(R.string.connect_create_contact_sign_up_date_type)))
                    }
                    if (item == Enums.Contacts.DateType.NEXT_CONTACT || !set.contains(Enums.Contacts.DateType.NEXT_CONTACT )) {
                        listItems.add(BottomSheetMenuListItem(context.getString(R.string.connect_create_contact_next_contact_date_type)))
                    }
                    if (item == Enums.Contacts.DateType.CANCEL || !set.contains(Enums.Contacts.DateType.CANCEL )) {
                        listItems.add(BottomSheetMenuListItem(context.getString(R.string.connect_create_contact_cancel_date_type)))
                    }
                }
                listItems
            }
            ItemType.SOCIAL -> {
                listItems.apply {
                    ConnectSocialMediaType.values()
                        .forEach { type -> add(BottomSheetMenuListItem(type.name)) }
                }
            }
            ItemType.ADDRESS -> {
                listItems.apply {
                    add(BottomSheetMenuListItem(ConnectAddressType.Work.name))
                    add(BottomSheetMenuListItem(ConnectAddressType.Home.name))
                    add(BottomSheetMenuListItem(ConnectAddressType.Other.name))
                }
            }
        }
    }

    fun setPhoneNumbers(phoneNumbers: ArrayList<PhoneNumber>) {
        (recyclerView.adapter as? GroupAddItemAdapter)?.addItems(phoneNumbers as ArrayList<Any>)
    }

    fun addNewPhoneNumber(phoneNumber: String) {
        val phoneToShow = if (TextUtils.isEmpty(PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().country))) {
            phoneNumber
        } else {
            PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().country)
        }
        (recyclerView.adapter as GroupAddItemAdapter).let { adapter ->
            adapter.addNewItem(
                PhoneNumber(
                    type = getPhoneNextType(adapter),
                    number = phoneToShow
                )
            )
        }
    }

    private fun getPhoneNextType(adapter: GroupAddItemAdapter): Int {
        val sortedType = linkedSetOf(
            ConnectPhoneType.Mobile,
            ConnectPhoneType.Work,
            ConnectPhoneType.Home,
            ConnectPhoneType.Other
        )
        adapter.items.forEach {
            (it as? PhoneNumber)?.let { phoneNumber ->
                sortedType.remove(ConnectPhoneType.fromIntType(phoneNumber.type))
            }
        }
        return sortedType.firstOrNull()?.numericType ?: ConnectPhoneType.Other.numericType
    }

    fun setEmails(emails: ArrayList<EmailAddress>) {
        (recyclerView.adapter as? GroupAddItemAdapter)?.addItems(emails as ArrayList<Any>)
    }

    private fun addNewEmail() {
        (recyclerView.adapter as GroupAddItemAdapter).let { adapter ->
            adapter.addNewItem(
                EmailAddress(
                    type = getEmailNextType(adapter),
                    address = "",
                    label = ""
                )
            )
        }
    }

    private fun getEmailNextType(adapter: GroupAddItemAdapter): Int {
        val sortedType = linkedSetOf(
            ConnectEmailType.Work,
            ConnectEmailType.Home,
            ConnectEmailType.Other
        )
        adapter.items.forEach {
            (it as? EmailAddress)?.let { emailAddress ->
                sortedType.remove(ConnectEmailType.fromIntType(emailAddress.type))
            }
        }
        return sortedType.firstOrNull()?.numericType ?: ConnectEmailType.Other.numericType
    }

    fun setDates(dates: ArrayList<DbDate>) {
        dates.mapNotNull { item -> item.type?.let { item } }.let {
            (recyclerView.adapter as? GroupAddItemAdapter)?.addItems(it as ArrayList<Any>)
        }
    }

    fun setSocialProfiles(profiles: ArrayList<SocialMediaAccount>) {
        (recyclerView.adapter as? GroupAddItemAdapter)?.addItems(profiles as ArrayList<Any>)
    }

    private fun addNewSocial() {
        (recyclerView.adapter as GroupAddItemAdapter).let { adapter ->
            adapter.addNewItem(
                SocialMediaAccount(
                    type = getSocialNextType(adapter),
                    link = ""
                )
            )
        }
    }

    private fun getSocialNextType(adapter: GroupAddItemAdapter): Int {
        val sortedType = linkedSetOf(
            ConnectSocialMediaType.Facebook,
            ConnectSocialMediaType.LinkedIn,
            ConnectSocialMediaType.Instagram,
            ConnectSocialMediaType.Twitter,
            ConnectSocialMediaType.Telegram,
            ConnectSocialMediaType.Other
        )
        adapter.items.forEach {
            (it as? SocialMediaAccount)?.let { socialAccount ->
                socialAccount.type?.let { socialType ->
                    sortedType.remove(ConnectSocialMediaType.fromIntType(socialType))
                }
            }
        }
        return sortedType.firstOrNull()?.numericType ?: ConnectSocialMediaType.Other.numericType
    }

    private fun addNewDate() {
        (recyclerView.adapter as GroupAddItemAdapter).let { adapter ->
            adapter.addNewItem(
                DbDate(
                    type = getDateNextType(adapter),
                    date = null
                )
            )
        }
    }

    private fun getDateNextType(adapter: GroupAddItemAdapter): Int {
        val sortedType = linkedSetOf(
            Enums.Contacts.DateType.BIRTH,
            Enums.Contacts.DateType.SIGN_UP,
            Enums.Contacts.DateType.NEXT_CONTACT,
            Enums.Contacts.DateType.CANCEL
        )
        adapter.items.forEach {
            (it as? DbDate)?.let { dbDate ->
                dbDate.type?.let { dateType ->
                    sortedType.remove(dateType)
                }
            }
        }
        return sortedType.firstOrNull() ?: Enums.Contacts.DateType.BIRTH
    }

    private fun addNewAddress() {
        (recyclerView.adapter as GroupAddItemAdapter).let { adapter ->
            adapter.addNewItem(Address(type = getAddressNextType(adapter)))
        }
    }

    private fun getAddressNextType(adapter: GroupAddItemAdapter): Int {
        val sortedType = linkedSetOf(
            ConnectAddressType.Work,
            ConnectAddressType.Home,
            ConnectAddressType.Other
        )
        adapter.items.forEach {
            (it as? Address)?.let { address ->
                address.type?.let { type ->
                    sortedType.remove(ConnectAddressType.fromIntType(type))
                }
            }
        }
        return sortedType.firstOrNull()?.numericType ?: ConnectAddressType.Other.numericType
    }

    fun setAddresses(addresses: ArrayList<Address>) {
        (recyclerView.adapter as? GroupAddItemAdapter)?.addItems(addresses as ArrayList<Any>)
    }

    fun getPhoneNumbers(): ArrayList<PhoneNumber>? =
        ((recyclerView.adapter as? GroupAddItemAdapter)?.items as? ArrayList<PhoneNumber>)

    fun getEmails(): ArrayList<EmailAddress>? =
        ((recyclerView.adapter as? GroupAddItemAdapter)?.items as? ArrayList<EmailAddress>)

    fun getDates(): ArrayList<DbDate>? =
        ((recyclerView.adapter as? GroupAddItemAdapter)?.items as? ArrayList<DbDate>)

    fun getSocialProfiles(): ArrayList<SocialMediaAccount>? =
        ((recyclerView.adapter as? GroupAddItemAdapter)?.items as? ArrayList<SocialMediaAccount>)


    fun getAddresses(): ArrayList<Address>? =
        ((recyclerView.adapter as? GroupAddItemAdapter)?.items as? ArrayList<Address>)


    fun setup(maxItems: Int, onValueChanged: () -> Unit, removeFocusCallback: () -> Unit) {
        recyclerView.adapter = GroupAddItemAdapter(
            onTypeSelectorChanged = typeSelectionCallback,
            onAdapterSizeChanged = { size ->
                icon.visibility = if (size >= maxItems) View.GONE else View.VISIBLE
                textView.visibility = if (size >= maxItems) View.GONE else View.VISIBLE
            },
            onValueChanged = onValueChanged
        )

        icon.setOnClickListener {

            removeFocusCallback()

            when (itemType) {
                ItemType.PHONE -> addNewPhoneNumber("")
                ItemType.EMAIL -> addNewEmail()
                ItemType.ADDRESS -> addNewAddress()
                ItemType.DATE -> addNewDate()
                ItemType.SOCIAL -> addNewSocial()
            }
        }
    }

    fun checkDuplicates() = (recyclerView.adapter as? GroupAddItemAdapter)?.checkDuplicates()

    fun clearAllFocus() {
    }
}