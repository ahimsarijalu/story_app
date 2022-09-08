package com.ahimsarijalu.storyapp.ui.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ahimsarijalu.storyapp.DataDummy
import com.ahimsarijalu.storyapp.data.AuthRepository
import com.ahimsarijalu.storyapp.data.Result
import com.ahimsarijalu.storyapp.data.remote.response.AuthResponse
import com.ahimsarijalu.storyapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class SignupViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var signupViewModel: SignupViewModel
    private val dummyCredential = DataDummy.generateDummyUser()
    private val dummyResponse = DataDummy.generateDummyRegister()

    @Before
    fun setup() {
        signupViewModel = SignupViewModel(authRepository)
    }

    @Test
    fun `when Signup success should return success`() {
        val expectedResponse = MutableLiveData<Result<AuthResponse>>()
        expectedResponse.value = Result.Success(dummyResponse)
        `when`(
            signupViewModel.signupUser(
                dummyCredential.name,
                dummyCredential.email,
                dummyCredential.password
            )
        ).thenReturn(expectedResponse)

        val actualResponse = signupViewModel.signupUser(
            dummyCredential.name,
            dummyCredential.email,
            dummyCredential.password
        ).getOrAwaitValue()

        Mockito.verify(authRepository)
            .registerUser(dummyCredential.name, dummyCredential.email, dummyCredential.password)
        Assert.assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when Signup failed should Return Error`() {
        val expectedResponse = MutableLiveData<Result<AuthResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(
            signupViewModel.signupUser(
                dummyCredential.name,
                dummyCredential.email,
                dummyCredential.password
            )
        ).thenReturn(expectedResponse)

        val actualResponse = signupViewModel.signupUser(
            dummyCredential.name,
            dummyCredential.email,
            dummyCredential.password
        ).getOrAwaitValue()

        Mockito.verify(authRepository)
            .registerUser(dummyCredential.name, dummyCredential.email, dummyCredential.password)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}