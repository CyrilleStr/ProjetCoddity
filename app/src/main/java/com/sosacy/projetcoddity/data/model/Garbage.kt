package com.sosacy.projetcoddity.data.model

import kotlin.random.Random

class Garbage(id: Int, latitude: Float, longitude: Float, discard: Boolean, accepted: Boolean) {
    var id: Int = id
    var latitude: Float = latitude
    var longitude: Float = longitude
    var discard: Boolean = discard
    var accepted: Boolean = accepted

    fun randomImage():String {
        val garbages: MutableList<String> = ArrayList()
        garbages.add("https://madeinmarseille.net/actualites-marseille/2017/11/poubelle-zero-dechet.jpg")
        garbages.add("https://www.anjoubleucommunaute.fr/medias/2019/07/dechets-1-1350x901.jpg")
        garbages.add("https://recyclingnetwerk.org/wp-content/uploads/2022/01/0-Intro-canette-800x350.jpg")
        garbages.add("https://static.latribune.fr/1473582/dechets-plastiques.jpg")
        garbages.add("https://www.linfodurable.fr/sites/linfodurable/files/styles/landscape_w800/public/2019-05/shutterstock_175414070.jpg")

        var randomNb = Random.nextInt(garbages.size)

        return garbages[randomNb]
    }
}