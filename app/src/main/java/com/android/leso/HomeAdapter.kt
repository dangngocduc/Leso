package com.android.leso

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.android.leso.model.Title
import com.android.leso.model.Title2
import com.android.leso.viewholders.ViewHolderSimple2
import com.android.leso.viewholders.ViewHolderSimpleTitle
import com.annotation.AdapterRecycleView

/**
 * Created by DANGNGOCDUC on 6/14/2017.
 */

@AdapterRecycleView(viewholders = [(ViewHolderSimpleTitle::class), (ViewHolderSimple2::class)])
class HomeAdapter(var context :Context) : HomeAdapter_Builder<Any>(context)  {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolderSimpleTitle -> {
                holder.bindData(context, mDatas[position] as Title)
            }
            is ViewHolderSimple2 -> {
                holder.bindData(context, mDatas[position] as Title2)
            }
        }

    }
}
