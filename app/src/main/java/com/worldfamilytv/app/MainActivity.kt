
package com.worldfamilytv.app

import android.content.res.Configuration
import android.os.Bundle
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

private val Background = Color(0xFF080B14)
private val Panel = Color(0xFF171F31)
private val Accent = Color(0xFF7568FF)
private val Muted = Color(0xFF9AA4B8)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WorldFamilyTV()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldFamilyTV() {

    var channels by remember {
        mutableStateOf<List<TVChannel>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var country by remember {
        mutableStateOf("All")
    }

    var category by remember {
        mutableStateOf("All")
    }

    var search by remember {
        mutableStateOf("")
    }

    var selectedChannel by remember {
        mutableStateOf<TVChannel?>(null)
    }

    var showFavourites by remember {
        mutableStateOf(false)
    }

    val favourites = remember {
        mutableStateListOf<String>()
    }

    val configuration = LocalConfiguration.current

    val isTV =
        configuration.uiMode and
        Configuration.UI_MODE_TYPE_MASK ==
        Configuration.UI_MODE_TYPE_TELEVISION

    val columns =
        if (isTV || configuration.screenWidthDp >= 800) 4 else 2

    // Download worldwide channels and streams

    LaunchedEffect(Unit) {

        isLoading = true
        errorMessage = ""

        try {

            channels = WorldTVRepository.getWorldwideChannels()

            if (channels.isEmpty()) {
                errorMessage = "No channels available. Check your internet connection."
            }

        } catch (e: Exception) {

            errorMessage = "Unable to load channels: ${e.message}"

        } finally {

            isLoading = false
        }
    }

    // Dynamic country and category lists

    val countries = remember(channels) {
        listOf("All") +
            channels.map { it.country }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()
    }

    val categories = remember(channels) {
        listOf("All") +
            channels.map { it.category }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()
    }

    // Filter channels

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
                onBack = {
                    selectedChannel = null
                }
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
                WorldTVLogo()

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
                                    if (item == "All") "🌍 All"
                                    else item
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
                                Text(
                                    if (item == "All") "All"
                                    else item.replaceFirstChar {
                                        it.uppercase()
                                    }
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(15.dp))

                when {

                    isLoading -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                CircularProgressIndicator(
                                    color = Accent
                                )

                                Spacer(Modifier.height(15.dp))

                                Text(
                                    "Loading worldwide channels...",
                                    color = Color.White
                                )
                            }
                        }
                    }

                    errorMessage.isNotEmpty() -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    errorMessage,
                                    color = Color.White
                                )

                                Spacer(Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        // Retry is handled by recreating
                                        // the activity or app.
                                        errorMessage =
                                            "Please restart the app to retry."
                                    }
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }

                    filteredChannels.isEmpty() -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                if (showFavourites)
                                    "No favourite channels yet"
                                else
                                    "No channels found",
                                color = Muted
                            )
                        }
                    }

                    else -> {

                        Text(
                            "${filteredChannels.size} channels available",
                            color = Muted,
                            fontSize = 12.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            horizontalArrangement =
                                Arrangement.spacedBy(12.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(12.dp),
                            contentPadding =
                                PaddingValues(bottom = 30.dp)
                        ) {

                            items(
                                filteredChannels,
                                key = { it.id }
                            ) { channel ->

                                ChannelCard(
                                    channel = channel,

                                    isFavourite =
                                        favourites.contains(channel.id),

                                    onFavourite = {

                                        if (
                                            favourites.contains(channel.id)
                                        ) {
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
}

@Composable
fun ChannelCard(
    channel: TVChannel,
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
                        channel.flag.ifBlank { "🌍" },
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
                    if (channel.streamUrl.isNotBlank())
                        "WATCH LIVE"
                    else
                        "STREAM UNAVAILABLE",
                    color = if (channel.streamUrl.isNotBlank())
                        Accent
                    else
                        Muted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TVPlayer(
    channel: TVChannel,
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
            "${channel.flag.ifBlank { "🌍" }} ${channel.country}",
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
                        "Stream unavailable",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "This channel has no available stream URL.",
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
