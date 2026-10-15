package com.cpxiao.idleballz.mode.extra;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.cpxiao.AppConfig;
import com.cpxiao.R;
import com.cpxiao.idleballz.mode.data.BoosterItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cpxiao on 2017/09/27.
 */

public final class BoostersExtra {
    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = BoostersExtra.class.getSimpleName();


    private static final int INDEX_TITLE = 0;
    private static final int INDEX_DRAWABLE = 1;
    private static final int INDEX_BTN_MSG = 2;

    private static final int[] boost0 = {R.string.boosters_level_up_1, R.drawable.boost_levelup, R.string.boost};
    private static final int[] boost1 = {R.string.boosters_tap_x10, R.drawable.boost_tap_x10, R.string.boost};
    private static final int[] boost2 = {R.string.boosters_power_x2, R.drawable.boost_power_x2, R.string.boost};

    private static final int[][] ITEM_ARRAY = {
            boost0,
            boost1,
            boost2,
    };

    public static List<BoosterItemData> getDataList(Context context) {
        List<BoosterItemData> list = new ArrayList<>();
        for (int[] item : ITEM_ARRAY) {
            BoosterItemData itemData = new BoosterItemData();
            itemData.title = context.getString(item[INDEX_TITLE]);
            itemData.drawable = ContextCompat.getDrawable(context, item[INDEX_DRAWABLE]);
            itemData.btnMsg = context.getString(item[INDEX_BTN_MSG]);
            list.add(itemData);
        }

        if (DEBUG) {
            Log.d(TAG, "getDataList: list.size() = " + list.size());
        }
        return list;
    }


}
