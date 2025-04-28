package com.nextiva.nextivaapp.android.features.messaging.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.ConnectNewTextActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ActivityConnectTextListBinding
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.MessageListViewModel
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessageListActivity : BaseActivity(), SearchView.OnQueryTextListener {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MessageListActivity::class.java)
        }
    }

    private lateinit var toolbar: Toolbar
    private lateinit var searchBar: SearchView
    private lateinit var addButton: FontTextView

    private lateinit var viewModel: MessageListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MessageListViewModel::class.java]

        setContentView(bindViews())
        setSupportActionBar(toolbar)

        title = getString(R.string.connect_home_messages_channel_title)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.connectSecondaryDarkBlue))

        toolbar.navigationIcon = FontDrawable(this, R.string.fa_arrow_left, Enums.FontAwesomeIconType.REGULAR)
            .withColor(ContextCompat.getColor(this, R.color.connectGrey09))
            .withSize(R.dimen.material_text_title)

        toolbar.setNavigationOnClickListener { onBackPressed()}

        toolbar.overflowIcon = FontDrawable(this, R.string.fa_comment_dots, Enums.FontAwesomeIconType.REGULAR)
            .withColor(ContextCompat.getColor(this, R.color.connectGrey09))
            .withSize(R.dimen.material_text_subhead)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setStatusBarColor(ContextCompat.getColor(this, R.color.connectGrey01))

        addButton.setOnClickListener {
            startActivity(ConnectNewTextActivity.newIntent(this@MessageListActivity))
        }
    }

    fun bindViews(): View {
        val binding = ActivityConnectTextListBinding.inflate(layoutInflater)
        toolbar = binding.toolbar
        searchBar = binding.search
        addButton = binding.addButton

        val icon: ImageView = searchBar.findViewById(androidx.appcompat.R.id.search_mag_icon)
        val drawable = icon.drawable
        drawable.setTint(ContextCompat.getColor(this, R.color.connectGrey09))
        icon.setImageDrawable(drawable)

        val searchEditText = searchBar.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.connectSecondaryDarkBlue))
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.connectGrey09))

        searchBar.setOnQueryTextListener(this)
        overrideEdgeToEdge(binding.root)

        return binding.root
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.onSearchTermUpdated(newText)
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.onSearchTermUpdated(query)
        return false
    }
}