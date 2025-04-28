package com.nextiva.nextivaapp.android.db.dao;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.Transaction;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry;
import com.nextiva.nextivaapp.android.models.CallLogEntry;
import com.nextiva.nextivaapp.android.models.CallsDbReturnModel;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class CallLogsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertCallLog(DbCallLogEntry callLogEntry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertCallLogs(List<DbCallLogEntry> callLogEntries);

    @Transaction
    public void replaceCallLogs(List<DbCallLogEntry> callLogEntries) {
        deleteAllBwCallLogs();
        insertCallLogs(callLogEntries);
    }

    @Transaction
    public void insertBWCallLogs(ArrayList<CallLogEntry> callLogEntries) {
        for (CallLogEntry callLogEntry : callLogEntries) {
            insertCallLog(new DbCallLogEntry(null,
                    callLogEntry.getCallLogId(),
                    callLogEntry.getDisplayName(),
                    callLogEntry.getCallTime(),
                    callLogEntry.getCountryCode(),
                    callLogEntry.getPhoneNumber(),
                    callLogEntry.getFormattedPhoneNumber(),
                    callLogEntry.getCallType(),
                    TextUtils.equals(Enums.Calls.CallTypes.MISSED, callLogEntry.getCallType()) ? 0 : 1,
                    null,
                    null,
                    callLogEntry.getCallDuration(),
                    callLogEntry.getCallStartTime()
            ));
        }
    }

    @Transaction
    @Query("SELECT calllogs.*," +
            "vcards.photo_data, " +
            "number_results.ui_name, " +
            "IFNULL(presences.presence_state, -1) AS presence_state, " +
            "status_text, " +
            "priority, " +
            "contact_type, " +
            "presences.jid, " +
            "IFNULL(presences.presence_type, -1) AS presence_type " +
            "FROM calllogs " +
            "LEFT JOIN (SELECT contacts.id as contact_id, MIN(contacts.ui_name) AS ui_name, phones.stripped_number " +
            "   FROM contacts " +
            "   INNER JOIN phones ON contacts.id = phones.contact_id " +
            "   GROUP BY phones.stripped_number " +
            "   ORDER BY contact_type ASC) " +
            "AS number_results ON " +
            "CASE WHEN length(number_results.stripped_number) < 9 " +
            "   THEN number_results.stripped_number = calllogs.phone_number " +
            "   ELSE number_results.stripped_number = calllogs.phone_number OR number_results.stripped_number = (calllogs.country_code || calllogs.phone_number) " +
            "END " +
            "LEFT JOIN (SELECT contacts.id as contact_id, phones.stripped_number, contacts.contact_type AS contact_type " +
            "   FROM contacts " +
            "   INNER JOIN phones ON contacts.id = phones.contact_id " +
            "   GROUP BY phones.stripped_number " +
            "   ORDER BY contact_type ASC) " +
            "AS results ON " +
            "CASE WHEN length(results.stripped_number) < 9 " +
            "   THEN results.stripped_number = calllogs.phone_number " +
            "   ELSE results.stripped_number = calllogs.phone_number OR results.stripped_number = (calllogs.country_code || calllogs.phone_number) " +
            "END " +
            "LEFT JOIN vcards ON vcards.contact_id = results.contact_id " +
            "LEFT JOIN presences ON presences.contact_id = results.contact_id " +
            "WHERE call_type IN (:callTypesList) " +
            "ORDER BY call_date_time DESC")
    public abstract LiveData<List<CallLogEntry>> getCallLogEntriesLiveData(List<String> callTypesList);

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT *, null as user_id " +
            "FROM calllogs " +
            "WHERE call_type IN (:callTypesList) " +
            "ORDER BY call_date_time DESC")
    public abstract PagingSource<Integer, CallsDbReturnModel> getCallLogEntriesPagingSource(List<String> callTypesList);

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM (" +
            "SELECT call_log_id, display_name, call_date_time, country_code, phone_number, formatted_phone_number, call_type, is_read, call_with_contact_id, " +
            "null as duration, null as address, null as name, null as user_id, calllogs.call_date_time as time, null as message_id, null as read, " +
            "null as transcription, null as rating, null as transaction_id, null as actual_voicemail_id, null as formatted_phone_number " +
            "FROM calllogs " +
            "WHERE call_type IN (:callTypesList) " +
            "UNION " +
            "SELECT null as call_log_id, null as display_name, null as call_date_time, null as country_code, null as phone_number, null as formatted_phone_number, null as call_type, null as is_read, " +
            "null as call_with_contact_id, duration, address, name, user_id, voicemails.time as time, message_id, read, " +
            "transcription, rating, transaction_id, actual_voicemail_id, formatted_phone_number " +
            "FROM voicemails " +
            ") ORDER BY time DESC")
    public abstract PagingSource<Integer, CallsDbReturnModel> getCallLogAndVoicemailPagingSource(List<String> callTypesList);

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT *, null as user_id " +
            "FROM calllogs " +
            "WHERE call_type IN (:callTypesList) AND ( display_name LIKE :query OR phone_number LIKE :query OR formatted_phone_number LIKE replace(replace(replace(replace(replace(:query, '+', ''), '-', ''), '(', ''), ')', ''), ' ', '')) " +
            "ORDER BY call_date_time DESC")
    public abstract PagingSource<Integer, CallsDbReturnModel> getCallLogEntriesPagingSource(List<String> callTypesList, String query);

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM (" +
            "SELECT call_log_id, display_name, call_date_time, country_code, phone_number, formatted_phone_number, call_type, is_read, call_with_contact_id, " +
            "call_duration as duration, call_start_time as start_time, null as address, null as name, null as user_id, calllogs.call_date_time as time, null as message_id, null as read, " +
            "null as transcription, null as rating, null as transaction_id, null as actual_voicemail_id, null as caller_id, null as formatted_phone_number " +
            "FROM calllogs " +
            "WHERE call_type IN (:callTypesList) AND ( display_name LIKE :query OR phone_number LIKE :query OR formatted_phone_number LIKE replace(replace(replace(replace(replace(:query, '+', ''), '-', ''), '(', ''), ')', ''), ' ', '')) " +
            "UNION " +
            "SELECT null as call_log_id, null as display_name, null as call_date_time, null as country_code, null as phone_number, null as formatted_phone_number, null as call_type, null as is_read, " +
            "null as call_with_contact_id, duration, null as start_time, address, name, user_id, voicemails.time as time, message_id, read, " +
            "transcription, rating, transaction_id, actual_voicemail_id, caller_id, formatted_phone_number " +
            "FROM voicemails WHERE name LIKE :query OR formatted_phone_number LIKE replace(replace(replace(replace(replace(:query, '+', ''), '-', ''), '(', ''), ')', ''), ' ', '')" +
            ") ORDER BY time DESC")
    public abstract PagingSource<Integer, CallsDbReturnModel> getCallLogAndVoicemailPagingSource(List<String> callTypesList, String query);

    @Transaction
    @Query("DELETE FROM calllogs")
    public abstract void deleteCallLogs();

    @Query("DELETE FROM calllogs WHERE call_log_id IN (:callLogIds)")
    public abstract void deleteCallLogs(ArrayList<String> callLogIds);

    @Query("DELETE FROM calllogs WHERE call_log_id = :callLogId")
    public abstract void deleteCallLogFromId(String callLogId);

    @Transaction
    @Query("UPDATE calllogs SET is_read = 1 WHERE is_read = 0")
    public abstract void markAllCallLogEntriesAsRead();

    @Transaction
    @Query("UPDATE calllogs SET is_read = :isRead WHERE call_log_id IN (:callLogIdList)")
    public abstract void updateCallLogEntriesReadStatus(int isRead, List<String> callLogIdList);

    @Transaction
    @Query("UPDATE calllogs SET is_read = 1 WHERE is_read = 0 AND call_log_id = :callLogId")
    public abstract void markCallLogEntryAsRead(String callLogId);

    @Transaction
    @Query("UPDATE calllogs SET is_read = 0 WHERE is_read = 1 AND call_log_id = :callLogId")
    public abstract void markCallLogEntryAsUnread(String callLogId);

    @Deprecated
    @Transaction
    @Query("SELECT COUNT(*) as count FROM calllogs WHERE is_read = 0")
    public abstract LiveData<Integer> getUnreadCallLogEntriesCount();

    @Transaction
    @Query("SELECT COUNT(*) as count FROM calllogs WHERE is_read = 0 AND call_type = 'missed'")
    public abstract LiveData<Integer> getUnreadMissedCallLogEntriesCount();

    @Transaction
    @Query("SELECT (SELECT COUNT(*) FROM calllogs WHERE is_read = 0) + (SELECT COUNT(*) FROM voicemails WHERE read = 0)")
    public abstract LiveData<Integer> getUnreadCallLogAndVoicemailCount();

    @Query("DELETE FROM calllogs WHERE call_log_id LIKE '%' || \":\" || '%'")
    public abstract void deleteAllBwCallLogs();

    @Query("SELECT IFNULL(MAX(call_page_number), 0) FROM calllogs")
    public abstract int getLastPageFetched();

    @Query("SELECT * FROM calllogs WHERE call_log_id = :callLogId")
    public abstract DbCallLogEntry getCallLogByLogId(String callLogId);
}
