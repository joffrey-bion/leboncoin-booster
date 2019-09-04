package org.hildan.leboncoin.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.parametersOf
import kotlinx.io.streams.asInput
import java.io.File
import java.nio.file.Path

data class AuthInfo(val access_token: String, val refresh_token: String)

private const val ROOT_URL = "https://api.leboncoin.fr/api"
private const val API_KEY = "ba0c2dad52b3ec"

object LeBonCoin {

    private val httpClient = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            }
        }
    }

    suspend fun login(email: String, password: String): LeBonCoinSession {
        val authInfo = requestToken(email, password)
        return LeBonCoinSession(httpClient, authInfo)
    }

    private suspend fun requestToken(email: String, password: String): AuthInfo =
            httpClient.post("$ROOT_URL/oauth/v1/token") {
                body = FormDataContent(
                    parametersOf(
                        "client_id" to listOf("frontweb"),
                        "grant_type" to listOf("password"),
                        "username" to listOf(email),
                        "password" to listOf(password)
                    )
                )
            }

    suspend fun getLocation(addressHint: String): Location = httpClient.get("$ROOT_URL/ad-geoloc/v1/geocode") {
        parameter("address", addressHint)
    }
}

class LeBonCoinSession(
    private val httpClient: HttpClient,
    private val authInfo: AuthInfo
) {
    suspend fun getUser(): User = fetchUserData().personalData.toUser()

    private suspend fun fetchUserData(): UserData = httpClient.get("$ROOT_URL/accounts/v1/accounts/me/personaldata") {
        headers {
            appendAuth()
        }
    }

    private suspend fun PersonalData.toUser(): User {
        val location = LeBonCoin.getLocation(addresses.billing.city)
        return User(firstname, lastname, email, phones.main.number, location)
    }

    suspend fun findAd(id: String): AdData = httpClient.get("$ROOT_URL/pintad/v1/public/manual/classified/$id") {
        headers {
            appendAuth()
        }
    }

    suspend fun createAd(ad: SimpleAd, location: Location? = null): String {
        val user = getUser()
        val adLocation = location ?: user.location
        val pricingId = getPricing(ad.category.id).pricingId
        val images = ad.imagePaths.map { uploadImage(it) }
        val adData =
                createAdData(user = user, simpleAd = ad, images = images, location = adLocation, pricingId = pricingId)
        val adResponse = createAd(adData)
        return adResponse.adId
    }

    private suspend fun createAd(adData: AdData): AdCreationResponse =
            httpClient.post("$ROOT_URL/adsubmit/v1/classifieds") {
                headers {
                    appendAuth()
                    append("Sec-Fetch-Mode", "no-cors")
                    append("Referer", "https://www.leboncoin.fr/deposer-une-annonce/")
                    append("Origin", "https://www.leboncoin.fr")
                    append("Content-Type", "application/json")
                }
                body = adData
            }

    private suspend fun getPricing(categoryId: Int): PricingResponse =
            httpClient.get("$ROOT_URL/options/v1/pricing/ad") {
                appendAuth()
                parameter("category", categoryId)
            }

    private suspend fun uploadImage(imagePath: Path): ImageRef {
        val imageFile = imagePath.toFile()
        if (!imageFile.exists() || !imageFile.isFile) {
            throw IllegalArgumentException("Image not found at path $imagePath")
        }
        return httpClient.post("$ROOT_URL/pintad/v1/public/upload/image") {
            headers {
                appendAuth()
                append("api_key", API_KEY)
                append("Sec-Fetch-Mode", "no-cors")
                append("Referer", "https://www.leboncoin.fr/deposer-une-annonce/")
                append("Origin", "https://www.leboncoin.fr")
            }
            body = MultiPartFormDataContent(formData {
                appendFileInput("file", imageFile, "image/jpeg")
            })
        }
    }

    private fun HttpRequestBuilder.appendAuth() {
        headers { appendAuth() }
    }

    private fun HeadersBuilder.appendAuth() {
        append("Authorization", "Bearer ${authInfo.access_token}")
    }

    private fun FormBuilder.appendFileInput(key: String, file: File, contentType: String) {
        val fileHeaders = Headers.build {
            append(HttpHeaders.ContentType, contentType)
            append(HttpHeaders.ContentDisposition, "filename=${file.name}")
        }
        appendInput(key, fileHeaders) { file.inputStream().asInput() }
    }
}

private fun createAdData(
    user: User,
    simpleAd: SimpleAd,
    images: List<ImageRef>,
    location: Location,
    pricingId: String
) = AdData(
    category_id = simpleAd.category.id.toString(),
    subject = simpleAd.title,
    body = simpleAd.body,
    price = simpleAd.price.toString(),
    attributes = simpleAd.attributes,
    images = images.map { AdImageRef(it.filename, it.url) },
    email = user.email,
    location = location,
    phone = user.phone,
    pricing_id = pricingId
)

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val location: Location
)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
private data class PricingResponse(val pricingId: String, val options: List<PricingOption>)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
private data class PricingOption(val name: String, val priceCentsTaxIncl: Int, val priceCentsTaxExcl: Int)

private data class ImageRef(val filename: String, val url: String)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
private data class AdCreationResponse(val status: String, val adId: String)
