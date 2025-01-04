package org.kalvari.ignite.retrofit

import org.kalvari.ignite.model.VerseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BibleApiService {
    @GET("/api/bible/getData.php")
    fun getVerseData(
        @Query("t") query: String
    ): Call<VerseData>
}