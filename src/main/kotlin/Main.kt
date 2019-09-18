package org.hildan.leboncoin

import kotlinx.coroutines.runBlocking
import org.hildan.leboncoin.api.Category
import org.hildan.leboncoin.api.LbcAd
import org.hildan.leboncoin.api.LeBonCoin
import org.hildan.leboncoin.serialization.readLbcAds
import java.nio.file.Paths

private val IMG_FOLDER = Paths.get("C:", "Users", "joffr", "Desktop", "annonces")

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
