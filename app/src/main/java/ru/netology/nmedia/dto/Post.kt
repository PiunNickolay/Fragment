package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean = false,
    val likeCount: Int = 100,
    val shareCount: Int = 100,
    val viewCount: Int = 100,
    val video: String? = null
)
