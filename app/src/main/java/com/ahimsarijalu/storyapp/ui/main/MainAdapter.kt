package com.ahimsarijalu.storyapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahimsarijalu.storyapp.data.remote.response.ListStoryItem
import com.ahimsarijalu.storyapp.databinding.ItemRowStoryBinding
import com.bumptech.glide.Glide

class MainAdapter(private val listStory: ArrayList<ListStoryItem>) :
    RecyclerView.Adapter<MainAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(var binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = listStory[position]
        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .centerCrop()
            .into(holder.binding.imgStory)
        holder.binding.tvName.text = story.name

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(holder, listStory[position])
        }
    }

    override fun getItemCount(): Int = listStory.size

    interface OnItemClickCallback {
        fun onItemClicked(view: ListViewHolder, data: ListStoryItem)
    }
}