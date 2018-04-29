package com.android.leso.viewholders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

import com.android.leso.R
import com.android.leso.model.Title
import com.annotation.ViewHolder
import kotlinx.android.synthetic.main.row_simple_title.view.*

@ViewHolder(layout = R.layout.row_simple_title, data = Title::class)
class ViewHolderSimpleTitle(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindData(context: Context, title: Title) {

        itemView.title.text = title.titlte
    }
}
