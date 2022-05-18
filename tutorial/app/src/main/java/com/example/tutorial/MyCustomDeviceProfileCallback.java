//DONE CUSTOMDEVICE
package com.example.tutorial;

import android.content.Context;

import androidx.annotation.Keep;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.Listener;
import org.forgerock.android.auth.callback.DeviceProfileCallback;
import org.forgerock.android.auth.collector.FRDeviceCollector;
import org.forgerock.android.auth.collector.LocationCollector;
import org.json.JSONObject;

public class MyCustomDeviceProfileCallback extends DeviceProfileCallback {

    public MyCustomDeviceProfileCallback() {
    }

    @Keep
    public MyCustomDeviceProfileCallback(JSONObject jsonObject, int index) {
        super(jsonObject, index);
    }

    @Override
    public void execute(Context context, FRListener<Void> listener) {
        FRDeviceCollector.FRDeviceCollectorBuilder builder = FRDeviceCollector.builder();
        if (isMetadata()) {
            builder.collector(new MyCustomMetadataCollector());
        }
        if (isLocation()) {
            builder.collector(new LocationCollector());
        }

        builder.build().collect(context, new FRListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                setValue(result.toString());
                Listener.onSuccess(listener, null);
            }

            @Override
            public void onException(Exception e) {
                Listener.onException(listener, null);
            }
        });
    }
}
