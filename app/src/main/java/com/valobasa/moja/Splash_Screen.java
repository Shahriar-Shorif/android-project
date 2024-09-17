package com.valobasa.moja;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class Splash_Screen extends AdsInitMOB {
    ImageView logo;
    TextView name;
    ProgressBar progress;
    private ConsentInformation consentInformation;
    ConsentDebugSettings debugSettings;
    ConsentRequestParameters params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        logo = findViewById(R.id.logo);
        name = findViewById(R.id.name);
        progress = findViewById(R.id.progress);
        Animation slideLeft = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation slideRight = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        logo.startAnimation(slideLeft);
        name.startAnimation(slideRight);
        progress.startAnimation(slideLeft);


        if (isNetworkConnected()){

            params = new ConsentRequestParameters
                    .Builder()
                    .setTagForUnderAgeOfConsent(false)
                    .build();

            consentInformation = UserMessagingPlatform.getConsentInformation(this);


            if (consentInformation.canRequestAds()) {
                showOpenAds();
            } else {

                consentInformation.requestConsentInfoUpdate(
                        this,
                        params,
                        (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                                    this,
                                    (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                        if (loadAndShowError != null) {
                                            // Consent gathering failed.
                                            Log.w(TAG, String.format("%s: %s",
                                                    loadAndShowError.getErrorCode(),
                                                    loadAndShowError.getMessage()));
                                            startApp();

                                        }

                                        // Consent has been gathered.
                                        if (consentInformation.canRequestAds()) {
                                            showOpenAds();
                                        }
                                    }
                            );
                        },
                        (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                            startApp();

                            // Consent gathering failed.
                            Log.w(TAG, String.format("%s: %s",
                                    requestConsentError.getErrorCode(),
                                    requestConsentError.getMessage()));
                        });
            }



        } else {
            startApp();
        }



    }

    private void startApp() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash_Screen.this, HomeActivity.class));
                finish();
            }
        }, 3000);

    }



    private void showOpenAds() {
        AppOpenAd.load(
                Splash_Screen.this,
                getResources().getString(R.string.OPEN_ADS_ID),
                new AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        ad.show(Splash_Screen.this);
                        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                startActivity(new Intent(Splash_Screen.this, HomeActivity.class));
                                finish();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                startActivity(new Intent(Splash_Screen.this, HomeActivity.class));
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle ad loading failure
                        startActivity(new Intent(Splash_Screen.this, HomeActivity.class));
                        finish();
                    }



                }
        );
    }




    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}