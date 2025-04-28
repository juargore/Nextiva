package com.nextiva.nextivaapp.android.view

import android.app.DatePickerDialog
import android.content.Context
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.Address
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectAddressType
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectEmailType
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectPhoneType
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ConnectAddItemView: LinearLayout {
    private lateinit var removeIcon: FontTextView
    private lateinit var typeIcon: FontTextView
    private lateinit var typeTextView: TextView
    private lateinit var typeSelector: LinearLayout
    private lateinit var errorText: TextView
    private lateinit var dateIcon: FontTextView
    private lateinit var addressLayout: ConstraintLayout
    private lateinit var streetOneEditText: EditText
    private lateinit var streetTwoEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var zipEditText: EditText
    lateinit var editText: EditText
    var id : String = ""
    var index: Int = -1

    var item: Int? = null
    val calendar = Calendar.getInstance()

    private var getDuplicates: (() -> MutableSet<Int>)? = null
    private var onValueChanged: ((ConnectAddItemView) -> Unit)? = null
    private var onFocusChanged: ((ConnectAddItemView, Boolean) -> Unit)? = null
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(text: Editable?) {
            onValueChanged?.invoke(this@ConnectAddItemView)
            getDuplicates?.invoke()?.let {
                setDuplicate(it.contains(index))
            }
        }
    }

    private val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateInView()
    }

    var itemType: ConnectGroupAddItemView.ItemType? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_connect_add_item, this, true)
        removeIcon = findViewById(R.id.view_connect_remove_item_icon)
        typeIcon = findViewById(R.id.view_connect_item_type_icon)
        typeTextView = findViewById(R.id.view_connect_item_type_text)
        typeSelector = findViewById(R.id.view_connect_item_type_selector)
        editText = findViewById(R.id.view_connect_item_edit_text)
        errorText = findViewById(R.id.view_connect_item_error_text)
        dateIcon = findViewById(R.id.view_connect_item_type_date_icon)
        addressLayout = findViewById(R.id.view_connect_address_layout)
        streetOneEditText = findViewById(R.id.view_connect_item_street_1)
        streetTwoEditText = findViewById(R.id.view_connect_item_street_2)
        cityEditText = findViewById(R.id.view_connect_item_city)
        stateEditText = findViewById(R.id.view_connect_item_state)
        zipEditText = findViewById(R.id.view_connect_item_zip)
    }

    fun setViews(item: Int, address: Address) {
        this.item = item
        this.itemType = ConnectGroupAddItemView.ItemType.ADDRESS

        typeTextView.text = when (item) {
            ConnectAddressType.Work.numericType -> context.getString(R.string.connect_create_contact_work_address_type)
            ConnectAddressType.Home.numericType -> context.getString(R.string.connect_create_contact_home_address_type)
            ConnectAddressType.Other.numericType -> context.getString(R.string.connect_create_contact_other_address_type)
            else -> ""
        }

        editText.setText(address.country)
        streetOneEditText.setText(address.addressLineOne)
        streetTwoEditText.setText(address.addressLineTwo)
        cityEditText.setText(address.city)
        stateEditText.setText(address.region)
        zipEditText.setText(address.postalCode)
    }

    fun setViews(
        item: Int,
        itemType: ConnectGroupAddItemView.ItemType,
        text: String?,
        isDuplicate: Boolean?
    ) {
        this.item = item
        this.itemType = itemType

        when (itemType) {
            ConnectGroupAddItemView.ItemType.PHONE -> {
                editText.inputType = InputType.TYPE_CLASS_PHONE
                typeTextView.text = context.getString(
                    ConnectPhoneType.fromIntType(item).labelIdentifier
                )
            }

            ConnectGroupAddItemView.ItemType.EMAIL -> {
                editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                typeTextView.text = context.getString(
                    ConnectEmailType.fromIntType(item).labelIdentifier
                )
            }

            ConnectGroupAddItemView.ItemType.DATE -> {
                editText.inputType = InputType.TYPE_DATETIME_VARIATION_DATE
                typeTextView.text = context.getString(
                    when (item) {
                        Enums.Contacts.DateType.BIRTH -> R.string.connect_create_contact_birthday_date_type
                        Enums.Contacts.DateType.NEXT_CONTACT -> R.string.connect_create_contact_next_contact_date_type
                        Enums.Contacts.DateType.SIGN_UP -> R.string.connect_create_contact_sign_up_date_type
                        Enums.Contacts.DateType.CANCEL -> R.string.connect_create_contact_cancel_date_type
                        else -> -1
                    }
                )
            }

            ConnectGroupAddItemView.ItemType.SOCIAL -> {
                editText.inputType = InputType.TYPE_CLASS_TEXT
                typeTextView.text = context.getString(
                    when (item) {
                        Enums.Contacts.SocialMediaType.FACEBOOK -> R.string.connect_create_contact_facebook_social_profile_type
                        Enums.Contacts.SocialMediaType.LINKEDIN -> R.string.connect_create_contact_linkedin_social_profile_type
                        Enums.Contacts.SocialMediaType.INSTAGRAM -> R.string.connect_create_contact_instagram_social_profile_type
                        Enums.Contacts.SocialMediaType.TWITTER -> R.string.connect_create_contact_twitter_social_profile_type
                        Enums.Contacts.SocialMediaType.TELEGRAM -> R.string.connect_create_contact_telegram_social_profile_type
                        Enums.Contacts.SocialMediaType.OTHER -> R.string.connect_create_contact_other_social_profile_type
                        else -> -1
                    }
                )
            }

            ConnectGroupAddItemView.ItemType.ADDRESS -> {
                editText.inputType = InputType.TYPE_CLASS_TEXT
                typeTextView.text = context.getString(
                    when (item) {
                        Enums.Contacts.AddressType.WORK -> R.string.connect_create_contact_work_address_type
                        Enums.Contacts.AddressType.HOME -> R.string.connect_create_contact_home_address_type
                        Enums.Contacts.AddressType.OTHER -> R.string.connect_create_contact_other_address_type
                        else -> -1
                    }
                )
            }
        }

        updateContentDescriptions(itemType)
        text?.let {
            if(it != editText.text.toString()) {
                editText.setText(it)
            }
        }

        if(isDuplicate.orFalse()) {
            setDuplicate(true)
        } else {
            setDuplicate(false)
        }
    }

    fun setDuplicate(isDuplicate: Boolean) {
        when (itemType) {
            ConnectGroupAddItemView.ItemType.PHONE -> {
                if (isDuplicate && editText.text.toString().isNotBlank()) {
                    showErrorState(context.getString(R.string.connect_create_contact_duplicate_phone_error_message))
                } else {
                    showNormalState()
                }
            }
            ConnectGroupAddItemView.ItemType.EMAIL -> {
                if (editText.text.toString().isNotBlank()) {
                    onTextChanged(isDuplicate)
                } else {
                    showNormalState()
                }
            }
            ConnectGroupAddItemView.ItemType.SOCIAL -> {
                if (isDuplicate && editText.text.toString().isNotBlank()) {
                    showErrorState(context.getString(R.string.connect_create_contact_duplicate_social_error_message))
                } else {
                    showNormalState()
                }
            }
            else -> {}
        }
    }

    fun setupCallbacks(
        typeSelectionCallback: (ConnectAddItemView) -> Unit,
        removeItemCallback: (ConnectAddItemView) -> Unit,
        onValueChanged: (ConnectAddItemView) -> Unit,
        getDuplicates: () -> MutableSet<Int>,
        onFocusChanged: (ConnectAddItemView, Boolean) -> Unit,
    ) {
        editText.addTextChangedListener(textWatcher)
        streetOneEditText.addTextChangedListener(textWatcher)
        streetTwoEditText.addTextChangedListener(textWatcher)
        cityEditText.addTextChangedListener(textWatcher)
        stateEditText.addTextChangedListener(textWatcher)
        zipEditText.addTextChangedListener(textWatcher)

        typeSelector.setOnClickListener { typeSelectionCallback(this) }
        removeIcon.setOnClickListener { removeItemCallback(this) }
        this.onValueChanged = onValueChanged
        this.getDuplicates = getDuplicates
        this.onFocusChanged = onFocusChanged
    }

    fun setup(index: Int, itemType: ConnectGroupAddItemView.ItemType) {
        this.index = index
        when (itemType) {
            ConnectGroupAddItemView.ItemType.PHONE -> {
                editText.hint = context.getString(R.string.connect_create_contact_phone_hint)
                editText.filters = arrayOf(InputFilter.LengthFilter(50))
                editText.inputType = InputType.TYPE_CLASS_PHONE
                editText.setOnFocusChangeListener { _, focused ->
                    onFocusChanged?.invoke(this, focused)
                        if ((editText.text.length == 10 || (editText.text.startsWith("1") && editText.text.length == 11)) &&
                                editText.text.isDigitsOnly()) {
                            editText.setText(PhoneNumberUtils.formatNumber(
                                    editText.text.toString(),
                                    Locale.getDefault().country))
                        }
                }
            }
            ConnectGroupAddItemView.ItemType.EMAIL -> {
                editText.hint = context.getString(R.string.connect_create_contact_email_hint)
                editText.setOnFocusChangeListener { _, focused ->
                    onFocusChanged?.invoke(this, focused)
                }
            }
            ConnectGroupAddItemView.ItemType.DATE -> {
                dateIcon.visibility = View.VISIBLE
                dateIcon.setOnClickListener {
                    DatePickerDialog(context,
                            dateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show()
                }
                editText.hint = context.getString(R.string.connect_create_contact_date_hint)
            }
            ConnectGroupAddItemView.ItemType.SOCIAL -> {
                editText.hint =
                    context.getString(R.string.connect_create_contact_social_profile_hint)
                editText.setOnFocusChangeListener { _, focused ->
                    onFocusChanged?.invoke(this, focused)
                }
            }
            ConnectGroupAddItemView.ItemType.ADDRESS -> {
                editText.hint = context.getString(R.string.connect_create_contact_address_country_hint)
                editText.contentDescription = itemType.name + " " + context.getString(R.string.add_contact_country_edit_text_content_description)
                addressLayout.visibility = View.VISIBLE
            }
        }

        updateContentDescriptions(itemType)
    }

    private fun updateContentDescriptions(itemType: ConnectGroupAddItemView.ItemType) {
        val typeItemValue = "${typeTextView.text} ${itemType.name.toLowerCase(Locale.ROOT)}"

        removeIcon.contentDescription = context.getString(R.string.connect_add_item_remove_icon_content_description, typeItemValue)
        typeSelector.contentDescription = context.getString(R.string.connect_add_item_type_selector_content_description, typeItemValue)
        editText.contentDescription = context.getString(R.string.connect_add_item_text_input_content_description, typeItemValue)
    }

    fun getInputText(): String {
        return editText.text.toString()
    }

    fun getAddress(): Address {
        return Address(streetOneEditText.text.toString(),
                streetTwoEditText.text.toString(),
                zipEditText.text.toString(),
                cityEditText.text.toString(),
                stateEditText.text.toString(),
                editText.text.toString(),
                null,
                item,
                null)
    }

    fun onTextChanged(isDuplicate: Boolean?) {
        if (itemType == ConnectGroupAddItemView.ItemType.EMAIL) {
            if (!Patterns.EMAIL_ADDRESS.matcher(editText.text as CharSequence).matches()) {
                showErrorState(context.getString(R.string.connect_create_contact_email_error_message))
            } else if (isDuplicate.orFalse()) {
                showErrorState(context.getString(R.string.connect_create_contact_duplicate_email_error_message))
            } else {
                showNormalState()
            }
        }
    }

    private fun showNormalState() {
        errorText.visibility = View.GONE
    }

    fun showErrorState(errorMessage: String) {
        errorText.visibility = View.VISIBLE
        errorText.text = errorMessage
    }

    fun resetValues() {
        item = null
        editText.text = null
        itemType = null
        showNormalState()
    }

    fun clearEditTextFocus() {
        editText.clearFocus()
    }

    private fun updateDateInView() {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        editText.setText(format.format(calendar.time))
    }
}