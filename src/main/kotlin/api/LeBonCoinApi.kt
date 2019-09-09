package org.hildan.leboncoin.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.parametersOf
import java.nio.file.Path

class LeBonCoin(
    val rootUrl: String = "https://api.leboncoin.fr/api",
    val apiKey: String = "ba0c2dad52b3ec"
) {
    private val http = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }

    suspend fun login(email: String, password: String): Session {
        val authInfo = requestToken(email, password)
        return Session(http, authInfo)
    }

    private suspend fun requestToken(email: String, password: String): AuthInfo =
            http.post("$rootUrl/oauth/v1/token") {
                body = FormDataContent(
                    parametersOf(
                        "client_id" to listOf("frontweb"),
                        "grant_type" to listOf("password"),
                        "username" to listOf(email),
                        "password" to listOf(password)
                    )
                )
            }

    suspend fun getLocation(addressHint: String): Location = http.get("$rootUrl/ad-geoloc/v1/geocode") {
        parameter("address", addressHint)
    }

    inner class Session(
        private val http: HttpClient,
        private val authInfo: AuthInfo
    ) {
        suspend fun getUser(): User = fetchUserData().toUser()

        private suspend fun fetchUserData(): UserData = http.authGet("/accounts/v1/accounts/me/personaldata")

        private suspend fun UserData.toUser(): User {
            with(personalData) {
                val location = getLocation(addresses.billing.city)
                return User(this@toUser.storeId, firstname, lastname, email, phones.main.number, location)
            }
        }

        suspend fun listUserAds(): AdSearchResult {
            val user = getUser()
            return http.authPost("/dashboard/v1/search") {
                headers {
                    append("Content-Type", "application/json")
                }
                body = AdSearchRequest.ofUser(user.storeId.toString())
            }
        }

        suspend fun recreateAllAds(useLocationFromUser: Boolean = false) {
            val ads = listUserAds().ads

            val idsOfAdsToDelete = mutableListOf<String>()
            ads.forEach {
                val id = it.list_id.toString()
                recreateAd(id, useLocationFromUser)
                idsOfAdsToDelete.add(id)
            }

            println("${ads.size} ads recreated, please delete the following:")
            idsOfAdsToDelete.forEach {
                println("https://www.leboncoin.fr/vetements/$it.htm/")
            }
        }

        suspend fun recreateAd(id: String, useLocationFromUser: Boolean = false): AdCreationResponse {
            val ad = findAd(id)
            println("Recreating ad $id: ${ad.subject}")
            val pricingId = getPricing(ad.category_id).pricingId
            val newLocation = if (useLocationFromUser) getUser().location else null
            val adToCreate = ad.toNewAd(pricingId, newLocation)
            return createAd(adToCreate)
        }

        suspend fun findAd(id: String): AdDetails = http.authGet("/pintad/v1/public/manual/classified/$id")

        suspend fun createAd(ad: SimpleAd, location: Location? = null): AdCreationResponse {
            val user = getUser()
            val adLocation = location ?: user.location
            val pricingId = getPricing(ad.category.id.toString()).pricingId
            val images = ad.imagePaths.map { uploadImage(it) }
            val adToCreate = AdToCreate(
                category_id = ad.category.id.toString(),
                subject = ad.title,
                body = ad.body,
                price = ad.price.toString(),
                attributes = ad.attributes,
                images = images.map { AdImageRef(it.filename, it.url) },
                email = user.email,
                location = adLocation,
                phone = user.phone,
                pricing_id = pricingId
            )
            return createAd(adToCreate)
        }

        private suspend fun createAd(ad: AdToCreate): AdCreationResponse =
                http.authPost("/adsubmit/v1/classifieds") {
                    headers {
                        append("Sec-Fetch-Mode", "no-cors")
                        append("Referer", "https://www.leboncoin.fr/deposer-une-annonce/")
                        append("Origin", "https://www.leboncoin.fr")
                        append("Content-Type", "application/json")
                    }
                    body = ad
                }

        private suspend fun getPricing(categoryId: String): PricingResponse =
                http.authGet("/options/v1/pricing/ad") {
                    parameter("category", categoryId)
                }

        private suspend fun uploadImage(imagePath: Path): ImageRef {
            val imageFile = imagePath.toFile()
            require(imageFile.exists() && imageFile.isFile) { "Image not found at path $imagePath" }

            return http.authPost("/pintad/v1/public/upload/image") {
                headers {
                    append("api_key", apiKey)
                    append("Sec-Fetch-Mode", "no-cors")
                    append("Referer", "https://www.leboncoin.fr/deposer-une-annonce/")
                    append("Origin", "https://www.leboncoin.fr")
                }
                body = MultiPartFormDataContent(formData {
                    appendFileInput("file", imageFile, "image/jpeg")
                })
            }
        }

        private suspend inline fun <reified T> HttpClient.authPost(
            path: String,
            block: HttpRequestBuilder.() -> Unit
        ): T = post("$rootUrl$path") {
            appendBearerToken(authInfo.accessToken)
            block()
        }

        private suspend inline fun <reified T> HttpClient.authGet(
            path: String,
            block: HttpRequestBuilder.() -> Unit = {}
        ): T = get("$rootUrl$path") {
            appendBearerToken(authInfo.accessToken)
            block()
        }
    }
}

data class User(
    val storeId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val location: Location
)

data class SimpleAd(
    val title: String,
    val body: String,
    val category: Category,
    val price: Int,
    val attributes: Map<String, String>,
    val imagePaths: List<Path>
)

enum class Category(val id: Int) {
    /** Vetements */
    CLOTHES(22),
    /** Meubles */
    FURNITURES(19),
    /** Electromenager */
    APPLIANCES(20),
    SPORT_HOBBIES(29),
    DECORATION(39),
    CONSOLES_AND_GAMES(43)
}

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class AuthInfo(val accessToken: String, val refreshToken: String)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
private data class PricingResponse(val pricingId: String, val options: List<PricingOption>)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
private data class PricingOption(val name: String, val priceCentsTaxIncl: Int, val priceCentsTaxExcl: Int)

private data class ImageRef(val filename: String, val url: String)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class AdCreationResponse(val status: String, val adId: String)
