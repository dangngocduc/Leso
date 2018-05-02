package com.leso.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class LesoViewHolder<T>  extends RecyclerView.ViewHolder {

    public LesoViewHolder(View view) {
        super(view);
    }

    public abstract void bindData(Context context, T data, View.OnClickListener onClickListener);



}
