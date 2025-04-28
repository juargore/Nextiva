package com.nextiva.nextivaapp.android.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.databinding.FragmentAboutPrivacyBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AboutPrivacyFragment extends Fragment {
    protected WebView mPrivacyWebView;

    @Inject
    protected IntentManager mIntentManager;
    @Inject
    protected SettingsManager mSettingsManager;

    public AboutPrivacyFragment() {

    }

    public static AboutPrivacyFragment newInstance() {

        AboutPrivacyFragment fragment = new AboutPrivacyFragment();

        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;

    }


    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }


    @Override
    @SuppressLint("RestrictedApi")
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = bindViews(inflater, container);

        mPrivacyWebView.getSettings().setLoadsImagesAutomatically(true);
        mPrivacyWebView.getSettings().setJavaScriptEnabled(true);
        mPrivacyWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mPrivacyWebView.loadUrl(getString(R.string.license_privacy_url));

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(mPrivacyWebView.getSettings(),
                                           getActivity() != null && ApplicationUtil.isNightModeEnabled(getActivity(), mSettingsManager) ?
                                                   WebSettingsCompat.FORCE_DARK_ON : WebSettingsCompat.FORCE_DARK_OFF);
        }

        mPrivacyWebView.setWebViewClient(WebViewClient());


        // Inflate the layout for this fragment

        return view;

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

    private View bindViews(LayoutInflater inflater, ViewGroup container) {
        FragmentAboutPrivacyBinding binding = FragmentAboutPrivacyBinding.inflate(inflater, container, false);

        mPrivacyWebView = binding.privacyFragmentWebView;

        return binding.getRoot();
    }

}
