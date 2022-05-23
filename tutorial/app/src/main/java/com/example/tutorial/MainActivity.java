/*
 *  Copyright (c) 2022 ForgeRock. All rights reserved.
 *
 *  This software may be modified and distributed under the terms
 *  of the MIT license. See the LICENSE file for details.
 */

package com.example.tutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.FRDevice;
import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.Logger;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;
import org.forgerock.android.auth.RequestInterceptorRegistry;
import org.forgerock.android.auth.callback.Callback;
import org.forgerock.android.auth.callback.CallbackFactory;
import org.forgerock.android.auth.callback.ChoiceCallback;
import org.forgerock.android.auth.callback.DeviceProfileCallback;
import org.forgerock.android.auth.callback.IdPCallback;
import org.forgerock.android.auth.callback.NameCallback;
import org.forgerock.android.auth.callback.PasswordCallback;
import org.forgerock.android.auth.callback.SelectIdPCallback;
import org.forgerock.android.auth.callback.StringAttributeInputCallback;
import org.forgerock.android.auth.callback.SuspendedTextOutputCallback;
import org.forgerock.android.auth.callback.WebAuthnAuthenticationCallback;
import org.forgerock.android.auth.callback.WebAuthnRegistrationCallback;
import org.forgerock.android.auth.detector.FRRootDetector;
import org.forgerock.android.auth.detector.RootDetector;
import org.json.JSONObject;

//MARK AUTH: NodeListener
public class MainActivity extends AppCompatActivity implements NodeListener<FRUser> {

    private static final String TAG = MainActivity.class.getName();

    private TextView status;
    private Button loginButton;
    private Button logoutButton;
    private Button centralButton;

    //TODO SUSPENDED: variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO TAMPER
        Logger.warn(TAG, "RootDetector score: ");

        //TODO CUSTOMDEVICE: register

        //TODO SELFSERVICE: interceptor

        //TODO AUTH: init

        //TODO DEVICE: manually


//        //MARK DEVICE: alternative way
//        FRDeviceCollector.DEFAULT.collect(this, new FRListener<JSONObject>() {
//            @Override
//            public void onSuccess(JSONObject result) {
//                Logger.warn(TAG, "device profile: " + result.toString());
//            }
//
//            @Override
//            public void onException(Exception e) {
//
//            }
//        });

        setContentView(R.layout.activity_main);
        status = findViewById(R.id.status);
        loginButton = findViewById(R.id.login);
        logoutButton = findViewById(R.id.logout);
        centralButton = findViewById(R.id.central);

        loginButton.setOnClickListener(view -> {
            Logger.debug(TAG, "Login button is pressed");
            //TODO SELFSERVICE:

            //TODO AUTH: onclick

        });

        //TODO CENTRAL: buttonListener
        centralButton.setOnClickListener(view -> {

        });

        //TODO CENTRAL: logout
        logoutButton.setOnClickListener(view -> {

        });

        updateStatus();

        if (getIntent() != null) {
            handleIntent(getIntent());
        }
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        Logger.warn(TAG, "applink" + appLinkAction + ", " + intent.getDataString());
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {

            //TODO SUSPENDED: resume

        }
    }

    //TODO SUSPENDED: newintent


    //Update the login status
    private void updateStatus() {
        runOnUiThread(() -> {
            //TODO SUSPENDED: status
            if (FRUser.getCurrentUser() == null) {
                status.setText("User is not authenticated");
                //TODO USERINFO: get userinfo or tokeninfo and display

                loginButton.setText("Login");
                loginButton.setEnabled(true);
                logoutButton.setEnabled(false);
            } else {
                status.setText("User is authenticated");

                //TODO SELFSERVICE: button
                      }
        });
    }

    @Override
    public void onCallbackReceived(Node node) {
        runOnUiThread(() -> {

            //DONE FOLLOW: handle
            //TODO STAGE: if


                //TODO AUTH: getcallback

                //TODO DEVICE: handle choicecallback

                //TODO SOCIAL: SelectIdpCallback


                //TODO SOCIAL: IdPCallback


                //TODO WEBAUTHN: handle registration

                //TODO WEBAUTHN: handle authentication


                //TODO DEVICE: handle callback


                //TODO REGISTER: handle


                //TODO SELFSERVICE: handle

                if (node.getCallback(NameCallback.class) != null && node.getCallback(PasswordCallback.class) == null) {
                    Logger.warn(TAG, "only NameCallback");
                    NameOnlyDialogFragment fragment = NameOnlyDialogFragment.newInstance(node);
                    fragment.show(getSupportFragmentManager(), NameOnlyDialogFragment.class.getName());


                //TODO SUSPENDED: handle callback


                } else {
                    //TODO AUTH: dialog

                }

                //MARK STAGE: else ends here
//            }
        });
    }

    @Override
    public void onSuccess(FRUser result) {
        Logger.debug(TAG, "onSuccess in MainActivity");
        updateStatus();

    }

    @Override
    public void onException(Exception e) {
        Logger.error(TAG, e.getMessage(), e);
        displayToast("Login failed!");
    }

    private void displayToast(CharSequence message) {
        runOnUiThread(() -> {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, message, duration);
            toast.show();
        });
    }

}