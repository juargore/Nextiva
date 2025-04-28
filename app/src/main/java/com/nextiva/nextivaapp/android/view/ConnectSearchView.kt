package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView

class ConnectSearchView: LinearLayout {

    private lateinit var closeIcon: FontTextView
    private lateinit var searchIcon: FontTextView
    private lateinit var searchView: SearchView

    private var onFocusChangedCallback: (Boolean) -> Unit = {}
    private var onCloseClickedCallback: (() -> Unit)? = null
    private var lockSearch: Boolean = false
    private var hasFocus: Boolean = false

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
        inflater.inflate(R.layout.view_connect_search, this, true)
        searchView = findViewById(R.id.connect_search_view)
        closeIcon = findViewById(R.id.connect_search_view_close_icon)
        searchIcon = findViewById(R.id.connect_search_view_search_icon)

        val searchEditText = searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue))
        searchEditText.setHintTextColor(ContextCompat.getColor(context, R.color.connectGrey09))

        closeIcon.setOnClickListener {
            onBackPressed()
        }

        searchView.setOnQueryTextFocusChangeListener { _, isFocused ->
            hasFocus = isFocused
            if (isFocused) {
                closeIcon.visibility = View.VISIBLE
                searchIcon.visibility = View.GONE
            } else if (!lockSearch) {
                closeIcon.visibility = View.GONE
                searchIcon.visibility = View.VISIBLE
            }

            onFocusChangedCallback(isFocused)
        }
    }

    fun setOnFocusChangedCallback(focusChangeCallback: ((Boolean) -> Unit)?) {
        focusChangeCallback?.let { onFocusChangedCallback = it }
    }

    fun setOnCloseClickedCallback(closeCLickedCallback: (() -> Unit)?) {
        closeCLickedCallback?.let { onCloseClickedCallback = it }
    }

    fun setOnQueryTextListener(listener: SearchView.OnQueryTextListener) {
        searchView.setOnQueryTextListener(listener)
    }

    fun lockSearch() {
        lockSearch = true
    }

    fun resetSearchView() {
        searchView.clearFocus()
        searchView.setQuery("", true)
    }

    fun getCurrentQuery() = searchView.query.toString()

    override fun clearFocus() {
        super.clearFocus()
        searchView.clearFocus()
    }

    fun isSearchLocked(): Boolean = searchIcon.visibility == View.GONE && !hasFocus

    fun onBackPressed() {
        if (lockSearch) {
            closeIcon.visibility = View.GONE
            searchIcon.visibility = View.VISIBLE
        }
        onCloseClickedCallback?.invoke()
        resetSearchView()
    }

}