package com.ahimsarijalu.storyapp.ui.addstory

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
import com.ahimsarijalu.storyapp.data.remote.response.FileUploadResponse
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
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {

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
    private lateinit var addStoryViewModel: AddStoryViewModel
    private val storyDummy = DataDummy.generateDummyStory()
    private val uploadResponse = DataDummy.generateDummyUpload()

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = mainCoroutineRule,
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
        userPreference = UserPreference.getInstance(dataStore)
        addStoryViewModel = AddStoryViewModel(userPreference, storyRepository)
    }

    @Test
    fun `when Upload success should return success`() {
        val expectedResponse = MutableLiveData<Result<FileUploadResponse>>()
        expectedResponse.value = Result.Success(uploadResponse)
        Mockito.`when`(
            addStoryViewModel.uploadImage(
                storyDummy.file,
                storyDummy.description,
                storyDummy.token,
                storyDummy.location
            )
        ).thenReturn(expectedResponse)

        val actualResponse =
            addStoryViewModel.uploadImage(
                storyDummy.file,
                storyDummy.description,
                storyDummy.token,
                storyDummy.location
            ).getOrAwaitValue()

        Mockito.verify(storyRepository).addStory(
            storyDummy.file,
            storyDummy.description,
            storyDummy.token,
            storyDummy.location
        )
        Assert.assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when Upload success failed should return Error`() {
        val expectedResponse = MutableLiveData<Result<FileUploadResponse>>()
        expectedResponse.value = Result.Error("Error")
        Mockito.`when`(
            addStoryViewModel.uploadImage(
                storyDummy.file,
                storyDummy.description,
                storyDummy.token,
                storyDummy.location
            )
        ).thenReturn(expectedResponse)

        val actualResponse =
            addStoryViewModel.uploadImage(
                storyDummy.file,
                storyDummy.description,
                storyDummy.token,
                storyDummy.location
            ).getOrAwaitValue()

        Mockito.verify(storyRepository).addStory(
            storyDummy.file,
            storyDummy.description,
            storyDummy.token,
            storyDummy.location
        )
        Assert.assertTrue(actualResponse is Result.Error)
    }

    @Test
    fun `when get User should not null`() = mainCoroutineRule.runBlockingTest {
        val actualResponse = addStoryViewModel.getUser()
        Assert.assertNotNull(actualResponse)
    }
}