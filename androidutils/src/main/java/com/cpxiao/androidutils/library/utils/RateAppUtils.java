package com.cpxiao.androidutils.library.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/** Opens the app's Play Store listing for rating. */
public final class RateAppUtils {
    private RateAppUtils() {
    }

    public static void rate(Context context) {
        String packageName = context.getPackageName();
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(marketIntent);
        } catch (ActivityNotFoundException ignored) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(webIntent);
        }
    }

    public static void rateApp(Context context) {
        rate(context);
    }
}
