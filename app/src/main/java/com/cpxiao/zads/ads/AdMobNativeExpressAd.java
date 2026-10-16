package com.cpxiao.zads.ads;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpxiao.AppConfig;
import com.cpxiao.R;
import com.cpxiao.zads.ads.core.Advertisement;
import com.cpxiao.zads.ads.core.BaseZAd;
import com.cpxiao.zads.core.AdConfigBean;
import com.cpxiao.zads.utils.AdRequestFactory;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.Queue;


/**
 * AdMob native ad.
 *
 * <p>Migrated from the removed {@code NativeExpressAdView} (deleted in
 * play-services-ads 21) to the unified native ad API: {@link AdLoader} loads a
 * {@link NativeAd} which is rendered into a {@link NativeAdView}. The view is
 * built in code so the ad remains self-contained inside the ZAds layout slot.
 *
 * @author cpxiao on 2017/08/12.
 */
public class AdMobNativeExpressAd extends BaseZAd {
    private static final String TAG = "AdMobNativeExpressAd";

    private AdLoader mAdManager;
    private NativeAd mNativeAd;

    public AdMobNativeExpressAd(AdConfigBean advertiser) {
        super(advertiser);
    }


    @Override
    public void loadAd(final Context c, final Queue<Advertisement> next) {
        if (TextUtils.isEmpty(mPlaceId)) {
            Log.e(TAG, "loadAd: param error!");
            onLoadZAdFail(get(), "empty place id", next);
            return;
        }

        if (DEBUG) {
            Log.d(TAG, "loadAd: mPublishId = " + mPublishId + ", mPlaceId = " + mPlaceId);
        }
        if (mNativeAd != null) {
            mNativeAd.destroy();
            mNativeAd = null;
        }

        mAdManager = new AdLoader.Builder(c, mPlaceId)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        if (DEBUG) {
                            Log.d(TAG, "onAdLoaded: mPlaceId = " + mPlaceId);
                        }
                        mNativeAd = nativeAd;
                        mLastAdView = generateView(c, nativeAd, mAdSize);
                        onLoadZAdSuccess(get());
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        String msg = "ErrorCode = " + loadAdError.getCode();
                        if (DEBUG) {
                            Log.d(TAG, "onAdFailedToLoad: mPlaceId = " + mPlaceId + ", msg = " + msg);
                        }
                        onLoadZAdFail(get(), msg, next);
                    }

                    @Override
                    public void onAdClicked() {
                        if (DEBUG) {
                            Log.d(TAG, "onAdClicked: ");
                        }
                        if (mZAdListener != null) {
                            mZAdListener.onAdClick(get());
                        }
                    }

                    @Override
                    public void onAdOpened() {
                        if (DEBUG) {
                            Log.d(TAG, "onAdOpened: ");
                        }
                        if (mZAdListener != null) {
                            mZAdListener.onAdClick(get());
                        }
                    }
                })
                .build();
        mAdManager.loadAd(AdRequestFactory.create(c));
    }


    public View getLastAdView() {
        removeFromParent(mLastAdView);
        return mLastAdView;
    }

    @Override
    public void destroyAllView(Context context) {
        super.destroyAllView(context);

        removeFromParent(mLastAdView);
        if (mNativeAd != null) {
            mNativeAd.destroy();
            mNativeAd = null;
        }
        mAdManager = null;
    }

    private View generateView(Context c, NativeAd nativeAd, int size) {
        if (c == null || nativeAd == null) {
            return null;
        }

        NativeAdView adView = new NativeAdView(c);
        adView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dip2px(size)));

        LinearLayout container = new LinearLayout(c);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TextView headline = new TextView(c);
        headline.setMaxLines(1);
        headline.setText(nativeAd.getHeadline());
        container.addView(headline);

        TextView body = new TextView(c);
        body.setMaxLines(2);
        body.setText(nativeAd.getBody());
        container.addView(body);

        TextView callToAction = new TextView(c);
        callToAction.setMaxLines(1);
        callToAction.setText(nativeAd.getCallToAction());
        container.addView(callToAction);

        adView.addView(container);
        adView.setHeadlineView(headline);
        adView.setBodyView(body);
        adView.setCallToActionView(callToAction);
        adView.setNativeAd(nativeAd);

        removeFromParent(adView);
        return adView;
    }


    @Override
    public String toString() {
        return TAG;
    }

}

