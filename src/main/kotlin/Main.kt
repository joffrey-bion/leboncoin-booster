package org.hildan.leboncoin

import kotlinx.coroutines.runBlocking
import org.hildan.leboncoin.api.Category
import org.hildan.leboncoin.api.ClothingAttributes
import org.hildan.leboncoin.api.ClothingCategory
import org.hildan.leboncoin.api.ClothingCondition
import org.hildan.leboncoin.api.ClothingSize
import org.hildan.leboncoin.api.ClothingType
import org.hildan.leboncoin.api.LeBonCoin
import org.hildan.leboncoin.api.SimpleAd
import java.nio.file.Paths

private val IMG_FOLDER = Paths.get("C:", "Users", "joffr", "Desktop", "Annonces vêtements")

fun main() {
    runBlocking {

        val session = LeBonCoin().login("joffrey.bion@gmail.com", "DarkLink40")

        val ad = createAdLinenPants()

        session.createAd(ad)
    }
}

private fun createAdCoat(): SimpleAd = SimpleAd(
    title = "Manteau long Jules col détachable",
    body = "Suite à prise de poids, et un départ imminent à l'étranger, je vends une partie de mes vêtements.\nRemise en mains propres uniquement, merci de votre compréhension.\nPrix négociable en cas d'achat de plusieurs articles (voir autres annonces).\n\nManteau long noir Jules.\n2 poches intérieures, 2 poches extérieures.\n\nLa partie manteau seule se ferme avec 3 boutons.\nLe col détachable se prolonge derrière la boutonnière et se ferme avec une fermeture éclair (voir photos).",
    price = 10,
    category = Category.CLOTHES,
    attributes = ClothingAttributes(
        type = ClothingType.MEN,
        category = ClothingCategory.COAT_JACKET,
        size = ClothingSize.M,
        condition = ClothingCondition.VERY_GOOD,
        brand = "jules",
        color = "noir"
    ).toMap(),
    imagePaths = listOf(
        IMG_FOLDER.resolve("manteau1.jpg"),
        IMG_FOLDER.resolve("manteau2.jpg"),
        IMG_FOLDER.resolve("manteau3.jpg")
    )
)

private fun createAdLinenPants(): SimpleAd = SimpleAd(
    title = "Pantalon en lin noir Tex taille 38",
    body = "Suite à prise de poid, et vu que je déménage à l'étranger, je vends une partie de mes vêtements.\nRemise en mains propres uniquement, merci de votre compréhension.\n\nPantalon en lin noir Tex taille 38\n55% Lin, 45% Coton",
    price = 5,
    category = Category.CLOTHES,
    attributes = ClothingAttributes(
        type = ClothingType.MEN,
        category = ClothingCategory.PANTS,
        size = ClothingSize.M,
        condition = ClothingCondition.VERY_GOOD,
        brand = "autre",
        color = "noir"
    ).toMap(),
    imagePaths = listOf(
        IMG_FOLDER.resolve("pant_lin1.jpg"),
        IMG_FOLDER.resolve("pant_lin2.jpg")
    )
)
