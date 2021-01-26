package com.weemusic.android.domain

import com.google.gson.JsonObject
import org.threeten.bp.LocalDate

data class Album(
    val id: Int,
    val name: String,
    val images: List<String>,
    val rights: String,
    val title: String,
    val artist: String,
    val category: String,
    val releaseDate: LocalDate,
    val price: Double
) {
    companion object {
        /**
         * Creates an Album from a JsonObject using Gson.
         *
         * @param json The JsonObject that will be parsed.
         *
         * @return The Album created from the json data.
         */
        fun from(json: JsonObject): Album {

            val id = json
                .getAsJsonObject("id")
                .getAsJsonObject("attributes")
                .getAsJsonPrimitive("im:id")
                .asInt
            val name = json
                .getAsJsonObject("im:name")
                .getAsJsonPrimitive("label")
                .asString
            val images = json
                .getAsJsonArray("im:image")
                .map {
                    it.asJsonObject
                        .getAsJsonPrimitive("label")
                        .asString
                }
            val rights = json
                .getAsJsonObject("rights")
                .getAsJsonPrimitive("label")
                .asString
            val title = json
                .getAsJsonObject("title")
                .getAsJsonPrimitive("label")
                .asString
            val artist = json
                .getAsJsonObject("im:artist")
                .getAsJsonPrimitive("label")
                .asString
            val category = json
                .getAsJsonObject("category")
                .getAsJsonObject("attributes")
                .getAsJsonPrimitive("label")
                .asString
            val releaseDate = LocalDate.parse(
                json.getAsJsonObject("im:releaseDate")
                    .getAsJsonPrimitive("label")
                    .asString
                    .substringBefore("T")
            )
            val price = json
                .getAsJsonObject("im:price")
                .getAsJsonObject("attributes")
                .getAsJsonPrimitive("amount")
                .asDouble

            return Album(id, name, images, rights, title, artist, category, releaseDate, price)
        }
    }
}
