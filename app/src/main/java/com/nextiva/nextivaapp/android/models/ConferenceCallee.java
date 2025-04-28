package com.nextiva.nextivaapp.android.models;


/**
 * Created by Thaddeus Dannar on 2019-09-03.
 */
public class ConferenceCallee {

    private String mName;
    private String mNumber;

    public ConferenceCallee() {
    }

    public ConferenceCallee(String name, String number) {
        mName = name;
        mNumber = number;
    }

    public String getName() {
        return mName;
    }

    public void setName(final String name) {
        mName = name;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(final String number) {
        mNumber = number;
    }
}
