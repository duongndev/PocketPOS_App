package com.duongnd.pocketposapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.duongnd.pocketposapp.data.remote.api.ProductAPI
import com.duongnd.pocketposapp.data.remote.mapper.toDomainModel
import com.duongnd.pocketposapp.domain.model.Product

class ProductPagingSource(
    private val api: ProductAPI,
    private val searchQuery: String?,
    private val categoryId: String?,
    private val onTotalItemsFetched: (Int) -> Unit = {}
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: 1
        return try {
            val response = api.getProducts(
                page = page,
                limit = params.loadSize,
                search = searchQuery,
                categoryId = if (categoryId == "Tất cả") null else categoryId
            )

            if (response.success) {
                val products = response.data.map { it.toDomainModel() }
                
                response.pagination?.let {
                    onTotalItemsFetched(it.totalItems)
                }

                LoadResult.Page(
                    data = products,
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

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
