package com.cpxiao.idleballz.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cpxiao.AppConfig;
import com.cpxiao.R;
import com.cpxiao.idleballz.imps.OnBallItemClicked;
import com.cpxiao.idleballz.mode.data.BallItemData;
import com.cpxiao.idleballz.mode.extra.BallsExtra;
import com.cpxiao.idleballz.mode.extra.GameExtra;

import java.util.List;


/**
 * @author cpxiao on 2017/9/28.
 */

public class BallsRecyclerViewAdapter extends RecyclerView.Adapter<CommonViewHolder> {
    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = BallsRecyclerViewAdapter.class.getSimpleName();

    private Context mContext;
    private List<BallItemData> mDataList;

    public BallsRecyclerViewAdapter(Context context, List<BallItemData> list) {
        mContext = context;
        mDataList = list;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (int) (parent.getMeasuredWidth() / 3.5F);
        int marginLR = (int) (Resources.getSystem().getDisplayMetrics().density * 3);
        params.leftMargin = marginLR;
        params.rightMargin = marginLR;
        params.width -= (2 * marginLR);
        view.setLayoutParams(params);
        return new CommonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        if (holder == null || mDataList == null) {
            if (DEBUG) {
                throw new IllegalArgumentException("holder == null || mDataList == null");
            }
            return;
        }
        if (position < 0 || position >= mDataList.size()) {
            if (DEBUG) {
                throw new IllegalArgumentException("position < 0 || position >= mDataList.size()");
            }
            return;
        }
        final int index = holder.getAdapterPosition();
        final BallItemData data = mDataList.get(index);
        if (data.level > 0) {
            holder.mTitle.setText(mContext.getString(R.string.level) + " " + data.level);
        } else {
            holder.mTitle.setText("");
        }
        holder.mIcon.setImageDrawable(data.drawable);
        holder.mMsg.setText(GameExtra.format2(data.power));
        holder.mBtnTextView.setText(GameExtra.format1(data.updatePrice));
        if (data.isCanBeUpdated) {
            holder.mBtnLayout.setClickable(true);
            holder.mBtnLayout.setBackgroundResource(R.drawable.home_btn_bg);
            holder.mBtnLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DEBUG) {
                        Log.d(TAG, "onClick: ");
                    }
                    clicked(data, index);
                }
            });
        } else {
            holder.mBtnLayout.setClickable(false);
            holder.mBtnLayout.setBackgroundResource(R.drawable.home_btn_untap_bg);
        }

        if (data.level <= 0) {
            holder.mBtnImageView.setImageResource(R.drawable.locked);
        } else {
            holder.mBtnImageView.setImageResource(R.drawable.update);
        }
    }

    private void clicked(BallItemData data, int index) {
        //the item ball level/power/price up
        data.level++;
        data.power = BallsExtra.getPower(index, data.level);
        data.updatePrice = BallsExtra.getUpdatePrice(index, data.level);

        //the game view ball power up
        if (mOnItemClicked != null) {
            mOnItemClicked.onItemClicked(index, data.updatePrice, data.level);
        } else {
            if (DEBUG) {
                Log.d(TAG, "onClick: ....mOnItemClicked == null");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    private OnBallItemClicked mOnItemClicked;

    public void setOnItemClicked(OnBallItemClicked onItemClicked) {
        mOnItemClicked = onItemClicked;
    }
}
