// Copyright 2004-present Facebook. All Rights Reserved.

package com.facebook.samples.AdUnitsSample;

import android.app.Application;
import android.os.StrictMode;

import com.facebook.ads.AdSettings;
import com.facebook.samples.ads.debugsettings.DebugSettings;

import static com.facebook.ads.BuildConfig.*;

public class AdUnitsSampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        DebugSettings.initialize(this);
        if (DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }
}
