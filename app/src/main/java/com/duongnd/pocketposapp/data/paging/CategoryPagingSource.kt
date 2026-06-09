package com.duongnd.pocketposapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.duongnd.pocketposapp.data.remote.api.CategoryAPI
import com.duongnd.pocketposapp.data.remote.dto.category.CategoryDTO
import com.duongnd.pocketposapp.data.remote.mapper.toDomainModel
import com.duongnd.pocketposapp.domain.model.Category

class CategoryPagingSource(
    private val api: CategoryAPI,
    private val sortBy: String? = null,
    private val sortOrder: String? = null
) : PagingSource<Int, Category>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Category> {
        val page = params.key ?: 1
        return try {
            val response = api.getCategories(
                page = page,
                limit = params.loadSize,
                sortBy = sortBy,
                sortOrder = sortOrder
            )

            if (response.success) {
                val categories = response.data.map { it: CategoryDTO -> it.toDomainModel() }
                
                LoadResult.Page(
                    data = categories,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (response.pagination?.hasNextPage == true) page + 1 else null
                )
            } else {
                LoadResult.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Category>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
