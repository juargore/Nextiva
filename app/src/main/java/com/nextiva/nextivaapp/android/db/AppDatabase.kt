/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.text.TextUtils
import androidx.room.Database
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.converters.DateTypeConverters
import com.nextiva.nextivaapp.android.db.converters.RoomConverters
import com.nextiva.nextivaapp.android.db.dao.AddressDao
import com.nextiva.nextivaapp.android.db.dao.AttachmentsDao
import com.nextiva.nextivaapp.android.db.dao.CallLogsDao
import com.nextiva.nextivaapp.android.db.dao.CompleteContactDao
import com.nextiva.nextivaapp.android.db.dao.CompleteContactDaoKt
import com.nextiva.nextivaapp.android.db.dao.ContactDao
import com.nextiva.nextivaapp.android.db.dao.ContactRecentDao
import com.nextiva.nextivaapp.android.db.dao.DateDao
import com.nextiva.nextivaapp.android.db.dao.EmailDao
import com.nextiva.nextivaapp.android.db.dao.GroupDao
import com.nextiva.nextivaapp.android.db.dao.LoggingDao
import com.nextiva.nextivaapp.android.db.dao.MeetingDao
import com.nextiva.nextivaapp.android.db.dao.MessageStateDao
import com.nextiva.nextivaapp.android.db.dao.MessagesDao
import com.nextiva.nextivaapp.android.db.dao.ParticipantsDao
import com.nextiva.nextivaapp.android.db.dao.PhoneDao
import com.nextiva.nextivaapp.android.db.dao.PresenceDao
import com.nextiva.nextivaapp.android.db.dao.RecipientsDao
import com.nextiva.nextivaapp.android.db.dao.SchedulesDao
import com.nextiva.nextivaapp.android.db.dao.SendersDao
import com.nextiva.nextivaapp.android.db.dao.SessionDao
import com.nextiva.nextivaapp.android.db.dao.SmsMessagesDao
import com.nextiva.nextivaapp.android.db.dao.SmsTeamDao
import com.nextiva.nextivaapp.android.db.dao.SocialMediaAccountDao
import com.nextiva.nextivaapp.android.db.dao.VCardDao
import com.nextiva.nextivaapp.android.db.dao.VoicemailDao
import com.nextiva.nextivaapp.android.db.model.Address
import com.nextiva.nextivaapp.android.db.model.DbAttachment
import com.nextiva.nextivaapp.android.db.model.DbCallLogEntry
import com.nextiva.nextivaapp.android.db.model.DbContact
import com.nextiva.nextivaapp.android.db.model.DbContactRecent
import com.nextiva.nextivaapp.android.db.model.DbDate
import com.nextiva.nextivaapp.android.db.model.DbGroup
import com.nextiva.nextivaapp.android.db.model.DbGroupRelation
import com.nextiva.nextivaapp.android.db.model.DbHoliday
import com.nextiva.nextivaapp.android.db.model.DbLogging
import com.nextiva.nextivaapp.android.db.model.DbMeeting
import com.nextiva.nextivaapp.android.db.model.DbMessage
import com.nextiva.nextivaapp.android.db.model.DbMessageState
import com.nextiva.nextivaapp.android.db.model.DbParticipant
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.db.model.DbRecipient
import com.nextiva.nextivaapp.android.db.model.DbSchedule
import com.nextiva.nextivaapp.android.db.model.DbSender
import com.nextiva.nextivaapp.android.db.model.DbSession
import com.nextiva.nextivaapp.android.db.model.DbSmsMessage
import com.nextiva.nextivaapp.android.db.model.DbThread
import com.nextiva.nextivaapp.android.db.model.DbVCard
import com.nextiva.nextivaapp.android.db.model.DbVoicemail
import com.nextiva.nextivaapp.android.db.model.DbWorkingHourBreak
import com.nextiva.nextivaapp.android.db.model.DbWorkingHours
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.db.model.SmsTeamRelation
import com.nextiva.nextivaapp.android.db.model.SocialMediaAccount
import com.nextiva.nextivaapp.android.db.util.DbConstants
import com.nextiva.nextivaapp.android.util.CallUtil

/**
 * Created by joedephillipo on 3/1/18.
 */

