package br.com.helderfarias.authz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Browser;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;
import android.net.Uri;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import android.app.Activity;
import android.content.Intent;


import android.content.ComponentName;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;

public class RNAuthzModule extends ReactContextBaseJavaModule  {

    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private static final String TAG = "RNAuthzModule";

    private CustomTabsClient mCustomTabsClient;
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsServiceConnection mCustomTabsServiceConnection;
    private CustomTabsIntent mCustomTabsIntent;

    public RNAuthzModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNAuthz";
    }

    boolean httpOrHttpsScheme(String url) {
        return url.startsWith("http") || url.startsWith("https");
    }

    @ReactMethod
    public void openURL(String url, Callback error) {
        if (url == null || url.equals("")) {
            Log.e(TAG, "Invalid URL: " + url);
            error.invoke("Invalid URL: " + url);
            return;
        }

        if (!httpOrHttpsScheme(url)) {
            Log.e(TAG, "Allow only http or https URL: " + url);
            error.invoke("Allow only http or https URL: " + url);
            return;
        }

        try {
            this.bootstrap();

            final Activity activity = getCurrentActivity();
            if (activity != null) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(mCustomTabsSession);
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(activity, Uri.parse(url));
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not open URL '" + url + "': " + e.getMessage());
            error.invoke("Could not open URL '" + url + "': " + e.getMessage());
        }
    }

    @ReactMethod
    public void dismiss() {
    }

    @ReactMethod
    public void isAvailable(Callback error) {
        this.bootstrap();
    }

    private void bootstrap() {
        if (mCustomTabsServiceConnection != null) {
            return;
        }

        final Activity activity = getCurrentActivity();

        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mCustomTabsClient= customTabsClient;
                mCustomTabsClient.warmup(0L);
                mCustomTabsSession = mCustomTabsClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mCustomTabsClient= null;
            }
        };

        CustomTabsClient.bindCustomTabsService(activity, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);
    }

}
