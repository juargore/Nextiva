/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import com.nextiva.nextivaapp.android.models.NextivaContact;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by adammacdonald on 2/27/18.
 */

public interface LocalContactsManager {

    Completable getLocalContacts();

    Single<ArrayList<NextivaContact>> getLocalContactsWithReturn();
}
