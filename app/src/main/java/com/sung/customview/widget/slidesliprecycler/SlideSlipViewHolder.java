package com.sung.customview.widget.slidesliprecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sung.customview.R;

/**
 * Created by sung on 16/6/12.
 */
public class SlideSlipViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView singer;
    public ImageView img;
    public LinearLayout layout;
    public RelativeLayout delete;

    public SlideSlipViewHolder(View itemView) {
        super(itemView);
//        name = (TextView) itemView.findViewById(R.id.tv_song_name);
//        singer = (TextView) itemView.findViewById(R.id.tv_song_singer);
//        img= (ImageView) itemView.findViewById(R.id.iv_delete);
//        layout= (LinearLayout) itemView.findViewById(R.id.ll_recycler);
//        delete = (RelativeLayout) itemView.findViewById(R.id.rl_delete);
    }
}
