package com.sosacy.projetcoddity.data.model

import android.util.Log
import org.json.JSONArray

class GarbageList() {
    var all = ArrayList<Garbage>()

    public fun parseJson(json: String) {
        var garbageList = JSONArray(json.toString())
        for (i in 0 until garbageList.length()) {
            var garbageJson = garbageList.getJSONObject(i)
            all.add(
                Garbage(
                    garbageJson.optInt("id"),
                    garbageJson.get("latitude").toString().toFloat(),
                    garbageJson.get("longitude").toString().toFloat(),
                    garbageJson.optBoolean("discard"),
                    garbageJson.optBoolean("accepted")
                )
            )
        }
    }
}