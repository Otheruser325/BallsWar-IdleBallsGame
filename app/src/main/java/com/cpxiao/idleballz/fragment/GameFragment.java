package com.cpxiao.idleballz.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.androidutils.library.utils.RateAppUtils;
import com.cpxiao.androidutils.library.utils.ShareAppUtils;
import com.cpxiao.gamelib.activity.BaseVideoAdsActivity;
import com.cpxiao.gamelib.fragment.BaseVideoAdsFragment;
import com.cpxiao.idleballz.adapter.BallsRecyclerViewAdapter;
import com.cpxiao.idleballz.adapter.BoostersRecyclerViewAdapter;
import com.cpxiao.idleballz.adapter.SettingsRecyclerViewAdapter;
import com.cpxiao.idleballz.imps.OnBallItemClicked;
import com.cpxiao.idleballz.imps.OnBoostersItemClicked;
import com.cpxiao.idleballz.imps.OnGameListener;
import com.cpxiao.idleballz.mode.data.BallItemData;
import com.cpxiao.idleballz.mode.data.BoosterItemData;
import com.cpxiao.idleballz.mode.data.SettingsItemData;
import com.cpxiao.idleballz.mode.extra.BallsExtra;
import com.cpxiao.idleballz.mode.extra.BoostersExtra;
import com.cpxiao.idleballz.mode.extra.Extra;
import com.cpxiao.idleballz.mode.extra.SettingsExtra;
import com.cpxiao.idleballz.views.GameView;
import com.cpxiao.zads.core.ZAdPosition;

import java.util.List;

/**
 * @author cpxiao on 2017/9/27.
 */

public class GameFragment extends BaseVideoAdsFragment implements View.OnClickListener {
    private BallsRecyclerViewAdapter mBallsRecyclerViewAdapter;
    private BoostersRecyclerViewAdapter mBoostersRecyclerViewAdapter;
    private SettingsRecyclerViewAdapter mSettingsRecyclerViewAdapter;
    private GameView mGameView;
    private Button mBallsBtn;
    private Button mBoostersBtn;
    private Button mSettingsBtn;
    private RecyclerView mRecyclerView;


    private List<BallItemData> mBallDataList;
    private List<BoosterItemData> mBoosterDataList;
    private List<SettingsItemData> mSettingsDataList;

    private int updateCount = 0;

    public static GameFragment newInstance(Bundle bundle) {
        GameFragment fragment = new GameFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_game;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        loadZAds(ZAdPosition.POSITION_GAME);

        final Context context = getHoldingActivity();
        mBallDataList = BallsExtra.getDataList(context);
        mBoosterDataList = BoostersExtra.getDataList(context);
        mSettingsDataList = SettingsExtra.getDataList(context);

        mGameView = (GameView) view.findViewById(R.id.game_view);
        mGameView.setOnGameListener(new OnGameListener() {
            @Override
            public void onCoinChange(float coin) {
                int currentUpdateCount = BallsExtra.getUpdateCount(coin, mBallDataList);
                if (updateCount != currentUpdateCount) {
                    mBallDataList = BallsExtra.parseList(coin, mBallDataList);
                    mGameView.post(mRunnable);
                    updateCount = currentUpdateCount;
                }

            }
        });

        mBallsBtn = (Button) view.findViewById(R.id.btn_balls);
        mBoostersBtn = (Button) view.findViewById(R.id.btn_boosters);
        mSettingsBtn = (Button) view.findViewById(R.id.btn_settings);
        mBallsBtn.setOnClickListener(this);
        mBoostersBtn.setOnClickListener(this);
        mSettingsBtn.setOnClickListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.ballsRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);


