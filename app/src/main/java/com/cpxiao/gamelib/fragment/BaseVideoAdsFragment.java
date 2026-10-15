package com.cpxiao.gamelib.fragment;

import android.util.Log;

import com.cpxiao.gamelib.activity.BaseVideoAdsActivity;
import com.cpxiao.gamelib.activity.BaseVideoAdsActivity.OnRewardedVideoCompletedListener;

/**
 * @author cpxiao on 2017/11/2.
 */

public abstract class BaseVideoAdsFragment extends BaseZAdsFragment {

    public void loadRewardedVideoAd() {
        if (getHoldingActivity() instanceof BaseVideoAdsActivity) {
            BaseVideoAdsActivity activity = (BaseVideoAdsActivity) getHoldingActivity();
            activity.loadRewardedVideoAd();
        }
    }

    public boolean isRewardedVideoAdLoaded() {
        if (getHoldingActivity() instanceof BaseVideoAdsActivity) {
            BaseVideoAdsActivity activity = (BaseVideoAdsActivity) getHoldingActivity();
            return activity.isRewardedVideoAdLoaded();
        }
        return false;
    }

    public boolean showVideoAds(OnRewardedVideoCompletedListener listener) {
        if (DEBUG) {
            Log.d(TAG, "showVideoAds: ");
        }
        if (getHoldingActivity() instanceof BaseVideoAdsActivity) {
            BaseVideoAdsActivity activity = (BaseVideoAdsActivity) getHoldingActivity();
            return activity.showRewardedVideoAd(listener);
        }
        return false;
    }


}
