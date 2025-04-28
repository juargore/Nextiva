package com.nextiva.nextivaapp.android.interfaces;

import com.nextiva.nextivaapp.android.fab.FloatingActionButton;

public interface FabFragmentListener {

    void onFabClicked(FloatingActionButton fab);

    void onFabSpeedDialClicked(int position);
}
