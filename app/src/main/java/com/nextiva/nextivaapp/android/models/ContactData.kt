package com.nextiva.nextivaapp.android.models

import com.nextiva.nextivaapp.android.db.model.DbGroup

data class ContactData(var nextivaContacts: ArrayList<NextivaContact>?,
                       var groups: ArrayList<DbGroup>?)