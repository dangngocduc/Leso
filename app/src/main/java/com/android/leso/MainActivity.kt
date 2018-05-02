package com.android.leso

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.leso.model.Title
import com.android.leso.model.Title2
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var mAdapter : HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAdapter = HomeAdapter(this, View.OnClickListener {  })
        recycleView.adapter = mAdapter
        mAdapter.addAll(arrayListOf(Title("Title"), Title("Title"),
                Title2("Title", "SubTitle"), Title2("Title", "SubTitle")))
    }
}
