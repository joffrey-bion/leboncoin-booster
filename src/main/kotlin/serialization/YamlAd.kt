package org.hildan.leboncoin.serialization

import com.charleskorn.kaml.Yaml
import org.hildan.leboncoin.api.Category
import org.hildan.leboncoin.api.ClothingAttributes
import java.nio.file.Path
import kotlinx.serialization.Serializable
import org.hildan.leboncoin.api.LbcAd

@Serializable
data class YamlAds(
    val email: String,
    val ads: List<YamlAd>
)

@Serializable
data class YamlAd(
    val title: String,
    val body: String,
    val category: Category,
    val price: Int,
    val fbPrice: Int,
    val images: List<String>,
    val clothingAttributes: ClothingAttributes? = null
) {
    fun toLbcAd(imgFolder: Path) = LbcAd(
        title = title,
        body = body,
        category = category,
        price = price,
        imagePaths = images.map { imgFolder.resolve(it) },
        attributes = clothingAttributes?.toLbcAttributes() ?: emptyMap()
    )
}

fun readYamlAds(path: Path): YamlAds = Yaml.default.decodeFromString(YamlAds.serializer(), path.toFile().readText())

data class LbcAds(
    val email: String,
    val ads: List<LbcAd>
)

fun readLbcAds(path: Path): LbcAds = readYamlAds(path).run {
    LbcAds(email, ads.map { it.toLbcAd(path.parent) })
}
