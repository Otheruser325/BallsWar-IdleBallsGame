package com.cpxiao.zads.ads;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cpxiao.AppConfig;
import com.cpxiao.zads.ads.core.Advertisement;
import com.cpxiao.zads.ads.core.BaseZAd;
import com.cpxiao.zads.core.AdConfigBean;
import com.cpxiao.zads.core.ZAdSize;
import com.cpxiao.zads.utils.AdRequestFactory;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.Queue;


/**
 * @author cpxiao on 2017/08/10.
 *         2017/8/19        大banner有点奇怪，会返回小banner
 */
public class AdMobBannerAd extends BaseZAd {
    private static final String TAG = "AdMobBannerAd";
    private static final String TEST_DEVICE = AppConfig.TEST_DEVICE_ADMOB;

    private AdView mAdManager;

    public AdMobBannerAd(AdConfigBean advertiser) {
        super(advertiser);
    }


    @Override
    public void loadAd(final Context c, final Queue<Advertisement> next) {
        //参数校验
        if (TextUtils.isEmpty(mPlaceId)) {
            Log.e(TAG, "loadAd: param error!");
            onLoadZAdFail(get(), "empty place id", next);
            return;
        }

        if (DEBUG) {
            Log.d(TAG, "loadAd: mPublishId = " + mPublishId + ", mPlaceId = " + mPlaceId);
        }
        if (mAdManager != null) {
            mAdManager.destroy();
            mAdManager = null;
        }
        mAdManager = new AdView(c);
        mAdManager.setAdUnitId(mPlaceId);
        mAdManager.setAdSize(getAdSize(mAdSize));

        mAdManager.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (DEBUG) {
                    Log.d(TAG, "onAdClosed: ");
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                String msg = "ErrorCode = " + loadAdError.getCode();
                if (DEBUG) {
                    Log.d(TAG, "onAdFailedToLoad: mPlaceId = " + mPlaceId + ", msg = " + msg);
                }
                onLoadZAdFail(get(), msg, next);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                if (DEBUG) {
                    Log.d(TAG, "onAdOpened: ");
                }
                if (mZAdListener != null) {
                    mZAdListener.onAdClick(get());
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (DEBUG) {
                    Log.d(TAG, "onAdLoaded: mPlaceId = " + mPlaceId);
                }
                mLastAdView = generateView(c, mAdManager, mAdSize);
                onLoadZAdSuccess(get());
            }
        });
        mAdManager.loadAd(AdRequestFactory.create(c));
    }


    public View getLastAdView() {
        removeFromParent(mLastAdView);
        return mLastAdView;
    }

    @Override
    public void destroyAllView(Context context) {
        super.destroyAllView(context);

        removeFromParent(mAdManager);
        if (mAdManager != null) {
            mAdManager.destroy();
            mAdManager = null;
        }

    }

    private View generateView(Context c, AdView bannerView, int size) {
        if (c == null || bannerView == null) {
            return null;
        }
        removeFromParent(bannerView);

        LinearLayout view = new LinearLayout(c);
        view.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(size));
        view.addView(bannerView, params);
        return view;
        //        bannerView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        //        return bannerView;
    }

    private AdSize getAdSize(int size) {
        if (DEBUG) {
            Log.d(TAG, "getAdSize: size = " + size);
        }
        if (size == ZAdSize.BANNER_320X50) {
            return AdSize.SMART_BANNER;
        } else if (size == ZAdSize.BANNER_320X90) {
            return AdSize.SMART_BANNER;
        } else if (size == ZAdSize.BANNER_320X100) {
            return AdSize.LARGE_BANNER;
        } else if (size == ZAdSize.BANNER_300X250) {
            return AdSize.MEDIUM_RECTANGLE;
        } else {
            Log.e(TAG, "No Size found in " + TAG + ", size = " + size);
            return AdSize.SMART_BANNER;
        }
    }

    @Override
    public String toString() {
        return TAG;
    }

}
