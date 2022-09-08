package com.ahimsarijalu.storyapp.ui.map

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.MutableLiveData
import com.ahimsarijalu.storyapp.DataDummy
import com.ahimsarijalu.storyapp.MainCoroutineRule
import com.ahimsarijalu.storyapp.data.Result
import com.ahimsarijalu.storyapp.data.StoryRepository
import com.ahimsarijalu.storyapp.data.local.model.UserPreference
import com.ahimsarijalu.storyapp.data.remote.response.ListStoryItem
import com.ahimsarijalu.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class MapsViewModelTest {

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
    private lateinit var mapsViewModel: MapsViewModel
    private val apiResponse = DataDummy.generateDummyStoryResponse()

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = mainCoroutineRule,
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
        userPreference = UserPreference.getInstance(dataStore)
        mapsViewModel = MapsViewModel(userPreference, storyRepository)
    }

    @Test
    fun `when get Story with Location success should return success`() {
        val expectedResponse = MutableLiveData<Result<List<ListStoryItem>>>()
        expectedResponse.value = Result.Success(apiResponse.listStory)
        `when`(mapsViewModel.getStoryWithLocation("token")).thenReturn(expectedResponse)

        val actualResponse = mapsViewModel.getStoryWithLocation("token").getOrAwaitValue()

        Mockito.verify(storyRepository).getStoryWithLocation("token")
        Assert.assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when get Story with Location failed should return Error`() {
        val expectedResponse = MutableLiveData<Result<List<ListStoryItem>>>()
        expectedResponse.value = Result.Error("Error")
        `when`(mapsViewModel.getStoryWithLocation("token")).thenReturn(expectedResponse)

        val actualResponse = mapsViewModel.getStoryWithLocation("token").getOrAwaitValue()

        Mockito.verify(storyRepository).getStoryWithLocation("token")
        Assert.assertTrue(actualResponse is Result.Error)
    }

    @Test
    fun `when get User should not null`() = mainCoroutineRule.runBlockingTest {
        val actualResponse = mapsViewModel.getUser()
        Assert.assertNotNull(actualResponse)
    }
}