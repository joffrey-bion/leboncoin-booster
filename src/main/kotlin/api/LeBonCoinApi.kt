package org.hildan.leboncoin.api

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import java.nio.file.*

class LeBonCoin(
    val rootUrl: String = "https://api.leboncoin.fr/api",
    val apiKey: String = "ba0c2dad52b3ec"
) {
    private val http = HttpClient(Apache) {
        install(ContentNegotiation) {
            jackson {
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
                setBody(FormDataContent(
                    parametersOf(
                        "client_id" to listOf("frontweb"),
                        "grant_type" to listOf("password"),
                        "username" to listOf(email),
                        "password" to listOf(password)
                    )
                ))
            }.body()

    suspend fun getLocation(addressHint: String): Location = http.get("$rootUrl/ad-geoloc/v1/geocode") {
        parameter("address", addressHint)
    }.body()

    inner class Session(
        private val http: HttpClient,
        private val authInfo: AuthInfo
    ) {
        suspend fun getUser(): LbcUser = fetchUserData().toUser()

        private suspend fun fetchUserData(): UserData = http.authGet("/accounts/v1/accounts/me/personaldata")

        private suspend fun UserData.toUser(): LbcUser {
            with(personalData) {
                val location = getLocation(addresses.billing.city)
                return LbcUser(this@toUser.storeId, firstname, lastname, email, phones.main.number, location)
            }
        }

        suspend fun listUserAds(): AdSearchResult {
            val user = getUser()
            return http.authPost("/dashboard/v1/search") {
                headers {
                    append("Content-Type", "application/json")
                }
                setBody(AdSearchRequest.ofUser(user.storeId.toString()))
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

        suspend fun createAd(ad: LbcAd, location: Location? = null): AdCreationResponse {
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
                    setBody(ad)
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
                setBody(MultiPartFormDataContent(formData {
                    appendFileInput("file", imageFile, "image/jpeg")
                }))
            }
        }

        private suspend inline fun <reified T> HttpClient.authPost(
            path: String,
            block: HttpRequestBuilder.() -> Unit
        ): T = post("$rootUrl$path") {
            appendBearerToken(authInfo.accessToken)
            block()
        }.body()

        private suspend inline fun <reified T> HttpClient.authGet(
            path: String,
            block: HttpRequestBuilder.() -> Unit = {}
        ): T = get("$rootUrl$path") {
            appendBearerToken(authInfo.accessToken)
            block()
        }.body()
    }
}

data class LbcUser(
    val storeId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val location: Location
)

data class LbcAd(
    val title: String,
    val body: String,
    val category: Category,
    val price: Int,
    val attributes: Map<String, String>,
    val imagePaths: List<Path>
) {
    init {
        require(body.trim().length >= 15) { "Body must be 15 characters minimum. Invalid ad: $title" }
    }
}

enum class Category(val id: Int) {
    /** Informatique */
    COMPUTERS(15),
    /** Image et son */
    AUDIO_AND_VIDEO(16),
    /** Téléphonie */
    PHONES(17),
    /** Meubles */
    FURNITURES(19),
    /** Electromenager */
    APPLIANCES(20),
    /** Vetements */
    CLOTHES(22),
    SPORT_HOBBIES(29),
    DECORATION(39),
    TOYS(41),
    CONSOLES_AND_GAMES(43),
    /** Arts de la Table */
    KITCHEN(45),
    GARDEN(52)
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
