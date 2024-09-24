package org.hildan.leboncoin.api

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.headers
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.streams.*
import java.io.File

fun FormBuilder.appendFileInput(key: String, file: File, contentType: String) {
    val fileHeaders = Headers.build {
        append(HttpHeaders.ContentType, contentType)
        append(HttpHeaders.ContentDisposition, "filename=${file.name}")
    }
    appendInput(key, fileHeaders) { file.inputStream().asInput() }
}

fun HttpRequestBuilder.appendBearerToken(token: String) {
    headers { appendBearerToken(token) }
}

fun HeadersBuilder.appendBearerToken(token: String) {
    append("Authorization", "Bearer $token")
}
