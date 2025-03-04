package com.sm.infratalk.presenter.chat.listener

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sm.infratalk.presenter.chat.adapter.ChatListAdapter


class OnChatScrollListener(
    private val moreItems: () -> Unit
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val adapter = recyclerView.adapter as ChatListAdapter
        val lastVisibleItemPosition =
            (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0

        adapter.apply {
            val itemCount = itemCount - 1
            if (!recyclerView.canScrollVertically(-1) && itemCount == lastVisibleItemPosition) {
                val lastPage = getItemAt(lastVisibleItemPosition).chatMessageEntity.isLastPage
                if(!lastPage) moreItems()
            }
        }
    }
}
