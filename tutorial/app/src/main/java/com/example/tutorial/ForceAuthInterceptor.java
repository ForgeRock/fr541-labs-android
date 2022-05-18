package com.example.tutorial;

import static org.forgerock.android.auth.Action.START_AUTHENTICATE;

import android.net.Uri;

import androidx.annotation.NonNull;

import org.forgerock.android.auth.Action;
import org.forgerock.android.auth.FRRequestInterceptor;
import org.forgerock.android.auth.Request;
import org.json.JSONException;

public class ForceAuthInterceptor implements FRRequestInterceptor<Action> {

    @NonNull
    @Override
    public Request intercept(@NonNull Request request, Action action) {
        if (action.getType().equals(START_AUTHENTICATE)) { //MARK: SELFSERVICE: action
            try {
                if (action.getPayload() != null && action.getPayload().getString("tree").equals("fr541-password")) { //MARK: SELFSERVICE: treename
                    return request.newBuilder()
                            .url(Uri.parse(request.url().toString())
                                    .buildUpon()
                                    .appendQueryParameter("ForceAuth", "true").toString())  //MARK: SELFSERVICE: param
                            .build();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return request;
    }
}
