package com.cpxiao.androidutils.library.utils;

import android.content.Context;
import android.content.Intent;

/** Creates a standard Android share sheet for game links or messages. */
public final class ShareAppUtils {
    private ShareAppUtils() {
    }

    public static void share(Context context, String title, String content) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        Intent chooser = Intent.createChooser(sendIntent, title);
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooser);
    }
}
