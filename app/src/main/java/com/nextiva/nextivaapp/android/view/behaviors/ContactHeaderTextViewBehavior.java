/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view.behaviors;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.CollapsingToolbarLayout;

@SuppressWarnings("unused")
class ContactHeaderTextViewBehavior extends CoordinatorLayout.Behavior<TextView> {

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull TextView child, @NonNull View dependency) {
        return dependency instanceof CollapsingToolbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull TextView child, @NonNull View dependency) {

        modifyChildDependencyState(child, dependency);
        return true;
    }

    private void modifyChildDependencyState(TextView child, View dependency) {
        child.setX(dependency.getX() / 2);
    }
}
