package com.nextiva.nextivaapp.android.db.dao;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.AppDatabase;
import com.nextiva.nextivaapp.android.db.model.Address;
import com.nextiva.nextivaapp.android.db.model.DbContact;
import com.nextiva.nextivaapp.android.db.model.DbDate;
import com.nextiva.nextivaapp.android.db.model.DbGroup;
import com.nextiva.nextivaapp.android.db.model.DbGroupRelation;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.db.model.DbVCard;
import com.nextiva.nextivaapp.android.db.model.EmailAddress;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.db.model.SocialMediaAccount;
import com.nextiva.nextivaapp.android.models.ConnectContactDbReturnModel;
import com.nextiva.nextivaapp.android.models.DbResponse;
import com.nextiva.nextivaapp.android.models.DbTableCountModel;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.util.CallUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import io.reactivex.Single;

@Dao
public abstract class CompleteContactDao {

    private final int[] connectContactTypePriorityFilter = new int[] {
            Enums.Contacts.ContactTypes.CONNECT_USER,
            Enums.Contacts.ContactTypes.CONNECT_PERSONAL,
            Enums.Contacts.ContactTypes.CONNECT_TEAM,
            Enums.Contacts.ContactTypes.CONNECT_SHARED,
            Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS,
            Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW,
            Enums.Contacts.ContactTypes.CONNECT_UNKNOWN,
            Enums.Contacts.ContactTypes.LOCAL
    };

    private final int[] classicContactTypePriorityFilter = new int[] {
            Enums.Contacts.ContactTypes.PERSONAL,
            Enums.Contacts.ContactTypes.CONFERENCE,
            Enums.Contacts.ContactTypes.LOCAL,
            Enums.Contacts.ContactTypes.ENTERPRISE
    };

    public List<NextivaContact> getNextivaContactsListInThread(@Enums.Contacts.CacheTypes.Type int cacheType) {

        switch (cacheType) {
            case Enums.Contacts.CacheTypes.ONLINE_ROSTER:
                return getNextivaOnlineRosterContactsInThread();
            case Enums.Contacts.CacheTypes.LOCAL:
                return getNextivaLocalContactsInThread();
            case Enums.Contacts.CacheTypes.ENTERPRISE:
                return getNextivaEnterpriseContactsInThread();
            default:
                return getAllNextivaRosterContactsInThread();
        }
    }

