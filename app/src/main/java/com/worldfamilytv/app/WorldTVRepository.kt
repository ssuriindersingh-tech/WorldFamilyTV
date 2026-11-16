
package com.worldfamilytv.app

object WorldTVRepository {

    suspend fun getWorldwideChannels(): List<TVChannel> {

        return try {
            val channels =
                ChannelRepository.getChannels()

            val streams =
                StreamRepository.getStreams()

            channels.map { channel ->
                channel.copy(
                    streamUrl = streams[channel.id] ?: ""
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
