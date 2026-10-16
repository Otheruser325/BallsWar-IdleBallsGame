package com.cpxiao.idleballz.mode.extra;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.cpxiao.AppConfig;
import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.idleballz.mode.data.BallItemData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cpxiao on 2017/09/27.
 */

public final class BallsExtra {
    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = BallsExtra.class.getSimpleName();


    public static final int INDEX_DRAWABLE_ID = 0;

    private static final int[] newBall0 = {R.drawable._0tap};
    private static final int[] newBall1 = {R.drawable._1};
    private static final int[] newBall2 = {R.drawable._2};
    private static final int[] newBall3 = {R.drawable._3};
    private static final int[] newBall4 = {R.drawable._4};
    private static final int[] newBall5 = {R.drawable._5};
    private static final int[] newBall6 = {R.drawable._6};
    private static final int[] newBall7 = {R.drawable._7};
    private static final int[] newBall8 = {R.drawable._8};
    private static final int[] newBall9 = {R.drawable._9};
    private static final int[] newBall10 = {R.drawable._10};
    private static final int[] newBall11 = {R.drawable._11};
    private static final int[] newBall12 = {R.drawable._12};
    private static final int[] newBall13 = {R.drawable._13};
    private static final int[] newBall14 = {R.drawable._14};
    private static final int[] newBall15 = {R.drawable._15};
    private static final int[] newBall16 = {R.drawable._16};
    private static final int[] newBall17 = {R.drawable._17};
    private static final int[] newBall18 = {R.drawable._18};
    private static final int[] newBall19 = {R.drawable._19};
    private static final int[] newBall20 = {R.drawable._20};
    private static final int[] newBall21 = {R.drawable._21};
    private static final int[] newBall22 = {R.drawable._22};
    private static final int[] newBall23 = {R.drawable._23};
    private static final int[] newBall24 = {R.drawable._24};
    private static final int[] newBall25 = {R.drawable._25};
    private static final int[] newBall26 = {R.drawable._26};
    private static final int[] newBall27 = {R.drawable._27};
    private static final int[] newBall28 = {R.drawable._28};
    private static final int[] newBall29 = {R.drawable._29};
    private static final int[] newBall30 = {R.drawable._30};

    private static final int[][] ITEM_ARRAY = {
            newBall0,
            newBall1, newBall2, newBall3, newBall4, newBall5, newBall6, newBall7, newBall8, newBall9, newBall10,
            newBall11, newBall12, newBall13, newBall14, newBall15, newBall16, newBall17, newBall18, newBall19, newBall20,
            newBall21, newBall22, newBall23, newBall24, newBall25, newBall26, newBall27, newBall28, newBall29, newBall30,
    };

    public static int getItemCount() {
        return ITEM_ARRAY.length;
    }

    public static int[] getItem(int index) {
        return ITEM_ARRAY[index];
    }

    public static List<BallItemData> parseList(float coin, List<BallItemData> mDataList) {
        for (int i = 0; i < mDataList.size(); i++) {
            BallItemData data = mDataList.get(i);
            data.isCanBeUpdated = coin >= data.updatePrice;
        }
        return mDataList;
    }

    public static int getUpdateCount(float coin, List<BallItemData> mDataList) {
        int count = 0;
        for (int i = 0; i < mDataList.size(); i++) {
            BallItemData data = mDataList.get(i);
            if (coin >= data.updatePrice) {
                count++;
            }
        }
        return count;
    }

    public static List<BallItemData> getDataList(Context context) {
        List<BallItemData> list = new ArrayList<>();
        for (int i = 0; i < ITEM_ARRAY.length; i++) {
            int[] item = ITEM_ARRAY[i];
            BallItemData itemData = new BallItemData();
            int defaultLevel;
            if (i == 0) {
                defaultLevel = 1;
            } else {
                defaultLevel = 0;
            }
            itemData.level = PreferencesUtils.getInt(context, Extra.Key.getItemLevelKey(i), defaultLevel);
            itemData.drawable = ContextCompat.getDrawable(context, item[INDEX_DRAWABLE_ID]);
            itemData.power = getPower(i, itemData.level);
            itemData.updatePrice = getUpdatePrice(i, itemData.level);
            list.add(itemData);
        }

        if (DEBUG) {
            Log.d(TAG, "getDataList: list.size() = " + list.size());
        }
        return list;
    }

    /**
     * power:   1,  1,  30,     90,     270,    810     ...
     * price:   11, 1,  300,    1.8k,   10.8k,  64.8k   ...
     */
    public static float getPower(int itemIndex, int itemLevel) {
        float basePower = 0;
        float rate = 1.07F;
        if (itemIndex < 0) {
            if (DEBUG) {
                Log.d(TAG, "getPower: ");
                throw new IllegalArgumentException("itemIndex < 0");
            }
        } else if (itemIndex == 0) {
            basePower = 1;
            rate = 1.1F;
        } else if (itemIndex == 1) {
            basePower = 1;
        } else {
            basePower = (float) (10 * Math.pow(3, itemIndex - 1));
        }
        if (itemLevel <= 0) {
            itemLevel = 1;
        }
        return (float) Math.max(itemLevel, basePower * Math.pow(rate, itemLevel - 1));
    }

    public static float getUpdatePrice(int itemIndex, int itemLevel) {
        float rate = 1.1F;
        float basePrice = 0;
        if (itemIndex < 0) {
            if (DEBUG) {
                Log.d(TAG, "getUpdatePrice: ");
                throw new IllegalArgumentException("itemIndex < 0");
            }
        } else if (itemIndex == 0) {
            basePrice = 11;
            rate = 1.15F;
        } else if (itemIndex == 1) {
            if (itemLevel == 0) {
                basePrice = 1;
            } else {
                basePrice = 10;
            }
        } else {
            basePrice = (float) (300 * Math.pow(6, itemIndex - 2));
        }
        return (float) Math.max(itemLevel, basePrice * Math.pow(rate, itemLevel));
    }


}
