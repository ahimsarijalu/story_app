package com.ahimsarijalu.storyapp.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahimsarijalu.storyapp.R
import com.ahimsarijalu.storyapp.data.model.UserPreference
import com.ahimsarijalu.storyapp.data.remote.response.ListStoryItem
import com.ahimsarijalu.storyapp.databinding.ActivityMainBinding
import com.ahimsarijalu.storyapp.ui.ViewModelFactory
import com.ahimsarijalu.storyapp.ui.addstory.AddStoryActivity
import com.ahimsarijalu.storyapp.ui.detailstory.DetailStoryActivity
import com.ahimsarijalu.storyapp.ui.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.token.isNotEmpty()) {
                mainViewModel.getStoriesFromApi(this, user.token)
            } else {
                Intent(this, LoginActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        mainViewModel.stories.observe(this) { stories ->
            showStories(stories)
            if (stories.isEmpty()) {
                binding.notFound.visibility = View.VISIBLE
            } else {
                binding.notFound.visibility = View.GONE
            }
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

    }

    private fun setupAction() {
        binding.fabAddStory.setOnClickListener {
            Intent(this, AddStoryActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun showStories(stories: List<ListStoryItem>) {
        val listStories = ArrayList<ListStoryItem>()
        for (story in stories) {
            listStories.add(
                ListStoryItem(
                    id = story.id,
                    name = story.name,
                    description = story.description,
                    photoUrl = story.photoUrl,
                    createdAt = story.createdAt,
                    lat = story.lat,
                    lon = story.lon
                )
            )
        }
        val listStoryAdapter = MainAdapter(listStories)
        binding.rvStory.adapter = listStoryAdapter

        listStoryAdapter.setOnItemClickCallback(object : MainAdapter.OnItemClickCallback {
            override fun onItemClicked(view: MainAdapter.ListViewHolder, data: ListStoryItem) {
                showSelectedStory(view, data)
            }
        })
    }

    private fun showSelectedStory(view: MainAdapter.ListViewHolder, story: ListStoryItem) {
        val moveToDetailActivity = Intent(this@MainActivity, DetailStoryActivity::class.java)
        moveToDetailActivity.putExtra(DetailStoryActivity.EXTRA_STORY, story)
        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@MainActivity as Activity,
                Pair(view.binding.imgStory, "story"),
                Pair(view.binding.tvName, "name"),
            )
        startActivity(moveToDetailActivity, optionsCompat.toBundle())
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.logout_question))
                    setNegativeButton(getString(R.string.cancel_text), null)
                    setPositiveButton(getString(R.string.logout)) { _, _ ->
                        mainViewModel.logout()
                    }
                    create()
                    show()
                }
                true
            }
            else -> true
        }
    }


}