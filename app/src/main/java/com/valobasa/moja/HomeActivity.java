package com.valobasa.moja;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class HomeActivity extends AppCompatActivity {

    private static final int RC_APP_UPDATE = 100;
    private CustomPref customPref;
    private MediaPlayer mediaPlayer;
    private AppUpdateManager mAppUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(com.google.android.play.core.install.InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showCompletedUpdate();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        customPref = new CustomPref(this);
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        inAppUpdate();
    }

    @Override
    protected void onStop() {
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Update completed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update canceled", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(result -> {
            if (result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, HomeActivity.this, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void inAppUpdate() {
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(result -> {
            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, HomeActivity.this, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showCompletedUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New app is ready!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", view -> mAppUpdateManager.completeUpdate());
        snackbar.show();
    }

    public void start(View view) {
        playSound(R.raw.click);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void favorite(View view) {
        playSound(R.raw.click);
        Intent intent = new Intent(this, Liked.class);
        startActivity(intent);
    }

    public void rateUs(View view) {
        playSound(R.raw.click);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID));
        startActivity(intent);
    }

    public void moreApps(View view) {
        String developerId = getResources().getString(R.string.DeveloperID);
        Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=" + developerId);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        playSound(R.raw.click);
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Rate us 5 stars!")
                .setIcon(R.drawable.app_icon)
                .setMessage("Would you like to give us a 5-star rating? You can also provide feedback. Do you want to exit the app?")
                .setPositiveButton("No", (dialogInterface, i) -> {
                    playSound(R.raw.click);
                    dialogInterface.dismiss();
                })
                .setNegativeButton("Exit", (dialogInterface, i) -> {
                    playSound(R.raw.click);
                    finishAndRemoveTask();
                })
                .setNeutralButton("Rate 5 Stars", (dialogInterface, i) -> {
                    playSound(R.raw.click);
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    } catch (Exception e) {
                        Toast.makeText(HomeActivity.this, "Could not open Play Store", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public void share(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void privacy(View view) {
        String privacyPolicyUrl = "https://sabingstore.blogspot.com/p/sabing-store.html?m=1";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
        startActivity(intent);
    }

    public void developer(View view) {
        Intent intent = new Intent(HomeActivity.this, DeveloperActivity.class);
        startActivity(intent);
    }

    private void playSound(int sound) {
        if (customPref.getSound()) {
            if (mediaPlayer != null) {
                mediaPlayer.release(); // Release any previous instance
            }
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(0.1f, 0.1f);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> mp.release()); // Release resources when done
            }
        }
    }
}
