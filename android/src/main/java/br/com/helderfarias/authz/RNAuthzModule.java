package br.com.helderfarias.authz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Browser;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.UnexpectedNativeTypeException;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.bridge.Callback;

import java.util.Map;

import javax.annotation.Nullable;

public class RNAuthzModule extends ReactContextBaseJavaModule implements CustomTabsFallback {

    private static final String TAG = "RNAuthzModule";

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
            final Activity activity = getCurrentActivity();
            if (activity != null) {
                CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                CustomTabsLauncher.launch(activity, intent, url);
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
    }

}
