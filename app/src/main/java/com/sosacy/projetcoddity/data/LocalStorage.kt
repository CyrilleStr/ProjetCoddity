package com.sosacy.projetcoddity.data

import com.sosacy.projetcoddity.data.model.GarbageList

class LocalStorage private constructor() {
    var garbageAdded:Boolean = false
    var garbageThrown:Boolean = false

    init {
        println("LocalStorage")
    }

    companion object {
        val instance = LocalStorage()
    }
}