package com.bidai.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bidai.app.data.model.Product
import com.bidai.app.ui.theme.BidAiBlue
import com.bidai.app.ui.theme.BidAiBlueLight
import com.bidai.app.ui.theme.BorderColor
import com.bidai.app.ui.theme.EcoGreen
import com.bidai.app.ui.theme.InputBg
import com.bidai.app.ui.theme.PureWhite
import com.bidai.app.ui.theme.TextBlack
import com.bidai.app.ui.theme.TextDarkGray
import com.bidai.app.ui.theme.TextGray
import com.bidai.app.ui.theme.TextLightGray
import com.bidai.app.utils.ValidationUtils

// The signature Bid.ai Logo with "Buy. Inspect. Deal" tagline
@Composable
fun BidAiLogo(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "3iD",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BidAiBlue,
                letterSpacing = (-1).sp
            )
            Text(
                text = ".ai",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
        }
        Text(
            text = "Buy. Inspect. Deal",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = TextDarkGray
        )
    }
}

// Top-right and bottom-left organic blue waves seen in Figma screens 1 & 2
@Composable
fun AuthWaveBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Top-right blue curve
        val topPath = Path().apply {
            moveTo(width * 0.2f, 0f)
            cubicTo(
                width * 0.45f, height * 0.05f,
                width * 0.95f, height * 0.12f,
                width, height * 0.30f
            )
            lineTo(width, 0f)
            close()
        }
        drawPath(topPath, color = BidAiBlue)

        // Bottom-left blue curve
        val bottomPath = Path().apply {
            moveTo(0f, height * 0.78f)
            cubicTo(
                width * 0.05f, height * 0.92f,
                width * 0.45f, height * 0.98f,
                width * 0.60f, height
            )
            lineTo(0f, height)
            close()
        }
        drawPath(bottomPath, color = BidAiBlue)
    }
}

// Clean Figma Form Field (Label above, rounded light gray box)
@Composable
fun FigmaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorMessage: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextBlack,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextLightGray, fontSize = 14.sp) },
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                focusedBorderColor = BidAiBlue,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = TextBlack,
                unfocusedTextColor = TextBlack
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

// Exact 5-tab Bottom Navigation Bar from Figma
@Composable
fun BidAiBottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PureWhite)
            .border(width = 0.5.dp, color = BorderColor)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            Triple("home", "Home", Icons.Default.Home),
            Triple("sell", "Sell", Icons.Default.Store),
            Triple("one_click", "One click sell", Icons.Default.AdsClick),
            Triple("chat", "Chat", Icons.AutoMirrored.Filled.Chat),
            Triple("items", "Your items", Icons.Default.Inventory)
        )

        navItems.forEach { (route, label, icon) ->
            val isSelected = selectedTab == route
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(route) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) BidAiBlue else TextGray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) BidAiBlue else TextGray
                )
            }
        }
    }
}

// Circular Category Icon seen in Home & Sell screen
@Composable
fun CategoryCircleItem(
    title: String,
    imageRes: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (isSelected) BidAiBlueLight else InputBg)
                .border(
                    width = if (isSelected) 2.dp else 0.5.dp,
                    color = if (isSelected) BidAiBlue else BorderColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageRes,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) BidAiBlue else TextDarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Figma Card for "Newly Listed Cars / Bikes / Recommendations"
@Composable
fun FigmaProductCard(
    product: Product,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl ?: "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&q=80",
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // 3iD.ai watermark logo on top-left of image
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3iD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BidAiBlue
                    )
                    Text(
                        text = ".ai",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                // "3iD.ai Verified Product" blue pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BidAiBlueLight)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = BidAiBlue,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Verified Product",
                        fontSize = 9.sp,
                        color = BidAiBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price and Eco score (Ecs)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ValidationUtils.formatCurrency(product.currentBid),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Text(
                        text = "Ecs: ${product.estimatedEcoScore * 200}",
                        fontSize = 10.sp,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location and Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Fallback to "Bengaluru" if no location in model, else use location (assume model might not have location right now)
                    Text(text = "Bengaluru", fontSize = 10.sp, color = TextGray)
                    
                    // Format Ends At date if available
                    val dateText = try {
                        if (!product.endsAt.isNullOrEmpty()) {
                            val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                            val formatter = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())
                            val date = parser.parse(product.endsAt)
                            if (date != null) formatter.format(date) else "N/A"
                        } else {
                            "N/A"
                        }
                    } catch (e: Exception) {
                        "N/A" // fallback on parsing error
                    }
                    Text(text = dateText, fontSize = 10.sp, color = TextGray)
                }
            }
        }
    }
}
