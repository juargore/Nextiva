package com.nextiva.nextivaapp.android.adapters

import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.db.model.Address
import com.nextiva.nextivaapp.android.db.model.DbDate
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.db.model.SocialMediaAccount
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectSocialMediaType
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.view.ConnectAddItemView
import com.nextiva.nextivaapp.android.view.ConnectGroupAddItemView.ItemType

class GroupAddItemAdapter(
    val items: ArrayList<Any> = ArrayList(),
    private val onTypeSelectorChanged: ((ConnectAddItemView) -> Unit),
    private val onAdapterSizeChanged: ((Int) -> Unit),
    private val onValueChanged: (() -> Unit)?,
) : RecyclerView.Adapter<GroupAddItemViewViewHolder>() {

    private var onFieldChangeEnabled = false
    private var focusedView: ConnectAddItemView? = null
    private val fineItems = mutableSetOf<Int>()
    private val duplicates = mutableSetOf<Int>()

    private val removeItemCallback = object : ((ConnectAddItemView) -> Unit) {
        override fun invoke(item: ConnectAddItemView) {
            removeItem(item)
        }
    }

    private val getDuplicates = object : (() -> MutableSet<Int>) {
        override fun invoke(): MutableSet<Int> {
            return if (onFieldChangeEnabled) duplicates else mutableSetOf()
        }
    }

    private val onFocusChanged = object : ((ConnectAddItemView, Boolean) -> Unit) {
        override fun invoke(view: ConnectAddItemView, focused: Boolean) {
            if (focused) {
                focusedView = view
            } else {
                focusedView = null
            }
        }
    }

    private val onFieldChanged = object : ((ConnectAddItemView) -> Unit) {
        override fun invoke(view: ConnectAddItemView) {
            if(!onFieldChangeEnabled) {
                return
            }
            when (items[view.index]) {
                is PhoneNumber -> {
                    if ((items[view.index] as PhoneNumber).number != view.editText.text.toString()) {
                        (items[view.index] as PhoneNumber).number = view.editText.text.toString()
                        extractDuplicates()
                        onValueChanged?.invoke()
                    }
                }

                is EmailAddress -> {
                    if ((items[view.index] as EmailAddress).address != view.editText.text.toString()) {
                        (items[view.index] as EmailAddress).address = view.editText.text.toString()
                        extractDuplicates()
                        onValueChanged?.invoke()
                    }
                }

                is SocialMediaAccount -> {
                    if ((items[view.index] as SocialMediaAccount).link != view.editText.text.toString()) {
                        (items[view.index] as SocialMediaAccount).link = view.editText.text.toString()
                        extractDuplicates()
                        onValueChanged?.invoke()
                    }
                }

                is Address -> items[view.index] = view.getAddress()
                is DbDate -> (items[view.index] as DbDate).date = view.editText.text.toString()
            }
        }
    }

    fun extractDuplicates() {
        fineItems.clear()
        duplicates.clear()
        val set = mutableMapOf<String, MutableSet<Int>>()
        items.forEachIndexed { index, item ->
            when (item) {
                is PhoneNumber -> {
                    CallUtil.getStrippedPhoneNumber(item.number.orEmpty())
                        .ifBlank { null }
                }

                is EmailAddress -> item.address?.ifBlank { null }
                is SocialMediaAccount -> {
                    item.link?.ifBlank { null }
                }

                else -> {
                    null
                }
            }?.let { text ->
                set.getOrPut(text) { mutableSetOf() }?.let {
                    it.add(index)
                }
            }
        }

        set.values.forEach { indexes ->
            if(indexes.size > 1) duplicates.addAll(indexes)
            else fineItems.addAll(indexes)
        }
    }

    fun checkDuplicates(): Boolean {

        fineItems.apply { addAll(duplicates) }
            .forEach { if (it != focusedView?.index) notifyItemChanged(it) }

        return duplicates.size > 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupAddItemViewViewHolder {
        val view = GroupAddItemViewViewHolder(
            ConnectAddItemView(parent.context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }.apply {
                setupCallbacks(
                    onTypeSelectorChanged,
                    removeItemCallback,
                    onFieldChanged,
                    getDuplicates,
                    onFocusChanged
                )
            }
        )
        return view
    }

    fun addItems(items: ArrayList<Any>) {
        val from = this.items.size
        this.items.addAll(items)
        onFieldChangeEnabled = false
        focusedView = null
        extractDuplicates()
        notifyItemRangeInserted(from, items.size)
        onValueChanged?.invoke()
        onAdapterSizeChanged(items.size)
        onFieldChangeEnabled = true
    }

    fun addNewItem(item: Any) {
        onFieldChangeEnabled = false
        items.add(item)
        extractDuplicates()
        notifyItemInserted(items.size+1)
        onAdapterSizeChanged(items.size)
        onFieldChangeEnabled = true
    }

    fun removeItem(item: ConnectAddItemView) {
        onFieldChangeEnabled = false
        items.removeAt(item.index)
        notifyItemRemoved(item.index)
        if(item.index < items.size) {
            notifyItemRangeChanged(item.index, items.size - item.index)
        }
        extractDuplicates()
        onValueChanged?.invoke()
        onAdapterSizeChanged(items.size)
        onFieldChangeEnabled = true
    }

    fun updateItemType(type: Int, view: ConnectAddItemView) {
        val index = view.index
        when (view.itemType) {
            ItemType.PHONE -> (items[index] as PhoneNumber).type = type
            ItemType.EMAIL -> (items[index] as EmailAddress).type = type
            ItemType.ADDRESS -> (items[index] as Address).type = type
            ItemType.SOCIAL -> (items[index] as SocialMediaAccount).type = type
            ItemType.DATE -> (items[index] as DbDate).type = type
            else -> {}
        }
        notifyItemChanged(index)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: GroupAddItemViewViewHolder, position: Int) {
        holder.bind(items[position], position, duplicates)
    }
}

class GroupAddItemViewViewHolder(val view: ConnectAddItemView) : RecyclerView.ViewHolder(view) {
    fun bind(item: Any, position: Int, duplicates: Set<Int>) {
        when (item) {
            is PhoneNumber -> {
                view.setup(position, ItemType.PHONE)
                view.setViews(
                    item.type,
                    ItemType.PHONE,
                    item.number,
                    duplicates.contains(position)
                )
            }

            is Address -> {
                view.setup(position, ItemType.ADDRESS)
                view.setViews(item.type.orZero(), item)
            }

            is EmailAddress -> {
                view.setup(position, ItemType.EMAIL)
                view.setViews(
                    item.type,
                    ItemType.EMAIL,
                    item.address,
                    duplicates.contains(position)
                )
            }

            is SocialMediaAccount -> {
                val type = item.type?.let { ConnectSocialMediaType.fromIntType(it).numericType }
                    ?: ConnectSocialMediaType.Other.numericType
                view.setup(position, ItemType.SOCIAL)
                view.setViews(
                    type,
                    ItemType.SOCIAL,
                    item.link,
                    duplicates.contains(position)
                )
            }

            is DbDate -> {
                view.setup(position, ItemType.DATE)
                view.setViews(item.type.orZero(), ItemType.DATE, item.date, null)
            }
        }
    }
}