package org.hildan.leboncoin.api

private val ATTRIBUTES_TO_REMOVE_FOR_CREATION = setOf("pseudo", "smart_tags", "geoip_country")

data class Ad(
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
    val phone_hidden: Boolean = false
) {
    fun toNewAd(pricingId: String, newLocation: Location? = null) = AdToCreate(
        ad_type = ad_type,
        category_id = category_id,
        subject = subject,
        body = body,
        price = price,
        attributes = attributes.filterKeys { it !in ATTRIBUTES_TO_REMOVE_FOR_CREATION },
        images = images,
        email = email,
        location = newLocation ?: location,
        no_salesmen = no_salesmen,
        phone = phone,
        phone_hidden = phone_hidden,
        pricing_id = pricingId
    )
}

data class AdToCreate(
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
