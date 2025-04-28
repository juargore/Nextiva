package com.nextiva.nextivaapp.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nextiva.nextivaapp.android.db.model.DbMeeting;

import java.util.List;

@Dao
public abstract class MeetingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract List<Long> insert(List<DbMeeting> meetings);

    @Query("SELECT meeting_info FROM meetings WHERE start_time BETWEEN :startDate AND :endDate")
    public abstract List<String> getMeetingsBetweenDates(Long startDate, Long endDate);

    @Query("DELETE FROM meetings WHERE start_time < :startDate")
    public abstract void deleteMeetingsOutOfTime(Long startDate);

    @Transaction
    public void refreshMeetings(List<DbMeeting> meetings,Long startDate){
        insert(meetings);
        deleteMeetingsOutOfTime(startDate);
    }
}