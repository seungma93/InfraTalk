package com.seungma.infratalk.presenter.mypage.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.seungma.infratalk.databinding.ListItemMyLikeBoardBinding
import com.seungma.infratalk.domain.board.entity.BoardEntity
import com.seungma.infratalk.domain.board.entity.BoardMetaEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MyLikeBoardListAdapter(
    private val itemClick: (BoardMetaEntity) -> Unit,
    private val bookmarkClick: (BoardEntity) -> Unit,
    private val likeClick: (BoardEntity) -> Unit,
    private val deleteClick: (BoardEntity) -> Unit
) : ListAdapter<BoardEntity, MyLikeBoardListAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemMyLikeBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClick, bookmarkClick, likeClick, deleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemMyLikeBoardBinding,
        private val itemClick: (BoardMetaEntity) -> Unit,
        private val bookmarkClick: (BoardEntity) -> Unit,
        private val likeClick: (BoardEntity) -> Unit,
        private val deleteClick: (BoardEntity) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private var boardEntity: BoardEntity? = null

        init {
            binding.apply {
                root.setOnClickListener {
                    boardEntity?.let {
                        Log.d("comment", "리스트어댑터 데이터" + it.boardMetaEntity.author.email)
                        itemClick(it.boardMetaEntity)
                    }
                }
                btnBookmark.setOnClickListener {
                    btnBookmark.isEnabled = false
                    boardEntity?.let {
                        bookmarkClick(it)
                    }
                }
                btnLike.setOnClickListener {
                    Log.d("board", "좋아요 람다 전달")
                    btnLike.isEnabled = false
                    boardEntity?.let {
                        likeClick(it)
                    }
                }
                btnDelete.setOnClickListener {
                    boardEntity?.let {
                        deleteClick(it)
                    }
                }
            }

        }

        fun bind(boardEntity: BoardEntity) {
            this.boardEntity = boardEntity
            binding.apply {
                boardEntity.let {
                    Log.d("BoardListAdpater", "셀렉트 바인딩")
                    title.text = it.boardMetaEntity.title
                    content.text = it.boardMetaEntity.content
                    date.text = modifiedDate(it.boardMetaEntity.createTime)
                    author.text = it.boardMetaEntity.author.nickname
                    btnBookmark.isSelected = it.bookmarkEntity.isBookmark
                    btnLike.isSelected = it.likeEntity.isLike
                    likeCount.text = it.likeCountEntity.likeCount.toString()
                    val requestOptions = RequestOptions.circleCropTransform().autoClone()
                    it.boardMetaEntity.author.image?.let {
                        Glide.with(itemView.context)
                            .load(it)
                            .apply(requestOptions)
                            .into(ivProfile)

                    }
                    btnLike.isEnabled = true
                    btnBookmark.isEnabled = true
                }
            }
        }

        private fun modifiedDate(date: Date?): String {

            // 현재 날짜
            val currentDate = Date()

            // 날짜 포맷 지정
            val sdf = SimpleDateFormat("MM월 dd일", Locale.getDefault())

            // 날짜를 문자열로 변환
            val dateFromDatabaseString = sdf.format(date)
            val currentDateString = sdf.format(currentDate)

            // 날짜를 비교하여 표시할 내용 결정
            val displayText = if (dateFromDatabaseString == currentDateString) {
                // 같은 날짜인 경우, 시간으로 표시
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            } else {
                // 하루가 지났으면 일자로 표시
                dateFromDatabaseString
            }
            return displayText
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BoardEntity>() {

            // 두 아이템이 동일한 아이템인지 체크. 보통 고유한 id를 기준으로 비교
            override fun areItemsTheSame(
                oldItem: BoardEntity,
                newItem: BoardEntity
            ): Boolean {
                return oldItem.boardMetaEntity.createTime == newItem.boardMetaEntity.createTime &&
                        oldItem.boardMetaEntity.author == newItem.boardMetaEntity.author
            }

            // 두 아이템이 동일한 내용을 가지고 있는지 체크. areItemsTheSame()이 true일때 호출됨
            override fun areContentsTheSame(
                oldItem: BoardEntity,
                newItem: BoardEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}