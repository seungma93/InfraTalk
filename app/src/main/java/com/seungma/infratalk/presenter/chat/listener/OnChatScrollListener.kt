package com.seungma.infratalk.presenter.chat.listener

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seungma.infratalk.presenter.chat.adapter.ChatListAdapter


class OnChatScrollListener(
    private val moreItems: () -> Unit,
    private val showToast: () -> Unit,
    private val showProgress: () -> Unit
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastVisibleItemPosition =
            (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0

        val adapter = recyclerView.adapter as ChatListAdapter

        adapter.apply {
            val itemCount = itemCount - 1
            if (!recyclerView.canScrollVertically(-1) && itemCount == lastVisibleItemPosition) {
                //showProgress()
                when (getItemAt(lastVisibleItemPosition).chatMessageEntity.isLastPage) {
                    true -> showToast()
                    false -> moreItems()
                }
            }
        }
    }
}
