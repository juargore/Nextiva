package com.nextiva.nextivaapp.android.features.rooms.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageListItemView
import com.nextiva.nextivaapp.android.features.rooms.view.components.SearchView
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.TeamChatListViewModel
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.Typography
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyH5
import com.nextiva.nextivaapp.android.fragments.GeneralRecyclerViewFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamChatListFragment(private val searchViewFocusChangeCallback: ((Boolean) -> Unit)?) : GeneralRecyclerViewFragment() {

    constructor() : this(null)

    private val viewModel: TeamChatListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mRecyclerView = RecyclerView(requireActivity().application)  // not used here, but required by GeneralRecyclerViewFragment
        fetchItemList(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val allConversations by viewModel.conversations.observeAsState(listOf())
                val scrollState = rememberLazyListState()

                Column(
                    modifier = Modifier
                        .background(colorResource(id = R.color.connectWhite))
                ) {

                    Box(
                        modifier = Modifier
                            .background(colorResource(id = R.color.connectGrey01))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(
                                    horizontal = dimensionResource(id = R.dimen.general_padding_medium),
                                    vertical = dimensionResource(id = R.dimen.general_padding_small)
                                )
                        ) {
                            SearchView()
                            Text(
                                text = stringResource(R.string.room_conversation_recent_texts),
                                color = colorResource(R.color.connectSecondaryDarkBlue),
                                style = Typography.body2,
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.general_padding_large))
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorResource(id = R.color.connectGrey01)),
                        state = scrollState,
                    ) {
                        itemsIndexed(
                            items = allConversations,
                            key = { index, item -> "$index:${viewModel.presenceMap[item.presenceUserId]?.value?.state ?: 0}" }
                        ) { _, item ->
                            MessageListItemView(
                                modifier = Modifier.clickable { onItemClicked(context, item.room) },
                                title = viewModel.displayName(item.room),
                                subtitle = viewModel.latestMessageText(item.room),
                                timestamp = viewModel.latestTimestamp(item.room),
                                avatarInfo = viewModel.avatarInfo(item.room, viewModel.presenceMap[item.presenceUserId]?.value),
                                unreadCount = viewModel.unreadCount(item.room)
                            )
                        }
                    }

                    if (allConversations.isEmpty()) {
                        EmptyListView(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        }
    }

    @Composable
    fun EmptyListView(modifier: Modifier = Modifier) {
        val fontSizeSp = with(LocalDensity.current) {
            dimensionResource(id = R.dimen.material_text_display3).toSp()
        }

        Column(modifier = modifier) {
            Spacer(modifier = Modifier.weight(1.0f))

            Box(
                modifier = modifier
                    .padding(dimensionResource(R.dimen.general_padding_medium))
                    .size(dimensionResource(R.dimen.general_view_xxxxxxxxxxxxlarge))
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .background(colorResource(R.color.connectPrimaryLightBlue)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = modifier
                        .padding(dimensionResource(R.dimen.general_padding_small)),
                    text = stringResource(id = R.string.fa_comment_dots),
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.connectGrey08),
                    fontSize = fontSizeSp,
                    fontFamily = FontAwesome,
                    fontWeight = FontWeight.Normal
                )
            }

            Text(
                modifier = Modifier
                    .padding(
                        vertical = dimensionResource(id = R.dimen.general_padding_medium)
                    )
                    .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.connect_team_chat_start_texting),
                color = colorResource(R.color.connectSecondaryDarkBlue),
                style = TypographyH5,
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.connect_team_chat_empty_list),
                color = colorResource(R.color.connectGrey10),
                style = Typography.body2,
            )

            Spacer(modifier = Modifier.weight(1.0f))
        }
    }

    private fun onItemClicked(context: Context, room: DbRoom) {
        val title = viewModel.displayName(room)
        val titleCount = viewModel.displayNameCount(room)
        context.startActivity(RoomConversationActivity.newIntent(context, room.roomId, title, titleCount, null))
    }

    //
    // GeneralRecyclerViewFragment - required for ConnectMainViewPagerAdapter
    //
    override fun fetchItemList(forceRefresh: Boolean) {
        viewModel.fetchRooms(forceRefresh) {
            stopRefreshing()
        }
    }

    override fun getLayoutId(): Int {
        return 0
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }

    override fun getRecyclerViewId(): Int {
        return 0
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.CONNECT_TEAM_CHAT_LIST
    }
}
