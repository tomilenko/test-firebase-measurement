package com.ionicframework.testfirebasemeasurement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.measurement.AppMeasurementReceiver;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
//import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();
    public AppMeasurementReceiver appMeasurementReceiver;
    private final String prefKey = "checkedInstallReferrer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appMeasurementReceiver = new AppMeasurementReceiver();
        checkInstallReferrer();
    }

    private void checkInstallReferrer() {
        Log.d("TAG", "checkInstallReferrer: 1");
        if (getPreferences(MODE_PRIVATE).getBoolean(prefKey, false)) {
            Log.d("TAG", "checkInstallReferrer: 2");
//            return;
        }

        Log.d("TAG", "checkInstallReferrer: 3");

        final InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(this).build();
        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getInstallReferrerFromClient(referrerClient);
            }
        });
//        appMeasurementReceiver.doGoAsync();
    }

    void getInstallReferrerFromClient(final InstallReferrerClient referrerClient) {
        Log.d("TAG", "getInstallReferrerFromClient: ");
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        ReferrerDetails response = null;
                        try {
                            response = referrerClient.getInstallReferrer();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            return;
                        }
                        final String referrerUrl = response.getInstallReferrer();
                        Log.d("TAG", "onInstallReferrerSetupFinished: 1" + new Gson().toJson(response));
                        Log.d("TAG", "onInstallReferrerSetupFinished: 2" + new Gson().toJson(referrerUrl));
                        Log.d("TAG", "onInstallReferrerSetupFinished: 3" + response);
                        Log.d("TAG", "onInstallReferrerSetupFinished: 4" + referrerUrl);




                        // TODO: If you're using GTM, call trackInstallReferrerforGTM instead.
                        trackInstallReferrer(referrerUrl);


                        Log.d(TAG, "onInstallReferrerSetupFinished: ");

//                        ReferrerDetails responseT = referrerClient.getInstallReferrer();
//                        String referrerUrl = response.getInstallReferrer();
//                        long referrerClickTime = response.getReferrerClickTimestampSeconds();
//                        long appInstallTime = response.getInstallBeginTimestampSeconds();
//                        boolean instantExperienceLaunched = response.getGooglePlayInstantParam();





                        // Only check this once.
                        getPreferences(MODE_PRIVATE).edit().putBoolean(prefKey, true).apply();

                        // End the connection
                        referrerClient.endConnection();

                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {

            }
        });
    }

    // Tracker for Classic GA (call this if you are using Classic GA only)
    private void trackInstallReferrer(final String referrerUrl) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
//                CampaignTrackingReceiver receiver = new CampaignTrackingReceiver();
//                Intent intent = new Intent("com.android.vending.INSTALL_REFERRER");
//                intent.putExtra("referrer", referrerUrl);
//                receiver.onReceive(getApplicationContext(), intent);
            }
        });
    }

    // Tracker for GTM + Classic GA (call this if you are using GTM + Classic GA only)
    private void trackInstallReferrerforGTM(final String referrerUrl) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
//                InstallReferrerReceiver receiver = new InstallReferrerReceiver();
//                Intent intent = new Intent("com.android.vending.INSTALL_REFERRER");
//                intent.putExtra("referrer", referrerUrl);
//                receiver.onReceive(getApplicationContext(), intent);
            }
        });
    }
}