@Database(
    entities = [DbContact::class, DbContactRecent::class, Address::class, EmailAddress::class, DbMessage::class, PhoneNumber::class,
        DbPresence::class, DbVCard::class, DbCallLogEntry::class, DbSession::class, DbGroup::class, DbGroupRelation::class,
        DbVoicemail::class, DbSmsMessage::class, DbParticipant::class, DbAttachment::class, DbRecipient::class, DbThread::class, DbSender::class, DbMessageState::class,
        DbLogging::class, DbDate::class, SocialMediaAccount::class, SmsTeamRelation::class, SmsTeam::class, DbMeeting::class, DbSchedule::class,
        DbWorkingHours::class, DbWorkingHourBreak::class, DbHoliday::class],
    version = 60,
    exportSchema = false
)
@TypeConverters(RoomConverters::class, DateTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vCardDao(): VCardDao

    abstract fun contactDao(): ContactDao

    abstract fun contactRecentDao(): ContactRecentDao

    abstract fun messagesDao(): MessagesDao

    abstract fun addressDao(): AddressDao

    abstract fun emailDao(): EmailDao

    abstract fun numberDao(): PhoneDao

    abstract fun presenceDao(): PresenceDao

    abstract fun callLogEntriesDao(): CallLogsDao

    abstract fun completeContactDao(): CompleteContactDao

    abstract fun sessionDao(): SessionDao

    abstract fun groupDao(): GroupDao

    abstract fun voicemailDao(): VoicemailDao

    abstract fun smsMessagesDao(): SmsMessagesDao

    abstract fun sendersDao(): SendersDao

    abstract fun participantDao(): ParticipantsDao

    abstract fun recipientsDao(): RecipientsDao

    abstract fun messageStateDao(): MessageStateDao

    abstract fun attachmentsDao(): AttachmentsDao

    abstract fun loggingDao(): LoggingDao

    abstract fun socialMediaAccountDao(): SocialMediaAccountDao

    abstract fun dateDao(): DateDao

    abstract fun smsTeamDao(): SmsTeamDao

    abstract fun meetingDao(): MeetingDao

    abstract fun schedulesDao(): SchedulesDao
    abstract fun completeContactDaoKt(): CompleteContactDaoKt

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DbConstants.DATABASE_NAME_CONTACTS
                )
                    .addMigrations(
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10,
                        MIGRATION_10_11,
                        MIGRATION_11_12,
                        MIGRATION_12_13,
                        MIGRATION_13_14,
                        MIGRATION_14_15,
                        MIGRATION_16_17,
                        MIGRATION_17_18,
                        MIGRATION_18_19,
                        MIGRATION_19_20,
                        MIGRATION_20_21,
                        MIGRATION_21_22,
                        MIGRATION_22_23,
                        MIGRATION_23_24,
                        MIGRATION_24_25,
                        MIGRATION_25_26,
                        MIGRATION_26_27,
                        MIGRATION_27_28,
                        MIGRATION_28_29,
                        MIGRATION_29_30,
                        MIGRATION_30_31,
                        MIGRATION_31_32,
                        MIGRATION_32_33,
                        MIGRATION_33_34,
                        MIGRATION_34_35,
                        MIGRATION_35_36,
                        MIGRATION_36_37,
                        MIGRATION_37_38,
                        MIGRATION_38_39,
                        MIGRATION_39_40,
                        MIGRATION_40_41,
                        MIGRATION_41_42,
                        MIGRATION_42_43,
                        MIGRATION_43_44,
                        MIGRATION_44_45,
                        MIGRATION_45_46,
                        MIGRATION_46_47,
                        MIGRATION_47_48,
                        MIGRATION_48_49,
                        MIGRATION_49_50,
                        MIGRATION_50_51,
                        MIGRATION_51_52,
                        MIGRATION_52_53,
                        MIGRATION_53_54,
                        MIGRATION_54_55,
                        MIGRATION_55_56,
                        MIGRATION_56_57,
                        MIGRATION_57_58,
                        MIGRATION_58_59,
                        MIGRATION_59_60
                    )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return INSTANCE!!
        }

        // To test this works I load up the roster off a branch with v4 and add thad since he has an address.  I cut internet connection
        // to ensure roster won't be updated again.  I export the db and check that there's an address and it still contains v4 address columns
        // I switch to v5 branch and build still with internet off.  I then export the db again and
        // check that the data was migrated over correctly and was not just dumped.
        private var MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("ALTER TABLE addresses RENAME TO temp_postal_addresses")
                db.execSQL(
                        "CREATE TABLE postal_addresses ( " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_CONTACT_ID + " INTEGER, " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_ADDRESS_LINE_ONE + " TEXT, " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_ADDRESS_LINE_TWO + " TEXT, " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_POSTAL_CODE + " TEXT, " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_CITY + " TEXT, " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_REGION + " TEXT, " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_COUNTRY + " TEXT, " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_LOCATION + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_CONTACT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_CONTACTS + "' (" +
                                DbConstants.CONTACTS_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL("CREATE INDEX index_postal_addresses_contact_id ON postal_addresses (contact_id)")
                db.execSQL(
                        "INSERT INTO postal_addresses(" +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_ID + ", " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_CONTACT_ID + ", " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_ADDRESS_LINE_ONE + ", " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_ADDRESS_LINE_TWO + ", " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_POSTAL_CODE + ", " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_CITY + ", " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_REGION + ", " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_COUNTRY + ", " +
                                DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_LOCATION + ") " +
                                "SELECT id, contact_id, street_one, street_two, postal_code, city, region, country, location FROM temp_postal_addresses"
                )
                db.execSQL("DROP TABLE temp_postal_addresses")
            }
        }

        private var MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                        "CREATE TABLE user_details ( " +
                                DbConstants.SESSION_COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                                DbConstants.SESSION_COLUMN_NAME_KEY + " TEXT, " +
                                DbConstants.SESSION_COLUMN_NAME_VALUE + " TEXT )"
                )

                db.execSQL("DROP TABLE vcards")
                db.execSQL(
                        "CREATE TABLE vcards ( " +
                                DbConstants.VCARDS_COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                                DbConstants.VCARDS_COLUMN_NAME_CONTACT_ID + " INTEGER, " +
                                DbConstants.VCARDS_COLUMN_NAME_PHOTO_DATA + " BLOB, " +
                                DbConstants.VCARDS_COLUMN_NAME_CALL_ID + " TEXT, " +
                                DbConstants.VCARDS_COLUMN_NAME_SIP_URI + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.VCARDS_COLUMN_NAME_CONTACT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_CONTACTS + "' (" +
                                DbConstants.CONTACTS_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL("CREATE INDEX index_vcards_contact_id ON vcards (contact_id)")
            }
        }

        private var MIGRATION_6_7: Migration = object : Migration(6, 7) {
            @SuppressLint("Range")
            override fun migrate(db: SupportSQLiteDatabase) {
                val cursor = db.query("SELECT * FROM emails")

                db.execSQL(
                        "CREATE TABLE new_emails ( " +
                                DbConstants.EMAILS_COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                                DbConstants.EMAILS_COLUMN_NAME_CONTACT_ID + " INTEGER, " +
                                DbConstants.EMAILS_COLUMN_NAME_ADDRESS + " TEXT, " +
                                DbConstants.EMAILS_COLUMN_NAME_TYPE + " INTEGER, " +
                                DbConstants.EMAILS_COLUMN_NAME_LABEL + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.EMAILS_COLUMN_NAME_CONTACT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_CONTACTS + "' (" +
                                DbConstants.CONTACTS_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )

                val contentValues = ContentValues()

                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    contentValues.clear()

                    contentValues.put(
                            DbConstants.EMAILS_COLUMN_NAME_ID,
                            cursor.getLong(cursor.getColumnIndex(DbConstants.EMAILS_COLUMN_NAME_ID))
                    )
                    contentValues.put(
                            DbConstants.EMAILS_COLUMN_NAME_CONTACT_ID,
                            cursor.getInt(cursor.getColumnIndex(DbConstants.EMAILS_COLUMN_NAME_CONTACT_ID))
                    )
                    contentValues.put(
                            DbConstants.EMAILS_COLUMN_NAME_ADDRESS,
                            cursor.getString(cursor.getColumnIndex(DbConstants.EMAILS_COLUMN_NAME_ADDRESS))
                    )

                    val emailType =
                            cursor.getString(cursor.getColumnIndex(DbConstants.EMAILS_COLUMN_NAME_TYPE))

                    when (emailType) {
                        "Email" -> contentValues.put(
                                DbConstants.EMAILS_COLUMN_NAME_TYPE,
                                Enums.Contacts.EmailTypes.EMAIL
                        )
                        "Work Email" -> contentValues.put(
                                DbConstants.EMAILS_COLUMN_NAME_TYPE,
                                Enums.Contacts.EmailTypes.WORK_EMAIL
                        )
                        "Home Email" -> contentValues.put(
                                DbConstants.EMAILS_COLUMN_NAME_TYPE,
                                Enums.Contacts.EmailTypes.HOME_EMAIL
                        )
                        "Mobile Email" -> contentValues.put(
                                DbConstants.EMAILS_COLUMN_NAME_TYPE,
                                Enums.Contacts.EmailTypes.MOBILE_EMAIL
                        )
                        "Other Email" -> contentValues.put(
                                DbConstants.EMAILS_COLUMN_NAME_TYPE,
                                Enums.Contacts.EmailTypes.OTHER_EMAIL
                        )
                        "Custom Email" -> contentValues.put(
                                DbConstants.EMAILS_COLUMN_NAME_TYPE,
                                Enums.Contacts.EmailTypes.CUSTOM_EMAIL
                        )
                        else -> {
                            contentValues.put(
                                    DbConstants.EMAILS_COLUMN_NAME_TYPE,
                                    Enums.Contacts.EmailTypes.CUSTOM_EMAIL
                            )
                            contentValues.put(DbConstants.EMAILS_COLUMN_NAME_LABEL, emailType)
                        }
                    }

                    db.insert("new_emails", OnConflictStrategy.REPLACE, contentValues)
                    cursor.moveToNext()
                }

                db.execSQL("DROP TABLE emails")
                db.execSQL("ALTER TABLE new_emails RENAME TO emails")
                db.execSQL("CREATE INDEX index_emails_contact_id ON emails (contact_id)")
            }
        }

        private var MIGRATION_7_8: Migration = object : Migration(7, 8) {
            @SuppressLint("Range")
            override fun migrate(db: SupportSQLiteDatabase) {
                val cursor = db.query("SELECT * FROM numbers")

                db.execSQL(
                        "CREATE TABLE phones ( " +
                                DbConstants.PHONES_COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
                                DbConstants.PHONES_COLUMN_NAME_CONTACT_ID + " INTEGER, " +
                                DbConstants.PHONES_COLUMN_NAME_NUMBER + " TEXT, " +
                                DbConstants.PHONES_COLUMN_NAME_STRIPPED_NUMBER + " TEXT, " +
                                DbConstants.PHONES_COLUMN_NAME_TYPE + " INTEGER, " +
                                DbConstants.PHONES_COLUMN_NAME_LABEL + " TEXT, " +
                                DbConstants.PHONES_COLUMN_NAME_PIN_ONE + " TEXT, " +
                                DbConstants.PHONES_COLUMN_NAME_PIN_TWO + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.PHONES_COLUMN_NAME_CONTACT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_CONTACTS + "' (" +
                                DbConstants.CONTACTS_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )

                val contentValues = ContentValues()

                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    contentValues.clear()

                    contentValues.put(
                            DbConstants.PHONES_COLUMN_NAME_ID,
                            cursor.getLong(cursor.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_ID))
                    )
                    contentValues.put(
                            DbConstants.PHONES_COLUMN_NAME_CONTACT_ID,
                            cursor.getInt(cursor.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_CONTACT_ID))
                    )
                    contentValues.put(
                            DbConstants.PHONES_COLUMN_NAME_NUMBER,
                            cursor.getString(cursor.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_NUMBER))
                    )
                    contentValues.put(
                            DbConstants.PHONES_COLUMN_NAME_STRIPPED_NUMBER,
                            CallUtil.getStrippedPhoneNumber(
                                    cursor.getString(
                                            cursor.getColumnIndex(
                                                    DbConstants.PHONES_COLUMN_NAME_NUMBER
                                            )
                                    )
                            )
                    )
                    contentValues.put(
                            DbConstants.PHONES_COLUMN_NAME_PIN_ONE,
                            cursor.getString(cursor.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_PIN_ONE))
                    )
                    contentValues.put(
                            DbConstants.PHONES_COLUMN_NAME_PIN_TWO,
                            cursor.getString(cursor.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_PIN_TWO))
                    )

                    val phoneLabel =
                            cursor.getString(cursor.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_LABEL))

                    when (phoneLabel) {
                        "Phone" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.PHONE
                        )
                        "Home Phone" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.HOME_PHONE
                        )
                        "Work Phone" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.WORK_PHONE
                        )
                        "Personal Phone" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.HOME_PHONE
                        )
                        "Mobile Phone" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.MOBILE_PHONE
                        )
                        "Work Mobile Phone" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.WORK_MOBILE_PHONE
                        )
                        "Home Fax" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.HOME_FAX
                        )
                        "Work Fax" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.WORK_FAX
                        )
                        "Main Phone" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.MAIN_PHONE
                        )
                        "Other Phone" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.OTHER_PHONE
                        )
                        "Other Fax" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.HOME_FAX
                        )
                        "Pager" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.PAGER
                        )
                        "Work Pager" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.WORK_PAGER
                        )
                        "Work Extension" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.WORK_EXTENSION
                        )
                        "Conference Number" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.CONFERENCE_PHONE
                        )
                        "Custom Phone" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.CUSTOM_PHONE
                        )
                        "Company Main" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.COMPANY_MAIN
                        )
                        "Assistant" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.ASSISTANT
                        )
                        "Car" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.CAR
                        )
                        "Radio" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.RADIO
                        )
                        "Callback" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.CALLBACK
                        )
                        "ISDN" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.ISDN
                        )
                        "Telex" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.TELEX
                        )
                        "TTY TDD" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.TTY_TDD
                        )
                        "MMS" -> contentValues.put(
                                DbConstants.PHONES_COLUMN_NAME_TYPE,
                                Enums.Contacts.PhoneTypes.MMS
                        )


                        else -> {
                            contentValues.put(
                                    DbConstants.PHONES_COLUMN_NAME_TYPE,
                                    Enums.Contacts.PhoneTypes.CUSTOM_PHONE
                            )
                            contentValues.put(DbConstants.PHONES_COLUMN_NAME_LABEL, phoneLabel)
                        }
                    }

                    db.insert("phones", OnConflictStrategy.REPLACE, contentValues)
                    cursor.moveToNext()
                }

                db.execSQL("DROP TABLE numbers")
                db.execSQL("CREATE INDEX index_phones_contact_id ON phones (contact_id)")
            }
        }

        private var MIGRATION_8_9: Migration = object : Migration(8, 9) {
            @SuppressLint("Range")
            override fun migrate(db: SupportSQLiteDatabase) {
                val cursor = db.query("SELECT * FROM contacts")

                db.execSQL("ALTER TABLE contacts ADD COLUMN sort_name TEXT")
                db.execSQL("ALTER TABLE contacts ADD COLUMN sort_name_first_initial TEXT")
                db.execSQL("ALTER TABLE contacts ADD COLUMN ui_name TEXT")

                val contentValues = ContentValues()
                var namesSet: Boolean
                var fullName: String?

                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    contentValues.clear()
                    namesSet = false

                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_DISPLAY_NAME))
                            ?.let {
                                contentValues.put(DbConstants.CONTACTS_COLUMN_NAME_UI_NAME, it)
                                contentValues.put(DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME, it)
                                contentValues.put(
                                        DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL,
                                        it[0].toString()
                                )
                                namesSet = true
                            }

                    if (!cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME))
                                    .isNullOrEmpty() &&
                            !cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME))
                                    .isNullOrEmpty() &&
                            !namesSet
                    ) {

                        fullName =
                                cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME)) + " " +
                                        cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME))

                        contentValues.put(DbConstants.CONTACTS_COLUMN_NAME_UI_NAME, fullName)
                        contentValues.put(DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME, fullName)
                        contentValues.put(
                                DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL,
                                fullName[0].toString()
                        )
                        namesSet = true
                    }

                    if (!namesSet) {
                        if (!cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME))
                                        .isNullOrEmpty()) {
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME))
                            )
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_UI_NAME,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME))
                            )
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME))[0].toString()
                            )
                            namesSet = true

                        } else if (!cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME))
                                        .isNullOrEmpty()) {
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_UI_NAME,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME))
                            )
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME))
                            )
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME))[0].toString()
                            )
                            namesSet = true

                        } else if (!cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_COMPANY))
                                        .isNullOrEmpty()) {
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_UI_NAME,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_COMPANY))
                            )
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_COMPANY))
                            )
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_COMPANY))[0].toString()
                            )
                            namesSet = true

                        } else if (!cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_JID))
                                        .isNullOrEmpty()) {
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_UI_NAME,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_JID))
                            )
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_JID))
                            )
                            contentValues.put(
                                    DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL,
                                    cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_JID))[0].toString()
                            )
                            namesSet = true

                        }
                    }

                    if (!namesSet) {
                        val phoneCursor = db.query(
                                "SELECT * FROM phones WHERE contact_id = "
                                        + cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_ID))
                        )

                        phoneCursor.let {
                            it.moveToFirst()

                            if (!it.getString(it.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_NUMBER))
                                            .isNullOrEmpty()
                            ) {
                                contentValues.put(
                                        DbConstants.CONTACTS_COLUMN_NAME_UI_NAME,
                                        it.getString(it.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_NUMBER))
                                )
                                contentValues.put(
                                        DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME,
                                        it.getString(it.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_NUMBER))
                                )
                                contentValues.put(
                                        DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL,
                                        it.getString(it.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_NUMBER))
                                                ?.get(0).toString()
                                )
                                namesSet = true

                            }
                        }

                        if (!namesSet) {
                            val emailCursor = db.query(
                                    "SELECT * FROM emails WHERE contact_id = "
                                            + cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_ID))
                            )

                            emailCursor.let {
                                it.moveToFirst()

                                if (!it.getString(it.getColumnIndex(DbConstants.EMAILS_COLUMN_NAME_ADDRESS))
                                                .isNullOrEmpty()
                                ) {
                                    contentValues.put(
                                            DbConstants.CONTACTS_COLUMN_NAME_UI_NAME,
                                            it.getString(it.getColumnIndex(DbConstants.EMAILS_COLUMN_NAME_ADDRESS))
                                    )
                                    contentValues.put(
                                            DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME,
                                            it.getString(it.getColumnIndex(DbConstants.EMAILS_COLUMN_NAME_ADDRESS))
                                    )
                                    contentValues.put(
                                            DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL,
                                            it.getString(it.getColumnIndex(DbConstants.EMAILS_COLUMN_NAME_ADDRESS))
                                                    ?.get(0).toString()
                                    )
                                    namesSet = true

                                }
                            }
                        }
                    }

                    if (namesSet) {
                        db.update(
                                "contacts",
                                OnConflictStrategy.REPLACE,
                                contentValues,
                                "id = " + cursor.getLong(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_ID)),
                                null
                        )
                    }

                    cursor.moveToNext()
                }
            }
        }

        private var MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE UNIQUE INDEX index_user_details_key ON user_details (`key`)")
            }
        }

        private var MIGRATION_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP INDEX index_user_details_key")
                db.execSQL("ALTER TABLE user_details RENAME TO session")
                db.execSQL("CREATE UNIQUE INDEX index_session_key ON session (`key`)")

                db.execSQL("ALTER TABLE contacts RENAME TO temp_contacts")
                db.execSQL(
                        "CREATE TABLE contacts ( " +
                                DbConstants.CONTACTS_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE_ID + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE + " INTEGER, " +
                                DbConstants.CONTACTS_COLUMN_NAME_JID + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_DISPLAY_NAME + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_HIRAGANA_FIRST_NAME + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_HIRAGANA_LAST_NAME + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_TITLE + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_COMPANY + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_IS_FAVORITE + " INTEGER, " +
                                DbConstants.CONTACTS_COLUMN_NAME_GROUP_ID + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_SHORT_JID + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_SUBSCRIPTION_STATE + " INTEGER, " +
                                DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL + " TEXT, " +
                                DbConstants.CONTACTS_COLUMN_NAME_UI_NAME + " TEXT " +
                                ")"
                )
                db.execSQL(
                        "INSERT INTO contacts(" +
                                DbConstants.CONTACTS_COLUMN_NAME_ID + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE_ID + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_JID + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_DISPLAY_NAME + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_HIRAGANA_FIRST_NAME + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_HIRAGANA_LAST_NAME + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_TITLE + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_COMPANY + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_IS_FAVORITE + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_GROUP_ID + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_SHORT_JID + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_SUBSCRIPTION_STATE + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_SORT_NAME_FIRST_INITIAL + ", " +
                                DbConstants.CONTACTS_COLUMN_NAME_UI_NAME + ") " +
                                "SELECT id, contact_type_id, contact_type, jid, display_name, first_name, last_name, hiragana_first_name, hiragana_last_name, title, company, is_favorite, group_id, short_jid, subscription_state, sort_name, sort_name_first_initial, ui_name FROM temp_contacts"
                )
                db.execSQL("DROP TABLE temp_contacts")

                db.execSQL("DROP TABLE vcards")
                db.execSQL(
                        "CREATE TABLE vcards ( " +
                                DbConstants.VCARDS_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.VCARDS_COLUMN_NAME_CONTACT_ID + " INTEGER, " +
                                DbConstants.VCARDS_COLUMN_NAME_PHOTO_DATA + " BLOB, " +
                                DbConstants.VCARDS_COLUMN_NAME_CALL_ID + " TEXT, " +
                                DbConstants.VCARDS_COLUMN_NAME_SIP_URI + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.VCARDS_COLUMN_NAME_CONTACT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_CONTACTS + "' (" +
                                DbConstants.CONTACTS_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL("CREATE INDEX index_vcards_contact_id ON vcards (contact_id)")
            }
        }

        private var MIGRATION_11_12: Migration = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP INDEX index_vcards_contact_id")
                db.execSQL("DROP TABLE vcards")
                db.execSQL(
                        "CREATE TABLE vcards ( " +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "contact_id INTEGER, " +
                                "photo_data BLOB, " +
                                "call_id TEXT, " +
                                "sip_uri TEXT, " +
                                "transaction_id TEXT, " +
                                "FOREIGN KEY (contact_id) " +
                                "REFERENCES 'contacts' (id) ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL("CREATE INDEX index_vcards_contact_id ON vcards (contact_id)")

                db.execSQL("DROP INDEX index_presences_presence_key")

                db.execSQL("ALTER TABLE presences RENAME TO temp_presences")
                db.execSQL(
                        "CREATE TABLE presences ( " +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "contact_id INTEGER, " +
                                "jid TEXT, " +
                                "presence_state INTEGER, " +
                                "presence_type INTEGER, " +
                                "priority INTEGER, " +
                                "status_text TEXT, " +
                                "transaction_id TEXT, " +
                                "FOREIGN KEY (contact_id) " +
                                "REFERENCES 'contacts' (id) ON DELETE CASCADE " +
                                ")"
                )

                db.execSQL("CREATE INDEX index_presences_contact_id ON presences (contact_id)")

                db.execSQL(
                        "INSERT INTO presences(" +
                                "id, " +
                                "contact_id, " +
                                "jid, " +
                                "presence_state, " +
                                "presence_type, " +
                                "priority, " +
                                "status_text) " +
                                "SELECT id, presence_key, presence_jid, presence_state, presence_type, priority, status_text FROM temp_presences"
                )
                db.execSQL("DROP TABLE temp_presences")

                db.execSQL("ALTER TABLE contacts ADD COLUMN transaction_id TEXT")
                db.execSQL("ALTER TABLE phones ADD COLUMN transaction_id TEXT")
                db.execSQL("ALTER TABLE emails ADD COLUMN transaction_id TEXT")
            }
        }

        private var MIGRATION_12_13: Migration = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM messages")
                db.execSQL("ALTER TABLE messages ADD COLUMN chat_with TEXT")
            }
        }

        private var MIGRATION_13_14: Migration = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE messages")
                db.execSQL(
                        "CREATE TABLE messages ( " +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_FROM + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TO + " TEXT, " +
                                "message_body TEXT, " +
                                "message_id TEXT, " +
                                "message_date INTEGER, " +
                                "flag_message_received TEXT, " +
                                "flag_message_system TEXT, " +
                                "flag_message_read INTEGER, " +
                                "message_system_type TEXT, " +
                                "chat_with TEXT " +
                                ")"
                )
            }
        }

        private var MIGRATION_14_15: Migration = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE messages")
                db.execSQL(
                        "CREATE TABLE messages ( " +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_FROM + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TO + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_BODY + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_MESSAGE_ID + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TIMESTAMP + " INTEGER, " +
                                DbConstants.MESSAGES_COLUMN_NAME_IS_SENDER + " INTEGER, " +
                                DbConstants.MESSAGES_COLUMN_NAME_IS_READ + " INTEGER, " +
                                DbConstants.MESSAGES_COLUMN_NAME_GUEST_FIRST + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_GUEST_LAST + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_LANGUAGE + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_PARTICIPANT + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_THREAD_ID + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TYPE + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_CHAT_WITH + " TEXT " +
                                ")"
                )
            }
        }

        private var MIGRATION_16_17: Migration = object : Migration(16, 17) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE messages")
                db.execSQL(
                        "CREATE TABLE messages ( " +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_FROM + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TO + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_BODY + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_MESSAGE_ID + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TIMESTAMP + " INTEGER, " +
                                DbConstants.MESSAGES_COLUMN_NAME_IS_SENDER + " INTEGER, " +
                                DbConstants.MESSAGES_COLUMN_NAME_IS_READ + " INTEGER, " +
                                DbConstants.MESSAGES_COLUMN_NAME_GUEST_FIRST + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_GUEST_LAST + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_LANGUAGE + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_PARTICIPANT + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_THREAD_ID + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TYPE + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_CHAT_WITH + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TRANSACTION_ID + " TEXT " +
                                ")"
                )
            }
        }

        private var MIGRATION_17_18: Migration = object : Migration(17, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE messages")
                db.execSQL(
                        "CREATE TABLE messages ( " +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_FROM + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TO + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_BODY + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_MESSAGE_ID + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TIMESTAMP + " INTEGER, " +
                                DbConstants.MESSAGES_COLUMN_NAME_IS_SENDER + " INTEGER, " +
                                DbConstants.MESSAGES_COLUMN_NAME_IS_READ + " INTEGER, " +
                                DbConstants.MESSAGES_COLUMN_NAME_GUEST_FIRST + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_GUEST_LAST + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_LANGUAGE + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_PARTICIPANT + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_THREAD_ID + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_MEMBERS + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TYPE + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_CHAT_WITH + " TEXT, " +
                                DbConstants.MESSAGES_COLUMN_NAME_TRANSACTION_ID + " TEXT " +
                                ")"
                )
            }
        }

        private var MIGRATION_18_19: Migration = object : Migration(18, 19) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                        "CREATE TABLE group_relations ( " +
                                "contact_id INTEGER NOT NULL, " +
                                "group_id TEXT NOT NULL, " +
                                "transaction_id TEXT, " +
                                "PRIMARY KEY (contact_id, group_id) " +
                                ")"
                )

                db.execSQL(
                        "CREATE TABLE groups ( " +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "group_id TEXT, " +
                                "`name` TEXT, " +
                                "`order` INTEGER, " +
                                "transaction_id TEXT " +
                                ")"
                )
            }
        }

        private var MIGRATION_19_20: Migration = object : Migration(19, 20) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_VOICEMAILS + " ( " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_DURATION + " INTEGER, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_ADDRESS + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_NAME + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_USER_ID + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_TIME + " INTEGER, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_MESSAGE_ID + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_READ + " INTEGER, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_TRANSACTION_ID + " TEXT " +
                                ")"
                )
            }
        }

        private var MIGRATION_20_21: Migration = object : Migration(20, 21) {
            @SuppressLint("Range")
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("ALTER TABLE messages RENAME TO temp_messages")
                db.execSQL(
                        "CREATE TABLE messages ( " +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_FROM} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_TO} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_BODY} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_MESSAGE_ID} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_TIMESTAMP} INTEGER, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_IS_SENDER} INTEGER, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_IS_READ} INTEGER, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_GUEST_FIRST} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_GUEST_LAST} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_LANGUAGE} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_PARTICIPANT} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_THREAD_ID} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_MEMBERS} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_TYPE} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_CHAT_WITH} TEXT, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_SENT_STATUS} INTEGER, " +
                                "${DbConstants.MESSAGES_COLUMN_NAME_TRANSACTION_ID} TEXT " +
                                ")"
                )

                val cursor = db.query("SELECT * FROM temp_messages")

                val contentValues = ContentValues()

                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    contentValues.clear()

                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_ID,
                            cursor.getLong(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_ID))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_FROM,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_FROM))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_TO,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_TO))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_BODY,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_BODY))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_MESSAGE_ID,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_MESSAGE_ID))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_TIMESTAMP,
                            cursor.getInt(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_TIMESTAMP))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_IS_SENDER,
                            cursor.getInt(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_IS_SENDER))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_IS_READ,
                            cursor.getInt(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_IS_READ))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_GUEST_FIRST,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_GUEST_FIRST))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_GUEST_LAST,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_GUEST_LAST))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_LANGUAGE,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_LANGUAGE))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_PARTICIPANT,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_PARTICIPANT))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_THREAD_ID,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_THREAD_ID))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_MEMBERS,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_MEMBERS))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_TYPE,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_TYPE))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_CHAT_WITH,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_CHAT_WITH))
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_SENT_STATUS,
                            Enums.Chats.SentStatus.SUCCESSFUL
                    )
                    contentValues.put(
                            DbConstants.MESSAGES_COLUMN_NAME_TRANSACTION_ID,
                            cursor.getString(cursor.getColumnIndex(DbConstants.MESSAGES_COLUMN_NAME_TRANSACTION_ID))
                    )

                    db.insert("messages", OnConflictStrategy.REPLACE, contentValues)
                    cursor.moveToNext()
                }

                db.execSQL("DROP TABLE temp_messages")
            }
        }

        private var MIGRATION_21_22: Migration = object : Migration(21, 22) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_SMS_MESSAGE)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_PARTICIPANT)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_ATTACHMENTS)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_THREADS)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_SENDER)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_RECIPIENT)

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_SMS_MESSAGE + " ( " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID + " TEXT NOT NULL, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_CHANNEL + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_BODY + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_PREVIEW + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_SENT + " TEXT NOT NULL, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_IS_READ + " INTEGER, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_PRIORITY + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_GROUP_VALUE + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_MESSAGE_STATE + " TEXT " +
                                ")"
                )
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_PARTICIPANT + " ( " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_NAME + " TEXT, " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_EMAIl + " TEXT, " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_PHONE_NUMBER + " TEXT, " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_TEAM_UUID + " TEXT, " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_USER_UUID + " TEXT " +
                                ")"
                )
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_ATTACHMENTS + " ( " +
                                DbConstants.ATTACHMENTS_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.ATTACHMENTS_COLUMN_TYPE + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_FILE_NAME + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_REFERENCE + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_MESSAGE_ID + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.ATTACHMENTS_COLUMN_MESSAGE_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_THREADS + " ( " +
                                DbConstants.THREAD_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.THREAD_COLUMN_NAME_THREAD_ID + " TEXT, " +
                                DbConstants.THREAD_COLUMN_NAME_MESSAGE_ID + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.THREAD_COLUMN_NAME_MESSAGE_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_SENDER + " ( " +
                                DbConstants.SENDER_COLUMN_NAME_MESSAGE_ID + " INTEGER NOT NULL, " +
                                DbConstants.SENDER_COLUMN_NAME_PARTICIPANT_ID + " INTEGER NOT NULL, " +
                                "PRIMARY KEY ( " + DbConstants.SENDER_COLUMN_NAME_MESSAGE_ID + ", " + DbConstants.SENDER_COLUMN_NAME_PARTICIPANT_ID + ")" +
                                "FOREIGN KEY (" + DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE , " +
                                "FOREIGN KEY (" + DbConstants.SENDER_COLUMN_NAME_PARTICIPANT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_PARTICIPANT + "' (" +
                                DbConstants.PARTICIPANT_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_RECIPIENT + " ( " +
                                DbConstants.RECIPIENT_COLUMN_NAME_MESSAGE_ID + " INTEGER NOT NULL, " +
                                DbConstants.RECIPIENT_COLUMN_NAME_PARTICIPANT_ID + " INTEGER NOT NULL, " +
                                "PRIMARY KEY ( " + DbConstants.RECIPIENT_COLUMN_NAME_MESSAGE_ID + ", " + DbConstants.RECIPIENT_COLUMN_NAME_PARTICIPANT_ID + " )" +
                                "FOREIGN KEY (" + DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE , " +
                                "FOREIGN KEY (" + DbConstants.RECIPIENT_COLUMN_NAME_PARTICIPANT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_PARTICIPANT + "' (" +
                                DbConstants.PARTICIPANT_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
            }
        }

        private var MIGRATION_22_23: Migration = object : Migration(22, 23) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_SMS_MESSAGE)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_PARTICIPANT)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_ATTACHMENTS)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_THREADS)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_SENDER)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_RECIPIENT)




                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_SMS_MESSAGE + " ( " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID + " TEXT NOT NULL, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_CHANNEL + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_BODY + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_PREVIEW + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_SENT + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_IS_READ + " INTEGER, " +
                                DbConstants.SMS_MESSAGE_COLUMN_IS_SENDER + " INTEGER, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_PRIORITY + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_GROUP_VALUE + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_MESSAGE_STATE + " TEXT " +
                                ")"
                )

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_PARTICIPANT + " ( " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_NAME + " TEXT, " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_EMAIl + " TEXT, " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_PHONE_NUMBER + " TEXT, " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_TEAM_UUID + " TEXT, " +
                                DbConstants.PARTICIPANT_COLUMN_NAME_USER_UUID + " TEXT " +
                                ")"
                )
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_ATTACHMENTS + " ( " +
                                DbConstants.ATTACHMENTS_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.ATTACHMENTS_COLUMN_TYPE + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_FILE_NAME + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_REFERENCE + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_MESSAGE_ID + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.ATTACHMENTS_COLUMN_MESSAGE_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_THREADS + " ( " +
                                DbConstants.THREAD_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.THREAD_COLUMN_NAME_THREAD_ID + " TEXT, " +
                                DbConstants.THREAD_COLUMN_NAME_MESSAGE_ID + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.THREAD_COLUMN_NAME_MESSAGE_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_SENDER + " ( " +
                                DbConstants.SENDER_COLUMN_NAME_MESSAGE_ID + " INTEGER NOT NULL, " +
                                DbConstants.SENDER_COLUMN_NAME_PARTICIPANT_ID + " INTEGER NOT NULL, " +
                                "PRIMARY KEY ( " + DbConstants.SENDER_COLUMN_NAME_MESSAGE_ID + ", " + DbConstants.SENDER_COLUMN_NAME_PARTICIPANT_ID + ")" +
                                "FOREIGN KEY (" + DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE , " +
                                "FOREIGN KEY (" + DbConstants.SENDER_COLUMN_NAME_PARTICIPANT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_PARTICIPANT + "' (" +
                                DbConstants.PARTICIPANT_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_RECIPIENT + " ( " +
                                DbConstants.RECIPIENT_COLUMN_NAME_MESSAGE_ID + " INTEGER NOT NULL, " +
                                DbConstants.RECIPIENT_COLUMN_NAME_PARTICIPANT_ID + " INTEGER NOT NULL, " +
                                "PRIMARY KEY ( " + DbConstants.RECIPIENT_COLUMN_NAME_MESSAGE_ID + ", " + DbConstants.RECIPIENT_COLUMN_NAME_PARTICIPANT_ID + " )" +
                                "FOREIGN KEY (" + DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE , " +
                                "FOREIGN KEY (" + DbConstants.RECIPIENT_COLUMN_NAME_PARTICIPANT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_PARTICIPANT + "' (" +
                                DbConstants.PARTICIPANT_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
            }
        }

        private var MIGRATION_23_24: Migration = object : Migration(23, 24) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_SMS_MESSAGE)

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_SMS_MESSAGE + " ( " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID + " TEXT NOT NULL, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_CHANNEL + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_BODY + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_PREVIEW + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_SENT + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_IS_READ + " INTEGER, " +
                                DbConstants.SMS_MESSAGE_COLUMN_IS_SENDER + " INTEGER, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_PRIORITY + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_GROUP_VALUE + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_MESSAGE_STATE + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_SENT_STATUS + " INTEGER " +
                                ")"
                )
            }
        }

        private var MIGRATION_24_25: Migration = object : Migration(24, 25) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_SMS_MESSAGE)
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_MESSAGE_STATE)

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_SMS_MESSAGE + " ( " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID + " TEXT NOT NULL, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_CHANNEL + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_BODY + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_PREVIEW + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_SENT + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_IS_SENDER + " INTEGER, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_PRIORITY + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_GROUP_VALUE + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_SENT_STATUS + " INTEGER " +
                                ")"
                )

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_MESSAGE_STATE + " ( " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_MESSAGE_ID + " TEXT, " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_PRIORITY + " TEXT, " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_READ_STATUS + " TEXT, " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_SMS_ID + " INTEGER NOT NULL, " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_DELETED + " INTEGER, " +
                                "FOREIGN KEY (" + DbConstants.MESSAGE_STATE_COLUMN_NAME_SMS_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )


            }
        }


        private var MIGRATION_25_26: Migration = object : Migration(25, 26) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_SMS_MESSAGE)

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_SMS_MESSAGE + " ( " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_MESSAGE_ID + " TEXT NOT NULL, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_CHANNEL + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_BODY + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_PREVIEW + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_SENT + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_IS_SENDER + " INTEGER, " +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_PRIORITY + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_GROUP_VALUE + " TEXT, " +
                                DbConstants.SMS_MESSAGE_COLUMN_SENT_STATUS + " INTEGER " +
                                ")"
                )

                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_ATTACHMENTS)

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_ATTACHMENTS + " ( " +
                                DbConstants.ATTACHMENTS_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.ATTACHMENTS_COLUMN_LINK + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_CONTENT_TYPE + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_SMS_ID + " INTEGER, " +
                                DbConstants.ATTACHMENTS_COLUMN_FILE_NAME + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.ATTACHMENTS_COLUMN_SMS_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )

            }
        }

        private var MIGRATION_26_27: Migration = object : Migration(26, 27) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_ATTACHMENTS)

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_ATTACHMENTS + " ( " +
                                DbConstants.ATTACHMENTS_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.ATTACHMENTS_COLUMN_LINK + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_CONTENT_TYPE + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_SMS_ID + " INTEGER, " +
                                DbConstants.ATTACHMENTS_COLUMN_FILE_NAME + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_CONTENT_DATA + " BLOB, " +
                                "FOREIGN KEY (" + DbConstants.ATTACHMENTS_COLUMN_SMS_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )

            }
        }

        private var MIGRATION_27_28: Migration = object : Migration(27, 28) {
            override fun migrate(db: SupportSQLiteDatabase) {


                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_ATTACHMENTS)

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_ATTACHMENTS + " ( " +
                                DbConstants.ATTACHMENTS_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.ATTACHMENTS_COLUMN_LINK + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_CONTENT_TYPE + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_SMS_ID + " INTEGER, " +
                                DbConstants.ATTACHMENTS_COLUMN_FILE_NAME + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_THUMB_NAIL_LINK + " TEXT, " +
                                DbConstants.ATTACHMENTS_COLUMN_CONTENT_DATA + " BLOB, " +
                                DbConstants.ATTACHMENTS_COLUMN_UPLOADED_DATE + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.ATTACHMENTS_COLUMN_SMS_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )

            }
        }

        private var MIGRATION_28_29: Migration = object : Migration(28, 29) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_MESSAGE_STATE)

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_MESSAGE_STATE + " ( " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_MESSAGE_ID + " TEXT, " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_PRIORITY + " TEXT, " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_READ_STATUS + " TEXT, " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_SMS_ID + " INTEGER NOT NULL PRIMARY KEY, " +
                                DbConstants.MESSAGE_STATE_COLUMN_NAME_DELETED + " INTEGER, " +
                                "FOREIGN KEY (" + DbConstants.MESSAGE_STATE_COLUMN_NAME_SMS_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                                DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )


            }
        }

        private var MIGRATION_29_30: Migration = object : Migration(29, 30) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE " + DbConstants.TABLE_NAME_ATTACHMENTS + " ADD COLUMN " + DbConstants.ATTACHMENTS_COLUMN_FILE_DURATION + " INTEGER ")

            }
        }

        private var MIGRATION_30_31: Migration = object : Migration(30, 31) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS ${DbConstants.TABLE_NAME_VOICEMAILS}")
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_VOICEMAILS + " ( " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_DURATION + " INTEGER, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_ADDRESS + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_NAME + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_USER_ID + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_TIME + " INTEGER, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_MESSAGE_ID + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_READ + " INTEGER, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_TRANSCRIPTION + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_RATING + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_TRANSACTION_ID + " TEXT " +
                                ")"
                )
            }
        }

        private var MIGRATION_31_32: Migration = object : Migration(31, 32) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_VOICEMAILS)
                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_VOICEMAILS + " ( " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_DURATION + " INTEGER, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_ADDRESS + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_NAME + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_USER_ID + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_TIME + " INTEGER, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_MESSAGE_ID + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_READ + " INTEGER, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_TRANSCRIPTION + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_RATING + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_ACTUAL_ID + " TEXT, " +
                                DbConstants.VOICEMAIL_COLUMN_NAME_TRANSACTION_ID + " TEXT " +
                                ")"
                )
            }
        }

        private var MIGRATION_32_33: Migration = object : Migration(32, 33) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_LOGGING)

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_LOGGING + " ( " +
                                DbConstants.LOGGING_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.LOGGING_COLUMN_LEVEL + " TEXT, " +
                                DbConstants.LOGGING_COLUMN_MESSAGE + " TEXT, " +
                                DbConstants.LOGGING_COLUMN_TIME + " INTEGER, " +
                                DbConstants.LOGGING_COLUMN_LOCATION + " TEXT " +
                                DbConstants.LOGGING_COLUMN_INTERNET_STATUS + " TEXT " +
                                ")"
                )
            }
        }

        private var MIGRATION_33_34: Migration = object : Migration(33, 34) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE contacts ADD COLUMN ${DbConstants.CONTACTS_COLUMN_NAME_WEBSITE} TEXT")
                db.execSQL("ALTER TABLE contacts ADD COLUMN ${DbConstants.CONTACTS_COLUMN_NAME_DEPARTMENT} TEXT")
                db.execSQL("ALTER TABLE contacts ADD COLUMN ${DbConstants.CONTACTS_COLUMN_NAME_DESCRIPTION} TEXT")
                db.execSQL("ALTER TABLE contacts ADD COLUMN ${DbConstants.CONTACTS_COLUMN_NAME_CREATED_BY} TEXT")
                db.execSQL("ALTER TABLE contacts ADD COLUMN ${DbConstants.CONTACTS_COLUMN_NAME_LAST_MODIFIED_BY} TEXT")
                db.execSQL("ALTER TABLE contacts ADD COLUMN ${DbConstants.CONTACTS_COLUMN_NAME_LAST_MODIFIED_ON} TEXT")

                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_POSTAL_ADDRESSES} ADD COLUMN ${DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_TYPE} INTEGER")
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_POSTAL_ADDRESSES} ADD COLUMN ${DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_TRANSACTION_ID} TEXT")

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_DATES + " ( " +
                                DbConstants.DATE_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.DATE_COLUMN_NAME_CONTACT_ID + " INTEGER, " +
                                DbConstants.DATE_COLUMN_NAME_DATE + " TEXT, " +
                                DbConstants.DATE_COLUMN_NAME_TYPE + " INTEGER, " +
                                DbConstants.DATE_COLUMN_NAME_TRANSACTION_ID + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.DATE_COLUMN_NAME_CONTACT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_CONTACTS + "' (" +
                                DbConstants.CONTACTS_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL("CREATE INDEX index_dates_contact_id ON ${DbConstants.TABLE_NAME_DATES} (${DbConstants.DATE_COLUMN_NAME_CONTACT_ID})")

                db.execSQL(
                        "CREATE TABLE " + DbConstants.TABLE_NAME_SOCIAL_MEDIA_ACCOUNTS + " ( " +
                                DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_CONTACT_ID + " INTEGER, " +
                                DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_LINK + " TEXT, " +
                                DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_TYPE + " INTEGER, " +
                                DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_TRANSACTION_ID + " TEXT, " +
                                "FOREIGN KEY (" + DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_CONTACT_ID + ") " +
                                "REFERENCES '" + DbConstants.TABLE_NAME_CONTACTS + "' (" +
                                DbConstants.CONTACTS_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL("CREATE INDEX index_social_media_accounts_contact_id ON ${DbConstants.TABLE_NAME_SOCIAL_MEDIA_ACCOUNTS} (${DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_CONTACT_ID})")
            }
        }

        private var MIGRATION_34_35: Migration = object : Migration(34, 35) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NAME_LOGGING)

                db.execSQL("CREATE TABLE " + DbConstants.TABLE_NAME_LOGGING + " ( " +
                        DbConstants.LOGGING_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DbConstants.LOGGING_COLUMN_LEVEL + " TEXT, " +
                        DbConstants.LOGGING_COLUMN_MESSAGE + " TEXT, " +
                        DbConstants.LOGGING_COLUMN_TIME + " INTEGER, " +
                        DbConstants.LOGGING_COLUMN_LOCATION + " TEXT, " +
                        DbConstants.LOGGING_COLUMN_INTERNET_STATUS + " TEXT " +
                        ")")
            }
        }

        private var MIGRATION_35_36: Migration = object : Migration(35, 36) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS ${DbConstants.TABLE_NAME_CALL_LOG_ENTRIES}")
                db.execSQL("""CREATE TABLE ${DbConstants.TABLE_NAME_CALL_LOG_ENTRIES} (
                    ${DbConstants.CALL_LOGS_COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT, 
                    ${DbConstants.CALL_LOGS_COLUMN_NAME_CALL_LOG_ID} TEXT, 
                    ${DbConstants.CALL_LOGS_COLUMN_NAME_DISPLAY_NAME} TEXT, 
                    ${DbConstants.CALL_LOGS_COLUMN_NAME_CALL_DATE_TIME} INTEGER, 
                    ${DbConstants.CALL_LOGS_COLUMN_NAME_COUNTRY_CODE} TEXT, 
                    ${DbConstants.CALL_LOGS_COLUMN_NAME_PHONE_NUMBER} TEXT, 
                    ${DbConstants.CALL_LOGS_COLUMN_NAME_CALL_TYPE} TEXT, 
                    ${DbConstants.CALL_LOGS_COLUMN_NAME_IS_READ} INTEGER)"""
                )
                db.execSQL("CREATE UNIQUE INDEX index_calllogs_call_log_id ON ${DbConstants.TABLE_NAME_CALL_LOG_ENTRIES} (${DbConstants.CALL_LOGS_COLUMN_NAME_CALL_LOG_ID})")
            }
        }

        private var MIGRATION_36_37: Migration = object : Migration(36, 37) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS ${DbConstants.TABLE_NAME_PRESENCES}")
                db.execSQL(
                        "CREATE TABLE ${DbConstants.TABLE_NAME_PRESENCES} ( " +
                                "${DbConstants.PRESENCES_COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "${DbConstants.PRESENCES_COLUMN_NAME_CONTACT_ID} INTEGER, " +
                                "${DbConstants.PRESENCES_COLUMN_NAME_USER_ID} TEXT, " +
                                "${DbConstants.PRESENCES_COLUMN_NAME_JID} TEXT, " +
                                "${DbConstants.PRESENCES_COLUMN_NAME_PRESENCE_STATE} INTEGER NOT NULL, " +
                                "${DbConstants.PRESENCES_COLUMN_NAME_PRESENCE_TYPE} INTEGER, " +
                                "${DbConstants.PRESENCES_COLUMN_NAME_PRIORITY} INTEGER, " +
                                "${DbConstants.PRESENCES_COLUMN_NAME_STATUS_TEXT} TEXT, " +
                                "${DbConstants.PRESENCES_COLUMN_NAME_TRANSACTION_ID} TEXT, " +
                                "FOREIGN KEY (${DbConstants.PRESENCES_COLUMN_NAME_CONTACT_ID}) " +
                                "REFERENCES '${DbConstants.TABLE_NAME_CONTACTS}' (${DbConstants.PRESENCES_COLUMN_NAME_ID}) ON DELETE CASCADE " +
                                ")"
                )
                db.execSQL("CREATE INDEX index_presences_contact_id ON ${DbConstants.TABLE_NAME_PRESENCES} (${DbConstants.PRESENCES_COLUMN_NAME_CONTACT_ID})")
            }
        }

        private var MIGRATION_37_38: Migration = object : Migration(37, 38) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE contacts ADD COLUMN ${DbConstants.CONTACTS_COLUMN_NAME_LOOKUP_KEY} TEXT")
            }
        }

        private var MIGRATION_38_39: Migration = object : Migration(38, 39) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE " + DbConstants.TABLE_NAME_CALL_LOG_ENTRIES + " ADD COLUMN " + DbConstants.CALL_LOGS_COLUMN_NAME_FORMATTED_PHONE_NUMBER + " TEXT ")

            }
        }

        private var MIGRATION_39_40: Migration = object : Migration(39, 40) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM " + DbConstants.TABLE_NAME_VOICEMAILS)
                db.execSQL("ALTER TABLE " + DbConstants.TABLE_NAME_VOICEMAILS + " ADD COLUMN " + DbConstants.VOICEMAIL_COLUMN_NAME_FORMATTED_PHONE_NUMBER + " TEXT ")
            }
        }

        private var MIGRATION_40_41: Migration = object : Migration(40, 41) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_CALL_LOG_ENTRIES} ADD COLUMN ${DbConstants.CALL_LOGS_COLUMN_NAME_CONTACT_ID} TEXT ")
            }
        }

        private var MIGRATION_41_42: Migration = object : Migration(41, 42) {
            @SuppressLint("Range")
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_CONTACTS} ADD COLUMN ${DbConstants.CONTACTS_COLUMN_NAME_SORT_GROUP} INTEGER ")

                val cursor = db.query("SELECT * FROM contacts")
                var sortGroup: Int
                var contactType: Int
                var firstName: String?
                var lastName: String?
                var companyName: String?
                var emailCount = 0

                if (cursor.moveToFirst()) {

                    while (!cursor.isAfterLast) {
                        cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_DISPLAY_NAME))?.let {

                            firstName = cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME))
                            lastName = cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME))
                            companyName = cursor.getString(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_COMPANY))
                            contactType = cursor.getInt(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE))

                            val emailCursor = db.query(
                                    "SELECT * FROM emails WHERE contact_id = "
                                            + cursor.getLong(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_ID))
                            )
                            if (emailCursor.moveToFirst()) {
                                if (!emailCursor.getString(emailCursor.getColumnIndex(DbConstants.EMAILS_COLUMN_NAME_ADDRESS))
                                                .isNullOrEmpty()
                                ) {
                                    emailCount++
                                }
                                emailCursor.moveToNext()
                            }

                            sortGroup = if (contactType == Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW || contactType == Enums.Contacts.ContactTypes.CONNECT_TEAM || contactType == Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS) {
                                4
                            } else if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && it.matches(Regex(("^([0-9]*(\\s)*(\\*)*(#)*(\\+)*(\\()*(\\))*(\\-)*)+$"))) && !it.equals("Unknown", ignoreCase = true)) {
                                2
                            } else if (!TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName) || !TextUtils.isEmpty(companyName) || emailCount > 0) {
                                1
                            } else if (it.equals("Unknown", ignoreCase = true)) {
                                3
                            } else {
                                5
                            }

                            db.execSQL("UPDATE " + DbConstants.TABLE_NAME_CONTACTS + " SET " + DbConstants.CONTACTS_COLUMN_NAME_SORT_GROUP + "= " + sortGroup + " WHERE " + DbConstants.CONTACTS_COLUMN_NAME_ID + "= " + cursor.getLong(cursor.getColumnIndex(DbConstants.CONTACTS_COLUMN_NAME_ID)))
                        }
                        cursor.moveToNext()
                    }
                }
            }
        }

        private var MIGRATION_42_43: Migration = object : Migration(42, 43) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_SMS_MESSAGE} ADD COLUMN ${DbConstants.SMS_MESSAGE_COLUMN_NAME_GROUP_ID} TEXT")

                db.execSQL(
                    "CREATE TABLE " + DbConstants.TABLE_NAME_SMS_TEAM + " ( " +
                            DbConstants.SMS_TEAM_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            DbConstants.SMS_TEAM_COLUMN_NAME_TEAM_ID + " TEXT, " +
                            DbConstants.SMS_TEAM_COLUMN_NAME_TEAM_NAME + " TEXT, " +
                            DbConstants.SMS_TEAM_COLUMN_NAME_TEAM_PHONE_NUMBER + " TEXT " +
                            ")"
                )

                db.execSQL(
                    "CREATE TABLE " + DbConstants.TABLE_NAME_SMS_TEAM_RELATION + " ( " +
                            DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_MESSAGE_ID + " INTEGER NOT NULL, " +
                            DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_TEAM_ID + " INTEGER NOT NULL, " +
                            "PRIMARY KEY ( " + DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_MESSAGE_ID + ", " +
                            DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_TEAM_ID + " )" +
                            "FOREIGN KEY (" + DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_MESSAGE_ID + ") " +
                            "REFERENCES '" + DbConstants.TABLE_NAME_SMS_MESSAGE + "' (" +
                            DbConstants.SMS_MESSAGE_COLUMN_NAME_ID + ") ON DELETE CASCADE , " +
                            "FOREIGN KEY (" + DbConstants.SMS_TEAM_RELATION_COLUMN_NAME_TEAM_ID + ") " +
                            "REFERENCES '" + DbConstants.TABLE_NAME_SMS_TEAM + "' (" +
                            DbConstants.SMS_TEAM_COLUMN_NAME_ID + ") ON DELETE CASCADE " +
                            ")"
                )
            }
        }

        private var MIGRATION_43_44: Migration = object : Migration(43, 44) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_PARTICIPANT} ADD COLUMN ${DbConstants.PARTICIPANT_COLUMN_NAME_GROUP_ID} TEXT")
            }
        }

        private var MIGRATION_44_45: Migration = object : Migration(44, 45) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_CALL_LOG_ENTRIES} ADD COLUMN ${DbConstants.CALL_LOGS_COLUMN_NAME_PAGE_NUMBER} INTEGER")
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_VOICEMAILS} ADD COLUMN ${DbConstants.VOICEMAIL_COLUMN_NAME_PAGE_NUMBER} INTEGER")
                db.execSQL("CREATE UNIQUE INDEX index_voicemails_message_id ON ${DbConstants.TABLE_NAME_VOICEMAILS} (${DbConstants.VOICEMAIL_COLUMN_NAME_ACTUAL_ID})")
            }
        }

        private var MIGRATION_45_46: Migration = object : Migration(45,46) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE ${DbConstants.TABLE_NAME_MEETINGS} (" +
                            "${DbConstants.MEETING_COLUMN_NAME_CALENDAR_ID} INTEGER NOT NULL, " +
                            "${DbConstants.MEETING_COLUMN_NAME_NAME} TEXT NOT NULL, " +
                            "${DbConstants.MEETING_COLUMN_NAME_START_TIME} INTEGER NOT NULL, " +
                            "${DbConstants.MEETING_COLUMN_NAME_CREATED_BY} TEXT NOT NULL, " +
                            "${DbConstants.MEETING_COLUMN_NAME_MEETING_INFO} TEXT NOT NULL, " +
                            "PRIMARY KEY(" +
                            "${DbConstants.MEETING_COLUMN_NAME_CALENDAR_ID}, " +
                            "${DbConstants.MEETING_COLUMN_NAME_NAME}, " +
                            "${DbConstants.MEETING_COLUMN_NAME_START_TIME}, " +
                            "${DbConstants.MEETING_COLUMN_NAME_CREATED_BY}))"
                )
            }
        }

        private var MIGRATION_46_47: Migration = object : Migration(46, 47) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_PRESENCES} ADD COLUMN ${DbConstants.PRESENCES_COLUMN_NAME_STATUS_EXPIRY_TIME} TEXT")
            }
        }

        private var MIGRATION_47_48: Migration = object : Migration(47, 48) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE ${DbConstants.TABLE_NAME_SCHEDULES} ( " +
                            "${DbConstants.SCHEDULES_COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "${DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID} TEXT, " +
                            "${DbConstants.SCHEDULES_COLUMN_NAME_IS_24_7} INTEGER, " +
                            "${DbConstants.SCHEDULES_COLUMN_NAME_MON_TO_FRI} INTEGER, " +
                            "${DbConstants.SCHEDULES_COLUMN_NAME_NAME} TEXT, " +
                            "${DbConstants.SCHEDULES_COLUMN_NAME_OLD_SCHEDULE_NAME} TEXT, " +
                            "${DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_LEVEL} TEXT," +
                            "${DbConstants.SCHEDULES_COLUMN_NAME_PAGE_NUMBER} INTEGER )")
                db.execSQL("CREATE UNIQUE INDEX index_schedules_schedule_id ON ${DbConstants.TABLE_NAME_SCHEDULES} (${DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID})")

                db.execSQL(
                    "CREATE TABLE ${DbConstants.TABLE_NAME_WORKING_HOURS} ( " +
                            "${DbConstants.WORKING_HOURS_COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "${DbConstants.WORKING_HOURS_COLUMN_NAME_SCHEDULE_ID} TEXT NOT NULL, " +
                            "${DbConstants.WORKING_HOURS_COLUMN_NAME_DAY} TEXT, " +
                            "${DbConstants.WORKING_HOURS_COLUMN_NAME_END} TEXT, " +
                            "${DbConstants.WORKING_HOURS_COLUMN_NAME_START} TEXT, " +
                            "FOREIGN KEY (${DbConstants.WORKING_HOURS_COLUMN_NAME_SCHEDULE_ID}) " +
                            "REFERENCES '${DbConstants.TABLE_NAME_SCHEDULES}' (" +
                            "${DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID}) ON DELETE CASCADE )")
            }
        }

        private var MIGRATION_48_49: Migration = object : Migration(48, 49) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_SCHEDULES} ADD COLUMN ${DbConstants.SCHEDULES_COLUMN_NAME_IS_DND_SCHEDULE} INTEGER DEFAULT 0 NOT NULL")
            }
        }

        private var MIGRATION_49_50: Migration = object : Migration(49, 50) {
            @SuppressLint("Range")
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_WORKING_HOURS} ADD COLUMN ${DbConstants.WORKING_HOURS_COLUMN_NAME_DAY_ID} TEXT DEFAULT '' NOT NULL")

                val cursor = db.query("SELECT * FROM ${DbConstants.TABLE_NAME_WORKING_HOURS}")

                if (cursor.moveToFirst()) {

                    while (!cursor.isAfterLast) {
                        cursor.getString(cursor.getColumnIndex(DbConstants.WORKING_HOURS_COLUMN_NAME_SCHEDULE_ID))?.let { scheduleId ->
                            cursor.getString(cursor.getColumnIndex(DbConstants.WORKING_HOURS_COLUMN_NAME_DAY))?.let { dayName ->

                                db.execSQL("UPDATE " + DbConstants.TABLE_NAME_WORKING_HOURS +
                                        " SET " + DbConstants.WORKING_HOURS_COLUMN_NAME_DAY_ID + " = \"${dayName}${scheduleId}\"" +
                                        " WHERE " + DbConstants.WORKING_HOURS_COLUMN_NAME_ID + " = " + cursor.getLong(cursor.getColumnIndex(DbConstants.WORKING_HOURS_COLUMN_NAME_ID)))

                            }
                        }

                        cursor.moveToNext()
                    }
                }

                db.execSQL("CREATE UNIQUE INDEX index_working_hours_day_id ON ${DbConstants.TABLE_NAME_WORKING_HOURS} (${DbConstants.WORKING_HOURS_COLUMN_NAME_DAY_ID})")

                db.execSQL(
                    "CREATE TABLE ${DbConstants.TABLE_NAME_WORKING_HOUR_BREAKS} ( " +
                            "${DbConstants.WORKING_HOUR_BREAKS_COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "${DbConstants.WORKING_HOUR_BREAKS_COLUMN_NAME_DAY_ID} TEXT NOT NULL, " +
                            "${DbConstants.WORKING_HOUR_BREAKS_COLUMN_NAME_NAME} TEXT, " +
                            "${DbConstants.WORKING_HOUR_BREAKS_COLUMN_START} TEXT, " +
                            "${DbConstants.WORKING_HOUR_BREAKS_COLUMN_END} TEXT, " +
                            "FOREIGN KEY (${DbConstants.WORKING_HOUR_BREAKS_COLUMN_NAME_DAY_ID}) " +
                            "REFERENCES '${DbConstants.TABLE_NAME_WORKING_HOURS}' (" +
                            "${DbConstants.WORKING_HOURS_COLUMN_NAME_DAY_ID}) ON DELETE CASCADE )")
            }
        }

        private var MIGRATION_50_51: Migration = object : Migration(50, 51) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE ${DbConstants.TABLE_NAME_HOLIDAYS} ( " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_SCHEDULE_ID} TEXT NOT NULL, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_DAY} INTEGER, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_DAY_OF_WEEK} TEXT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_START_DATE} TEXT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_END_DATE} TEXT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_START_HOUR} TEXT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_END_HOUR} TEXT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_TYPE} TEXT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_MONTH} TEXT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_NAME} TEXT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_OCCURRENCE} TEXT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_RECURRENCE_RANGE_END_DATE} TEXT, " +
                            "${DbConstants.HOLIDAYS_COLUMN_NAME_RECURRENCE_RANGE_NUMBER} TEXT, " +
                            "FOREIGN KEY (${DbConstants.HOLIDAYS_COLUMN_NAME_SCHEDULE_ID}) " +
                            "REFERENCES '${DbConstants.TABLE_NAME_SCHEDULES}' (" +
                            "${DbConstants.SCHEDULES_COLUMN_NAME_SCHEDULE_ID}) ON DELETE CASCADE )")
            }
        }

        private var MIGRATION_51_52: Migration = object : Migration(51, 52) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_SMS_MESSAGE} ADD COLUMN ${DbConstants.SMS_MESSAGE_COLUMN_NAME_CONVERSATION_ID} TEXT")
            }
        }

        private var MIGRATION_52_53: Migration = object : Migration(52, 53) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_PRESENCES} ADD COLUMN ${DbConstants.PRESENCES_COLUMN_NAME_IN_CALL} INTEGER")
            }
        }

        private var MIGRATION_53_54: Migration = object : Migration(53, 54) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE " + DbConstants.TABLE_NAME_CALL_LOG_ENTRIES + " ADD COLUMN " + DbConstants.CALL_LOGS_COLUMN_NAME_CALL_DURATION + " INTEGER ")
            }
        }

        private var MIGRATION_54_55: Migration = object : Migration(54, 55) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE " + DbConstants.TABLE_NAME_SMS_TEAM + " ADD COLUMN " + DbConstants.SMS_TEAM_COLUMN_NAME_TEAM_LEGACY_ID + " TEXT ")
            }
        }
        //MIGRATION_54_55 RELEASED IN 26.4.0

        private var MIGRATION_55_56: Migration = object : Migration(55, 56) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS ${DbConstants.TABLE_NAME_CONTACTS_RECENT}")

                db.execSQL(
                    "CREATE TABLE ${DbConstants.TABLE_NAME_CONTACTS_RECENT} ( " +
                            "${DbConstants.CONTACTS_COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "${DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE_ID} TEXT, " +
                            "${DbConstants.CONTACTS_COLUMN_NAME_TRANSACTION_ID} TEXT " +
                            ")"
                )
            }
        }

        //MIGRATION_54_55 RELEASED IN 26.5.0

        private var MIGRATION_56_57: Migration = object : Migration(56, 57) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE " + DbConstants.TABLE_NAME_CONTACTS + " ADD COLUMN " + DbConstants.CONTACTS_COLUMN_NAME_ALIASES + " TEXT ")
            }
        }

        private var MIGRATION_57_58: Migration = object : Migration(57,58){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE " + DbConstants.TABLE_NAME_VOICEMAILS + " ADD COLUMN " + DbConstants.VOICEMAIL_COLUMN_NAME_CALLER_ID + " TEXT ")
            }
        }

        private var MIGRATION_58_59: Migration = object : Migration(58,59){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE " + DbConstants.TABLE_NAME_CALL_LOG_ENTRIES + " ADD COLUMN " + DbConstants.CALL_LOGS_COLUMN_NAME_CALL_START_TIME + " TEXT ")
            }
        }

        private var MIGRATION_59_60: Migration = object : Migration(59, 60) {
            @SuppressLint("Range")
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_PHONES} ADD COLUMN ${DbConstants.PHONES_COLUMN_NAME_EXTENSION} TEXT")

                val cursor = db.query("SELECT * FROM ${DbConstants.TABLE_NAME_PHONES}")

                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast) {
                        cursor.getString(cursor.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_NUMBER))?.let { number ->
                            if (number.contains("x")) {
                                number.split("x").lastOrNull()?.let { extension ->
                                    db.execSQL("UPDATE " + DbConstants.TABLE_NAME_PHONES +
                                            " SET " + DbConstants.PHONES_COLUMN_NAME_EXTENSION + " = \"$extension\"" +
                                            " WHERE " + DbConstants.PHONES_COLUMN_NAME_ID + " = " + cursor.getLong(cursor.getColumnIndex(DbConstants.PHONES_COLUMN_NAME_ID)))
                                }
                            }
                        }

                        cursor.moveToNext()
                    }
                }
            }
        }
    }
}