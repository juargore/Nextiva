package com.nextiva.nextivaapp.android.models

import androidx.room.Embedded
import com.nextiva.nextivaapp.android.db.model.DbMessageState
import java.io.Serializable

class MessageStateList : Serializable {
    @Embedded
    var messageState: DbMessageState? = null


}