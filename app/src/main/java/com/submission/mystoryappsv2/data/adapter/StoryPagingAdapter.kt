package com.submission.mystoryappsv2.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submission.mystoryappsv2.R
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.databinding.ItemStoryBinding

class StoryPagingAdapter(private val onClick: (Story, View) -> Unit) :
    PagingDataAdapter<Story, StoryPagingAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    class StoryViewHolder(
        private val binding: ItemStoryBinding,
        private val onClick: (Story, View) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.tvItemName.text = story.name
            Glide.with(binding.root)
                .load(story.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.image_placeholder) // Placeholder drawable
                .into(binding.ivItemPhoto)

            binding.root.setOnClickListener {
                onClick(story, binding.root)
            }
        }
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem == newItem
        }
    }
}
