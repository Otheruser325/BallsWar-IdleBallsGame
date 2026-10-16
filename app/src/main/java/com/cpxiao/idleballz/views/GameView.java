package com.cpxiao.idleballz.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.gamelib.mode.common.Sprite;
import com.cpxiao.gamelib.mode.common.SpriteControl;
import com.cpxiao.gamelib.views.BaseSurfaceViewFPS;
import com.cpxiao.idleballz.imps.OnGameListener;
import com.cpxiao.idleballz.mode.Ball;
import com.cpxiao.idleballz.mode.EnemyBall;
import com.cpxiao.idleballz.mode.extra.BallsExtra;
import com.cpxiao.idleballz.mode.extra.EnemyBallExtra;
import com.cpxiao.idleballz.mode.extra.Extra;
import com.cpxiao.idleballz.mode.extra.GameExtra;
import com.cpxiao.idleballz.mode.ui.UIProgressBar;
import com.cpxiao.idleballz.mode.ui.UIText;

import java.util.concurrent.ConcurrentLinkedQueue;

import hugo.weaving.DebugLog;

/**
 * @author cpxiao on 2017/9/27.
 */

public class GameView extends BaseSurfaceViewFPS {

    private UIText mTitleBarBg;
    private UIText mCoinText, mGameLevelText;
    private UIProgressBar mProgressBar;
    private float mCoin;
    private int mGameLevel;

    private ConcurrentLinkedQueue<Ball> mBallQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<EnemyBall> mEnemyBallQueue = new ConcurrentLinkedQueue<>();
    private int mCreatedEnemyCount = 0;
    private static final int mMaxEnemyCount = 16;

    private RectF mMovingRangeRectF;
    private static final float TITLE_BAR_HEIGHT = 0.15F;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initWidget() {
        Context context = getContext();
        setBgColor(ContextCompat.getColor(getContext(), R.color.GameViewBg));

        mCoin = PreferencesUtils.getFloat(context, Extra.Key.COIN, Extra.Key.COIN_DEFAULT);
        if (mOnGameListener != null) {
            mOnGameListener.onCoinChange(mCoin);
        }

        mGameLevel = PreferencesUtils.getInt(context, Extra.Key.GAME_LEVEL, Extra.Key.GAME_LEVEL_DEFAULT);


        mMovingRangeRectF = new RectF(0, TITLE_BAR_HEIGHT * mViewHeight, mViewWidth, mViewHeight);

        mPaint.setTextSize(0.03F * mViewWidth);

        mTitleBarBg = (UIText) new UIText.Build()
                .setColorBg(ContextCompat.getColor(context, R.color.commonBg))
                .setW(mViewWidth)
                .setH(TITLE_BAR_HEIGHT * mViewHeight)
                .build();
        mProgressBar = (UIProgressBar) new UIProgressBar.Build()
                .setProgress(1.1F)
                .setColorProgress(ContextCompat.getColor(context, R.color.progressBar))
                .setColorBg(ContextCompat.getColor(context, R.color.progressBarBg))
                .setW(0.8F * mViewWidth)
                .setH(0.015F * mViewHeight)
                .centerTo(0.5F * mViewWidth, 0.8F * TITLE_BAR_HEIGHT * mViewHeight)
                .build();
        mCoinText = (UIText) new UIText.Build()
                .setTextSize(0.3F * TITLE_BAR_HEIGHT * mViewHeight)
                .setTextColor(ContextCompat.getColor(context, R.color.coinText))
                .setW(0.5F * mViewWidth)
                .setH(0.3F * TITLE_BAR_HEIGHT * mViewHeight)
                .centerTo(0.5F * mViewWidth, 0.4F * TITLE_BAR_HEIGHT * mViewHeight)
                .build();
        mGameLevelText = (UIText) new UIText.Build()
                .setTextSize(0.18F * TITLE_BAR_HEIGHT * mViewHeight)
                .setTextColor(ContextCompat.getColor(context, R.color.gameLevelText))
                .setW(0.5F * mViewWidth)
                .setH(0.3F * TITLE_BAR_HEIGHT * mViewHeight)
                .centerTo(0.5F * mViewWidth, 0.7F * TITLE_BAR_HEIGHT * mViewHeight)
                .build();

        for (int i = 1; i < BallsExtra.getItemCount(); i++) {
            int level = PreferencesUtils.getInt(context, Extra.Key.getItemLevelKey(i), 0);
            if (level <= 0) {
                continue;
            }
            createBall(i, level);
        }
    }

    public void createNewBall(Context context, int index) {
        if (index == 0) {
            return;
        }
        int level = PreferencesUtils.getInt(context, Extra.Key.getItemLevelKey(index), 0);
        if (level > 0) {
            return;
        }
        createBall(index, level);
    }

