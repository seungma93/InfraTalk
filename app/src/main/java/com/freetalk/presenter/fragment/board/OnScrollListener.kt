package com.freetalk.presenter.fragment.board

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OnScrollListener(private val moreItems: () -> Unit, private val showToast: () -> Unit) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastVisibleItemPosition =
            (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0
        recyclerView.adapter?.let {
            val itemCount = it.itemCount - 1
            if (!recyclerView.canScrollVertically(1) && itemCount == lastVisibleItemPosition) {
                val morePage = it.itemCount % 10 == 0
                if (morePage) moreItems() else showToast()
            }
        }
    }
}