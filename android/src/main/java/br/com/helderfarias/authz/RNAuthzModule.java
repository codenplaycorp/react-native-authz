package br.com.helderfarias.authz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;
import android.net.Uri;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import android.app.Activity;
import android.content.Intent;

import android.support.customtabs.CustomTabsIntent;
import android.content.ComponentName;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import org.json.JSONObject;
import org.json.JSONException;

public class RNAuthzModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    private static final String TAG = "RNAuthzModule";

    private CustomTabsClient mCustomTabsClient;
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsServiceConnection mCustomTabsServiceConnection;
    private CustomTabsCallback mSession;
    private EventManager eventManager;

    public RNAuthzModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.eventManager = new EventManager(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "RNAuthz";
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
        this.eventManager.send("InAppBrowserTabOnDismiss");
    }

    @ReactMethod
    public void isAvailable(Callback error) {
        boolean ok = this.bootstrap();

        if (!ok) {
            error.invoke(true);
        } else {
            error.invoke(false);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Ignore
    }

    public void onNewIntent(Intent intent) {
        WritableMap params = Arguments.createMap();

        params.putString("url", intent.getDataString());

        this.eventManager.send("InAppBrowserTabOnDismiss", params);
    }

    private boolean httpOrHttpsScheme(String url) {
        return url.startsWith("http") || url.startsWith("https");
    }

    private boolean bootstrap() {
        if (mCustomTabsServiceConnection != null) {
            return true;
        }

        String packageName = CustomTabsHelper.getPackageNameToUse(getReactApplicationContext());
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }

        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient client) {
                mCustomTabsClient = client;
                mCustomTabsClient.warmup(0L);
                mCustomTabsSession = mCustomTabsClient.newSession(null);
                eventManager.send("InAppBrowserTabOnShow");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                eventManager.send("InAppBrowserTabOnDismiss");
                mCustomTabsClient = null;
            }
        };

        return CustomTabsClient.bindCustomTabsService(getCurrentActivity(), packageName, mCustomTabsServiceConnection);
    }

}
