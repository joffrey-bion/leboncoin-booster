package org.hildan.leboncoin

import kotlinx.coroutines.runBlocking
import org.hildan.leboncoin.api.Category
import org.hildan.leboncoin.api.LbcAd
import org.hildan.leboncoin.api.LeBonCoin
import org.hildan.leboncoin.serialization.readLbcAds
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

        val lbc = readLbcAds(IMG_FOLDER.resolve("annonces2.yml"))
        println(lbc)
        lbc.ads.forEach { session.createAd(it) }
    }
}

private fun createAdGardenTable(): LbcAd = LbcAd(
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

private fun createAdRollers(): LbcAd = LbcAd(
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

private fun createAdGamecubeAdapter(): LbcAd = LbcAd(
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
