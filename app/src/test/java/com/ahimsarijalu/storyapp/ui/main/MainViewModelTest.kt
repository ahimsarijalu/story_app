package com.ahimsarijalu.storyapp.ui.main

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.ahimsarijalu.storyapp.DataDummy
import com.ahimsarijalu.storyapp.MainCoroutineRule
import com.ahimsarijalu.storyapp.data.StoryRepository
import com.ahimsarijalu.storyapp.data.local.model.UserPreference
import com.ahimsarijalu.storyapp.data.remote.response.ListStoryItem
import com.ahimsarijalu.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var context: Context
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userPreference: UserPreference
    private lateinit var mainViewModel: MainViewModel

    @Mock private lateinit var mockedMainViewModel: MainViewModel

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = mainCoroutineRule,
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
        userPreference = UserPreference.getInstance(dataStore)

        mainViewModel = MainViewModel(userPreference, storyRepository)
    }

    @Test
    fun `when stories should not empty`() = mainCoroutineRule.runBlockingTest {
        val dummyStory = DataDummy.generateDummyStoryResponse().listStory
        val data = PagedTestDataSources.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<ListStoryItem>>()
        stories.value = data
        `when`(mockedMainViewModel.stories).thenReturn(stories)
        val actualStories = mockedMainViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRule.dispatcher,
            workerDispatcher = mainCoroutineRule.dispatcher
        )
        differ.submitData(actualStories)
        advanceUntilIdle()

        Mockito.verify(mockedMainViewModel).stories
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `when get User should not null`() = mainCoroutineRule.runBlockingTest {
        val actualResponse = mainViewModel.getUser()
        Assert.assertNotNull(actualResponse)
    }

    @Test
    fun `when logout datastore should be empty`() = mainCoroutineRule.runBlockingTest {
        mainViewModel.logout()
        val dataStoreValue = mainViewModel.getUser().value
        Assert.assertNull(dataStoreValue?.userId)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class PagedTestDataSources private constructor() :
    PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}