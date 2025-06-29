package ru.netology.nmedia.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Counts
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import java.util.Objects

interface OnInteractionListener {
    fun onLikeListener(post: Post)
    fun onShareListener(post: Post)
    fun onRemoveListener(post: Post)
    fun onEditListener(post: Post)
    fun onPost(post: Post)
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) = with(binding) {
        root.setOnClickListener {
            onInteractionListener.onPost(post)
        }
        author.text = post.author
        published.text = post.published
        content.text = post.content

        like.apply {
            isChecked = post.likedByMe
            text = Counts.countFormat(post.likeCount)
        }

        share.text = Counts.countFormat(post.shareCount)
        view.text = Counts.countFormat(post.viewCount)

        like.setOnClickListener {
            onInteractionListener.onLikeListener(post)
        }

        like.isClickable = true
        share.setOnClickListener {
            onInteractionListener.onShareListener(post)
        }

        share.isClickable = true
        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.post_actions)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            onInteractionListener.onRemoveListener(post)
                            true
                        }

                        R.id.edit -> {
                            onInteractionListener.onEditListener(post)
                            true
                        }

                        else -> false
                    }
                }
            }.show()
        }
        menu.isClickable = true

        if (!post.video.isNullOrBlank()) {
            binding.videoContainer.visibility = View.VISIBLE
            binding.playButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, post.video.toUri())
                it.context.startActivity(intent)
            }
            playButton.isClickable = true
        } else {
            binding.videoContainer.visibility = View.GONE
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem

}