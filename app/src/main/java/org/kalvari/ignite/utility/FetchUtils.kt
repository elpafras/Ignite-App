package org.kalvari.ignite.utility

import android.util.Log
import org.kalvari.ignite.model.VerseData
import org.kalvari.ignite.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FetchUtils {

    private const val TAG = "FetchUtils"

    fun fetchVerseTexts(ayat: String, callback: (Map<String, String>?) -> Unit) {
        val query = "/ayat $ayat tb"

        Log.d(TAG, "Query: $query")

        RetrofitClient.apiService.getVerseData(query).enqueue(object : Callback<VerseData> {
            override fun onResponse(call: Call<VerseData>, response: Response<VerseData>) {
                if (response.isSuccessful) {
                    val verseData = response.body()
                    val parsedData = parseVerseTexts(verseData)
                    Log.d(TAG, "onResponse: $response")
                    Log.d(TAG, "onResponse: $verseData dan $parsedData")
                    callback(parsedData) // Pass data back to the caller
                } else {
                    Log.e(TAG, "API Error: ${response.errorBody()?.string()}")
                    // Jika query pertama gagal, gunakan query alternatif
                    fetchFallbackQuery(ayat, callback)
                }
            }

            override fun onFailure(call: Call<VerseData>, t: Throwable) {
                Log.e(TAG, "Request failed: ${t.message}")
                // Jika query pertama gagal, gunakan query alternatif
                fetchFallbackQuery(ayat, callback)
            }
        })
    }

    // Fungsi untuk menangani query fallback
    private fun fetchFallbackQuery(ayat: String, callback: (Map<String, String>?) -> Unit) {
        val fallbackQuery = "$ayat tb"
        Log.d(TAG, "Fallback Query: $fallbackQuery")

        RetrofitClient.apiService.getVerseData(fallbackQuery).enqueue(object : Callback<VerseData> {
            override fun onResponse(call: Call<VerseData>, response: Response<VerseData>) {
                if (response.isSuccessful) {
                    val verseData = response.body()
                    val parsedData = parseVerseTexts(verseData)
                    Log.d(TAG, "Fallback onResponse: $response")
                    Log.d(TAG, "Fallback onResponse: $verseData dan $parsedData")
                    callback(parsedData) // Pass fallback data back to the caller
                } else {
                    Log.e(TAG, "Fallback API Error: ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<VerseData>, t: Throwable) {
                Log.e(TAG, "Fallback Request failed: ${t.message}")
                callback(null)
            }
        })
    }

    private fun parseVerseTexts(response: VerseData?): Map<String, String>? {
        if (response?.text?.results?.su?.data?.results.isNullOrEmpty()) {
            Log.d(TAG, "Empty or invalid response.")
            return null
        }

        return response.text.results.su.data.results.flatMap { resultDetail ->
            resultDetail.res?.mapNotNull { (_, verseDetail) ->
                val verse = verseDetail.texts?.verse ?: "0"
                val text = verseDetail.texts?.verseText ?: "No Text Available"
                "($verse)" to text
            } ?: emptyList()
        }.toMap().ifEmpty {
            Log.d(TAG, "Parsed text map is empty.")
            null
        }
    }
}