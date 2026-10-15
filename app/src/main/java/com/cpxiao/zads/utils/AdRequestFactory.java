package com.cpxiao.zads.utils;

import android.content.Context;

import com.cpxiao.AppConfig;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Arrays;

/**
 * Builds AdMob {@link AdRequest}s.
 *
 * <p>Test devices are registered through {@link RequestConfiguration} because
 * {@code AdRequest.Builder.addTestDevice} was removed in play-services-ads 21.
 * Registering them unconditionally is harmless: a production device never
 * matches the test-device hashes, so live ads are still served.
 */
public final class AdRequestFactory {

    private AdRequestFactory() {
    }

    public static AdRequest create(Context context) {
        if (AppConfig.ADS_DEBUG) {
            RequestConfiguration configuration = new RequestConfiguration.Builder()
                    .setTestDeviceIds(Arrays.asList(
                            AdRequest.DEVICE_ID_EMULATOR,
                            AppConfig.TEST_DEVICE_ADMOB))
                    .build();
            MobileAds.setRequestConfiguration(configuration);
        }
        return new AdRequest.Builder().build();
    }
}
