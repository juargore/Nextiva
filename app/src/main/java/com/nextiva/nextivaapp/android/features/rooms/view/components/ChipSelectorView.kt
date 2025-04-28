package com.nextiva.nextivaapp.android.features.rooms.view.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.children
import androidx.lifecycle.MutableLiveData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.view.ConnectMaxHeightScrollView
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChipSelectorView: ConstraintLayout {

    companion object {
        const val MAX_CHIP_LINES_COUNT = 3
        const val CHIP_MARGINS_AND_PADDING = 20
    }

    private lateinit var searchIcon: FontTextView
    private lateinit var cancelSearch: FontTextView
    private lateinit var chipGroupScrollview: ConnectMaxHeightScrollView
    private lateinit var contactsChipGroup: ChipGroup
    private lateinit var editTextSearchBox: EditText
    private lateinit var toConstraintLayout: ConstraintLayout

    var hintStringId = R.string.connect_new_chat_search_box_hint
    var chipObjects = MutableLiveData<List<Any>>()

    @Inject
    lateinit var avatarManager: AvatarManager

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
        inflater.inflate(R.layout.view_chip_selector, this, true)

        searchIcon = findViewById(R.id.search_icon)
        toConstraintLayout = findViewById(R.id.to_constraint_layout)
        chipGroupScrollview = findViewById(R.id.chip_group_scrollview)
        contactsChipGroup = findViewById(R.id.chat_sms_contacts_in_chat_chip_group)
        editTextSearchBox = findViewById(R.id.edit_text_search_box)
        cancelSearch = findViewById(R.id.chat_sms_search_cancel)

        cancelSearch.setOnClickListener {
            editTextSearchBox.editableText.clear()
            removeAllChips()
            chipObjects.postValue(listOf())
            setSearchBoxHint()
            searchIcon.visibility = View.VISIBLE
        }

        val params = chipGroupScrollview.layoutParams as MarginLayoutParams
        chipGroupScrollview.setMaxHeight(Chip(context).chipMinHeight * MAX_CHIP_LINES_COUNT +
                params.bottomMargin + params.topMargin + CHIP_MARGINS_AND_PADDING)
        setSearchBoxHint()
    }

    private fun removeAllChips() {
        (contactsChipGroup.children.first() as? Chip)?.let {
            contactsChipGroup.removeView(it)
            removeAllChips()
        }
    }

    fun currentChipCount(): Int {
        // the chip group includes editTextSearchBox as a child, so exclude the editText from the chip count
        return contactsChipGroup.childCount - 1
    }

    fun addChip(text: String, tag: Any) {
        searchIcon.visibility = View.GONE
        val chip = getContactChip(text, tag)
        contactsChipGroup.addView(chip, currentChipCount())
        setSearchBoxHint()
        chipGroupScrollview.post { chipGroupScrollview.fullScroll(View.FOCUS_DOWN) }
    }

    fun getEditText(): EditText {
        return editTextSearchBox
    }

    private fun getContactChip(text: String, tag: Any): Chip {
        val chip = Chip(context)
        context?.let{ context ->
            chip.text = text
            chip.tag = tag

            val chipList = (chipObjects.value ?: listOf()).plus(tag)
            chipObjects.postValue(chipList)

            chip.setChipBackgroundColorResource(R.color.connectGrey02)
            chip.setTextColor(ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue))

            chip.closeIcon?.let {
                it.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(context, R.color.connectGrey09), BlendModeCompat.SRC_ATOP)
            }

            chip.chipStrokeWidth = 1.0f
            chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.connectGrey02))
            chip.setEnsureMinTouchTargetSize(false)

            val avatarInfo = AvatarInfo.Builder()
                .setDisplayName(text)
                .isConnect(true)
                .build()
            val bitmap = avatarManager.getBitmap(avatarInfo)
            chip.chipIcon = BitmapDrawable(context.resources, bitmap)

            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                contactsChipGroup.removeView(chip)
                chipObjects.postValue(chipObjects.value?.minus(chip.tag))
                if (currentChipCount() == 0) {
                    searchIcon.visibility = View.VISIBLE
                }
                setSearchBoxHint()
            }
        }
        return chip
    }

    fun setSearchBoxHint(hintStringId: Int) {
        this.hintStringId = hintStringId
        setSearchBoxHint()
    }

    private fun setSearchBoxHint() {
        if (currentChipCount() > 0) {
            editTextSearchBox.hint = ""
            editTextSearchBox.setText("")
        } else {
            editTextSearchBox.hint = context.getString(hintStringId)
        }
        cancelSearch.visibility = if (currentChipCount() > 0) VISIBLE else GONE
    }

}