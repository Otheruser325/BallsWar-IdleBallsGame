package com.cpxiao.gamelib.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cpxiao.BuildConfig;
import com.cpxiao.zads.utils.AdRequestFactory;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

/**
 * @author cpxiao on 2017/11/02.
 */

public abstract class BaseVideoAdsActivity extends BaseZAdsActivity {
    private static final String PLACE_ID = BuildConfig.ADMOB_REWARDED_UNIT_ID;

    private RewardedAd mRewardedAd;
    private boolean mIsLoading;
    private boolean mRewardEarned;
    private OnRewardedVideoCompletedListener mPendingListener;

    /**
     * Notified once a rewarded video is dismissed. {@code rewardEarned} is true
     * only when the user watched the video to completion.
     */
    public interface OnRewardedVideoCompletedListener {
        void onRewardedVideoCompleted(boolean rewardEarned);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadRewardedVideoAd();
    }

    public void loadRewardedVideoAd() {
        if (mIsLoading || mRewardedAd != null) {
            return;
        }
        mIsLoading = true;
        RewardedAd.load(this, PLACE_ID, AdRequestFactory.create(this), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedAd ad) {
                mIsLoading = false;
                mRewardedAd = ad;
                ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        mRewardEarned = false;
                        if (DEBUG) {
                            Log.d(TAG, "onRewardedVideoAdOpened: ");
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(
                            com.google.android.gms.ads.AdError adError) {
                        mRewardEarned = false;
                        if (DEBUG) {
                            Log.d(TAG, "onRewardedVideoAdFailedToShow: " + adError.getCode());
                        }
                        // The ad never displayed, so no reward is owed. Notify here
                        // because onAdDismissedFullScreenContent will not be called.
                        notifyRewardCompleted();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mRewardedAd = null;
                        loadRewardedVideoAd();
                        if (DEBUG) {
                            Log.d(TAG, "onRewardedVideoAdClosed: rewardEarned = " + mRewardEarned);
                        }
                        notifyRewardCompleted();
                    }
                });
                if (DEBUG) {
                    Log.d(TAG, "onRewardedVideoAdLoaded: ");
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                mIsLoading = false;
                mRewardedAd = null;
                if (DEBUG) {
                    Log.d(TAG, "onRewardedVideoAdFailedToLoad: " + loadAdError.getCode());
                }
            }
        });
    }

    /**
     * Shows the rewarded video. {@code listener} is invoked when the video is
     * dismissed, with {@code rewardEarned} telling whether it was watched to
     * completion. Callers must grant the reward only when that flag is true; the
     * reward is never granted at the moment of the tap.
     */
    public boolean showRewardedVideoAd(OnRewardedVideoCompletedListener listener) {
        if (mRewardedAd == null) {
            return false;
        }
        mPendingListener = listener;
        mRewardEarned = false;
        mRewardedAd.show(this, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(RewardItem rewardItem) {
                mRewardEarned = true;
                if (DEBUG) {
                    Toast.makeText(BaseVideoAdsActivity.this,
                            "onRewarded! currency: " + rewardItem.getType()
                                    + "  amount: " + rewardItem.getAmount(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }

    public boolean isRewardedVideoAdLoaded() {
        return mRewardedAd != null;
    }

    private void notifyRewardCompleted() {
        OnRewardedVideoCompletedListener listener = mPendingListener;
        mPendingListener = null;
        if (listener != null) {
            listener.onRewardedVideoCompleted(mRewardEarned);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRewardedAd == null) {
            loadRewardedVideoAd();
        }
    }

    @Override
    public void onDestroy() {
        mRewardedAd = null;
        mPendingListener = null;
        super.onDestroy();
    }
}
