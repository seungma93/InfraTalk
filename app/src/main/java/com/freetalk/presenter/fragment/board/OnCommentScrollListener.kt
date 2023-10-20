package com.freetalk.presenter.fragment.board

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freetalk.presenter.adapter.CommentListAdapter
import com.freetalk.presenter.adapter.ListItem


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
