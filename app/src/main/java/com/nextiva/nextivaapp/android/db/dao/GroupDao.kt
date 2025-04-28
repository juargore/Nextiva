package com.nextiva.nextivaapp.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.nextiva.nextivaapp.android.db.model.DbGroup
import com.nextiva.nextivaapp.android.db.model.DbGroupRelation
import io.reactivex.Single

@Dao
interface GroupDao {
    @Insert
    fun insertGroup(group: DbGroup)

    @Insert
    fun insertGroupRelation(groupRelation: DbGroupRelation)

    @Transaction
    @Query("DELETE FROM groups WHERE transaction_id != :transactionId OR transaction_id IS NULL")
    fun clearOldGroupsByTransactionId(transactionId: String)

    @Transaction
    @Query("DELETE FROM group_relations WHERE transaction_id != :transactionId OR transaction_id IS NULL")
    fun clearOldGroupRelationsByTransactionId(transactionId: String)

    @Query("UPDATE group_relations SET transaction_id = :transactionId WHERE contact_id = :contactId AND group_id = :groupId")
    fun updateGroupRelationTransactionId(transactionId: String, contactId: Int, groupId: String)

    @Query("SELECT * FROM group_relations WHERE contact_id = :contactId AND group_id = :groupId")
    fun getGroupRelation(contactId: Int, groupId: String): Single<DbGroupRelation>

    @Query("SELECT * FROM groups ORDER BY `order` ASC")
    fun getGroups(): List<DbGroup>
}