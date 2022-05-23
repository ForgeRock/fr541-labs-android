/*
 *  Copyright (c) 2022 ForgeRock. All rights reserved.
 *
 *  This software may be modified and distributed under the terms
 *  of the MIT license. See the LICENSE file for details.
 */

package com.example.tutorial;

import android.app.Application;

import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.Logger;

public class TutorialApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.set(Logger.Level.DEBUG);
        //FRAuth.start(this);

    }
}
