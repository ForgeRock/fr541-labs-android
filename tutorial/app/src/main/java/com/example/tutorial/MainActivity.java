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

    //DONE SUSPENDED: variable
    private boolean isSuspended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //DONE TAMPER
        RootDetector rootDetector = FRRootDetector.DEFAULT;
        Logger.warn (TAG, "RootDetector score: " + rootDetector.isRooted(this));

        //DONE CUSTOMDEVICE: register
        CallbackFactory.getInstance().register(MyCustomDeviceProfileCallback.class);

        //DONE SELFSERVICE: interceptor
        RequestInterceptorRegistry.getInstance().register(new ForceAuthInterceptor());


        //DONE AUTH: init
        FRAuth.start(this);

        //DONE DEVICE: manually
        FRDevice.getInstance().getProfile(new FRListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Logger.warn(TAG, "device metadata: " + result.toString());
            }

            @Override
            public void onException(Exception e) {
                Logger.error(TAG, "Device profile collection failed: " + e.getMessage(), e);
            }
        });

//        //DONE DEVICE: alternative way
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
            //DONE SELFSERVICE:
            if (FRUser.getCurrentUser() != null) {
                FRSession.authenticate(getApplicationContext(), "fr541-password", new NodeListener<FRSession>() {
                    @Override
                    public void onCallbackReceived(Node node) {
                        Logger.warn(TAG, "callback received in self service flow");
                        MainActivity.this.onCallbackReceived(node);
                    }

                    @Override
                    public void onSuccess(FRSession result) {
                        Logger.warn(TAG, "onSuccess in selfservice flow");
                        updateStatus();
                    }

                    @Override
                    public void onException(Exception e) {
                        Logger.error(TAG, e.getMessage(), e);
                        updateStatus();
                    }
                });
            } else {
                //DONE AUTH: onclick
                FRUser.login(getApplicationContext(), this);
            }
        });

        //DONE CENTRAL: buttonListener
        centralButton.setOnClickListener(view -> {
            Logger.debug(TAG, "Centralized button is pressed");
            FRUser.browser().login(this, this);
        });

        //DONE CENTRAL: logout
        logoutButton.setOnClickListener(view -> {
            Logger.debug(TAG, "Logout button is pressed");
            try {
                FRUser.getCurrentUser().logout();
            } catch (Exception e) {
                Logger.error(TAG, e.getMessage(), e);
            }
            updateStatus();
        });

        updateStatus();

        if (getIntent() != null) {handleIntent(getIntent());}
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        Logger.warn(TAG, "applink" + appLinkAction + ", " + intent.getDataString());
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {

            //DONE SUSPENDED: resume
            MainActivity.this.isSuspended = false;
            FRSession.authenticate(getApplicationContext(), appLinkData, new NodeListener<FRSession>() {
                @Override
                public void onCallbackReceived(Node node) {
                    Logger.warn(TAG, "link handler onCallbackReceived");
                }

                @Override
                public void onSuccess(FRSession result) {
                    Logger.warn(TAG, "link handler onSuccess");
                    updateStatus();
                }

                @Override
                public void onException(Exception e) {
                    Logger.warn(TAG, "link handler" + e.getMessage(), e);

                }
            });
        }
    }

    //DONE SUSPENDED: newintent
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    //Update the login status
    private void updateStatus() {
        runOnUiThread(() -> {
            //TODO SUSPENDED: status
            if (MainActivity.this.isSuspended) {
                status.setText("Check your email");
                loginButton.setEnabled(false);
            } else if (FRUser.getCurrentUser() == null) {
                status.setText("User is not authenticated");
                //TODO USERINFO: get userinfo or tokeninfo and display

                //DONE CENTR 11: comment setEnabled
                loginButton.setText("Login");
                loginButton.setEnabled(true);
                logoutButton.setEnabled(false);
            } else {
                status.setText("User is authenticated");

                //DONE SELFSERVICE: button
                loginButton.setText("Chg pwd");
                logoutButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onCallbackReceived(Node node) {
        runOnUiThread(() -> {

            //DONE FOLLOW: handle
            //DONE STAGE: if
            String stage = node.getStage();
            if ("namepass".equals(stage)) {
                NodeDialogFragment fragment = NodeDialogFragment.newInstance(node);
                fragment.show(getSupportFragmentManager(), NodeDialogFragment.class.getName());
            } else {

                //DONE AUTH: getcallback
                Callback callback = node.getCallbacks().get(0);

                //DONE DEVICE: handle choicecallback
                if (callback instanceof ChoiceCallback) {
                    Logger.warn(TAG, "ChoiceCallback");
                    ChoiceCallbackDialogFragment fragment = ChoiceCallbackDialogFragment.newInstance(node);
                    fragment.show(getSupportFragmentManager(), ChoiceCallbackDialogFragment.class.getName());

                //DONE SOCIAL: SelectIdpCallback
                } else if (callback instanceof SelectIdPCallback) {
                    Logger.warn(TAG, "SelectIdPCallback");
                    SelectIdpDialogFragment fragment = SelectIdpDialogFragment.newInstance(node);
                    fragment.show(getSupportFragmentManager(), SelectIdpDialogFragment.class.getName());

                // DONE SOCIAL: IdPCallback
                } else if (callback instanceof IdPCallback) {
                    Logger.warn(TAG, "IdPCallback");
                    ((IdPCallback) callback).signIn(null, new FRListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            node.next(MainActivity.this, MainActivity.this);
                            updateStatus();
                        }

                        @Override
                        public void onException(Exception e) {
                            Logger.error(TAG, e.getMessage(), e);
                            node.next(MainActivity.this, MainActivity.this);
                        }
                    });

                //DONE WEBAUTHN: handle registration
                } else if (callback instanceof WebAuthnRegistrationCallback) {
                    Logger.warn(TAG, "WebAuthn Registration" + callback.getContent());
                    ((WebAuthnRegistrationCallback) callback).register(node, new FRListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Logger.warn(TAG, "reg success branch");
                            node.next(MainActivity.this, MainActivity.this);
                        }

                        @Override
                        public void onException(Exception e) {
                            Logger.error(TAG, e.getMessage(), e);
                            displayToast("WebAuthn Registration Error!");
                            node.next(MainActivity.this, MainActivity.this);
                        }
                    });

                //DONE WEBAUTHN: handle authentication
                } else if (callback instanceof WebAuthnAuthenticationCallback) {
                    Logger.warn(TAG, "Webauthn Authn");
                    ((WebAuthnAuthenticationCallback) callback).authenticate(node, null, new FRListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            node.next(MainActivity.this, MainActivity.this);
                        }

                        @Override
                        public void onException(Exception e) {
                            Logger.error(TAG, e.getMessage(), e);
                            displayToast("WebAuthn Error (not registered?)!");
                            node.next(MainActivity.this, MainActivity.this);
                        }
                    });

                    //DONE DEVICE: handle callback
                } else if (callback instanceof DeviceProfileCallback) {
                    Logger.warn(TAG, "Device Profile");
                    Context context = getApplicationContext();

                    //MAR CUSTOMDEVICE: note that this is actually MyCustomDeviceProfileCallback
                    ((DeviceProfileCallback) callback).execute(context, new FRListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Logger.warn(TAG, "device success branch");
                            displayToast("Device Profile Collected");

                            node.next(context /* QQ George, is it `getapplicationcontext` or `this` or what when? */,
                                    MainActivity.this);
                        }

                        @Override
                        public void onException(Exception e) {
                            Logger.error(TAG, e.getMessage(), e);
                            displayToast("Device Profile collection error");
                        }
                    });

                //DONE REGISTER: handle
                } else if (callback instanceof StringAttributeInputCallback) {
                    Logger.warn(TAG, "String Attribute Input Callback");
                    StringAttributesDialogFragment fragment = StringAttributesDialogFragment.newInstance(node);
                    fragment.show(getSupportFragmentManager(), StringAttributesDialogFragment.class.getName());

                //DONE SELFSERVICE: handle
                } else if (node.getCallback(NameCallback.class) == null && node.getCallback(PasswordCallback.class) != null) {
                    Logger.warn(TAG, "only PasswordCallback");
                    PasswordOnlyDialogFragment fragment = PasswordOnlyDialogFragment.newInstance(node);
                    fragment.show(getSupportFragmentManager(), PasswordOnlyDialogFragment.class.getName());

                } else if (node.getCallback(NameCallback.class) != null && node.getCallback(PasswordCallback.class) == null) {
                    Logger.warn(TAG, "only NameCallback");
                    NameOnlyDialogFragment fragment = NameOnlyDialogFragment.newInstance(node);
                    fragment.show(getSupportFragmentManager(), NameOnlyDialogFragment.class.getName());


                //DONE SUSPENDED: handle callback
                } else if (node.getCallback(SuspendedTextOutputCallback.class) != null) {
                    Logger.warn(TAG, "suspended callback received");
                    MainActivity.this.isSuspended = true;
                    updateStatus();

                } else {
                    //DONE AUTH: dialog
                    NodeDialogFragment fragment = NodeDialogFragment.newInstance(node);
                    fragment.show(getSupportFragmentManager(), NodeDialogFragment.class.getName());
                }

            //TODO STAGE: else ends here
            }
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