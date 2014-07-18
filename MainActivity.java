/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.example.game; // CHANGE THIS TO MATCH THE PACKAGE NAME FROM YOUR PROJECT.XML

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

/**
 * Welcome to the world of Android Market licensing. We're so glad to have you
 * onboard!
 * <p>
 * The first thing you need to do is get your hands on your public key.
 * Update the BASE64_PUBLIC_KEY constant below with your encoded public key,
 * which you can find on the
 * <a href="http://market.android.com/publish/editProfile">Edit Profile</a>
 * page of the Market publisher site.
 * <p>
 * Log in with the same account on your Cupcake (1.5) or higher phone or
 * your FroYo (2.2) emulator with the Google add-ons installed. Change the
 * test response on the Edit Profile page, press Save, and see how this
 * application responds when you check your license.
 * <p>
 * After you get this sample running, peruse the
 * <a href="http://developer.android.com/guide/publishing/licensing.html">
 * licensing documentation.</a>
 */

public class MainActivity extends org.haxe.lime.GameActivity {

	private static final String BASE64_PUBLIC_KEY = "REPLACE THIS WITH YOUR PUBLIC KEY";

    // Generate your own 20 random bytes, and put them here.
    private static final byte[] SALT = new byte[] {
        -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64,
        89
    };
    
    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    
    // A handler on the UI thread.
    private Handler mLVLHandler;

    @Override
    public void onCreate(Bundle state) {
    
        super.onCreate(state);
        
        mLVLHandler = new Handler();
        
        // Try to use more data here. ANDROID_ID is a single point of attack.
        String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
 
 		Log.i("MainActivityLicense", "License check for package: " + getPackageName() + " and device: " + deviceId);
 		
        // Library calls this when it's done.
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        // Construct the LicenseChecker with a policy.
        mChecker = new LicenseChecker(
            this, new ServerManagedPolicy(this,
                new AESObfuscator(SALT, getPackageName(), deviceId)),
            BASE64_PUBLIC_KEY);
            
        doLicenseCheck();
    }

    protected Dialog onCreateDialog(int id) {
        final boolean bRetry = id == 1;
        return new AlertDialog.Builder(this)
            .setTitle("Application not licensed.")
            .setMessage(bRetry ? "Unable to validate license. Check to see if a network connection is available." : "This application is not licensed. Please purchase it from the Play Store.")
            .setPositiveButton(bRetry ? "Retry" : "Buy app", new DialogInterface.OnClickListener() {
                boolean mRetry = bRetry;
                public void onClick(DialogInterface dialog, int which) {
                    if ( mRetry ) {
                        doLicenseCheck();
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "http://market.android.com/details?id=" + getPackageName()));
                            startActivity(marketIntent);
                        finish();
                    }
                }
            })
            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).create();
    }

    private void doLicenseCheck() {
    	Log.i("MainActivityLicense", "checking license...");
        mChecker.checkAccess(mLicenseCheckerCallback);
    }

    private void displayResult(final String result) {
        mLVLHandler.post(new Runnable() {
            public void run() {
                Log.i("MainActivityLicense", "License check result: " + result);
            }
        });
    }
    
    private void displayDialog(final boolean showRetry) {
        mLVLHandler.post(new Runnable() {
            public void run() {
                showDialog(showRetry ? 1 : 0);
            }
        });
    }    
    
    private void destroyLicenseChecker () {
    	if (mChecker != null) {
    	 	Log.i("MainActivityLicense", "Destroying LicenseChecker..");
    		mChecker.onDestroy();
    		mChecker = null;
    	}
    }

    
    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
    	
        public void allow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should allow user access.
            displayResult("Access allowed.");
        }

        public void dontAllow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            displayResult("Access disallowed.");
            // Should not allow access. In most cases, the app should assume
            // the user has access unless it encounters this. If it does,
            // the app should inform the user of their unlicensed ways
            // and then either shut down the app or limit the user to a
            // restricted set of features.
            // In this example, we show a dialog that takes the user to Market.
            // If the reason for the lack of license is that the service is
            // unavailable or there is another problem, we display a
            // retry button on the dialog and a different message.
            displayDialog(policyReason == Policy.RETRY);
            if (policyReason != Policy.RETRY) {
            	destroyLicenseChecker();
            }
        }

        public void applicationError(int errorCode) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // This is a polite way of saying the developer made a mistake
            // while setting up or calling the license checker library.
            // Please examine the error code and fix the error.
            
            /*
             * LICENSED = 0x0;
			 * NOT_LICENSED = 0x1;
			 * LICENSED_OLD_KEY = 0x2;
			 * ERROR_NOT_MARKET_MANAGED = 0x3;
			 * ERROR_SERVER_FAILURE = 0x4;
			 * ERROR_OVER_QUOTA = 0x5;
			 *
			 * ERROR_CONTACTING_SERVER = 0x101;
			 * ERROR_INVALID_PACKAGE_NAME = 0x102;
			 * ERROR_NON_MATCHING_UID = 0x103;
             */
            
            String result = String.format("Application error: " + errorCode);
            if (errorCode == 1) {
            	displayDialog(false);
            }
            displayResult(result);
            destroyLicenseChecker();
        }
    }

}
