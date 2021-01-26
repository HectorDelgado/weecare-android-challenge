package com.weemusic.android.network

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface iTunesApi {

    @GET("/us/rss/topalbums/limit={limit}/json")
    fun getTopAlbums(@Path("limit") limit: Int = 25): Single<JsonObject>

    /**
     * GET request for retrieving the top albums from iTunes.
     *
     * @param limit The maximum amount of albums to query.
     *
     * @return A JsonObject containing the result of the GET request.
     */
    @GET("/us/rss/topalbums/limit={limit}/json")
    suspend fun getTopAlbumsWithCoroutine(@Path("limit") limit: Int = 25): JsonObject
}