
package com.worldfamilytv.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

object StreamRepository {

    private const val STREAMS_URL =
        "https://iptv-org.github.io/api/streams.json"

    suspend fun getStreams(): Map<String, String> =
        withContext(Dispatchers.IO) {
            try {
                val json = URL(STREAMS_URL)
                    .openConnection()
                    .apply {
                        connectTimeout = 15000
                        readTimeout = 30000
                    }
                    .getInputStream()
                    .bufferedReader()
                    .use { it.readText() }

                val array = JSONArray(json)
                val streams = mutableMapOf<String, String>()

                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)

                    val channelId = item.optString("channel")
                    val streamUrl = item.optString("url")

                    if (
                        channelId.isNotBlank() &&
                        streamUrl.startsWith("http")
                    ) {
                        streams.putIfAbsent(
                            channelId,
                            streamUrl
                        )
                    }
                }

                streams

            } catch (e: Exception) {
                e.printStackTrace()
                emptyMap()
            }
        }
}
