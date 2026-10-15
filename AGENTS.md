# AGENTS.md

Android/Java idle game (single `:app` module + `:androidutils` library).

## Build

The build needs a JDK and an Android SDK. In this environment:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export ANDROID_HOME=/opt/android-sdk
export PATH=$JAVA_HOME/bin:$PATH

./gradlew :app:assembleGpDebug --no-daemon
./gradlew :app:assembleGpRelease --no-daemon
```

- AGP 8.7.3, Gradle 8.9 (wrapper pinned with `distributionSha256Sum`).
- `compileSdk`/`targetSdk` 35, `minSdk` 21, Java 11.
- Flavor dimension is `channel`; the only flavor is `gp`, so the task names are
  `...GpDebug` / `...GpRelease`.
- `:app:compileGpDebugJavaWithJavac` is the fast syntax/type check.
- Release builds run R8 (`minifyEnabled true`, `shrinkResources true`). R8 fails
  the build on missing third-party classes, so keep rules for ad/analytics SDKs
  live in `app/proguard-rules.pro`.

## Architecture

- `com.cpxiao.zads.ZAdsApplication` — `Application` subclass. Umeng v9+ dropped
  manifest-driven self-initialisation, so `UMConfigure.init` must run here before
  any `MobclickAgent` call. Umeng also installs its own activity lifecycle
  callbacks, so do NOT add manual `MobclickAgent.onResume/onPause` calls; with
  `PageMode.AUTO` they double-count sessions.
- `com.cpxiao.gamelib.activity.*` — activity bases. `BaseAdsActivity` (AdMob +
  Facebook Audience Network init/banner), `BaseZAdsActivity` (interstitials),
  `BaseVideoAdsActivity` (rewarded video), `core.BaseActivity` (fragments).
- `com.cpxiao.zads.*` — the ad mediation layer. `ZAdManager` picks an advertiser
  per `ZAdPosition` from the JSON config in `app/src/main/assets/<applicationId>.json`.
- `com.cpxiao.idleballz.*` — the game. `GameFragment` wires the ball/booster/
  settings recyclers; `views.GameView` is the `SurfaceView` render loop.
- Game progress is persisted through `PreferencesUtils` (default
  `SharedPreferences`), keyed by `mode.extra.Extra.Key`.

## Ad provider keys

Ad unit IDs, the AdMob application ID and `UMENG_APPKEY` are injected by
`app/build.gradle` from `local.properties` (not version controlled) or the
environment, falling back to the sample project's published values. Java reads
them from `BuildConfig`; the manifest reads them from `manifestPlaceholders`.
Never hardcode a new key in Java or XML — add a `buildConfigField` instead.

The AdMob *application* ID is the one exception: the project's production value
was never committed, so the fallback is Google's published test application ID.
Set `admob.applicationId` in `local.properties` before shipping a release, or
ads will be served as test ads.

## Conventions

- `AppConfig.DEBUG` and `AppConfig.ADS_DEBUG` are hardcoded `false`. Guard every
  log line with `if (DEBUG)`; never gate a functional `return`/`throw` on a DEBUG
  flag, or release builds lose the guard entirely.

## Security invariants

- Rewarded-video rewards must be granted only from
  `OnRewardedVideoCompletedListener` with `rewardEarned == true`. Never pay out at
  the tap site.
- `allowBackup="false"` plus explicit `dataExtractionRules` /
  `fullBackupContent` XML; keep it that way so game state is not extractable via
  `adb backup` or cloud restore.
- `MainActivity` keeps `android:taskAffinity=""` and
  `android:launchMode="singleTask"` as task-hijacking (StrandHogg) mitigations.
- The AdMob application ID meta-data must stay present; AdMob refuses to serve
  and warns without it.
