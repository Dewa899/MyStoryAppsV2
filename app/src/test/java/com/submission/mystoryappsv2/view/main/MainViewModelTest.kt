package com.submission.mystoryappsv2.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.submission.mystoryappsv2.CoroutineTestRule
import com.submission.mystoryappsv2.DataDummy
import com.submission.mystoryappsv2.FlowTestUtils.getOrAwaitValue
import com.submission.mystoryappsv2.LogMock
import com.submission.mystoryappsv2.data.repository.Repository
import com.submission.mystoryappsv2.view.story.StoryAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var mainViewModel: MainViewModel

    @Mock
    private lateinit var repository: Repository

    private val token = DataDummy.generateToken()

    @Before
    fun setUp() {
        LogMock.mockLog()
        mainViewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        LogMock.clearMock()
    }

    @Test
    fun `successfully Get Stories & Not Null`() = runTest {
        val storyPagingData = DataDummy.generateStoryPagingData()

        `when`(repository.getStories(token)).thenReturn(storyPagingData)

        val actualStories = mainViewModel.getStoriesFlow(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )

        differ.submitData(actualStories)

        Mockito.verify(repository).getStories(token)
        Assert.assertNotNull(differ.snapshot())
        assertEquals(DataDummy.generateStoryList().size, differ.snapshot().size)
        assertEquals(DataDummy.generateStoryList()[0], differ.snapshot()[0])
    }

    @Test
    fun `Get Stories returns empty PagingData`() = runTest {
        `when`(repository.getStories(token)).thenReturn(flowOf(PagingData.empty()))

        val stories = mainViewModel.getStoriesFlow(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )

        differ.submitData(stories)

        Assert.assertEquals(0, differ.snapshot().size)
    }

    private val listUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}

