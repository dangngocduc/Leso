package com.android.leso.viewholders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

import com.android.leso.R
import com.android.leso.model.Title
import com.android.leso.model.Title2
import com.annotation.ViewHolder
import kotlinx.android.synthetic.main.row_simple_title2.view.*

@ViewHolder(layout = R.layout.row_simple_title2, data = Title2::class)
class ViewHolderSimple2(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindData(context: Context, title: Title2) {
        itemView.title.text = title.titlte
        itemView.subTitle.text = title.subTitle
    }
}
