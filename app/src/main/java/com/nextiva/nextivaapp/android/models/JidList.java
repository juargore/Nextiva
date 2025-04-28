package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 2019-11-04.
 */
public class JidList implements Serializable {
    private ArrayList<String> mJidList;

    public JidList(@Nullable ArrayList<String> jidList) {
        mJidList = jidList;
    }

    public ArrayList<String> getJidList() {
        return mJidList;
    }

    public void setJidList(final ArrayList<String> jidList) {
        mJidList = jidList;
    }
}
