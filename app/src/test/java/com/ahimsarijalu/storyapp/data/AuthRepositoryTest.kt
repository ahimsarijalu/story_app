package com.ahimsarijalu.storyapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ahimsarijalu.storyapp.DataDummy
import com.ahimsarijalu.storyapp.MainCoroutineRule
import com.ahimsarijalu.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var apiService: ApiService
    private lateinit var authRepository: AuthRepository
    private val dummyCredential = DataDummy.generateDummyUser()

    @Before
    fun setup() {
        apiService = FakeApi()
        authRepository = AuthRepository(apiService)
    }

    @Test
    fun `when Register success should return success`() = mainCoroutineRule.runBlockingTest {
        val expectedResponse = DataDummy.generateDummyRegister()
        val actualResponse = apiService.registerUser(
            dummyCredential.name,
            dummyCredential.email,
            dummyCredential.password
        )
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse == expectedResponse)
    }

    @Test
    fun `when Login success should return success`() = mainCoroutineRule.runBlockingTest {
        val expectedResponse = DataDummy.generateDummyLogin()
        val actualResponse = apiService.loginUser(dummyCredential.email, dummyCredential.password)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse == expectedResponse)
    }
}