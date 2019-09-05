package org.hildan.leboncoin.api

data class AdSearchResult(
    val total: Int,
    val total_private: Int,
    val total_pro: Int,
    val ads: List<AdResult>
)

data class AdResult(
    val list_id: Int,
    val ad_type: String,
    val subject: String,
    val body: String,
    val price: List<Int>,
    val attributes: List<AdResultAttribute>,
    val category_id: String,
    val category_name: String,
    val url: String,
    val images: AdResultImages
)

data class AdResultAttribute(
    val generic: Boolean,
    val key: String,
    val key_label: String?,
    val value: String,
    val value_label: String?
)

data class AdResultImages(
    val nb_images: Int,
    val small_url: String,
    val thumb_url: String,
    val urls: List<String>,
    val urls_large: List<String>,
    val urls_thumb: List<String>
)
