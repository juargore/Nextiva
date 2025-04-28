/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.features.rooms.db

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.db.converters.RoomConverters
import com.nextiva.nextivaapp.android.features.rooms.model.DbChatMessage
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom

@Database(
        entities = [DbRoom::class, DbChatMessage::class],
        version = 4,
        exportSchema = false
)
@TypeConverters(RoomsConverters::class, RoomConverters::class)
abstract class RoomsDatabase : RoomDatabase() {

    abstract fun roomDao(): RoomDao

    companion object {

        private var INSTANCE: RoomsDatabase? = null

        fun getRoomsDatabase(context: Context): RoomsDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<RoomsDatabase>(
                        context.applicationContext,
                        RoomsDatabase::class.java,
                        DbConstants.DATABASE_NAME_ROOMS
                )
                        .addMigrations(
                                MIGRATION_1_2,
                                MIGRATION_2_3,
                                MIGRATION_3_4,
                        )
                        .fallbackToDestructiveMigration()
                        .build()
            }

            return INSTANCE!!
        }

        var MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    database.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_CHAT_MESSAGES} ADD COLUMN ${DbConstants.CHAT_MESSAGE_COLUMN_NAME_ATTACHMENTS} TEXT")
                } catch (e: SQLiteException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        var MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    database.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_ROOMS} ADD COLUMN ${DbConstants.ROOM_COLUMN_NAME_UNREAD_MESSAGE_COUNT} INTEGER")
                } catch (e: SQLiteException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        var MIGRATION_3_4: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    database.execSQL("ALTER TABLE ${DbConstants.TABLE_NAME_ROOMS} ADD COLUMN ${DbConstants.ROOM_COLUMN_NAME_OWNER_ID} STRING")
                } catch (e: SQLiteException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}