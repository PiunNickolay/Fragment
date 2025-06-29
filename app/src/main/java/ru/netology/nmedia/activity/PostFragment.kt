package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.Counts
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater, container, false)
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
        val card = binding.post
        val postId = arguments?.getLong("postId") ?: return binding.root

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == postId }
            if (post == null) {
                Toast.makeText(context, getString(R.string.the_post_is_missing), Toast.LENGTH_LONG)
                    .show()
                findNavController().navigateUp()
                return@observe
            }

            with(card) {
                author.text = post.author
                published.text = post.published
                content.text = post.content

                like.isChecked = post.likedByMe
                like.text = Counts.countFormat(post.likeCount)
                like.setOnClickListener {
                    viewModel.likeById(post.id)
                }

                share.text = Counts.countFormat(post.shareCount)
                share.setOnClickListener {
                    viewModel.shareById(post.id)
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val sharedIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(sharedIntent)
                }

                view.text = Counts.countFormat(post.viewCount)

                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.post_actions)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    viewModel.removeById(post.id)
                                    findNavController().navigateUp()
                                    true
                                }

                                R.id.edit -> {
                                    viewModel.edit(post)
                                    findNavController().navigate(
                                        R.id.action_feedFragment_to_newPostFragment,
                                        Bundle().apply { putString("textArg", post.content) }
                                    )
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                }
                if (!post.video.isNullOrBlank()) {
                    videoContainer.visibility = View.VISIBLE
                    playButton.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, post.video.toUri())
                        startActivity(intent)
                    }
                } else {
                    videoContainer.visibility = View.GONE
                }

            }
        }

        return binding.root
    }
}