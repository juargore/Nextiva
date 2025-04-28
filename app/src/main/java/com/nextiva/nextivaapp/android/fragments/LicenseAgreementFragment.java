package com.nextiva.nextivaapp.android.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.databinding.FragmentLicenseBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


/**
 * A simple {@link Fragment} subclass.
 */
@AndroidEntryPoint
public class LicenseAgreementFragment extends BaseFragment {

    protected WebView mLicenseWebView;

    @Inject
    protected IntentManager mIntentManager;
    @Inject
    protected SettingsManager mSettingsManager;

    public LicenseAgreementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() == null) {
            return;
        }

    }

    @Override
    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = bindViews(inflater, container);

        mLicenseWebView.getSettings().setLoadsImagesAutomatically(true);
        mLicenseWebView.getSettings().setJavaScriptEnabled(true);
        mLicenseWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mLicenseWebView.loadUrl(getString(R.string.license_terms_conditions_url));

        try {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(mLicenseWebView.getSettings(),
                                               getActivity() != null && ApplicationUtil.isNightModeEnabled(getActivity(), mSettingsManager) ?
                                                       WebSettingsCompat.FORCE_DARK_ON : WebSettingsCompat.FORCE_DARK_OFF);
            }
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            FirebaseCrashlytics.getInstance().recordException(e);

        }

        mLicenseWebView.setWebViewClient(WebViewClient());
        // Inflate the layout for this fragment
        return view;
    }

    private View bindViews(LayoutInflater inflater, ViewGroup container) {
        FragmentLicenseBinding binding = FragmentLicenseBinding.inflate(inflater, container, false);

        mLicenseWebView = binding.licenseFragmentWebView;

        return binding.getRoot();
    }

    private WebViewClient WebViewClient() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        };
    }
}
