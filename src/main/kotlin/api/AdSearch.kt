package org.hildan.leboncoin.api

data class AdSearchRequest(
    val context: String = "default",
    val filters: Filters,
    val limit: Int = 50,
    val offset: Int = 0,
    val sort_by: String = "date",
    val sort_order: String = "desc",
    val include_inactive: Boolean = true,
    val pivot: String? = null
) {
    companion object {
        fun ofUser(storeId: String) = AdSearchRequest(filters = Filters(OwnerFilter(storeId)))
    }
}

data class Filters(
    val owner: OwnerFilter
)

data class OwnerFilter(
    val store_id: String
)
