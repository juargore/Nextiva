package com.nextiva.nextivaapp.android.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseViewModel extends AndroidViewModel {

    protected final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public void clearCompositeDisposable() {
        mCompositeDisposable.clear();
    }

    @Override
    protected void onCleared() {
        mCompositeDisposable.clear();
        super.onCleared();
    }

    @VisibleForTesting
    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }
}
