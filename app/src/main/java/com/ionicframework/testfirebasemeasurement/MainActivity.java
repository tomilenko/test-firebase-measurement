package com.ionicframework.testfirebasemeasurement;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.gms.analytics.CampaignTrackingReceiver;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    InstallReferrerClient referrerClient;
    private final String prefKey = "checkedInstallReferrer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (getPreferences(MODE_PRIVATE).getBoolean(prefKey, false)) { // Referrer information should be get only once
            checkInstallReferrer();
//        }
    }

    private void checkInstallReferrer() {
        referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerResponse.OK:
                        getInstallReferrerFromClient();
                        referrerClient.endConnection(); // Closing the connection will help avoid leaks and performance problems.
//                        getPreferences(MODE_PRIVATE).edit().putBoolean(prefKey, false).apply(); // Referrer information should be get only once
                        break;
                    case InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    private void getInstallReferrerFromClient() {
        Log.d("TAG", "getInstallReferrerFromClient: ");
        ReferrerDetails response = null;
        Toast.makeText(getApplicationContext(), "getInstallReferrerFromClient", Toast.LENGTH_LONG).show();
        try {
            response = referrerClient.getInstallReferrer();
            String referrerUrl = response.getInstallReferrer();
            long referrerClickTime = response.getReferrerClickTimestampSeconds();
            long appInstallTime = response.getInstallBeginTimestampSeconds();
            boolean instantExperienceLaunched = response.getGooglePlayInstantParam();

            // TODO: 12/04/20  Parse referrerUrl and send params to the internal campaign tracker -

            ((TextView) findViewById(R.id.referrerUrl)).setText(referrerUrl);
            /* ???
            String[] params = referrer.split("&");
            for (String param : params) {
                // collect param's and send them to the internal campaign tracker
            }
            */

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        // Make sure the connection is ended, closing the connection will help avoid leaks and performance problems.
        if (referrerClient != null) {
            referrerClient.endConnection();
        }
        super.onDestroy();
    }
}
