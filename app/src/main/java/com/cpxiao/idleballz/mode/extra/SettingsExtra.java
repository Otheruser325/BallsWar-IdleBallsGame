package com.cpxiao.idleballz.mode.extra;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.cpxiao.AppConfig;
import com.cpxiao.R;
import com.cpxiao.idleballz.mode.data.SettingsItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cpxiao on 2017/09/27.
 */

public final class SettingsExtra {
    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = SettingsExtra.class.getSimpleName();


    private static final int INDEX_TITLE_ID = 0;
    private static final int INDEX_DRAWABLE_ID = 1;
    private static final int INDEX_BTN_MSG = 2;

    private static final int[] settings0 = {R.string.rate_app, R.drawable.ic_rate, R.string.rate_app};
    private static final int[] settings1 = {R.string.share, R.drawable.ic_share, R.string.share};
    private static final int[] settings2 = {R.string.settings_sound, R.drawable.ic_sound, R.string.settings_sound};

    private static final int[][] ITEM_ARRAY = {
            settings0,
            settings1,
            settings2,
    };

    public static List<SettingsItemData> getDataList(Context context) {
        List<SettingsItemData> list = new ArrayList<>();
        for (int[] item : ITEM_ARRAY) {
            SettingsItemData itemData = new SettingsItemData();
            itemData.title = context.getString(item[INDEX_TITLE_ID]);
            //            itemData.title = "";
            itemData.drawable = ContextCompat.getDrawable(context, item[INDEX_DRAWABLE_ID]);
            itemData.btnMsg = context.getString(item[INDEX_BTN_MSG]);
            list.add(itemData);
        }

        if (DEBUG) {
            Log.d(TAG, "getDataList: list.size() = " + list.size());
        }
        return list;
    }


}
