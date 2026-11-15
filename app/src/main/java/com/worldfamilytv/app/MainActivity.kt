
package com.worldfamilytv.app

import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import org.json.JSONArray

private val Background = Color(0xFF080B14)
private val Panel = Color(0xFF171F31)
private val Accent = Color(0xFF7568FF)
private val Muted = Color(0xFF9AA4B8)

data class Channel(
    val id: String,
    val name: String,
    val country: String,
    val flag: String,
    val category: String,
    val language: String,
    val streamUrl: String,
    val logoUrl: String
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val channels = loadChannels()

        setContent {
            WorldFamilyTV(channels)
        }
    }

    private fun loadChannels(): List<Channel> {
        val json = assets.open("channels.json")
            .bufferedReader()
            .use { it.readText() }

        val array = JSONArray(json)

        return (0 until array.length()).map { i ->
            val item = array.getJSONObject(i)

            Channel(
                id = item.optString("id"),
                name = item.optString("name"),
                country = item.optString("country"),
                flag = item.optString("flag"),
                category = item.optString("category"),
                language = item.optString("language"),
                streamUrl = item.optString("streamUrl"),
                logoUrl = item.optString("logoUrl")
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldFamilyTV(channels: List<Channel>) {

    var country by remember { mutableStateOf("All") }
    var category by remember { mutableStateOf("All") }
    var search by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf<Channel?>(null) }
    var showFavourites by remember { mutableStateOf(false) }

    val favourites = remember {
        mutableStateListOf<String>()
    }

    val configuration = LocalConfiguration.current

    val isTV =
        configuration.uiMode and Configuration.UI_MODE_TYPE_MASK ==
        Configuration.UI_MODE_TYPE_TELEVISION

    val columns =
        if (isTV || configuration.screenWidthDp >= 800) 4 else 2

    val countries = listOf(
        "All",
        "India",
        "United Kingdom",
        "United States",
        "Canada",
        "Australia",
        "France",
        "Germany",
        "United Arab Emirates"
    )

    val categories = listOf(
        "All",
        "News",
        "Movies",
        "Sports",
        "Kids",
        "Music",
        "Entertainment",
        "Documentary"
    )

    val filteredChannels = channels.filter { channel ->

        (country == "All" || channel.country == country) &&

        (category == "All" || channel.category == category) &&

        (!showFavourites || favourites.contains(channel.id)) &&

        (
            search.isBlank() ||
            channel.name.contains(search, true) ||
            channel.country.contains(search, true) ||
            channel.language.contains(search, true)
        )
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            background = Background,
            surface = Panel
        )
    ) {

        if (selectedChannel != null) {

            BackHandler {
                selectedChannel = null
            }

            TVPlayer(
                channel = selectedChannel!!,
                onBack = { selectedChannel = null }
            )

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF19233D),
                                Background
                            )
                        )
                    )
                    .padding(
                        horizontal = if (isTV) 36.dp else 18.dp
                    )
            ) {

                Spacer(Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            "WORLD FAMILY TV",
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )

                        Text(
                            "YOUR WORLD. YOUR ENTERTAINMENT.",
                            color = Muted,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp
                        )
                    }

                    Text(
                        "● LIVE",
                        color = Color(0xFFFF596F),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(25.dp))

                Text(
                    "Discover the world of television",
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Explore free-to-watch channels worldwide",
                    color = Muted,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(18.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(
                            "Search channels or countries...",
                            color = Muted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    "BROWSE COUNTRIES",
                    color = Muted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    countries.forEach { item ->

                        FilterChip(
                            selected = country == item,
                            onClick = {
                                country = item
                                showFavourites = false
                            },
                            label = {
                                Text(
                                    when (item) {
                                        "All" -> "🌍 All"
                                        "India" -> "🇮🇳 India"
                                        "United Kingdom" -> "🇬🇧 UK"
                                        "United States" -> "🇺🇸 USA"
                                        "Canada" -> "🇨🇦 Canada"
                                        "Australia" -> "🇦🇺 Australia"
                                        "France" -> "🇫🇷 France"
                                        "Germany" -> "🇩🇪 Germany"
                                        else -> "🇦🇪 UAE"
                                    }
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "CHANNEL CATEGORIES",
                        color = Muted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = {
                            showFavourites = !showFavourites
                        }
                    ) {

                        Icon(
                            if (showFavourites)
                                Icons.Default.Favorite
                            else
                                Icons.Default.FavoriteBorder,
                            contentDescription = "Favourites",
                            tint = Accent
                        )

                        Text("Favourites")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    categories.forEach { item ->

                        FilterChip(
                            selected = category == item,
                            onClick = {
                                category = item
                            },
                            label = {
                                Text(item)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(15.dp))

                if (filteredChannels.isEmpty()) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            "No channels found",
                            color = Muted
                        )
                    }

                } else {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 30.dp)
                    ) {

                        items(
                            filteredChannels,
                            key = { it.id }
                        ) { channel ->

                            ChannelCard(
                                channel = channel,
                                isFavourite = favourites.contains(channel.id),

                                onFavourite = {

                                    if (favourites.contains(channel.id)) {
                                        favourites.remove(channel.id)
                                    } else {
                                        favourites.add(channel.id)
                                    }
                                },

                                onClick = {
                                    selectedChannel = channel
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelCard(
    channel: Channel,
    isFavourite: Boolean,
    onFavourite: () -> Unit,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Panel
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0xFF29344C),
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        channel.flag,
                        fontSize = 25.sp
                    )
                }

                Spacer(Modifier.weight(1f))

                IconButton(
                    onClick = onFavourite
                ) {

                    Icon(
                        if (isFavourite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,

                        contentDescription = "Favourite",

                        tint = if (isFavourite)
                            Color(0xFFFF6685)
                        else
                            Muted
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                channel.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Text(
                "${channel.country} • ${channel.language}",
                color = Muted,
                fontSize = 10.sp
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Accent
                )

                Text(
                    "WATCH LIVE",
                    color = Accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TVPlayer(
    channel: Channel,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val player = remember(channel.id, channel.streamUrl) {

        if (channel.streamUrl.isNotBlank()) {

            ExoPlayer.Builder(context)
                .build()
                .apply {

                    setMediaItem(
                        MediaItem.fromUri(channel.streamUrl)
                    )

                    prepare()
                    playWhenReady = true
                }

        } else {
            null
        }
    }

    DisposableEffect(player) {

        onDispose {
            player?.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(18.dp)
    ) {

        TextButton(
            onClick = onBack
        ) {

            Text(
                "← Back to channels",
                color = Accent
            )
        }

        Text(
            channel.name,
            color = Color.White,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "${channel.flag} ${channel.country}",
            color = Muted
        )

        Spacer(Modifier.height(18.dp))

        if (player != null) {

            AndroidView(
                factory = { ctx ->

                    PlayerView(ctx).apply {
                        this.player = player
                        useController = true
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )

        } else {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(
                        Panel,
                        RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(55.dp)
                    )

                    Text(
                        "Stream not configured yet",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "An official streaming URL is required.",
                        color = Muted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(15.dp))

        Text(
            "World Family TV • Live Entertainment",
            color = Muted,
            fontSize = 12.sp
        )
    }
}
