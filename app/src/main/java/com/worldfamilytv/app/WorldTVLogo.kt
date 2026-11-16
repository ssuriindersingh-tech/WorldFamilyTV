
package com.worldfamilytv.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WorldTVLogo(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF192B55),
                            Color(0xFF432A86),
                            Color(0xFF11182D)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF42D8FF),
                                Color(0xFF7568FF)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    "◎",
                    color = Color.White,
                    fontSize = 65.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "World TV",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            "WORLD FAMILY TV",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
        )

        Text(
            "YOUR WORLD. YOUR ENTERTAINMENT.",
            color = Color(0xFF9AA4B8),
            fontSize = 8.sp,
            letterSpacing = 1.5.sp
        )
    }
}
