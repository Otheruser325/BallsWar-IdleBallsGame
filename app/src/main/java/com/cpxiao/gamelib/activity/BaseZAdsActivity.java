package com.cpxiao.gamelib.activity;

import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import com.cpxiao.AppConfig;
import com.cpxiao.R;
import com.cpxiao.zads.ZAdManager;
import com.cpxiao.zads.utils.AdRequestFactory;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

/**
 * @author cpxiao on 2017/3/1.
 * @version cpxiao on 2017/8/10     改成使用ZAdsSdk.
 *          cpxiao on 2017/8/19     添加插屏广告
 *          cpxiao on 2017/8/24     提取test device
 *          cpxiao on 2017/8/31     修改继承类
 *          cpxiao on 2017/9/4     修改继承类, extends BaseAdsActivity
 */
public abstract class BaseZAdsActivity extends BaseAdsActivity {
    protected final String TEST_DEVICE_FB = AppConfig.TEST_DEVICE_FB;
    protected final String TEST_DEVICE_ADMOB = AppConfig.TEST_DEVICE_ADMOB;

    private InterstitialAd mAdMobInterstitialAd;
    private String mAdMobInterstitialAdUnitId;
    private com.facebook.ads.InterstitialAd mFbInterstitialAd;
    private InterstitialAdListener mFbInterstitialAdListener;

    @Override
    protected void onDestroy() {
        if (mFbInterstitialAd != null) {
            mFbInterstitialAd.destroy();
            mFbInterstitialAd = null;
        }
        super.onDestroy();
    }

    public void loadZAds(int zAdPosition) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_ads);
        ZAdManager.getInstance().loadAd(getApplicationContext(), zAdPosition, layout);
    }

    /**
     * 插页广告
     *
     * @param unitId id
     */
    protected void initAdMobInterstitialAd(String unitId) {
        if (TextUtils.isEmpty(unitId)) {
            Log.e(TAG, "initAdMobInterstitialAd: unitId is empty!");
            return;
        }
        mAdMobInterstitialAdUnitId = unitId;
        loadAdMobInterstitialAd();
    }

    private void loadAdMobInterstitialAd() {
        if (DEBUG) {
            Log.d(TAG, "loadAdMobInterstitialAd: ");
        }
        if (mAdMobInterstitialAd != null || mAdMobInterstitialAdUnitId == null) {
            return;
        }
        InterstitialAd.load(this, mAdMobInterstitialAdUnitId, AdRequestFactory.create(this),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd ad) {
                        mAdMobInterstitialAd = ad;
                        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                mAdMobInterstitialAd = null;
                                loadAdMobInterstitialAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(
                                    com.google.android.gms.ads.AdError adError) {
                                mAdMobInterstitialAd = null;
                                loadAdMobInterstitialAd();
                            }
                        });
                        if (DEBUG) {
                            Log.d(TAG, "onAdLoaded: ");
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        mAdMobInterstitialAd = null;
                        if (DEBUG) {
                            Log.d(TAG, "onAdFailedToLoad: " + loadAdError.getCode());
                        }
                    }
                });
    }

    protected void showAdMobInterstitialAd() {
        if (mAdMobInterstitialAd != null) {
            mAdMobInterstitialAd.show(this);
        } else {
            loadAdMobInterstitialAd();
        }
    }

    protected void initFbInterstitialAd(String placeId) {
        if (TextUtils.isEmpty(placeId)) {
            Log.e(TAG, "initFbInterstitialAd: placeId is empty!");
            return;
        }
        mFbInterstitialAd = new com.facebook.ads.InterstitialAd(this, placeId);
        // FB 6.x removed InterstitialAd#setAdListener; the listener is attached to the
        // load config and consumed by loadFbInterstitialAd below.
        mFbInterstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onInterstitialDisplayed: ");
                }
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onInterstitialDismissed: ");
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (DEBUG) {
                    Log.d(TAG, "onError: " + adError.getErrorCode() + adError.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onAdLoaded: ");
                }
                showFbInterstitialAd();
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onAdClicked: ");
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onLoggingImpression: ");
                }
            }
        };

        loadFbInterstitialAd();
    }

    private void loadFbInterstitialAd() {
        if (DEBUG) {
            Log.d(TAG, "loadFbInterstitialAd: ");
        }
        if (mFbInterstitialAd == null) {
            return;
        }
        if (DEBUG) {
            AdSettings.addTestDevice(TEST_DEVICE_FB);
        }
        mFbInterstitialAd.loadAd(mFbInterstitialAd.buildLoadAdConfig()
                .withAdListener(mFbInterstitialAdListener)
                .build());
    }

    protected void showFbInterstitialAd() {
        if (mFbInterstitialAd != null) {
            if (mFbInterstitialAd.isAdLoaded()) {
                mFbInterstitialAd.show();
            } else {
                loadFbInterstitialAd();
            }
        }
    }

}
