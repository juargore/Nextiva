package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FontAwesomeListItem
import com.nextiva.nextivaapp.android.constants.Enums
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FontAwesomeUtilityViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

    private var baseListItemsLiveData: MutableLiveData<ArrayList<BaseListItem>?> = MutableLiveData()

    fun loadListItems() {
        val listItems: ArrayList<BaseListItem> = ArrayList()
        
        listItems.add(FontAwesomeListItem(R.string.fa_user, "user", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_user_circle, "user_circle", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_user_plus, "user_plus", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_user_friends, "user_friends", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_users, "users", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_building, "building", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_briefcase, "briefcase", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_door_open, "door_open", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_home, "home", Enums.FontAwesomeIconType.REGULAR))

        listItems.add(FontAwesomeListItem(R.string.fa_microphone, "microphone", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_microphone_slash, "microphone_slash", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_play, "play", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_video, "video", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_video_slash, "video_slash", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_pause, "pause", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_voicemail, "voicemail", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_circle, "circle", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_do_not_disturb, "dnd", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_stop, "stop", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_volume, "volume", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_volume_slash, "volume_slash", Enums.FontAwesomeIconType.REGULAR))

        listItems.add(FontAwesomeListItem(R.string.fa_phone_alt, "phone_alt", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_phone_plus, "phone_plus", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_paper_plane, "paper_plane", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_envelope, "envelope", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_comment_dots, "comment_dots", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_comment_plus, "comment_plus", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_inbox, "inbox", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_calendar, "calendar", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_calendar_alt, "calendar_alt", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_reply, "reply", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_share, "share", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_at, "at", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_exchange, "exchange", Enums.FontAwesomeIconType.REGULAR))

        listItems.add(FontAwesomeListItem(R.string.fa_search, "search", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_ellipsis_v, "ellipsis_v", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_cog, "cog", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_ban, "ban", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_sync, "sync", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_cloud, "cloud", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_lock, "lock", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_times, "times", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_plus, "plus", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_flag, "flag", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_folder, "folder", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_archive, "archive", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_sticky_note, "sticky_note", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_highlighter, "highlighter", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_edit, "edit", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_pen, "pen", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_trash_alt, "trash_alt", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_filter, "filter", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_clock, "clock", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_expand_alt, "expand_alt", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_compress_alt, "compress_alt", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_clipboard_check, "clipboard_check", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_download, "download", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_upload, "upload", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_copy, "copy", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_caret_down_solid, "caret_down_solid", Enums.FontAwesomeIconType.SOLID))
        listItems.add(FontAwesomeListItem(R.string.fa_chevron_left, "chevron_left", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_chevron_right, "chevron_right", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_chevron_down, "chevron_down", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_check, "check", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_star, "star", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_dollar_sign, "dollar_sign", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_tasks, "tasks", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_ellipsis_h, "ellipsis_h", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_times_circle, "times_circle", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_plus_circle, "plus_circle", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_search_plus, "search_plus", Enums.FontAwesomeIconType.REGULAR))

        listItems.add(FontAwesomeListItem(R.string.fa_file, "file", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_file_word, "file_word", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_file_spreadsheet, "file_spreadsheet", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_file_archive, "file_archive", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_file_pdf, "file_pdf", Enums.FontAwesomeIconType.REGULAR))

        listItems.add(FontAwesomeListItem(R.string.fa_question_circle, "question_circle", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_info_circle, "info_circle", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_exclamation_circle, "exclamation_circle", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_bell, "bell", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_lightbulb_on, "lightbulb_on", Enums.FontAwesomeIconType.REGULAR))

        listItems.add(FontAwesomeListItem(R.string.fa_eye, "eye", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_eye_slash, "eye_slash", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_window_maximize, "window_maximize", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_chart_bar, "chart_bar", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_bold, "bold", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_italic, "italic", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_underline, "underline", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_list_ul, "list_ul", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_list_ol, "list_ol", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_indent, "indent", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_link, "link", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_smile, "smile", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_paperclip, "paperclip", Enums.FontAwesomeIconType.REGULAR))

        listItems.add(FontAwesomeListItem(R.string.fa_th_solid, "th_solid", Enums.FontAwesomeIconType.SOLID))
        listItems.add(FontAwesomeListItem(R.string.fa_globe, "globe", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_map_marker_alt, "max_marker_alt", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_external_link, "external_link", Enums.FontAwesomeIconType.REGULAR))
        listItems.add(FontAwesomeListItem(R.string.fa_arrow_left, "arrow_left", Enums.FontAwesomeIconType.REGULAR))

        listItems.add(FontAwesomeListItem(R.string.fa_custom_team_chat, "custom_team_chat", Enums.FontAwesomeIconType.CUSTOM))
        listItems.add(FontAwesomeListItem(R.string.fa_custom_report, "custom_report", Enums.FontAwesomeIconType.CUSTOM))
        listItems.add(FontAwesomeListItem(R.string.fa_custom_present_share, "custom_present_share", Enums.FontAwesomeIconType.CUSTOM))
        listItems.add(FontAwesomeListItem(R.string.fa_custom_outbound_call, "custom_outbound_call", Enums.FontAwesomeIconType.CUSTOM))
        listItems.add(FontAwesomeListItem(R.string.fa_custom_inbound_call, "custom_inbound_call", Enums.FontAwesomeIconType.CUSTOM))
        listItems.add(FontAwesomeListItem(R.string.fa_custom_dialer, "custom_dialer", Enums.FontAwesomeIconType.CUSTOM))
        listItems.add(FontAwesomeListItem(R.string.fa_custom_missed_call, "custom_missed_call", Enums.FontAwesomeIconType.CUSTOM))

        baseListItemsLiveData.value = listItems
    }

    fun getListItemLiveData(): LiveData<ArrayList<BaseListItem>?> {
        return baseListItemsLiveData
    }
}