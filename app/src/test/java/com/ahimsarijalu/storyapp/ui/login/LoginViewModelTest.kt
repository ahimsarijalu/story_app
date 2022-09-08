package com.ahimsarijalu.storyapp.ui.login

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.MutableLiveData
import com.ahimsarijalu.storyapp.DataDummy
import com.ahimsarijalu.storyapp.MainCoroutineRule
import com.ahimsarijalu.storyapp.data.AuthRepository
import com.ahimsarijalu.storyapp.data.Result
import com.ahimsarijalu.storyapp.data.local.model.UserPreference
import com.ahimsarijalu.storyapp.data.remote.response.AuthResponse
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
class LoginViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var context: Context
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userPreference: UserPreference
    private lateinit var loginViewModel: LoginViewModel
    private val credential = DataDummy.generateDummyUser()
    private val dummyResponse = DataDummy.generateDummyLogin()

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = mainCoroutineRule,
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
        userPreference = UserPreference.getInstance(dataStore)
        loginViewModel = LoginViewModel(userPreference, authRepository)
    }

    @Test
    fun `when Login success should return success`() {
        val expectedResponse = MutableLiveData<Result<AuthResponse>>()
        expectedResponse.value = Result.Success(dummyResponse)
        `when`(loginViewModel.loginUser(credential.email, credential.password)).thenReturn(
            expectedResponse
        )

        val actualResponse =
            loginViewModel.loginUser(credential.email, credential.password).getOrAwaitValue()

        Mockito.verify(authRepository).loginUser(credential.email, credential.password)
        Assert.assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when Login failed should return Error`() {
        val expectedResponse = MutableLiveData<Result<AuthResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(loginViewModel.loginUser(credential.email, credential.password)).thenReturn(
            expectedResponse
        )

        val actualResponse =
            loginViewModel.loginUser(credential.email, credential.password).getOrAwaitValue()

        Mockito.verify(authRepository).loginUser(credential.email, credential.password)
        Assert.assertTrue(actualResponse is Result.Error)
    }

    @Test
    fun `when get User should not null`() = mainCoroutineRule.runBlockingTest {
        val actualResponse = loginViewModel.getUser()
        Assert.assertNotNull(actualResponse)
    }

    @Test
    fun `when save User should save User to datastore`() = mainCoroutineRule.runBlockingTest {
        val dummyData = DataDummy.generateDummyUserDatastore()
        loginViewModel.saveUser(dummyData)
        Assert.assertNotNull(dataStore.data)
    }
}