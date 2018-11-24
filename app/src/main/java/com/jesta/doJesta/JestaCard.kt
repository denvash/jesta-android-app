package com.jesta.doJesta

import android.content.res.Resources
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jesta.R
import java.io.BufferedReader
import java.util.*

class JestaCard(val difficulty: String, dynamicUrl: String, val url: String, val description: String) {

    val dynamicUrl: Uri = Uri.parse(dynamicUrl)

    companion object {
        fun initJestaCardList(resources: Resources): List<JestaCard> {
            val inputStream = resources.openRawResource(R.raw.jesta_posts)
            val jsonProductsString = inputStream.bufferedReader().use(BufferedReader::readText)
            val gson = Gson()
            val jestaListType = object : TypeToken<ArrayList<JestaCard>>() {}.type
            return gson.fromJson<List<JestaCard>>(jsonProductsString, jestaListType)
        }
    }
}