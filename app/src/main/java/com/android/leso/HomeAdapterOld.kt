package com.android.leso

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.leso.model.Title
import com.android.leso.model.Title2
import com.android.leso.viewholders.ViewHolderSimple2
import com.android.leso.viewholders.ViewHolderSimpleTitle
import java.util.ArrayList

class HomeAdapterOld(var mContext : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val mLayoutInflater = LayoutInflater.from(mContext)!!
    protected var mDatas = ArrayList<Any>()

    fun addDatas(data: ArrayList<Any>) {
        mDatas.addAll(data)
        notifyItemRangeInserted(mDatas.size - data.size, data.size)
    }

    fun addData(data: Any) {
        mDatas.add(data)
        notifyItemInserted(mDatas.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return when (mDatas[position]) {
            is Title -> 1
            is Title2 -> 2
            else -> {
                throw ExceptionInInitializerError("not accept this Type")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            1 -> ViewHolderSimpleTitle(mLayoutInflater.inflate(R.layout.row_simple_title, parent, false))

            2 -> ViewHolderSimple2(mLayoutInflater.inflate(R.layout.row_simple_title2, parent, false))

            else ->  throw ExceptionInInitializerError("not accept this Type")

        }
    }

    override fun getItemCount() = mDatas.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolderSimpleTitle -> {
            }
            is ViewHolderSimple2 -> {
            }
        }
    }

}