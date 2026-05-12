package com.duongnd.pocketposapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.duongnd.pocketposapp.data.remote.api.ProductAPI
import com.duongnd.pocketposapp.data.remote.mapper.toDomainModel
import com.duongnd.pocketposapp.domain.model.VariantDisplayItem

class ProductVariantPagingSource(
    private val api: ProductAPI,
    private val searchQuery: String?
) : PagingSource<Int, VariantDisplayItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VariantDisplayItem> {
        val page = params.key ?: 1
        return try {
            val response = api.getProductVariants(
                page = page,
                limit = params.loadSize,
                search = searchQuery
            )

            if (response.success) {
                val items = response.data.variants.map { variantDto ->
                    VariantDisplayItem(
                        product = variantDto.productId.toDomainModel(),
                        variant = variantDto.toDomainModel()
                    )
                }

                LoadResult.Page(
                    data = items,
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

    override fun getRefreshKey(state: PagingState<Int, VariantDisplayItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
