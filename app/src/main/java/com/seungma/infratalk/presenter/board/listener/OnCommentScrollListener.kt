package com.seungma.infratalk.presenter.board.listener

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seungma.infratalk.presenter.board.adpater.CommentListAdapter
import com.seungma.infratalk.presenter.board.adpater.ListItem


class OnCommentScrollListener(
    private val moreItems: () -> Unit, private val showToast: () -> Unit
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastVisibleItemPosition =
            (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0

        val adapter = recyclerView.adapter as CommentListAdapter

        adapter.apply {
            val itemCount = itemCount - 1
            if (!recyclerView.canScrollVertically(1) && itemCount == lastVisibleItemPosition) {
                when (val item = adapter.getItemAt(lastVisibleItemPosition)) {
                    is ListItem.CommentItem -> {
                        when (item.commentEntity.commentMetaEntity.isLastPage) {
                            true -> showToast()
                            false -> moreItems()
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}
