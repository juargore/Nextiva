package com.nextiva.nextivaapp.android.adapters.pagedlistadapters.datasources;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class ContactsDataSourceFactory extends DataSource.Factory<Integer, ContactListItem> {

    @Inject
    DbManager mDbManager;

    private final @Enums.Contacts.CacheTypes.Type
    int mCacheType;
    private final boolean mIsListItemLongClickable;

    @Inject
    public ContactsDataSourceFactory(@ApplicationContext Context context, @Enums.Contacts.CacheTypes.Type int cacheType, boolean isListItemLongClickable) {
        mCacheType = cacheType;
        mIsListItemLongClickable = isListItemLongClickable;
    }

    private String mSearchTerm = "";

    @NonNull
    @Override
    public DataSource<Integer, ContactListItem> create() {
        return mDbManager.getContactsDataSourceFactory(mCacheType, mSearchTerm, mIsListItemLongClickable).create();
    }

    public void setSearchTerm(String searchTerm) {
        mSearchTerm = searchTerm;
    }
}