    private synchronized void createBall(int index, int level) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), BallsExtra.getItem(index)[BallsExtra.INDEX_DRAWABLE_ID]);
        float wh = 0.06F * mViewWidth;
        float speed = 0.02F * mViewWidth + 0.1F * index;
        float angle = (float) (Math.random() * Math.PI * 2);
        float speedX = (float) (speed * Math.sin(angle));
        float speedY = (float) (speed * Math.cos(angle));
        float power = BallsExtra.getPower(index, level);
        Ball ball = (Ball) new Ball.Build()
                .setBitmap(bitmap)
                .setW(wh)
                .setH(wh)
                .centerTo(0.5F * mViewWidth, 0.5F * mViewHeight)
                .setSpeedX(speedX)
                .setSpeedY(speedY)
                .setMovingRangeRectF(mMovingRangeRectF)
                .build();
        ball.setGameView(this);
        ball.setEnemyBallQueue(mEnemyBallQueue);
        ball.setPower(power);
        mBallQueue.add(ball);
    }


    private synchronized void createEnemyBall() {
        if (mMovingRangeRectF == null) {
            return;
        }
        if (mFrame % 3 != 0) {
            return;
        }
        if (mCreatedEnemyCount >= mMaxEnemyCount) {
            return;
        }

        int indexX = mCreatedEnemyCount / 4;
        int indexY = mCreatedEnemyCount % 4;
        float enemyBallWH = 0.11F * Math.min(mMovingRangeRectF.width(), mMovingRangeRectF.height());
        mCreatedEnemyCount++;
        float quarterW = mMovingRangeRectF.width() / 4;
        float quarterH = mMovingRangeRectF.height() / 4;

        float cX = (float) (mMovingRangeRectF.left + 0.5F * enemyBallWH + (indexX * quarterW) + Math.random() * (quarterW - enemyBallWH));
        float cY = (float) (mMovingRangeRectF.top + 0.5F * enemyBallWH + (indexY * quarterH) + Math.random() * (quarterH - enemyBallWH));

        createEnemyBall(enemyBallWH, cX, cY);
    }

    private synchronized void createEnemyBall(float wh, float cX, float cY) {
        EnemyBall enemyBall = (EnemyBall) new EnemyBall.Build()
                .setW(wh)
                .setH(wh)
                .centerTo(cX, cY)
                .setMovingRangeRectF(mMovingRangeRectF)
                .build();
        enemyBall.setValue(EnemyBallExtra.getEnemyBallMaxValue(mGameLevel));

        mEnemyBallQueue.add(enemyBall);
    }

    @DebugLog
    @Override
    public void drawCache() {
        drawSpriteQueue(mCanvasCache, mPaint);

        mTitleBarBg.draw(mCanvasCache, mPaint);

        if (mCoin < 0) {
            mCoin = 0;
            if (DEBUG) {
                throw new IllegalArgumentException("mCoin < 0");
            }
        }
        mCoinText.setText(GameExtra.format1(mCoin));
        mCoinText.draw(mCanvasCache, mPaint);

        mProgressBar.draw(mCanvasCache, mPaint);

        String gameLevelText = getContext().getString(R.string.level) + " " + mGameLevel;
        mGameLevelText.setText(gameLevelText);
        mGameLevelText.draw(mCanvasCache, mPaint);
    }

    @DebugLog
    private void drawSpriteQueue(Canvas canvas, Paint paint) {
        for (Sprite sprite : mEnemyBallQueue) {
            sprite.draw(canvas, paint);
        }
        for (Sprite sprite : mBallQueue) {
            sprite.draw(canvas, paint);
        }
    }

    @Override
    protected void timingLogic() {
        createEnemyBall();

        removeDestroyedSprite();

        if (mProgressBar != null && mCreatedEnemyCount == mMaxEnemyCount) {
            mProgressBar.setProgress(100F - 100F * mEnemyBallQueue.size() / 16F);
        }
        levelUp();
    }

    private synchronized void levelUp() {
        if (mEnemyBallQueue.isEmpty() && mCreatedEnemyCount != 0) {
            mCreatedEnemyCount = 0;
            mProgressBar.setProgress(0);
            mGameLevel++;
        }
    }

    private void removeDestroyedSprite() {
        for (EnemyBall enemyBall : mEnemyBallQueue) {
            if (enemyBall.isDestroyed()) {
                mEnemyBallQueue.remove(enemyBall);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            float eventX = event.getX();
            float eventY = event.getY();
            for (EnemyBall enemyBall : mEnemyBallQueue) {
                if (SpriteControl.isClicked(enemyBall, eventX, eventY)) {
                    int tapLevel = PreferencesUtils.getInt(getContext(), Extra.Key.getItemLevelKey(0), 1);
                    float tapPower = BallsExtra.getPower(0, tapLevel);
                    // 取tapPower与enemyBall.getValue()的较小值
                    float value = Math.min(tapPower, enemyBall.getValue());
                    enemyBall.deleteValue(value);
                    addCoin(value);
                }
            }
        }
        return true;
    }

    public void deleteCoin(float coin) {
        if (mCoin > coin) {
            mCoin -= coin;
        }
        if (mOnGameListener != null) {
            mOnGameListener.onCoinChange(mCoin);
        }
    }

    public void addCoin(float coin) {
        mCoin += coin;
        if (mOnGameListener != null) {
            mOnGameListener.onCoinChange(mCoin);
        }
    }

    private OnGameListener mOnGameListener;

    public void setOnGameListener(OnGameListener onGameListener) {
        mOnGameListener = onGameListener;
        if (mOnGameListener != null) {
            mOnGameListener.onCoinChange(mCoin);
        }
    }

    public void save(Context context) {
        PreferencesUtils.putFloat(context, Extra.Key.COIN, mCoin);

        PreferencesUtils.putInt(context, Extra.Key.GAME_LEVEL, mGameLevel);

        for (int i = 0; i < mEnemyBallQueue.size(); i++) {

        }
    }
}
