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

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Please provide email and password as arguments")
        return
    }
    runBlocking {
        val email = args[0]
        val password = args[1]
        val session = LeBonCoin().login(email, password)

        session.createAd(createAdCupboard())
    }
}

private fun createAdCoat(): SimpleAd = SimpleAd(
    title = "Manteau long Jules col détachable",
    body = """
Suite à prise de poids, et un départ imminent à l'étranger, je vends une partie de mes vêtements.
Remise en mains propres uniquement, merci de votre compréhension.
Prix négociable en cas d'achat de plusieurs articles (voir autres annonces).

Manteau long noir Jules.
2 poches intérieures, 2 poches extérieures.

La partie manteau seule se ferme avec 3 boutons.
Le col détachable se prolonge derrière la boutonnière et se ferme avec une fermeture éclair (voir photos).
        """.trimIndent(),
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
    body = """
Suite à prise de poid, et vu que je déménage à l'étranger, je vends une partie de mes vêtements.
Remise en mains propres uniquement, merci de votre compréhension.

Pantalon en lin noir Tex taille 38
55% Lin, 45% Coton
        """.trimIndent(),
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

private fun createAdGardenTable(): SimpleAd = SimpleAd(
    title = "Table de jardin en verre + 4 chaises + parasol",
    body = """
Au vu d'un déménagement imminent à l'étranger, je vends ma table de jardin avec ses 4 chaises, son parasol et son pied de parasol.

Table en verre en excellent état, diamètre 139cm, hauteur 86cm.
Chaises et barre du parasol légèrement rouillées par endroits mais parfaitement fonctionnelles.
        """.trimIndent(),
    price = 90,
    category = Category.FURNITURES,
    attributes = emptyMap(),
    imagePaths = listOf(
        IMG_FOLDER.resolve("table_jardin1.jpg"),
        IMG_FOLDER.resolve("table_jardin2.jpg"),
        IMG_FOLDER.resolve("table_jardin3.jpg")
    )
)

private fun createAdCupboard(): SimpleAd = SimpleAd(
    title = "Etagère IKEA BESTA brun-noir 120x193cm",
    body = """
Je vends mon étagère car je déménage à l'étranger.
Très bon état.
C'est une IKEA BESTA, elle vient en plusieurs parties que l'on peut assembler comme on veut.

Elle comporte:
- un cadre de base avec montant central (dimensions LxPxH = 120x40x193cm)
- 10 étagères
- 2 tiroirs
- 2 portes de placards (avec ouverture par pression)

Les étagères peuvent être placées à n'importe quelle hauteur, et les tiroirs et portes peuvent aussi être déplacés.
        """.trimIndent(),
    price = 70,
    category = Category.FURNITURES,
    attributes = emptyMap(),
    imagePaths = listOf(
        IMG_FOLDER.resolve("etagere1.jpg"),
        IMG_FOLDER.resolve("etagere2.jpg")
    )
)

private fun createAdRollers(): SimpleAd = SimpleAd(
    title = "Rollers femme T39 (état neuf) + Set 3 protections",
    body = """
Vends rollers femme (taille 39) + Set 3 protections (genoux, coudes, poignets) avec pochette d'origine.
État neuf (utilisés 3 fois).

Prix d'origine rollers : 44.95€
Prix d'origine set 3 protections :14.95 €

Vendus 40€
        """.trimIndent(),
    price = 40,
    category = Category.SPORT_HOBBIES,
    attributes = emptyMap(),
    imagePaths = listOf(IMG_FOLDER.resolve("rollers.jpg"))
)

private fun createAdGamecubeAdapter(): SimpleAd = SimpleAd(
    title = "Adaptateur manette GameCube vers USB pour PC",
    body = """
Vends adaptateur pour brancher une manette de Gamecube à un PC en USB.
Je vends aussi une manette officielle GameCube blanche en excellent état, les 2 pour 15€.
        """.trimIndent(),
    price = 7,
    category = Category.CONSOLES_AND_GAMES,
    attributes = emptyMap(),
    imagePaths = listOf(IMG_FOLDER.resolve("gamecube_adapter.jpg"))
)

private fun createAdCarpet(): SimpleAd = SimpleAd(
    title = "Tapis IKEA ADUM rouge poils hauts",
    body = """
Très bon état. Poils hauts.
Vente pour départ imminent à l'étranger.
Dimensions 195x133cm
        """.trimIndent(),
    price = 20,
    category = Category.DECORATION,
    attributes = emptyMap(),
    imagePaths = listOf(
        IMG_FOLDER.resolve("tapis1.jpg"),
        IMG_FOLDER.resolve("tapis2.jpg"),
        IMG_FOLDER.resolve("tapis3.jpg")
    )
)

private fun createAdPhotoFrame(): SimpleAd = SimpleAd(
    title = "Tapis IKEA ADUM rouge poils hauts",
    body = """
Très bon état. Poils hauts.
Vente pour départ imminent à l'étranger.
Dimensions 195x133cm
        """.trimIndent(),
    price = 20,
    category = Category.DECORATION,
    attributes = emptyMap(),
    imagePaths = listOf(
        IMG_FOLDER.resolve("tapis1.jpg"),
        IMG_FOLDER.resolve("tapis2.jpg"),
        IMG_FOLDER.resolve("tapis3.jpg")
    )
)
