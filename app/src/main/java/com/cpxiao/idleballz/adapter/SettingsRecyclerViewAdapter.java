package com.cpxiao.idleballz.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cpxiao.AppConfig;
import com.cpxiao.R;
import com.cpxiao.idleballz.imps.OnBoostersItemClicked;
import com.cpxiao.idleballz.mode.data.SettingsItemData;

import java.util.List;


/**
 * @author cpxiao on 2017/10/31.
 */

public class SettingsRecyclerViewAdapter extends RecyclerView.Adapter<CommonViewHolder> {
    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = SettingsRecyclerViewAdapter.class.getSimpleName();

    private static final float ITEM_COUNT = 3;

    private Context mContext;
    private List<SettingsItemData> mDataList;

    public SettingsRecyclerViewAdapter(Context context, List<SettingsItemData> list) {
        mContext = context;
        mDataList = list;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (int) (parent.getMeasuredWidth() / ITEM_COUNT);
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
        final SettingsItemData data = mDataList.get(index);
        holder.mTitle.setText(data.title);
        holder.mIcon.setImageDrawable(data.drawable);
        holder.mBtnImageView.setImageResource(R.drawable.boost_btn);
        holder.mBtnLayout.setBackgroundResource(R.drawable.home_btn_bg);
        holder.mBtnTextView.setText(data.btnMsg);
        holder.mBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBoostersItemClicked != null) {
                    mOnBoostersItemClicked.onItemClicked(index);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    private OnBoostersItemClicked mOnBoostersItemClicked;

    public void setOnBoostersItemClicked(OnBoostersItemClicked onBoostersItemClicked) {
        mOnBoostersItemClicked = onBoostersItemClicked;
    }
}
