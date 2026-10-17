package com.bidai.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bidai.app.ui.components.BidAiBottomNavBar
import com.bidai.app.ui.components.BidAiLogo
import com.bidai.app.ui.components.CategoryCircleItem
import com.bidai.app.ui.components.FigmaProductCard
import com.bidai.app.ui.theme.BidAiBlue
import com.bidai.app.ui.theme.BidAiBlueDark
import com.bidai.app.ui.theme.BidAiBlueLight
import com.bidai.app.ui.theme.BorderColor
import com.bidai.app.ui.theme.InputBg
import com.bidai.app.ui.theme.PureWhite
import com.bidai.app.ui.theme.TextBlack
import com.bidai.app.ui.theme.TextDarkGray
import com.bidai.app.ui.theme.TextGray
import com.bidai.app.ui.theme.TextLightGray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSell: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = PureWhite,
        bottomBar = {
            BidAiBottomNavBar(
                selectedTab = "home",
                onTabSelected = { tab ->
                    if (tab == "sell" || tab == "one_click") {
                        onNavigateToSell()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. TOP HEADER (Logo, Location selector, Icons)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BidAiLogo()

                    // Location Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(InputBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = BidAiBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Taj Retreat, Bengaluru",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextBlack
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = TextGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = TextBlack,
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = TextBlack,
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Receipts",
                            tint = TextBlack,
                            modifier = Modifier.size(20.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(BidAiBlueLight)
                                .clickable { onLogout() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = BidAiBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 2. SEARCH BAR
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchChange(it) },
                    placeholder = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Search For ", color = TextLightGray, fontSize = 13.sp)
                            Text("Cars", color = TextBlack, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextBlack)
                    },
                    trailingIcon = {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Visual Search", tint = TextGray)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = BidAiBlue,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextBlack,
                        unfocusedTextColor = TextBlack
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // 3. HERO BANNER CAROUSEL
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Find Your\nDream job\nwith",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextBlack,
                                    lineHeight = 22.sp
                                )
                                Text(
                                    text = "BID.AI",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BidAiBlue
                                )
                            }
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=300&q=80",
                                contentDescription = "Dream Job",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }

            // 4. EXPLORE CATEGORIES (4x4 Grid)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(
                        text = "Explore Categories",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val figmaCategories = listOf(
                        Pair("Cars", "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=100&q=80"),
                        Pair("Real Estate", "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=100&q=80"),
                        Pair("Mobiles", "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=100&q=80"),
                        Pair("Jobs", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=100&q=80"),
                        Pair("Bikes", "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=100&q=80"),
                        Pair("Electronics", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100&q=80"),
                        Pair("Home & Garden", "https://images.unsplash.com/photo-1580481077195-c3a82145d875?w=100&q=80"),
                        Pair("Beauty", "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=100&q=80"),
                        Pair("Clothing", "https://images.unsplash.com/photo-1523381294911-8d3cead13475?w=100&q=80"),
                        Pair("Books", "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=100&q=80"),
                        Pair("Arts and Crafts", "https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=100&q=80"),
                        Pair("Services", "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=100&q=80")
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxItemsInEachRow = 4,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        figmaCategories.forEach { (catName, catImg) ->
                            CategoryCircleItem(
                                title = catName,
                                imageUrl = catImg,
                                isSelected = uiState.selectedCategory == catName,
                                onClick = { viewModel.onCategorySelect(catName) }
                            )
                        }
                    }
                }
            }

            // 5. BANNER 2: FURNITURE LISTING
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BidAiBlue)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "From Home to Market\n– List Furniture Fast\nwith BID.ai",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PureWhite)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "List Now ↗",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BidAiBlue
                                )
                            }
                        }
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1580481077195-c3a82145d875?w=300&q=80",
                            contentDescription = "Chairs",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }

            // 6. NEWLY LISTED CARS SECTION
            item {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Newly Listed Cars",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    // Car filter chips
                    val carChips = listOf("Mercedes-Benz E-Class", "Hyundai Creta", "Mahindra Thar", "Toyota Fortuner", "Swift")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(carChips) { chip ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(0.5.dp, BorderColor, RoundedCornerShape(6.dp))
                                    .background(PureWhite)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(chip, fontSize = 11.sp, color = TextDarkGray)
                            }
                        }
                    }

                    // Car 2-column product cards
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (uiState.products.isNotEmpty()) {
                            FigmaProductCard(
                                product = uiState.products[0],
                                onCardClick = { viewModel.placeQuickBid(uiState.products[0]) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (uiState.products.size > 1) {
                            FigmaProductCard(
                                product = uiState.products[1],
                                onCardClick = { viewModel.placeQuickBid(uiState.products[1]) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 7. NEWLY LISTED BUDGET BIKES SECTION
            item {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Newly Listed Budget Bikes",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    val bikeChips = listOf("TVS Raider", "Royal Enfield", "Yamaha Motor Company", "KTM", "Bajaj Pulsar")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bikeChips) { chip ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(0.5.dp, BorderColor, RoundedCornerShape(6.dp))
                                    .background(PureWhite)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(chip, fontSize = 11.sp, color = TextDarkGray)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (uiState.products.size > 2) {
                            FigmaProductCard(
                                product = uiState.products[2],
                                onCardClick = { viewModel.placeQuickBid(uiState.products[2]) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (uiState.products.size > 3) {
                            FigmaProductCard(
                                product = uiState.products[3],
                                onCardClick = { viewModel.placeQuickBid(uiState.products[3]) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 8. HOURGLASS PLAN EXPIRY BANNER
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BidAiBlue)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Your Plan is About to Expire!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Renew now to keep listing and boosting your products without interruption.",
                                fontSize = 11.sp,
                                color = PureWhite.copy(alpha = 0.85f),
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PureWhite)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Renew Plan ↗",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BidAiBlue
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = "Hourglass",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
            }

            // 9. RECOMMENDATIONS FOR YOU
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Recommendations For You",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (uiState.products.size > 4) {
                            FigmaProductCard(
                                product = uiState.products[4],
                                onCardClick = { viewModel.placeQuickBid(uiState.products[4]) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (uiState.products.size > 5) {
                            FigmaProductCard(
                                product = uiState.products[5],
                                onCardClick = { viewModel.placeQuickBid(uiState.products[5]) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