        initBallsAdapter(context);
        initBoostersAdapter(context);
        initSettingsAdapter(context);
        loadBall();
    }

    private void initBallsAdapter(final Context context) {
        mBallsRecyclerViewAdapter = new BallsRecyclerViewAdapter(getContext(), mBallDataList);
        mBallsRecyclerViewAdapter.setOnItemClicked(new OnBallItemClicked() {
            @Override
            public void onItemClicked(int index, float price, int level) {
                if (DEBUG) {
                    Log.d(TAG, "onItemClicked: ");
                }
                mBallsRecyclerViewAdapter.notifyItemChanged(index);
                mGameView.deleteCoin(price);
                mGameView.createNewBall(context, index);
                PreferencesUtils.putInt(context, Extra.Key.getItemLevelKey(index), level);
            }
        });


    }

    private void initBoostersAdapter(final Context context) {
        mBoostersRecyclerViewAdapter = new BoostersRecyclerViewAdapter(getContext(), mBoosterDataList);
        mBoostersRecyclerViewAdapter.setOnBoostersItemClicked(new OnBoostersItemClicked() {
            @Override
            public void onItemClicked(final int index) {
                if (DEBUG) {
                    Log.d(TAG, "onItemClicked: ....");
                }
                if (!isRewardedVideoAdLoaded()) {
                    Toast.makeText(context, R.string.video_not_ready_msg, Toast.LENGTH_SHORT).show();
                    loadRewardedVideoAd();
                    return;
                }
                // The booster is granted from the completion callback only. Granting it
                // here (as before) paid out even when the video failed to load or the
                // user closed it immediately.
                showVideoAds(new BaseVideoAdsActivity.OnRewardedVideoCompletedListener() {
                    @Override
                    public void onRewardedVideoCompleted(boolean rewardEarned) {
                        if (!rewardEarned) {
                            return;
                        }
                        BoosterItemData item = mBoosterDataList.get(index);
                        if (TextUtils.equals(item.title, getString(R.string.boosters_level_up_1))) {
                            levelUp(context);
                        } else if (TextUtils.equals(item.title, getString(R.string.boosters_tap_x10))) {
                            tapX10(context);
                        } else if (TextUtils.equals(item.title, getString(R.string.boosters_power_x2))) {
                            powerX2(context);
                        }
                    }
                });
            }
        });

    }

    private void levelUp(Context context) {
        for (BallItemData item : mBallDataList) {
            if (item.level > 0) {
                item.level++;
            }
        }
        Toast.makeText(context, R.string.boosters_level_up_1, Toast.LENGTH_SHORT).show();
    }

    private void tapX10(Context context) {
        BallItemData item = mBallDataList.get(0);
        if (item.level > 0) {
            item.power *= 10;
        }
        Toast.makeText(context, R.string.boosters_tap_x10, Toast.LENGTH_SHORT).show();
    }

    private void powerX2(Context context) {
        for (int i = 1; i < mBallDataList.size(); i++) {
            BallItemData item = mBallDataList.get(i);
            if (item.level > 0) {
                item.power *= 2;
            }
        }
        Toast.makeText(context, R.string.boosters_power_x2, Toast.LENGTH_SHORT).show();
    }


    private void initSettingsAdapter(final Context context) {
        mSettingsRecyclerViewAdapter = new SettingsRecyclerViewAdapter(getContext(), mSettingsDataList);
        mSettingsRecyclerViewAdapter.setOnBoostersItemClicked(new OnBoostersItemClicked() {
            @Override
            public void onItemClicked(int index) {
                if (DEBUG) {
                    Log.d(TAG, "onItemClicked: ....");
                }
                SettingsItemData item = mSettingsDataList.get(index);

                if (TextUtils.equals(item.title, getString(R.string.rate_app))) {
                    RateAppUtils.rate(context);
                } else if (TextUtils.equals(item.title, getString(R.string.share))) {
                    String msg = getString(R.string.share_msg) + "\n" +
                            getString(R.string.app_name) + "\n" +
                            "https://play.google.com/store/apps/details?id=" + context.getPackageName();
                    ShareAppUtils.share(context, getString(R.string.share), msg);
                } else if (TextUtils.equals(item.title, getString(R.string.settings_sound))) {

                } else if (TextUtils.equals(item.title, getString(R.string.settings_music))) {

                }

            }
        });

    }

    private void loadBall() {
        mRecyclerView.setAdapter(mBallsRecyclerViewAdapter);
    }

    private void loadBooster() {
        mRecyclerView.setAdapter(mBoostersRecyclerViewAdapter);
    }

    private void loadSettings() {
        mRecyclerView.setAdapter(mSettingsRecyclerViewAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGameView != null) {
            mGameView.save(getHoldingActivity());
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBallsRecyclerViewAdapter != null) {
                mBallsRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_balls) {
            loadBall();
        } else if (id == R.id.btn_boosters) {
            loadBooster();
        } else if (id == R.id.btn_settings) {
            loadSettings();
        }
    }
}
