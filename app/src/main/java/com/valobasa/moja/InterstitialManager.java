package com.valobasa.moja;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdLoadCallback;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class InterstitialManager {
    private static final String TAG = "InterstitialManager";
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"; // Replace with your interstitial ad unit ID

    private InterstitialAd mInterstitialAd;
    private Activity mActivity;

    public InterstitialManager(Activity activity) {
        this.mActivity = activity;
        loadInterstitialAd();
    }

    /** Loads an interstitial ad. */
    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(mActivity, AD_UNIT_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.d(TAG, "Interstitial ad loaded.");
                    }


                });
    }

    /** Shows the interstitial ad if one is loaded. */
    public void showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed.");
                    mInterstitialAd = null; // Load a new ad
                    loadInterstitialAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.d(TAG, "Failed to show interstitial ad: " + adError.getMessage());
                    mInterstitialAd = null; // Load a new ad
                    loadInterstitialAd();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad showed.");
                }
            });
            mInterstitialAd.show(mActivity);
        } else {
            Log.d(TAG, "Interstitial ad is not loaded yet.");
        }
    }
}
