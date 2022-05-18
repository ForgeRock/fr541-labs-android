//DONE CUSTOMDEVICE
package com.example.tutorial;

import android.content.Context;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.collector.DeviceCollector;
import org.forgerock.android.auth.collector.NetworkCollector;
import org.forgerock.android.auth.collector.PlatformCollector;
import org.forgerock.android.auth.collector.TelephonyCollector;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyCustomMetadataCollector implements DeviceCollector {

    private static final List<DeviceCollector> COLLECTORS = new ArrayList<>();

    static {
        //Pick from existing Collector or implement your own collector
        COLLECTORS.add(new PlatformCollector());
        COLLECTORS.add(new NetworkCollector());
        COLLECTORS.add(new TelephonyCollector());
    }

    @Override
    public String getName() {
        return "metadata";
    }

    @Override
    public void collect(Context context, FRListener<JSONObject> listener) {
        collect(context, listener, new JSONObject(), COLLECTORS);
    }
}