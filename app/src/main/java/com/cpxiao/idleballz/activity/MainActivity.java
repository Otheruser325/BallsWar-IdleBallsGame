package com.cpxiao.idleballz.activity;

import android.os.Bundle;

import com.cpxiao.BuildConfig;
import com.cpxiao.gamelib.activity.BaseVideoAdsActivity;
import com.cpxiao.gamelib.fragment.BaseFragment;
import com.cpxiao.idleballz.fragment.GameFragment;
import com.cpxiao.zads.ZAdManager;
import com.cpxiao.zads.core.ZAdPosition;

public class MainActivity extends BaseVideoAdsActivity {

    @Override
    protected BaseFragment getFirstFragment() {
//        return HomeFragment.newInstance(null);
        return GameFragment.newInstance(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZAdManager.getInstance().init(getApplicationContext());
        loadAds();
    }

    private void loadAds() {
        initAdMobAds100(BuildConfig.ADMOB_BANNER_UNIT_ID);
        initFbAds90(BuildConfig.FB_BANNER_PLACE_ID);
        loadZAds(ZAdPosition.POSITION_MAIN);
    }

    @Override
    public void onDestroy() {
        ZAdManager.getInstance().destroyAllPosition(this);
        super.onDestroy();
    }
}
