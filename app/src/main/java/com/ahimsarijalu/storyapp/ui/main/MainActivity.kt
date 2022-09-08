package com.ahimsarijalu.storyapp.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.ahimsarijalu.storyapp.data.local.model.UserPreference
import com.ahimsarijalu.storyapp.data.remote.response.ListStoryItem
import com.ahimsarijalu.storyapp.databinding.ActivityMainBinding
import com.ahimsarijalu.storyapp.ui.ViewModelFactory
import com.ahimsarijalu.storyapp.ui.addstory.AddStoryActivity
import com.ahimsarijalu.storyapp.ui.detailstory.DetailStoryActivity
import com.ahimsarijalu.storyapp.ui.login.LoginActivity
import com.ahimsarijalu.storyapp.ui.map.MapsActivity

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
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[MainViewModel::class.java]

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        mainViewModel.getUser().observe(this) { user ->
            if (user.token.isNotEmpty()) {
                showStories()
            } else {
                Intent(this, LoginActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }
        }
    }

    private fun setupAction() {
        binding.fabAddStory.setOnClickListener {
            Intent(this, AddStoryActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun showStories() {

        val adapter = MainAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        mainViewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
        }

        adapter.setOnItemClickCallback(object : MainAdapter.OnItemClickCallback {
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
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
            R.id.btn_map -> {
                Intent(this, MapsActivity::class.java).apply {
                    startActivity(this)
                }
                true
            }
            else -> true
        }
    }
}