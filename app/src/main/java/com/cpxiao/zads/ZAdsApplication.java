package com.cpxiao.zads;

import android.app.Application;

import com.cpxiao.AppConfig;
import com.cpxiao.BuildConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

/**
 * Initialises Umeng analytics. v9+ requires an explicit {@link UMConfigure#init}
 * call; the SDK no longer self-initialises from the manifest meta-data, so
 * without this every MobclickAgent.onEvent call is silently dropped.
 */
public class ZAdsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Must run before any MobclickAgent call. Passing the appkey and channel
        // explicitly keeps the values out of source; see app/build.gradle.
        UMConfigure.preInit(this, BuildConfig.UMENG_APPKEY, BuildConfig.FLAVOR);
        UMConfigure.init(this, BuildConfig.UMENG_APPKEY, BuildConfig.FLAVOR,
                UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(AppConfig.ADS_DEBUG);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }
}
