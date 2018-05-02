package com.android.leso.viewholders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

import com.android.leso.R
import com.android.leso.model.Title
import com.annotation.ViewHolder
import com.leso.viewholder.LesoViewHolder
import kotlinx.android.synthetic.main.row_simple_title.view.*

@ViewHolder(layout = R.layout.row_simple_title, data = Title::class)
class ViewHolderSimpleTitle(itemView: View) : LesoViewHolder<Title>(itemView) {
    override fun bindData(context: Context?, data: Title?, onClickListener: View.OnClickListener?) {

    }


}