    public Single<DbResponse<NextivaContact>> getNextivaContact(@Nullable String phoneNumber, ExecutorService executorService) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return Single.just(new DbResponse<>(null));
        }

        final String cleanedNumber = CallUtil.cleanPhoneNumber(phoneNumber);
        final String strippedCleanedNumber = (!TextUtils.isEmpty(cleanedNumber) && cleanedNumber.charAt(0) == '1') ? cleanedNumber.substring(1) : cleanedNumber;
        final String[] countryCodedNumbers = getCountryCodedPhoneNumbers(strippedCleanedNumber);

        List<NextivaContactListFetcher> list;

        // Fetch Contacts based on classicContactTypePriorityFilter array to keep same behavior across all app, do not
        // modify order of contact type priority here, modify the {classicContactTypePriorityFilter} variable

        if(cleanedNumber.length() > 9) {
            list = Arrays.stream(classicContactTypePriorityFilter)
                    .mapToObj(contactType -> (NextivaContactListFetcher) () ->
                            getNextivaListFromPhoneNumber(contactType, executorService, countryCodedNumbers))
                    .collect(Collectors.toList());

        } else {
            list = Arrays.stream(classicContactTypePriorityFilter)
                    .mapToObj(contactType -> (NextivaContactListFetcher) () ->
                            getNextivaListFromExtension(contactType, executorService, cleanedNumber))
                    .collect(Collectors.toList());
        }


        return getNextivaContactFromLists(list)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new DbResponse<>(null);
                });

    }

    public Single<DbResponse<NextivaContact>> getConnectContact(@Nullable String phoneNumber, ExecutorService executorService) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return Single.just(new DbResponse<>(null));
        }

        final String cleanedNumber = CallUtil.cleanPhoneNumber(phoneNumber);
        final String strippedCleanedNumber = (!TextUtils.isEmpty(cleanedNumber) && cleanedNumber.charAt(0) == '1') ? cleanedNumber.substring(1) : cleanedNumber;
        final String[] countryCodedNumbers = getCountryCodedPhoneNumbers(strippedCleanedNumber);

        // Fetch Contacts based on connect ContactType array to keep same behavior across all app, do not
        // modify order of contact type priority here, modify the {connectContactTypes} variable
        List<NextivaContactListFetcher> list;

        if (cleanedNumber.length() > 9) {
            list = Arrays.stream(connectContactTypePriorityFilter)
                    .mapToObj(contactType -> (NextivaContactListFetcher) () ->
                            getNextivaListFromPhoneNumber(
                                    contactType, executorService, countryCodedNumbers))
                    .collect(Collectors.toList());
        } else {
            list = Arrays.stream(connectContactTypePriorityFilter)
                    .mapToObj(contactType -> (NextivaContactListFetcher) () ->
                            getNextivaListFromExtension(contactType, executorService, cleanedNumber))
                    .collect(Collectors.toList());
        }

        return getNextivaContactFromLists(list)
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new DbResponse<>(null);
                });
    }

    public DbResponse<NextivaContact> getNextivaContactInThread(@Nullable String phoneNumber, ExecutorService executorService) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return new DbResponse<>(null);
        }

        final String cleanedNumber = CallUtil.cleanPhoneNumber(phoneNumber);
        final String strippedCleanedNumber = (!TextUtils.isEmpty(cleanedNumber) && cleanedNumber.charAt(0) == '1') ? cleanedNumber.substring(1) : cleanedNumber;
        final String[] countryCodedNumbers = getCountryCodedPhoneNumbers(strippedCleanedNumber);

        if (cleanedNumber.length() > 9) {

            return getNextivaContactFromListsWithThread(() -> getNextivaListFromPhoneNumber(Enums.Contacts.ContactTypes.PERSONAL, executorService, countryCodedNumbers),
                                                        () -> getNextivaListFromPhoneNumber(Enums.Contacts.ContactTypes.CONFERENCE, executorService, countryCodedNumbers),
                                                        () -> getNextivaListFromPhoneNumber(Enums.Contacts.ContactTypes.LOCAL, executorService, countryCodedNumbers),
                                                        () -> getNextivaListFromPhoneNumber(Enums.Contacts.ContactTypes.ENTERPRISE, executorService, countryCodedNumbers));

        } else {
            return getNextivaContactFromListsWithThread(() -> getNextivaListFromExtension(Enums.Contacts.ContactTypes.PERSONAL, executorService, cleanedNumber),
                                                        () -> getNextivaListFromExtension(Enums.Contacts.ContactTypes.CONFERENCE, executorService, cleanedNumber),
                                                        () -> getNextivaListFromExtension(Enums.Contacts.ContactTypes.LOCAL, executorService, cleanedNumber),
                                                        () -> getNextivaListFromExtension(Enums.Contacts.ContactTypes.ENTERPRISE, executorService, cleanedNumber));
        }
    }

    public NextivaContact getConnectContactFromUuid(String userUuid) {
        return getContactFromUuid(userUuid, connectContactTypePriorityFilter);
    }

    public String getUiNameFromPhoneNumber(@Nullable String phoneNumber, ExecutorService executorService) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return null;
        }

        //TODO: This should probably be rethought at present in looking for (number, 1number, +1number). This seems backward and could cause issues if an extension uses a 1 in the first position.

        final String cleanedNumber = CallUtil.cleanPhoneNumber(phoneNumber != null ? phoneNumber : "");

        final String strippedCleanedNumber = (!TextUtils.isEmpty(cleanedNumber) && cleanedNumber.charAt(0) == '1') ? cleanedNumber.substring(1) : cleanedNumber;
        final String[] countryCodedNumbers = getCountryCodedPhoneNumbers(strippedCleanedNumber);

        if (cleanedNumber.length() > 9) {
            if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.PERSONAL, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.PERSONAL, executorService, countryCodedNumbers);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONFERENCE, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONFERENCE, executorService, countryCodedNumbers);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.LOCAL, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.LOCAL, executorService, countryCodedNumbers);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.ENTERPRISE, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.ENTERPRISE, executorService, countryCodedNumbers);

            } else {
                return null;
            }

        } else {
            if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.PERSONAL, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.PERSONAL, executorService, cleanedNumber);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONFERENCE, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONFERENCE, executorService, cleanedNumber);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.LOCAL, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.LOCAL, executorService, cleanedNumber);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.ENTERPRISE, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.ENTERPRISE, executorService, cleanedNumber);

            } else {
                return null;
            }
        }
    }

    public String getContactTypeIdFromPhoneNumberInThread(String phoneNumber) {
        final String cleanedNumber = CallUtil.cleanPhoneNumber(phoneNumber != null ? phoneNumber : "");

        final String strippedCleanedNumber = (!TextUtils.isEmpty(cleanedNumber) && cleanedNumber.charAt(0) == '1') ? cleanedNumber.substring(1) : cleanedNumber;
        final String[] countryCodedNumbers = getCountryCodedPhoneNumbers(strippedCleanedNumber);

        if (cleanedNumber.length() > 9) {
            return getContactTypeIdFromPhoneNumber(connectContactTypePriorityFilter, countryCodedNumbers);

        } else {
            return getContactTypeIdFromPhoneNumber(connectContactTypePriorityFilter, cleanedNumber);
        }
    }

    public String getConnectUiNameFromPhoneNumber(@Nullable String phoneNumber, ExecutorService executorService) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return null;
        }

        //TODO: This should probably be rethought at present in looking for (number, 1number, +1number). This seems backward and could cause issues if an extension uses a 1 in the first position.

        final String cleanedNumber = CallUtil.cleanPhoneNumber(phoneNumber != null ? phoneNumber : "");

        final String strippedCleanedNumber = (!TextUtils.isEmpty(cleanedNumber) && cleanedNumber.charAt(0) == '1') ? cleanedNumber.substring(1) : cleanedNumber;
        final String[] countryCodedNumbers = getCountryCodedPhoneNumbers(strippedCleanedNumber);

        if (cleanedNumber.length() > 9) {
            if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_PERSONAL, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_PERSONAL, executorService, countryCodedNumbers);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_SHARED, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_SHARED, executorService, countryCodedNumbers);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_USER, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_USER, executorService, countryCodedNumbers);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_TEAM, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_TEAM, executorService, countryCodedNumbers);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS, executorService, countryCodedNumbers);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW, executorService, countryCodedNumbers);

            }  else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS, executorService, countryCodedNumbers);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_UNKNOWN, executorService, countryCodedNumbers))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_UNKNOWN, executorService, countryCodedNumbers);

            } else {
                return null;
            }

        } else {
            if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_PERSONAL, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_PERSONAL, executorService, cleanedNumber);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_SHARED, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_SHARED, executorService, cleanedNumber);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_USER, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_USER, executorService, cleanedNumber);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_TEAM, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_TEAM, executorService, cleanedNumber);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS, executorService, cleanedNumber);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW, executorService, cleanedNumber);

            } else if (!TextUtils.isEmpty(getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_UNKNOWN, executorService, cleanedNumber))) {
                return getUiNameFromPhoneNumber(Enums.Contacts.ContactTypes.CONNECT_UNKNOWN, executorService, cleanedNumber);

            } else {
                return null;
            }
        }
    }

    public Single<List<NextivaContact>> getNextivaContactsList(@Enums.Contacts.CacheTypes.Type int cacheType) {

        switch (cacheType) {
            case Enums.Contacts.CacheTypes.ALL_ROSTER:
                return getAllRosterContacts();
            case Enums.Contacts.CacheTypes.ONLINE_ROSTER:
                return getOnlineRosterContacts();
            case Enums.Contacts.CacheTypes.LOCAL:
                return getLocalContacts();
            case Enums.Contacts.CacheTypes.ENTERPRISE:
                return getEnterpriseContacts();
            default:
                return getAllRosterContacts();
        }
    }

    public LiveData<List<NextivaContact>> getNextivaContactsListLiveData(@Enums.Contacts.CacheTypes.Type int cacheType) {

        switch (cacheType) {
            case Enums.Contacts.CacheTypes.ALL_ROSTER:
                return getAllRosterContactsLiveData();
            case Enums.Contacts.CacheTypes.ONLINE_ROSTER:
                return getOnlineRosterContactsLiveData();
            case Enums.Contacts.CacheTypes.LOCAL:
                return getLocalContactsLiveData();
            case Enums.Contacts.CacheTypes.ENTERPRISE:
                return getEnterpriseContactsLiveData();
            default:
                return getAllRosterContactsLiveData();
        }
    }

    public PagingSource<Integer, NextivaContact> getContactTypePagingSource(int[] types, String searchTerm) {
        return getContactTypePagingSourceQuery(types, "%" + searchTerm + "%");
    }

    public Integer getContactTypeSearchCount(int[] types, String searchTerm) {
        return getContactTypeSearchCountQuery(types, "%" + searchTerm + "%");
    }

    public PagingSource<Integer, ConnectContactDbReturnModel> getConnectContactPagingSource(boolean favoritesExpanded, boolean teammatesExpanded, boolean businessExpanded, boolean allExpanded) {
        String pagingSourceQuery = "";

        if (favoritesExpanded || teammatesExpanded || businessExpanded || allExpanded) {
            pagingSourceQuery = "SELECT * FROM ( ";

            String CONNECT_CONTACTS_UNION_QUERY = "UNION ALL ";
            if (favoritesExpanded) {
                String CONNECT_CONTACTS_FAVORITES_QUERY = "SELECT contacts.id, contact_type_id, contact_type, display_name, first_name, last_name, is_favorite, " +
                        "ui_name, subscription_state, contacts.sort_name, 0 AS sort_group, presences.presence_state, presences.status_text, 0 AS groupValue FROM contacts " +
                        "LEFT JOIN presences ON contacts.id = presences.contact_id " +
                        "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.CONNECT_PERSONAL + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_USER + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_TEAM + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_SHARED + ") AND " +
                        "is_favorite == 1 ";
                pagingSourceQuery += CONNECT_CONTACTS_FAVORITES_QUERY;

                if (teammatesExpanded || businessExpanded || allExpanded) {
                    pagingSourceQuery += CONNECT_CONTACTS_UNION_QUERY;
                }
            }

            if (teammatesExpanded) {
                String CONNECT_CONTACTS_TEAMMATES_QUERY = "SELECT contacts.id, contact_type_id, contact_type, display_name, first_name, last_name, is_favorite, " +
                        "ui_name, subscription_state, contacts.sort_name, contacts.sort_group, presences.presence_state, presences.status_text, 1 AS groupValue FROM contacts " +
                        "LEFT JOIN presences ON contacts.id = presences.contact_id " +
                        "WHERE contacts.contact_type == " + Enums.Contacts.ContactTypes.CONNECT_USER + " ";
                pagingSourceQuery += CONNECT_CONTACTS_TEAMMATES_QUERY;

                if (businessExpanded || allExpanded) {
                    pagingSourceQuery += CONNECT_CONTACTS_UNION_QUERY;
                }
            }

            if (businessExpanded) {
                String CONNECT_CONTACTS_BUSINESS_QUERY = "SELECT contacts.id, contact_type_id, contact_type, display_name, first_name, last_name, is_favorite, " +
                        "ui_name, subscription_state, contacts.sort_name, contacts.sort_group, presences.presence_state, presences.status_text, 2 AS groupValue FROM contacts " +
                        "LEFT JOIN presences ON contacts.id = presences.contact_id " +
                        "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.CONNECT_PERSONAL + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_SHARED + ") ";
                pagingSourceQuery += CONNECT_CONTACTS_BUSINESS_QUERY;

                if (allExpanded) {
                    pagingSourceQuery += CONNECT_CONTACTS_UNION_QUERY;
                }
            }

            String CONNECT_CONTACTS_QUERY_END = "";
            if (allExpanded) {
                String CONNECT_CONTACTS_ALL_QUERY = "SELECT contacts.id, contact_type_id, contact_type, display_name, first_name, last_name, is_favorite, " +
                        "ui_name, subscription_state, contacts.sort_name, contacts.sort_group, presences.presence_state, presences.status_text, 3 AS groupValue FROM contacts " +
                        "LEFT JOIN presences ON contacts.id = presences.contact_id " +
                        "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.CONNECT_PERSONAL + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_SHARED + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_TEAM + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS + ", " +
                        Enums.Contacts.ContactTypes.CONNECT_USER + ") ";
                pagingSourceQuery += CONNECT_CONTACTS_ALL_QUERY;

                CONNECT_CONTACTS_QUERY_END = "ORDER BY groupValue ASC, sort_group ASC, contacts.display_name ASC) ";

            } else {
                CONNECT_CONTACTS_QUERY_END = "ORDER BY " +
                        "groupValue ASC, sort_group ASC, contacts.display_name ASC) ";
            }


            pagingSourceQuery += CONNECT_CONTACTS_QUERY_END;

        } else {
            String CONNECT_CONTACTS_QUERY_COLLAPSED = "SELECT * FROM contacts WHERE contact_type = -1";
            pagingSourceQuery += CONNECT_CONTACTS_QUERY_COLLAPSED;
        }

        return getConnectContactsPagingSourceRawQuery(new SimpleSQLiteQuery(pagingSourceQuery, null));
    }

    public DataSource.Factory<Integer, NextivaContact> getContactsDataSourceFactory(
            @Enums.Contacts.CacheTypes.Type int cacheType,
            String searchTerm) {

        switch (cacheType) {
            case Enums.Contacts.CacheTypes.ALL_ROSTER:
                return getAllRosterContactsDataSourceFactory("%" + searchTerm + "%");
            case Enums.Contacts.CacheTypes.ONLINE_ROSTER:
                return getOnlineRosterContactsDataSourceFactory("%" + searchTerm + "%");
            case Enums.Contacts.CacheTypes.LOCAL:
                return getLocalContactsDataSourceFactory("%" + searchTerm + "%");
            case Enums.Contacts.CacheTypes.ENTERPRISE:
                return getEnterpriseContactsDataSourceFactory("%" + searchTerm + "%");
            default:
                return getAllRosterContactsDataSourceFactory("%" + searchTerm + "%");
        }
    }

    private Single<DbResponse<NextivaContact>> getNextivaContactFromLists(List<NextivaContactListFetcher> nextivaContactListFetchers) {
        List<NextivaContact> nextivaContactList;

        for (NextivaContactListFetcher nextivaContactListFetcher : nextivaContactListFetchers) {
            if (nextivaContactListFetcher != null) {
                nextivaContactList = nextivaContactListFetcher.getNextivaContacts();

                if (nextivaContactList != null &&
                        !nextivaContactList.isEmpty() &&
                        nextivaContactList.get(0) != null) {

                    return Single.just(new DbResponse<>(nextivaContactList.get(0)));
                }
            }
        }

        return Single.just(new DbResponse<>(null));
    }

    private DbResponse<NextivaContact> getNextivaContactFromListsWithThread(NextivaContactListFetcher... nextivaContactListFetchers) {
        List<NextivaContact> nextivaContactList;

        for (NextivaContactListFetcher nextivaContactListFetcher : nextivaContactListFetchers) {
            if (nextivaContactListFetcher != null) {
                nextivaContactList = nextivaContactListFetcher.getNextivaContacts();

                if (nextivaContactList != null &&
                        !nextivaContactList.isEmpty() &&
                        nextivaContactList.get(0) != null) {

                    return new DbResponse<>(nextivaContactList.get(0));
                }
            }
        }

        return new DbResponse<>(null);
    }

    @NonNull
    private String[] getCountryCodedPhoneNumbers(@Nullable String number) {
        if (!TextUtils.isEmpty(number)) {
            return new String[] {number, "1" + number, "+1" + number};
        } else {
            return new String[] {};
        }
    }

    @Nullable
    private List<NextivaContact> getNextivaListFromPhoneNumber(@Enums.Contacts.ContactTypes.Type int contactType, ExecutorService executorService, String... numbersToSearch) {
        try {
            return executorService
                    .submit(() -> getNextivaContactsListFromPhoneNumber(contactType, numbersToSearch))
                    .get();

        } catch (InterruptedException | ExecutionException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }

    @Nullable
    private List<NextivaContact> getNextivaListFromExtension(@Enums.Contacts.ContactTypes.Type int contactType, ExecutorService executorService, String extension) {
        try {
            return executorService
                    .submit(() -> getNextivaContactsListFromExtension(contactType, "%x" + extension, extension))
                    .get();

        } catch (InterruptedException | ExecutionException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }

    @Nullable
    private String getUiNameFromPhoneNumber(@Enums.Contacts.ContactTypes.Type int contactType, ExecutorService executorService, String... numbersToSearch) {
        try {
            return executorService
                    .submit(() -> getUiNameFromPhoneNumber(contactType, numbersToSearch))
                    .get();

        } catch (InterruptedException | ExecutionException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }

    @Nullable
    private String getUiNameFromExtension(@Enums.Contacts.ContactTypes.Type int contactType, ExecutorService executorService, String extension) {
        try {
            return executorService
                    .submit(() -> getUiNameFromExtension(contactType, extension))
                    .get();

        } catch (InterruptedException | ExecutionException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }

    public Single<NextivaContact> getCompleteRosterContactByJid(String jid) {
        return getCompleteRosterContact(jid);
    }

    public Single<NextivaContact> getCompleteContactByJid(String jid) {
        return getCompleteContact(jid);
    }

    public Single<NextivaContact> getCompleteContactByUserId(String userId) {
        return getCompleteContactFromUserId(userId);
    }

    @Transaction
    @Query("SELECT COUNT(*) FROM contacts " +
            "LEFT JOIN emails ON emails.contact_id = contacts.id " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.CONNECT_SHARED + ", " + Enums.Contacts.ContactTypes.CONNECT_PERSONAL + ") " +
            "AND emails.address = :address")
    public abstract int doesContactWithPrimaryEmailExist(String address);

    @Query("SELECT COUNT(*) FROM contacts " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.CONNECT_SHARED + ", " + Enums.Contacts.ContactTypes.CONNECT_PERSONAL + ") " +
            "AND lookup_key = :lookupKey")
    public abstract int doesLocalContactWithLookupKeyExist(String lookupKey);

    @Query("SELECT lookup_key FROM contacts " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.CONNECT_SHARED + ", " + Enums.Contacts.ContactTypes.CONNECT_PERSONAL + ") " +
            "AND lookup_key IS NOT NULL")
    public abstract Single<List<String>> getBusinessContactLookupKeys();


    @Query("SELECT lookup_key FROM contacts " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.CONNECT_SHARED + ", " + Enums.Contacts.ContactTypes.CONNECT_PERSONAL + ") " +
            "AND lookup_key IS NOT NULL " +
            "UNION " +
            "SELECT emails.address FROM contacts " +
            "LEFT JOIN emails ON emails.contact_id = contacts.id " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.CONNECT_SHARED + ", " + Enums.Contacts.ContactTypes.CONNECT_PERSONAL + ") " +
            "AND emails.type IN (" + Enums.Contacts.EmailTypes.CONNECT_PRIMARY_EMAIL + ", " + Enums.Contacts.EmailTypes.WORK_EMAIL + ") " +
            "AND emails.address IS NOT NULL")
    public abstract Single<List<String>> getBusinessContactLookupKeysAndPrimaryWorkEmails();


    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.PERSONAL + ", " + Enums.Contacts.ContactTypes.CONFERENCE + ") " +
            " ORDER BY sort_name COLLATE NOCASE ASC")
    abstract Single<List<NextivaContact>> getAllRosterContacts();

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "INNER JOIN presences ON contacts.id = presences.contact_id " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.PERSONAL + ", " + Enums.Contacts.ContactTypes.CONFERENCE + ") " +
            "AND presences.presence_state != " + Enums.Contacts.PresenceStates.NONE + " " +
            "ORDER BY sort_name COLLATE NOCASE ASC")
    abstract Single<List<NextivaContact>> getOnlineRosterContacts();

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.PERSONAL + ", " + Enums.Contacts.ContactTypes.CONFERENCE + ") " +
            " ORDER BY sort_name COLLATE NOCASE ASC")
    abstract LiveData<List<NextivaContact>> getAllRosterContactsLiveData();

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "WHERE contacts.contact_type = :contactType AND contacts.contact_type_id = :contactId " +
            " ORDER BY sort_name COLLATE NOCASE ASC")
    public abstract LiveData<NextivaContact> getContactLiveData(int contactType, String contactId);

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "WHERE contacts.contact_type_id = :contactId " +
            "ORDER BY sort_name COLLATE NOCASE ASC")
    public abstract LiveData<NextivaContact> getContactLiveData(String contactId);

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "LEFT JOIN phones ON contacts.id = phones.contact_id " +
            "LEFT JOIN emails ON contacts.id = emails.contact_id " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.PERSONAL + ", " + Enums.Contacts.ContactTypes.CONFERENCE + ")" +
            "AND (contacts.first_name LIKE :searchTerm OR " +
            "contacts.last_name LIKE :searchTerm OR " +
            "contacts.company LIKE :searchTerm OR " +
            "contacts.display_name LIKE :searchTerm OR " +
            "(contacts.first_name || \" \" || contacts.last_name) LIKE :searchTerm OR " +
            "contacts.jid LIKE :searchTerm OR " +
            "phones.number LIKE :searchTerm OR " +
            "phones.stripped_number LIKE :searchTerm OR " +
            "emails.address LIKE :searchTerm) " +
            "ORDER BY sort_name COLLATE NOCASE ASC")
    abstract DataSource.Factory<Integer, NextivaContact> getAllRosterContactsDataSourceFactory(String searchTerm);

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "INNER JOIN presences ON contacts.id = presences.contact_id " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.PERSONAL + ", " + Enums.Contacts.ContactTypes.CONFERENCE + ") " +
            "AND presences.presence_state != " + Enums.Contacts.PresenceStates.NONE + " " +
            "ORDER BY sort_name COLLATE NOCASE ASC")
    abstract LiveData<List<NextivaContact>> getOnlineRosterContactsLiveData();


    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "INNER JOIN presences ON contacts.id = presences.contact_id " +
            "LEFT JOIN phones ON contacts.id = phones.contact_id " +
            "LEFT JOIN emails ON contacts.id = emails.contact_id " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.PERSONAL + ", " + Enums.Contacts.ContactTypes.CONFERENCE + ")" +
            "AND presences.presence_state != " + Enums.Contacts.PresenceStates.NONE + " " +
            "AND (contacts.first_name LIKE :searchTerm OR " +
            "contacts.last_name LIKE :searchTerm OR " +
            "contacts.company LIKE :searchTerm OR " +
            "contacts.display_name LIKE :searchTerm OR " +
            "(contacts.first_name || \" \" || contacts.last_name) LIKE :searchTerm OR " +
            "contacts.jid LIKE :searchTerm OR " +
            "phones.number LIKE :searchTerm OR " +
            "phones.stripped_number LIKE :searchTerm OR " +
            "emails.address LIKE :searchTerm) " +
            "ORDER BY sort_name COLLATE NOCASE ASC")
    abstract DataSource.Factory<Integer, NextivaContact> getOnlineRosterContactsDataSourceFactory(String searchTerm);

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.PERSONAL + ", " + Enums.Contacts.ContactTypes.CONFERENCE + ") " +
            " ORDER BY sort_name COLLATE NOCASE ASC")
    abstract List<NextivaContact> getAllNextivaRosterContactsInThread();

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data " +
            "FROM contacts INNER JOIN presences ON contacts.id = presences.contact_id " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "WHERE contacts.contact_type IN (" + Enums.Contacts.ContactTypes.PERSONAL + ", " + Enums.Contacts.ContactTypes.CONFERENCE + ") " +
            "AND presences.presence_state != " + Enums.Contacts.PresenceStates.NONE + " " +
            "ORDER BY sort_name COLLATE NOCASE ASC")
    abstract List<NextivaContact> getNextivaOnlineRosterContactsInThread();

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "WHERE contacts.contact_type = " + Enums.Contacts.ContactTypes.LOCAL +
            " ORDER BY sort_name COLLATE NOCASE ASC")
    abstract Single<List<NextivaContact>> getLocalContacts();

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "WHERE contacts.contact_type = " + Enums.Contacts.ContactTypes.LOCAL +
            " ORDER BY sort_name COLLATE NOCASE ASC")
    abstract LiveData<List<NextivaContact>> getLocalContactsLiveData();

    @Transaction
    @Query("SELECT DISTINCT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "LEFT JOIN phones ON contacts.id = phones.contact_id " +
            "LEFT JOIN emails ON contacts.id = emails.contact_id " +
            "WHERE contacts.contact_type = " + Enums.Contacts.ContactTypes.LOCAL + " " +
            "AND (contacts.first_name LIKE :searchTerm OR " +
            "contacts.last_name LIKE :searchTerm OR " +
            "contacts.company LIKE :searchTerm OR " +
            "contacts.display_name LIKE :searchTerm OR " +
            "(contacts.first_name || \" \" || contacts.last_name) LIKE :searchTerm OR " +
            "contacts.jid LIKE :searchTerm OR " +
            "phones.number LIKE :searchTerm OR " +
            "phones.stripped_number LIKE :searchTerm OR " +
            "emails.address LIKE :searchTerm) " +
            "ORDER BY sort_name COLLATE NOCASE ASC")
    abstract DataSource.Factory<Integer, NextivaContact> getLocalContactsDataSourceFactory(String searchTerm);

    @Transaction
    @RawQuery(observedEntities = {DbContact.class})
    abstract PagingSource<Integer, ConnectContactDbReturnModel> getConnectContactsPagingSourceRawQuery(SupportSQLiteQuery query);

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "WHERE contacts.contact_type IN (" +

            Enums.Contacts.ContactTypes.CONNECT_PERSONAL + ", " +
            Enums.Contacts.ContactTypes.CONNECT_SHARED + ", " +
            Enums.Contacts.ContactTypes.CONNECT_USER + ", " +
            Enums.Contacts.ContactTypes.CONNECT_TEAM + ", " +
            Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS + ", " +
            Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW + ") " +

            " ORDER BY sort_name COLLATE NOCASE ASC")
    public abstract List<NextivaContact> getConnectSmsContactList();

    @Transaction
    @Query("SELECT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "WHERE contacts.contact_type = " + Enums.Contacts.ContactTypes.LOCAL +
            " ORDER BY sort_name COLLATE NOCASE ASC")
    abstract List<NextivaContact> getNextivaLocalContactsInThread();

    @Transaction
    @Query("SELECT *, vcards.photo_data FROM contacts " +
            "LEFT JOIN (SELECT contacts.id as contact_id, contacts.ui_name AS ui_name, contacts.jid AS contact_jid FROM contacts WHERE contact_type = 2) AS results ON results.contact_jid = jid " +
            "LEFT JOIN vcards ON vcards.contact_id = results.contact_id " +
            "WHERE contacts.contact_type = " + Enums.Contacts.ContactTypes.ENTERPRISE +
            " ORDER BY sort_name COLLATE NOCASE ASC")
    abstract Single<List<NextivaContact>> getEnterpriseContacts();

    @Transaction
    @Query("SELECT *, vcards.photo_data FROM contacts " +
            "LEFT JOIN (SELECT contacts.id as contact_id, contacts.ui_name AS ui_name, contacts.jid AS contact_jid FROM contacts WHERE contact_type = 2) AS results ON results.contact_jid = jid " +
            "LEFT JOIN vcards ON vcards.contact_id = results.contact_id " +
            "WHERE contacts.contact_type = " + Enums.Contacts.ContactTypes.ENTERPRISE +
            " ORDER BY sort_name COLLATE NOCASE ASC")
    abstract LiveData<List<NextivaContact>> getEnterpriseContactsLiveData();

    @Transaction
    @Query("SELECT DISTINCT contacts.*, vcards.photo_data " +
            "FROM contacts " +
            "LEFT JOIN phones ON contacts.id = phones.contact_id " +
            "LEFT JOIN emails ON contacts.id = emails.contact_id " +
            "LEFT JOIN (SELECT contacts.id AS contact_id, jid FROM contacts WHERE contact_type = " + Enums.Contacts.ContactTypes.PERSONAL + ") " +
            "AS results ON results.jid = contacts.jid COLLATE NOCASE " +
            "LEFT JOIN vcards ON vcards.contact_id = results.contact_id " +
            "WHERE contacts.contact_type = " + Enums.Contacts.ContactTypes.ENTERPRISE + " " +
            "AND (contacts.first_name LIKE :searchTerm OR " +
            "contacts.last_name LIKE :searchTerm OR " +
            "contacts.company LIKE :searchTerm OR " +
            "contacts.display_name LIKE :searchTerm OR " +
            "(contacts.first_name || \" \" || contacts.last_name) LIKE :searchTerm OR " +
            "contacts.jid LIKE :searchTerm OR " +
            "phones.number LIKE :searchTerm OR " +
            "phones.stripped_number LIKE :searchTerm OR " +
            "emails.address LIKE :searchTerm) " +
            "ORDER BY sort_name COLLATE NOCASE ASC")
    abstract DataSource.Factory<Integer, NextivaContact> getEnterpriseContactsDataSourceFactory(String searchTerm);

    @Transaction
    @Query("SELECT DISTINCT contacts.*, phones.number, vcards.photo_data FROM contacts " +
            "LEFT JOIN (SELECT DISTINCT contacts.id as contact_id, contacts.ui_name AS ui_name, contacts.jid AS contact_jid FROM contacts WHERE contact_type = 2) AS results ON results.contact_jid = jid " +
            "LEFT JOIN (SELECT DISTINCT * FROM vcards WHERE vcards.contact_id = id LIMIT 1)vcards " +
            "LEFT JOIN (SELECT DISTINCT * FROM phones WHERE id = phones.contact_id)phones " +
            "WHERE contacts.contact_type = " + Enums.Contacts.ContactTypes.ENTERPRISE +
            " ORDER BY sort_name COLLATE NOCASE ASC")
    abstract List<NextivaContact> getNextivaEnterpriseContactsInThread();

    @Transaction
    @Query("SELECT DISTINCT contacts.*, vcards.photo_data FROM contacts " +
            "LEFT JOIN vcards ON vcards.contact_id = contacts.id " +
            "LEFT JOIN phones ON contacts.id = phones.contact_id " +
            "LEFT JOIN emails ON contacts.id = emails.contact_id " +
            "WHERE contacts.contact_type IN (:types)" +
            "AND (contacts.first_name LIKE :searchTerm OR " +
            "contacts.last_name LIKE :searchTerm OR " +
            "contacts.company LIKE :searchTerm OR " +
            "contacts.display_name LIKE :searchTerm OR " +
            "(contacts.first_name || \" \" || contacts.last_name) LIKE :searchTerm OR " +
            "contacts.jid LIKE :searchTerm OR " +
            "phones.number LIKE :searchTerm OR " +
            "phones.stripped_number LIKE :searchTerm OR " +
            "emails.address LIKE :searchTerm) " +
            "ORDER BY sort_name COLLATE NOCASE ASC")
    abstract PagingSource<Integer, NextivaContact> getContactTypePagingSourceQuery(int[] types, String searchTerm);

    @Query("SELECT * FROM contacts WHERE " +
            "contact_type_id = :userUuid AND " +
            "contact_type IN (:contactTypes)")
    abstract NextivaContact getContactFromUuid(String userUuid, int[] contactTypes);

    @Transaction
    @Query("SELECT COUNT(DISTINCT contacts.id) FROM contacts " +
            "LEFT JOIN phones ON contacts.id = phones.contact_id " +
            "LEFT JOIN emails ON contacts.id = emails.contact_id " +
            "WHERE contacts.contact_type IN (:types)" +
            "AND (contacts.first_name LIKE :searchTerm OR " +
            "contacts.last_name LIKE :searchTerm OR " +
            "contacts.company LIKE :searchTerm OR " +
            "contacts.display_name LIKE :searchTerm OR " +
            "(contacts.first_name || \" \" || contacts.last_name) LIKE :searchTerm OR " +
            "contacts.jid LIKE :searchTerm OR " +
            "phones.number LIKE :searchTerm OR " +
            "phones.stripped_number LIKE :searchTerm OR " +
            "emails.address LIKE :searchTerm) " +
            "ORDER BY sort_name COLLATE NOCASE ASC")
    abstract Integer getContactTypeSearchCountQuery(int[] types, String searchTerm);

    @Transaction
    @Query("SELECT * FROM contacts WHERE jid = :jid AND contact_type = " + Enums.Contacts.ContactTypes.PERSONAL + " COLLATE NOCASE LIMIT 1")
    abstract Single<NextivaContact> getCompleteRosterContact(String jid);

    @Transaction
    @Query("SELECT * FROM contacts WHERE jid = :jid COLLATE NOCASE LIMIT 1")
    abstract Single<NextivaContact> getCompleteContact(String jid);

    @Transaction
    @Query("SELECT * FROM contacts WHERE ui_name = :uiName COLLATE NOCASE LIMIT 1")
    public abstract NextivaContact getCompleteContactFromUIName(String uiName);

    @Transaction
    @Query("SELECT * FROM contacts WHERE short_jid = :userId COLLATE NOCASE LIMIT 1")
    abstract Single<NextivaContact> getCompleteContactFromUserId(String userId);

    @Transaction
    @Query("SELECT " +
            "(SELECT COUNT(DISTINCT id) FROM postal_addresses) AS addressCount, " +
            "(SELECT COUNT(DISTINCT id) FROM attachments) AS attachmentCount, " +
            "(SELECT COUNT(DISTINCT id) FROM calllogs) AS callLogCount, " +
            "(SELECT COUNT(DISTINCT id) FROM contacts) AS contactCount, " +
            "(SELECT COUNT(DISTINCT id) FROM dates) AS dateCount, " +
            "(SELECT COUNT(DISTINCT id) FROM emails) AS emailCount, " +
            "(SELECT COUNT(DISTINCT id) FROM groups) AS groupCount, " +
            "(SELECT COUNT(DISTINCT id) FROM logging) AS loggingCount, " +
            "(SELECT COUNT(DISTINCT id) FROM messages) AS chatMessageCount, " +
            "(SELECT COUNT(DISTINCT sms_id) FROM message_state) AS messageStateCount, " +
            "(SELECT COUNT(DISTINCT id) FROM participant) AS participantCount, " +
            "(SELECT COUNT(DISTINCT id) FROM phones) AS phoneCount, " +
            "(SELECT COUNT(DISTINCT id) FROM presences) AS presenceCount, " +
            "(SELECT COUNT(*) FROM recipient) AS recipientCount, " +
            "(SELECT COUNT(*) FROM sender_table) AS senderCount, " +
            "(SELECT COUNT(DISTINCT id) FROM session) AS sessionCount, " +
            "(SELECT COUNT(DISTINCT id) FROM sms_messages) AS smsMessageCount, " +
            "(SELECT COUNT(DISTINCT id) FROM social_media_accounts) AS socialMediaAccountCount, " +
            "(SELECT COUNT(DISTINCT id) FROM vcards) AS vCardCount, " +
            "(SELECT COUNT(DISTINCT id) FROM voicemails) AS voicemailCount")
    public abstract LiveData<DbTableCountModel> getTableCountsLiveData();

    @Transaction
    @Query("SELECT * FROM contacts WHERE jid COLLATE NOCASE IN (:jids) AND contact_type = " + Enums.Contacts.ContactTypes.ENTERPRISE)
    public abstract Single<List<NextivaContact>> getCompleteDirectoryContactsByJids(List<String> jids);

    @Query("SELECT contacts.*, vcards.photo_data " +
            "FROM contacts " +
            "INNER JOIN phones ON contacts.id = phones.contact_id " +
            "LEFT JOIN vcards ON vcards.contact_id = phones.contact_id " +
            "WHERE phones.stripped_number IN (:filterNumbers) " +
            "AND contacts.contact_type = :contactType " +
            "ORDER BY ui_name COLLATE NOCASE ASC, contact_type COLLATE NOCASE ASC")
    abstract List<NextivaContact> getNextivaContactsListFromPhoneNumber(@Enums.Contacts.ContactTypes.Type int contactType, String... filterNumbers);

    @Query("SELECT contacts.*, vcards.photo_data " +
            "FROM contacts " +
            "INNER JOIN phones ON contacts.id = phones.contact_id " +
            "LEFT JOIN vcards ON vcards.contact_id = phones.contact_id " +
            "WHERE (phones.number LIKE :xExtension OR phones.extension = :extension OR phones.stripped_number = :extension)" +
            "AND contacts.contact_type = :contactType " +
            "ORDER BY ui_name COLLATE NOCASE ASC, contact_type COLLATE NOCASE ASC")
    abstract List<NextivaContact> getNextivaContactsListFromExtension(@Enums.Contacts.ContactTypes.Type int contactType, String xExtension, String extension);

    @Query("SELECT contacts.ui_name " +
            "FROM contacts " +
            "INNER JOIN phones ON contacts.id = phones.contact_id " +
            "LEFT JOIN vcards ON vcards.contact_id = phones.contact_id " +
            "WHERE phones.stripped_number IN (:filterNumbers) " +
            "AND contacts.contact_type = :contactType " +
            "ORDER BY ui_name COLLATE NOCASE ASC, contact_type COLLATE NOCASE ASC LIMIT 1")
    abstract String getUiNameFromPhoneNumber(@Enums.Contacts.ContactTypes.Type int contactType, String... filterNumbers);

    @Query("SELECT contacts.ui_name " +
            "FROM contacts " +
            "INNER JOIN phones ON contacts.id = phones.contact_id " +
            "LEFT JOIN vcards ON vcards.contact_id = phones.contact_id " +
            "WHERE phones.stripped_number = :extension " +
            "AND contacts.contact_type = :contactType " +
            "ORDER BY ui_name COLLATE NOCASE ASC, contact_type COLLATE NOCASE ASC LIMIT 1")
    abstract String getUiNameFromExtension(@Enums.Contacts.ContactTypes.Type int contactType, String extension);

    @Query("SELECT contacts.contact_type_id " +
            "FROM contacts " +
            "INNER JOIN phones ON contacts.id = phones.contact_id " +
            "WHERE phones.stripped_number IN (:filterNumbers) " +
            "AND contacts.contact_type IN (:types)" +
            "ORDER BY contact_type COLLATE NOCASE ASC LIMIT 1")
    abstract String getContactTypeIdFromPhoneNumber(int[] types, String... filterNumbers);

    @Query("SELECT contacts.contact_type_id " +
            "FROM contacts " +
            "INNER JOIN phones ON contacts.id = phones.contact_id " +
            "WHERE phones.stripped_number = :extension " +
            "AND contacts.contact_type IN (:types)" +
            "ORDER BY contact_type COLLATE NOCASE ASC LIMIT 1")
    abstract String getContactTypeIdFromExtension(int[] types, String extension);

    private void clearOldContactsDataByTransactionId(AppDatabase appDatabase, int contactType, String transactionId) {
        int[] contactTypeArray = contactType == Enums.Contacts.ContactTypes.PERSONAL ?
                new int[] {Enums.Contacts.ContactTypes.PERSONAL, Enums.Contacts.ContactTypes.CONFERENCE} :
                new int[] {contactType};

        appDatabase.numberDao().clearOldPhonesByTransactionId(contactTypeArray, transactionId);
        appDatabase.emailDao().clearOldEmailsByTransactionId(contactTypeArray, transactionId);
        appDatabase.addressDao().clearOldAddressesByTransactionId(contactTypeArray, transactionId);

        if (contactType == Enums.Contacts.ContactTypes.PERSONAL) {
            appDatabase.presenceDao().clearOldPresencesByTransactionId(contactTypeArray, transactionId);
            appDatabase.vCardDao().clearOldVCardsByTransactionId(contactTypeArray, transactionId);
            appDatabase.groupDao().clearOldGroupRelationsByTransactionId(transactionId);
        }

        appDatabase.vCardDao().clearOldVCardsByTransactionId(contactTypeArray, transactionId);
        appDatabase.contactDao().clearOldContactsByTransactionId(contactTypeArray, transactionId);
    }

    @Transaction
    public void updateConnectContact(NextivaContact contact, AppDatabase appDatabase, String transactionId) {
        Long contactId = contact.getDbId();

        appDatabase.addressDao().deleteAddresses(contactId.intValue());
        appDatabase.dateDao().deleteDates(contactId.intValue());
        appDatabase.emailDao().deleteEmails(contactId.intValue());
        appDatabase.socialMediaAccountDao().deleteAccounts(contactId.intValue());
        appDatabase.numberDao().deleteNumbers(contactId.intValue());

        appDatabase.contactDao().updateContact(contact.getDbId(),
                                               contact.getUserId(),
                                               contact.getContactType(),
                                               contact.getJid(),
                                               contact.getDisplayName(),
                                               contact.getFirstName(),
                                               contact.getLastName(),
                                               contact.getHiraganaFirstName(),
                                               contact.getHiraganaLastName(),
                                               contact.getTitle(),
                                               contact.getCompany(),
                                               contact.isFavorite() ? 1 : 0,
                                               contact.getGroupId(),
                                               contact.getServerUserId(),
                                               contact.getSubscriptionState(),
                                               contact.getUiName(),
                                               contact.getUiName() != null ? String.valueOf(contact.getUiName().charAt(0)) : null,
                                               contact.getUiName(),
                                               contact.getWebsite(),
                                               contact.getDepartment(),
                                               contact.getDescription(),
                                               contact.getCreatedBy(),
                                               contact.getLastModifiedBy(),
                                               contact.getLastModifiedOn(),
                                               contact.getLookupKey(),
                                               transactionId,
                                               contact.getSortGroup(),
                                               contact.getAliases());

        if (contact.getAddresses() != null) {
            insertNewContactAddresses(contact.getAddresses(), contactId.intValue(), appDatabase, transactionId);
        }

        if (contact.getEmailAddresses() != null) {
            insertNewContactEmails(contact.getEmailAddresses(), contactId.intValue(), appDatabase, transactionId);
        }

        if (contact.getSocialMediaAccounts() != null) {
            insertNewSocialMediaAccounts(contact.getSocialMediaAccounts(), contactId.intValue(), appDatabase, transactionId);
        }

        if (contact.getDates() != null) {
            insertNewDates(contact.getDates(), contactId.intValue(), appDatabase, transactionId);
        }

        insertNewContactNumbers(contact.getAllPhoneNumbers(), contactId.intValue(), appDatabase, transactionId);

        if (contact.getPresence() != null) {
            updateContactPresence(contact.getPresence(), contactId.intValue(), appDatabase, transactionId);
        }

        if (contact.getGroups() != null) {
            updateContactGroupRelations(contact.getGroups(), contactId.intValue(), appDatabase, transactionId);
        }

        if (contact.getVCard() != null) {
            updateContactVCard(contact.getVCard(), contactId.intValue(), appDatabase, transactionId);
        }
    }

    @Transaction
    public void insertConnectContacts(List<NextivaContact> contacts, AppDatabase appDatabase, int[] contactTypeArray, String transactionId) {
        ArrayList<Address> addressList = new ArrayList<>();
        ArrayList<DbDate> dateList = new ArrayList<>();
        ArrayList<EmailAddress> emailList = new ArrayList<>();
        ArrayList<PhoneNumber> phoneList = new ArrayList<>();
        ArrayList<SocialMediaAccount> accountList = new ArrayList<>();

        appDatabase.contactDao().clearOldContactsByTransactionId(contactTypeArray, transactionId);
        appDatabase.addressDao().clearOldAddressesByTransactionId(contactTypeArray, transactionId);
        appDatabase.dateDao().clearOldDatesByTransactionId(contactTypeArray, transactionId);
        appDatabase.emailDao().clearOldEmailsByTransactionId(contactTypeArray, transactionId);
        appDatabase.numberDao().clearOldPhonesByTransactionId(contactTypeArray, transactionId);
        appDatabase.socialMediaAccountDao().clearOldAccountsByTransactionId(contactTypeArray, transactionId);

        for (NextivaContact contact : contacts) {
            if (contact != null) {
                Long contactId = appDatabase.contactDao().insertContact(new DbContact(contact, transactionId));

                if (contact.getAddresses() != null) {
                    for (Address address : contact.getAddresses()) {
                        addressList.add(new Address(null,
                                                    contactId.intValue(),
                                                    address.getAddressLineOne(),
                                                    address.getAddressLineTwo(),
                                                    address.getPostalCode(),
                                                    address.getCity(),
                                                    address.getRegion(),
                                                    address.getCountry(),
                                                    address.getLocation(),
                                                    address.getType(),
                                                    transactionId));
                    }
                }

                if (contact.getDates() != null) {
                    for (DbDate date : contact.getDates()) {
                        dateList.add(new DbDate(null,
                                                contactId.intValue(),
                                                date.getDate(),
                                                date.getType(),
                                                transactionId));
                    }
                }

                if (contact.getEmailAddresses() != null) {
                    for (EmailAddress emailAddress : contact.getEmailAddresses()) {
                        emailList.add(new EmailAddress(null,
                                                       contactId.intValue(),
                                                       emailAddress.getAddress(),
                                                       emailAddress.getType(),
                                                       emailAddress.getLabel(),
                                                       transactionId));
                    }
                }

                if (contact.getAllPhoneNumbers() != null) {
                    for (PhoneNumber phoneNumber : contact.getAllPhoneNumbers()) {
                        String numberWithoutExtension;
                        if(phoneNumber.getNumber() != null) {
                            numberWithoutExtension = phoneNumber.getNumber().split("x")[0];
                        } else {
                            numberWithoutExtension = phoneNumber.getNumber();
                        }

                        String extension = null;

                        if (phoneNumber.getNumber().contains("x")) {
                            String[] nums = phoneNumber.getNumber().split("x");
                            if(nums.length > 1 && nums[1] != null && !nums[1].isEmpty()) {
                                extension = phoneNumber.getNumber().split("x")[1];
                            }
                        }

                        phoneList.add(new PhoneNumber(
                                null,
                                contactId.intValue(),
                                phoneNumber.getNumber(),
                                CallUtil.getStrippedPhoneNumber(numberWithoutExtension),
                                phoneNumber.getType(),
                                phoneNumber.getLabel(),
                                extension,
                                phoneNumber.getPinOne(),
                                phoneNumber.getPinTwo(),
                                transactionId));
                    }
                }

                if (contact.getSocialMediaAccounts() != null) {
                    for (SocialMediaAccount account : contact.getSocialMediaAccounts()) {
                        accountList.add(new SocialMediaAccount(null,
                                                               contactId.intValue(),
                                                               account.getLink(),
                                                               account.getType(),
                                                               transactionId));
                    }
                }
            }
        }

        appDatabase.dateDao().insertAllDates(dateList);
        appDatabase.socialMediaAccountDao().insertAllAccounts(accountList);
        appDatabase.addressDao().insertAllAddress(addressList);
        appDatabase.emailDao().insertAllEmails(emailList);
        appDatabase.numberDao().insertAllNumbers(phoneList);
    }

    @Transaction
    public void insertContacts(List<NextivaContact> contacts, AppDatabase appDatabase, Integer contactType, String transactionId) {
        for (NextivaContact contact : contacts) {
            Long contactId = appDatabase.contactDao().getContactId(contact.getUserId()).onErrorReturn(throwable -> {
                return -1L;
            }).blockingGet();

            //Contact exists so we should update the current one instead of create a brand new one
            if (contactId != -1L) {
                appDatabase.contactDao().updateContact(contactId,
                                                       contact.getUserId(),
                                                       contact.getContactType(),
                                                       contact.getJid(),
                                                       contact.getDisplayName(),
                                                       contact.getFirstName(),
                                                       contact.getLastName(),
                                                       contact.getHiraganaFirstName(),
                                                       contact.getHiraganaLastName(),
                                                       contact.getTitle(),
                                                       contact.getCompany(),
                                                       contact.isFavorite() ? 1 : 0,
                                                       contact.getGroupId(),
                                                       contact.getServerUserId(),
                                                       contact.getSubscriptionState(),
                                                       contact.getUiName(),
                                                       contact.getUiName() != null ? String.valueOf(contact.getUiName().charAt(0)) : null,
                                                       contact.getUiName(),
                                                       contact.getWebsite(),
                                                       contact.getDepartment(),
                                                       contact.getDescription(),
                                                       contact.getCreatedBy(),
                                                       contact.getLastModifiedBy(),
                                                       contact.getLastModifiedOn(),
                                                       contact.getLookupKey(),
                                                       transactionId,
                                                       contact.getSortGroup(),
                                                       contact.getAliases());
                // Why contactId is Int in the database function??? it should be Long
                appDatabase.addressDao().deleteAddresses(contactId.intValue());
                if (contact.getAddresses() != null) {
                    insertNewContactAddresses(contact.getAddresses(), contactId.intValue(), appDatabase, transactionId);
                }

                if (contact.getEmailAddresses() != null) {
                    insertNewContactEmails(contact.getEmailAddresses(), contactId.intValue(), appDatabase, transactionId);
                }

                if (contact.getSocialMediaAccounts() != null) {
                    insertNewSocialMediaAccounts(contact.getSocialMediaAccounts(), contactId.intValue(), appDatabase, transactionId);
                }

                if (contact.getDates() != null) {
                    insertNewDates(contact.getDates(), contactId.intValue(), appDatabase, transactionId);
                }

                cleanUpContactsAndSmsNumbers(contact, contactId.intValue(), appDatabase);

                insertNewContactNumbers(contact.getAllPhoneNumbers(), contactId.intValue(), appDatabase, transactionId);

                if (contact.getPresence() != null) {
                    updateContactPresence(contact.getPresence(), contactId.intValue(), appDatabase, transactionId);
                }

                if (contact.getGroups() != null) {
                    updateContactGroupRelations(contact.getGroups(), contactId.intValue(), appDatabase, transactionId);
                }

                if (contact.getVCard() != null) {
                    updateContactVCard(contact.getVCard(), contactId.intValue(), appDatabase, transactionId);
                }

            } else {
                //Contact doesn't exist... insert a brand new contact
                contactId = appDatabase.contactDao().insertContact(new DbContact(contact, transactionId));

                if (contactId != -1) {
                    if (contact.getAddresses() != null) {
                        insertNewContactAddresses(contact.getAddresses(), contactId.intValue(), appDatabase, transactionId);
                    }

                    if (contact.getEmailAddresses() != null) {
                        insertNewContactEmails(contact.getEmailAddresses(), contactId.intValue(), appDatabase, transactionId);
                    }

                    if (contact.getSocialMediaAccounts() != null) {
                        insertNewSocialMediaAccounts(contact.getSocialMediaAccounts(), contactId.intValue(), appDatabase, transactionId);
                    }

                    if (contact.getDates() != null) {
                        insertNewDates(contact.getDates(), contactId.intValue(), appDatabase, transactionId);
                    }

                    cleanUpContactsAndSmsNumbers(contact, contactId.intValue(), appDatabase);

                    insertNewContactNumbers(contact.getAllPhoneNumbers(), contactId.intValue(), appDatabase, transactionId);

                    if (contact.getPresence() != null) {
                        insertNewContactPresence(contact.getPresence(), contactId.intValue(), appDatabase, transactionId);
                    }

                    if (contact.getGroups() != null) {
                        for (DbGroup group : contact.getGroups()) {
                            insertContactGroupRelations(group, contactId.intValue(), appDatabase, transactionId);
                        }
                    }

                    if (contact.getVCard() != null) {
                        insertNewContactVCard(contact.getVCard(), contactId.intValue(), appDatabase, transactionId);
                    }
                }
            }
        }

        if (contactType != Enums.Contacts.ContactTypes.NONE) {
            clearOldContactsDataByTransactionId(appDatabase, contactType, transactionId);
        }
    }

    private static void insertNewContactAddresses(@Nullable List<Address> addresses, int contactId, AppDatabase appDatabase, String transactionId) {
        if (addresses != null) {
            for (Address address : addresses) {
                    appDatabase.addressDao().insertAddress(new Address(null,
                                                                       contactId,
                                                                       address.getAddressLineOne(),
                                                                       address.getAddressLineTwo(),
                                                                       address.getPostalCode(),
                                                                       address.getCity(),
                                                                       address.getRegion(),
                                                                       address.getCountry(),
                                                                       address.getLocation(),
                                                                       address.getType(),
                                                                       transactionId));
            }
        }
    }

    private static void insertNewContactEmails(@Nullable ArrayList<EmailAddress> emailAddresses, int contactId, AppDatabase appDatabase, String transactionId) {
        if (emailAddresses != null) {
            for (EmailAddress emailAddress : emailAddresses) {
                if (appDatabase.emailDao().getEmailId(contactId, emailAddress.getAddress()) == 0) {
                    appDatabase.emailDao().insertEmail(new EmailAddress(null,
                                                                        contactId,
                                                                        emailAddress.getAddress(),
                                                                        emailAddress.getType(),
                                                                        emailAddress.getLabel(),
                                                                        transactionId));
                } else {
                    appDatabase.emailDao().updateEmail(contactId,
                                                       emailAddress.getAddress(),
                                                       emailAddress.getType(),
                                                       emailAddress.getLabel(),
                                                       transactionId);
                }
            }
        }
    }

    private static void insertNewSocialMediaAccounts(@Nullable ArrayList<SocialMediaAccount> socialMediaAccounts, int contactId, AppDatabase appDatabase, String transactionId) {
        if (socialMediaAccounts != null) {
            for (SocialMediaAccount account : socialMediaAccounts) {
                if (appDatabase.socialMediaAccountDao().getAccountId(contactId, account.getLink()) == 0) {
                    appDatabase.socialMediaAccountDao().insertAccount(new SocialMediaAccount(null,
                                                                                             contactId,
                                                                                             account.getLink(),
                                                                                             account.getType(),
                                                                                             transactionId));
                } else {
                    appDatabase.socialMediaAccountDao().updateAccount(contactId,
                                                                      account.getLink(),
                                                                      account.getType(),
                                                                      transactionId);
                }
            }
        }
    }

    private static void insertNewDates(@Nullable ArrayList<DbDate> dates, int contactId, AppDatabase appDatabase, String transactionId) {
        if (dates != null) {
            for (DbDate date : dates) {
                if (appDatabase.dateDao().getDateId(contactId, date.getDate()) == 0) {
                    appDatabase.dateDao().insertDate(new DbDate(null,
                                                                contactId,
                                                                date.getDate(),
                                                                date.getType(),
                                                                transactionId));
                } else {
                    appDatabase.dateDao().updateDate(contactId,
                                                     date.getDate(),
                                                     date.getType(),
                                                     transactionId);
                }
            }
        }
    }


    private static void cleanUpContactsAndSmsNumbers( NextivaContact contact,
                                                      int contactId,
                                                      AppDatabase appDatabase) {
        List<PhoneNumber> phoneNumbers = contact.getAllPhoneNumbers();
        String contactUserId= contact.getUserId();
        String name = contact.getDisplayName();

        List<PhoneNumber> newPhoneNumbers = new ArrayList<>();
        if(phoneNumbers != null) newPhoneNumbers.addAll(phoneNumbers);

        // 1) Get All local phone numbers
        // 2) check which new numbers match with local numbers
        // 3) add contact reference in Sms-participant table for new phone numbers
        // 4) Delete local numbers that are not present in new phones
        // 5) then delete user reference in SMS participant table for deleted numbers
        if(contactUserId != null) {
            List<PhoneNumber> localNumbers = appDatabase.numberDao().getPhoneNumbers(contactId);
            Map<String, PhoneNumber> itemsToDelete = new HashMap<>();
            int i = localNumbers.size() - 1;
            while(i >= 0) {
                PhoneNumber local = localNumbers.get(i);
                if (local.getStrippedNumber() != null) {
                    // delete duplicates here in case number is not deleted
                    if(itemsToDelete.containsKey(local.getStrippedNumber())) {
                        appDatabase.numberDao().deletePhoneByRowId(local.getId());
                    } else {
                        itemsToDelete.put(local.getStrippedNumber(), local);
                    }
                    i--;
                }
            }
            // Prepare to edit contact reference in SMS participant table
            // items to delete: itemsToDelete
            // items to add: newPhoneNumbers
            i = newPhoneNumbers.size() - 1;
            while(i >= 0) {
                PhoneNumber remote = newPhoneNumbers.get(i);
                if(itemsToDelete.remove(remote.getStrippedNumber()) != null) {
                    newPhoneNumbers.remove(i);
                }
                i--;
            }
            // Set contact reference in Sms-participant table for new phone numbers
            newPhoneNumbers.forEach(remote -> {
                                        if (name != null) {
                                            appDatabase.participantDao().setParticipantReferenceByUUID(
                                                    contactUserId,
                                                    name,
                                                    CallUtil.getStrippedNumberWithCountryCode(remote.getStrippedNumber())
                                            );
                                        }
                                    }
            );
            // Remove contact reference in Sms-participant table for deleted phone numbers
            for(Map.Entry<String, PhoneNumber> item : itemsToDelete.entrySet()) {
                PhoneNumber phoneNumber = item.getValue();
                if(phoneNumber.getStrippedNumber() != null) {
                    appDatabase.numberDao().deleteContactStrippedNumber(contactId, phoneNumber.getStrippedNumber());
                    appDatabase.participantDao().cleanParticipantReferenceByUUIDAndPhoneNumber(
                            contactUserId,
                            CallUtil.getStrippedNumberWithCountryCode(phoneNumber.getStrippedNumber())
                    );
                }
            }
        }
    }

    private static void insertNewContactNumbers(@Nullable List<PhoneNumber> phoneNumbers, int contactId, AppDatabase appDatabase, String transactionId) {
        if (phoneNumbers != null) {
            for (PhoneNumber phoneNumber : phoneNumbers) {
                if (!TextUtils.isEmpty(phoneNumber.getNumber())) {
                    String numberWithoutExtension;
                    if(phoneNumber.getNumber() != null) {
                        numberWithoutExtension = phoneNumber.getNumber().split("x")[0];
                    } else {
                        numberWithoutExtension = phoneNumber.getNumber();
                    }

                    String extension = null;
                    if (phoneNumber.getNumber().contains("x")) {
                        extension = phoneNumber.getNumber().split("x")[1];
                    }

                    if (appDatabase.numberDao().getNumberId(contactId, phoneNumber.getNumber()) == 0) {
                        appDatabase.numberDao().insertNumber(new PhoneNumber(
                                null,
                                contactId,
                                phoneNumber.getNumber(),
                                CallUtil.getStrippedPhoneNumber(numberWithoutExtension),
                                phoneNumber.getType(),
                                phoneNumber.getLabel(),
                                extension,
                                phoneNumber.getPinOne(),
                                phoneNumber.getPinTwo(),
                                transactionId));
                    } else {
                        appDatabase.numberDao().updateNumber(contactId,
                                                             phoneNumber.getNumber(),
                                                             CallUtil.getStrippedPhoneNumber(numberWithoutExtension),
                                                             phoneNumber.getType(),
                                                             phoneNumber.getLabel(),
                                                             phoneNumber.getPinOne(),
                                                             phoneNumber.getPinTwo(),
                                                             transactionId);
                    }
                }
            }
        }
    }

    private static void insertNewContactPresence(DbPresence nextivaPresence, int contactId, AppDatabase appDatabase, String transactionId) {
        nextivaPresence.setContactId(contactId);
        nextivaPresence.setTransactionId(transactionId);
        appDatabase.presenceDao().insertPresence(nextivaPresence);
    }

    private static void updateContactPresence(DbPresence nextivaPresence, int contactId, AppDatabase appDatabase, String transactionId) {
        if (appDatabase.presenceDao().getPresenceByJid(nextivaPresence.getJid())
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new DbPresence();
                })
                .blockingGet().getId() != null) {
            appDatabase.presenceDao().updatePresenceFromJid(nextivaPresence.getState(),
                                                            nextivaPresence.getType(),
                                                            nextivaPresence.getPriority(),
                                                            nextivaPresence.getStatus(),
                                                            nextivaPresence.getJid(),
                                                            transactionId);
        } else {
            appDatabase.presenceDao().insertPresence(new DbPresence(contactId, nextivaPresence, transactionId));
        }
    }

    private static void updateContactGroupRelations(List<DbGroup> groups, int contactId, AppDatabase appDatabase, String transactionId) {
        for (DbGroup group : groups) {
            if (!TextUtils.isEmpty(group.getGroupId())) {
                DbGroupRelation dbGroupRelation = appDatabase.groupDao().getGroupRelation(contactId, group.getGroupId())
                        .onErrorReturn(throwable -> {
                            FirebaseCrashlytics.getInstance().recordException(throwable);
                            return new DbGroupRelation();
                        })
                        .blockingGet();

                if (!TextUtils.equals(dbGroupRelation.getGroupId(), group.getGroupId())) {
                    insertContactGroupRelations(group, contactId, appDatabase, transactionId);

                } else {
                    appDatabase.groupDao().updateGroupRelationTransactionId(transactionId, contactId, dbGroupRelation.getGroupId());
                }
            }
        }
    }

    private static void insertContactGroupRelations(DbGroup group, int contactId, AppDatabase appDatabase, String transactionId) {
        if (!TextUtils.isEmpty(group.getGroupId())) {
            appDatabase.groupDao().insertGroupRelation(new DbGroupRelation(contactId, group.getGroupId(), transactionId));
        }
    }

    private static void insertNewContactVCard(DbVCard vCard, int contactId, AppDatabase appDatabase, String transactionId) {
        vCard.setContactId(contactId);
        vCard.setTransactionId(transactionId);
        appDatabase.vCardDao().insertVCard(vCard);
    }

    private static void updateContactVCard(DbVCard vCard, int contactId, AppDatabase appDatabase, String transactionId) {
        DbVCard dbVCard = appDatabase.vCardDao().getVCardFromUserJid(vCard.getJid())
                .onErrorReturn(throwable -> {
                    FirebaseCrashlytics.getInstance().recordException(throwable);
                    return new DbVCard();
                }).blockingGet();

        if (dbVCard != null && dbVCard.getId() != null) {
            appDatabase.vCardDao().updateVCardWithNewAvatar(vCard.getJid(), vCard.getPhotoData(), transactionId);
        } else {
            vCard.setContactId(contactId);
            vCard.setTransactionId(transactionId);

            appDatabase.vCardDao().insertVCard(vCard);
        }
    }

    private interface NextivaContactListFetcher {
        List<NextivaContact> getNextivaContacts();
    }
}
