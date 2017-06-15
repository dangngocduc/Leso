package com.android.leso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.annotation.Viewholder;

/**
 * Created by DANGNGOCDUC on 6/14/2017.
 */

@Viewholder(layout = R.layout.activity_main, data = Object.class)
public class TestViewHolder extends RecyclerView.ViewHolder {
    public TestViewHolder(View itemView) {
        super(itemView);
    }

    public void bindData(Context context, Object object) {

    }
}
