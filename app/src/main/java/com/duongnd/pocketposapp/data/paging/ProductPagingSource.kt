package com.duongnd.pocketposapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.duongnd.pocketposapp.data.remote.api.ProductAPI
import com.duongnd.pocketposapp.data.remote.mapper.toDomainModel
import com.duongnd.pocketposapp.domain.model.Product

class ProductPagingSource(
    private val api: ProductAPI,
    private val searchQuery: String?,
    private val categoryName: String?,
    private val onTotalItemsFetched: (Int) -> Unit = {}
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: 1
        return try {
            val response = api.getProducts(
                page = page,
                limit = params.loadSize,
                search = searchQuery
            )

            if (response.success && response.data != null) {
                val allProducts = response.data.products.map { it.toDomainModel() }
                
                // Cập nhật tổng số mục từ pagination metadata
                onTotalItemsFetched(response.data.pagination.totalItems)

                // Filter by category locally if API doesn't support it directly in search query
                // Or if it does, this could be optimized later
                val filteredProducts = if (categoryName == null || categoryName == "Tất cả") {
                    allProducts
                } else {
                    allProducts.filter { it.categoryName == categoryName }
                }

                LoadResult.Page(
                    data = filteredProducts,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (response.data.pagination.hasNextPage) page + 1 else null
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
