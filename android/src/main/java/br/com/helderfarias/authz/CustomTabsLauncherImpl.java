package br.com.helderfarias.authz;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.customtabs.CustomTabsIntent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Intent.ACTION_VIEW;
import static android.content.pm.PackageManager.GET_META_DATA;

class CustomTabsLauncherImpl {

    static final String PACKAGE_STABLE = "com.android.chrome";
    static final String PACKAGE_BETA = "com.chrome.beta";
    static final String PACKAGE_DEV = "com.chrome.dev";
    static final String PACKAGE_LOCAL = "com.google.android.apps.chrome";

    private static final List<String> CHROME_PACKAGES = Arrays.asList(
            PACKAGE_STABLE,
            PACKAGE_BETA,
            PACKAGE_DEV,
            PACKAGE_LOCAL);

     private static final String ACTION_CUSTOM_TABS_CONNECTION =
            "android.support.customtabs.action.CustomTabsService";

    void launch(@NonNull Activity activity,
                @NonNull CustomTabsIntent customTabsIntent,
                @NonNull Uri uri,
                @Nullable CustomTabsFallback fallback) {

        final PackageManager pm = activity.getPackageManager();
        final String chromePackage = packageNameToUse(pm, uri);
        if (chromePackage == null && fallback != null) {
            fallback.openUri(activity, uri);
            return;
        }

        customTabsIntent.intent.setPackage(chromePackage);
        customTabsIntent.launchUrl(activity, uri);
    }

    @Nullable
    @VisibleForTesting
    String packageNameToUse(PackageManager pm, Uri uri) {
        final String defaultPackageName = defaultViewHandlerPackage(pm, uri);

        // If Chrome is default browser, use it.
        if (defaultPackageName != null) {
            if (CHROME_PACKAGES.contains(defaultPackageName) &&
                    supportedCustomTabs(pm, defaultPackageName)) {
                return defaultPackageName;
            }
        }

        final List<String> chromePackages = installedPackages(pm);
        if (chromePackages.isEmpty()) {
            return null;
        }

        // Stable comes first.
        return decidePackage(pm, chromePackages);
    }

    @Nullable
    @VisibleForTesting
    String defaultViewHandlerPackage(PackageManager pm, Uri uri) {
        // Get default VIEW intent handler.
        final Intent activityIntent = new Intent(ACTION_VIEW, uri);
        final ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
        if (defaultViewHandlerInfo != null) {
            return defaultViewHandlerInfo.activityInfo.packageName;
        }
        return null;
    }

    @NonNull
    @VisibleForTesting
    List<String> installedPackages(PackageManager pm) {
        final List<ApplicationInfo> installedApps = pm.getInstalledApplications(GET_META_DATA);
        final List<String> installedChromes = new ArrayList<>(CHROME_PACKAGES.size());
        for (ApplicationInfo app : installedApps) {
            if (CHROME_PACKAGES.contains(app.packageName)) {
                installedChromes.add(app.packageName);
            }
        }
        return installedChromes;
    }

    @VisibleForTesting
    String decidePackage(PackageManager pm, List<String> candidates) {
        for (String chromePackage : CHROME_PACKAGES) {
            if (candidates.contains(chromePackage) &&
                    supportedCustomTabs(pm, chromePackage)) {
                return chromePackage;
            }
        }
        return null;
    }

    @VisibleForTesting
    boolean supportedCustomTabs(PackageManager pm, String chromePackage) {
        final Intent serviceIntent = new Intent(ACTION_CUSTOM_TABS_CONNECTION).setPackage(chromePackage);

        return pm.resolveService(serviceIntent, 0) != null;
    }
}
