openfl_playlicensing
====================

Integrate Play Store Licensing into your OpenFL project targeting Android.

INSTRUCTIONS FOR USE WITH OPENFL:

1. Download the Play Licensing library via the SDK manager

2. Copy it to another location (if you need to modify it)
3. 
3. You'll also need to tell the project where the Android SDK is located. You do this by running the following command from `android-sdk/tools`:

```android list targets```

_Choose the target number that matches your API level and use it in the next command_

```android update lib-project --path C:/path/to/your/copy/of/LVL --target 8```


4. Copy and place this MainActivity.java file into a templates directory in your project directory (you can change the location if you wish)

5. Follow the Android Market Licensing instructions on how to put in your app's public key and your own salt. Keep in mind you must have created the app entry in the Google Play Developer Console in order to get a key.

6. Add these lines to your project XML (changing the paths as appropriate):

```
    <set name="googleplay" if="android" /> <!-- COMMENT OUT THIS LINE IF TARGETING OTHER STORES -->
    <template path="templates/MainActivity.java" rename="src/com/example/game/MainActivity.java" if="googleplay" />
    <android permission="com.android.vending.CHECK_LICENSE" if="googleplay" />
    <dependency name="LicensingVerificationLibrary" path="/Path/To/play_licensing/library/" if="googleplay" />
```	
_Keep in mind that this is REPLACING your MainActivity.java. If you are already using a custom MainActivity.java you will need to find another way to integrate this into your project._
	
7. Test on your device with Eclipse/ADT Running. Open the DDMS Perspective and filter Logcat to tag:MainActivityLicense

8. You should get an Error 3. If so, you'll need to upload an Alpha or Beta to the Google Play store and publish it. It may take a few hours for Google Play to recognize the licensing.

9. Test your app again on a device that you've logged into with the Google Account that matches your Developer account email address.

10. If all goes well, you should either get an error 0, 1, or no error. You can manipulate the testing value you want to recieve in the Google play developer console under "Account Details -> License Test Response"

_NOTE: If you want to test with other beta testers, they must use the standard procedure to opt-in to be a beta tester, and you must add them to "Account Details -> Gmail accounts with testing access". They will need to pay for the app (!!) if it is paid in order to be beta testers, or you can send them the APK._

__IMPORTANT: Keep in mind that build numbers (versionCode) matter, and may cause errors in licensing if the build number you use in testing does not match the one you published to the Play Store alpha or beta channel.__
