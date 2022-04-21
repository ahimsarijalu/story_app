package com.ahimsarijalu.storyapp.ui.detailstory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ahimsarijalu.storyapp.R
import com.ahimsarijalu.storyapp.data.remote.response.ListStoryItem
import com.ahimsarijalu.storyapp.databinding.ActivityDetailStoryBinding
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)

        binding.apply {
            Glide.with(this@DetailStoryActivity)
                .load(story?.photoUrl)
                .centerCrop()
                .into(imgDetailStory)
            tvDetailName.text = story?.name ?: getString(R.string.name_hint)
            tvDescription.text = story?.description ?: getString(R.string.description)
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}