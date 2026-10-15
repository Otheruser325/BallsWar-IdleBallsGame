package com.cpxiao.gamelib.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cpxiao.AppConfig;
import com.cpxiao.R;
import com.cpxiao.gamelib.activity.core.BaseAppActivity;
import com.cpxiao.zads.utils.AdRequestFactory;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

/**
 * @author cpxiao on 2017/3/1.
 * @version cpxiao on 2017/3/17   更新打印log信息
 *          cpxiao on 2017/8/24   提取test device
 *          cpxiao on 2017/8/31   修改继承类
 */
public abstract class BaseAdsActivity extends BaseAppActivity {
    protected final String TEST_DEVICE_FB = AppConfig.TEST_DEVICE_FB;
    protected final String TEST_DEVICE_ADMOB = AppConfig.TEST_DEVICE_ADMOB;

    protected com.google.android.gms.ads.AdView mAdMobAdView;
    protected AdView mFbAdView;
    private AdListener mFbAdViewListener;

    @Override
    protected void onDestroy() {
        if (mFbAdView != null) {
            mFbAdView.destroy();
            mFbAdView = null;
        }
        if (mAdMobAdView != null) {
            mAdMobAdView.destroy();
            mAdMobAdView = null;
        }
        super.onDestroy();
    }

    protected void initAdMobAds50(String placementId) {
        initAdMobAds(placementId, com.google.android.gms.ads.AdSize.SMART_BANNER);
    }

    protected void initAdMobAds100(String placementId) {
        initAdMobAds(placementId, com.google.android.gms.ads.AdSize.LARGE_BANNER);
    }

    protected void initAdMobAds250(String placementId) {
        initAdMobAds(placementId, com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE);
    }

    private void initAdMobAds(String unitId, com.google.android.gms.ads.AdSize adSize) {
        if (DEBUG) {
            Log.d(TAG, "initAdMobAds: ");
        }

        if (TextUtils.isEmpty(unitId)) {
            Log.e(TAG, "initAdMobAds: unitId is empty!");
            return;
        }
        mAdMobAdView = new com.google.android.gms.ads.AdView(this);
        mAdMobAdView.setAdUnitId(unitId);
        mAdMobAdView.setAdSize(adSize);
        mAdMobAdView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (DEBUG) {
                    Log.d(TAG, "AdMob -> " + "onAdClosed: ");
                }

            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (DEBUG) {
                    Log.d(TAG, "AdMob -> " + "onAdFailedToLoad: i = " + loadAdError.getCode());
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                if (DEBUG) {
                    Log.d(TAG, "AdMob -> " + "onAdOpened: ");
                }

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (DEBUG) {
                    Log.d(TAG, "AdMob -> " + "onAdLoaded: ");
                }
                addToLayout(mAdMobAdView);
            }
        });
        if (DEBUG) {
            Log.d(TAG, "initAdMobAds: mAdMobAdView.loadAd(adRequest);");
        }
        mAdMobAdView.loadAd(AdRequestFactory.create(this));

    }

    protected void initFbAds50(String placementId) {
        initFbAds(placementId, AdSize.BANNER_HEIGHT_50);
    }

    protected void initFbAds90(String placementId) {
        initFbAds(placementId, AdSize.BANNER_HEIGHT_90);
    }

    protected void initFbAds250(String placementId) {
        initFbAds(placementId, AdSize.RECTANGLE_HEIGHT_250);
    }

    private void initFbAds(String placeId, AdSize adSize) {
        if (DEBUG) {
            Log.d(TAG, "initFbAds: ");
        }

        if (TextUtils.isEmpty(placeId)) {
            Log.e(TAG, "initFbAds: placeId is empty!");
            return;
        }

        mFbAdView = new AdView(this, placeId, adSize);

        mFbAdViewListener = new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                if (DEBUG) {
                    Log.d(TAG, "Fb -> " + "onError: " + error.getErrorCode() + "," + error.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "Fb -> " + "onAdLoaded: ");
                }
                addToLayout(mFbAdView);
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "Fb -> " + "onAdClicked: ");
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "Fb -> " + "onLoggingImpression: ");
                }
            }

        };
        if (DEBUG) {
            AdSettings.addTestDevice(TEST_DEVICE_FB);

            //            // 如果想要添加多台测试设备，只需创建一个字符串列表，添加到加载广告前的位置：
            //            List<String> testDevices = new ArrayList<>();
            //            testDevices.add("55c4f301d7c1183f1fa6ede6b3f2fe2e");
            //            testDevices.add("e6298923190b4e7e7119e0f14c44f097");
            //            AdSettings.addTestDevices(testDevices);
        }
        if (DEBUG) {
            Log.d(TAG, "initFbAds: mFbAdView.loadAd();");
        }
        mFbAdView.loadAd(mFbAdView.buildLoadAdConfig()
                .withAdListener(mFbAdViewListener)
                .build());

    }

    private void addToLayout(View view) {
        if (DEBUG) {
            Log.d(TAG, "addToLayout: ");
        }
        if (view == null) {
            return;
        }
        removeFromParent(view);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_ads);
        if (layout == null) {
            Log.e(TAG, "addToLayout: R.id.layout_ads not found!");
            return;
        }
        layout.removeAllViews();
        layout.addView(view);
    }

    private void removeFromParent(View view) {
        if (view == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }
}
