
package com.worldfamilytv.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

data class TVChannel(
    val id: String,
    val name: String,
    val country: String,
    val flag: String,
    val category: String,
    val language: String,
    val streamUrl: String,
    val logoUrl: String
)

object ChannelRepository {

    private const val DATABASE_URL =
        "https://iptv-org.github.io/api/channels.json"

    suspend fun getChannels(): List<TVChannel> =
        withContext(Dispatchers.IO) {
            try {
                val json = URL(DATABASE_URL)
                    .openConnection()
                    .apply {
                        connectTimeout = 15000
                        readTimeout = 30000
                    }
                    .getInputStream()
                    .bufferedReader()
                    .use { it.readText() }

                val array = JSONArray(json)

                val channels = mutableListOf<TVChannel>()

                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)

                    channels.add(
                        TVChannel(
                            id = item.optString("id"),
                            name = item.optString("name"),
                            country = item.optString("country"),
                            flag = "",
                            category = item.optJSONArray("categories")
                                ?.optString(0) ?: "General",
                            language = item.optJSONArray("languages")
                                ?.optString(0) ?: "",
                            streamUrl = "",
                            logoUrl = item.optString("logo")
                        )
                    )
                }

                channels

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
}
