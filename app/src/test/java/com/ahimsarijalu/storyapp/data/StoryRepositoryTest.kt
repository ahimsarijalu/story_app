package com.ahimsarijalu.storyapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.ahimsarijalu.storyapp.DataDummy
import com.ahimsarijalu.storyapp.MainCoroutineRule
import com.ahimsarijalu.storyapp.data.remote.response.FileUploadResponse
import com.ahimsarijalu.storyapp.data.remote.response.ListStoryItem
import com.ahimsarijalu.storyapp.getOrAwaitValue
import com.ahimsarijalu.storyapp.ui.main.MainAdapter
import com.ahimsarijalu.storyapp.ui.main.noopListUpdateCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when get Story should not empty`() = mainCoroutineRule.runBlockingTest {
        val dummyUser = DataDummy.generateDummyLogin().loginResult
        val dummyStory = DataDummy.generateDummyStoryResponse().listStory
        val data = DataDummy.generatePagingData()
        val stories = MutableLiveData<PagingData<ListStoryItem>>()
        stories.value = data

        `when`(storyRepository.getStory(dummyUser!!.token)).thenReturn(stories)
        val actualStories = storyRepository.getStory(dummyUser.token)

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRule.dispatcher,
            workerDispatcher = mainCoroutineRule.dispatcher
        )
        differ.submitData(actualStories.getOrAwaitValue())
        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
    }

    @Test
    fun `when get Story with Location should return Success`() = mainCoroutineRule.runBlockingTest {
        val dummyUser = DataDummy.generateDummyLogin().loginResult
        val expectedStories = DataDummy.generateDummyStoryResponse().listStory

        val expectedResponse = MutableLiveData<Result<List<ListStoryItem>>>()
        expectedResponse.value = Result.Success(expectedStories)
        `when`(storyRepository.getStoryWithLocation(dummyUser!!.token)).thenReturn(expectedResponse)

        val actualStories = storyRepository.getStoryWithLocation(dummyUser.token).getOrAwaitValue()

        Assert.assertTrue(actualStories is Result.Success)
    }

    @Test
    fun `when upload story should return Success`() = mainCoroutineRule.runBlockingTest {
        val dummyStory = DataDummy.generateDummyStory()

        val expectedResponse = MutableLiveData<Result<FileUploadResponse>>()
        expectedResponse.value = Result.Success(DataDummy.generateDummyUpload())
        `when`(
            storyRepository.addStory(
                dummyStory.file,
                dummyStory.description,
                dummyStory.token,
                dummyStory.location
            )
        ).thenReturn(expectedResponse)

        val actualResponse = storyRepository.addStory(
            dummyStory.file,
            dummyStory.description,
            dummyStory.token,
            dummyStory.location
        ).getOrAwaitValue()

        Assert.assertTrue(actualResponse is Result.Success)

    }
}