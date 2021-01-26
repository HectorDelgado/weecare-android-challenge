package com.weemusic.android.domain

import com.google.gson.JsonObject
import com.weemusic.android.network.iTunesApi
import io.reactivex.Single
import javax.inject.Inject

class GetTopAlbumsUseCase @Inject constructor(private val iTunesApi: iTunesApi) {

    fun perform(): Single<JsonObject> = iTunesApi.getTopAlbums(limit = 25)

    /**
     * Retrieves the top albums from iTunes.
     *
     * @return A JsonObject containing the result of the GET request.
     */
    suspend fun performWithCoroutine(): JsonObject = iTunesApi.getTopAlbumsWithCoroutine(limit = 25)
}