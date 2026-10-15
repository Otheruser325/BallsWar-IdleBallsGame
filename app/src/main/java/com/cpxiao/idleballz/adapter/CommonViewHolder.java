package com.cpxiao.idleballz.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpxiao.R;

/**
 * @author cpxiao on 2017/11/01.
 */

public class CommonViewHolder extends RecyclerView.ViewHolder {
    TextView mTitle;
    ImageView mIcon;
    TextView mMsg;
    LinearLayout mBtnLayout;
    ImageView mBtnImageView;
    TextView mBtnTextView;

    CommonViewHolder(View itemView) {
        super(itemView);
        mTitle = (TextView) itemView.findViewById(R.id.title);
        mIcon = (ImageView) itemView.findViewById(R.id.icon);
        mMsg = (TextView) itemView.findViewById(R.id.msg);
        mBtnLayout = (LinearLayout) itemView.findViewById(R.id.btnLayout);
        mBtnImageView = (ImageView) itemView.findViewById(R.id.btnImageView);
        mBtnTextView = (TextView) itemView.findViewById(R.id.btnTextView);
    }
}