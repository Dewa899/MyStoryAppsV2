import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.submission.mystoryappsv2.data.remote.ApiService
import com.submission.mystoryappsv2.data.remote.Story
import java.io.IOException
import retrofit2.HttpException

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String,
    private val includeLocation: Int = 0
) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getStories(token, page, params.loadSize, includeLocation)
            Log.d("StoryPagingSource", "Loaded page: $page, Data size: ${response.listStory.size}")

            LoadResult.Page(
                data = response.listStory,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.listStory.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            Log.e("StoryPagingSource", "IOException: ${exception.message}", exception)
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.e("StoryPagingSource", "HttpException: ${exception.message}", exception)
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

