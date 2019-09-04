package org.hildan.leboncoin.api

data class AdData(
    val ad_type: String = "sell",
    val category_id: String,
    val subject: String,
    val body: String,
    val price: String,
    val attributes: Map<String, String>,
    val images: List<AdImageRef>,
    val email: String,
    val location: Location,
    val no_salesmen: Boolean = true,
    val phone: String,
    val phone_hidden: Boolean = false,
    val pricing_id: String
)

data class AdImageRef(val name: String, val url: String)

data class Location(
    val address: String,
    val city: String,
    val label: String,
    val lat: Double,
    val lng: Double,
    val zipcode: String
)
