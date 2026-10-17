package com.bidai.app.ui.screens.home

import androidx.compose.foundation.background
import com.bidai.app.R
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
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bidai.app.data.model.Product
import com.bidai.app.ui.components.BidAiBottomNavBar
import com.bidai.app.ui.components.BidAiLogo
import com.bidai.app.ui.components.CategoryCircleItem
import com.bidai.app.ui.components.FigmaProductCard
import com.bidai.app.ui.theme.BidAiBlue
import com.bidai.app.ui.theme.BidAiBlueLight
import com.bidai.app.ui.theme.BorderColor
import com.bidai.app.ui.theme.InputBg
import com.bidai.app.ui.theme.PureWhite
import com.bidai.app.ui.theme.TextBlack
import com.bidai.app.ui.theme.TextDarkGray
import com.bidai.app.ui.theme.TextGray
import com.bidai.app.ui.theme.TextLightGray
import com.bidai.app.utils.ValidationUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSell: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedProductForBid by remember { mutableStateOf<Product?>(null) }

    // Quick Bid Modal Dialog
    selectedProductForBid?.let { product ->
        AlertDialog(
            onDismissRequest = { selectedProductForBid = null },
            containerColor = PureWhite,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Place Bid on ${product.title}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
            },
            text = {
                Column {
                    Text(
                        text = "Current Highest Bid: ${ValidationUtils.formatCurrency(product.currentBid)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BidAiBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your Next Bid: ${ValidationUtils.formatCurrency(product.currentBid + 1000.0)}",
                        fontSize = 13.sp,
                        color = TextDarkGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "3iD.ai verified product with Eco Score: ${product.estimatedEcoScore * 200} Ecs",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.placeQuickBid(product)
                        selectedProductForBid = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BidAiBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Confirm ₹${(product.currentBid + 1000).toInt()} Bid", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { selectedProductForBid = null },
                    colors = ButtonDefaults.buttonColors(containerColor = InputBg, contentColor = TextBlack),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Success Toast/Dialog Banner
    uiState.bidSuccessMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            containerColor = PureWhite,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = BidAiBlue,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = { Text("Bid Successful!", fontWeight = FontWeight.Bold, color = TextBlack) },
            text = { Text(msg, color = TextGray, fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearMessages() },
                    colors = ButtonDefaults.buttonColors(containerColor = BidAiBlue)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

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
            // 1. TOP HEADER (Logo, Location selector, Header Action Icons)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BidAiLogo()

                    // Location Pill (Image 5 from Figma: "Taj Retreat, Bengaluru")
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

                    // Action Icons
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

            // 2. SEARCH BAR (Figma: Search For Cars)
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

            // 4. EXPLORE CATEGORIES (4x4 Grid from Figma)
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
                        Pair("Cars", R.drawable.cars),
                        Pair("Real Estate", R.drawable.realestate),
                        Pair("Mobiles", R.drawable.mobile),
                        Pair("Jobs", R.drawable.jobs),
                        Pair("Bikes", R.drawable.bikes),
                        Pair("Electronics", R.drawable.electronics),
                        Pair("Home & Garden", R.drawable.homegarden),
                        Pair("Beauty", R.drawable.beautyproducts),
                        Pair("Clothing", R.drawable.clothing),
                        Pair("Books", R.drawable.books),
                        Pair("Arts and Crafts", R.drawable.artcraft),
                        Pair("Services", R.drawable.services),
                        Pair("General Service", R.drawable.generalscience),
                        Pair("Yodha", R.drawable.yodha),
                        Pair("Agriculture", R.drawable.agriculture)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxItemsInEachRow = 4,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        figmaCategories.forEach { (catName, catImgRes) ->
                            CategoryCircleItem(
                                title = catName,
                                imageRes = catImgRes,
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
                                    .clickable { onNavigateToSell() }
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
                    val carChips = listOf("Mahindra & Mahindra", "Tata Motors", "Maruti Suzuki", "Hyundai", "Honda", "Toyota")
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
                                    .clickable { viewModel.onSearchChange(chip) }
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
                                onCardClick = { selectedProductForBid = uiState.products[0] },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (uiState.products.size > 1) {
                            FigmaProductCard(
                                product = uiState.products[1],
                                onCardClick = { selectedProductForBid = uiState.products[1] },
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

                    val bikeChips = listOf("TVS Motor", "Bajaj Auto", "Royal Enfield", "Yamaha Motor Company", "KTM")
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
                                    .clickable { viewModel.onSearchChange(chip) }
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
                                onCardClick = { selectedProductForBid = uiState.products[2] },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (uiState.products.size > 3) {
                            FigmaProductCard(
                                product = uiState.products[3],
                                onCardClick = { selectedProductForBid = uiState.products[3] },
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
                                text = "Renew now to keep listing and boosting your products without interruption",
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
                                onCardClick = { selectedProductForBid = uiState.products[4] },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (uiState.products.size > 5) {
                            FigmaProductCard(
                                product = uiState.products[5],
                                onCardClick = { selectedProductForBid = uiState.products[5] },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 10. FEATURE HIGHLIGHTS (Image 5 from Figma)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = InputBg)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sell and buy every thing with BID.ai",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Feature 1
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Category, contentDescription = null, tint = BidAiBlue, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "More then 13+\nCategories are in\nBid.ai",
                                    fontSize = 10.sp,
                                    color = TextDarkGray,
                                    lineHeight = 12.sp
                                )
                            }

                            // Feature 2
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AdsClick, contentDescription = null, tint = BidAiBlue, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Sell your unused\nitems in 30 seconds\nWith One click sell",
                                    fontSize = 10.sp,
                                    color = TextDarkGray,
                                    lineHeight = 12.sp
                                )
                            }

                            // Feature 3
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PointOfSale, contentDescription = null, tint = BidAiBlue, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Earn upto\n₹100000000 in\nBID.ai credits by\nreferral!",
                                    fontSize = 10.sp,
                                    color = TextDarkGray,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // 11. PROPERTY BANNER
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(130.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BidAiBlue)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(
                                    text = "Turn Your Property\ninto Profit – List in Just\nOne Click!",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PureWhite,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "AI based smart selling. Fast. Efficient.",
                                    fontSize = 10.sp,
                                    color = PureWhite.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "#SmartSellRevolution",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700)
                                )
                            }
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=300&q=80",
                                contentDescription = "House",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }

            // 12. TESTIMONIALS (Community Trust)
            item {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Transact with a trusted local community",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    val testimonials = listOf(
                        Triple(
                            "\"I sold my cooler, sofa, and gym set using BID.ai. Now even my apartment group uses it to sell or swap items. It's that useful!\"",
                            "Neha A",
                            "Mumbai"
                        ),
                        Triple(
                            "\"I listed my bike on BID.ai just to try it out, and boom – got an offer the same day! No commission, no calls from dealers. Totally smooth.\"",
                            "Ravi S",
                            "Pune"
                        ),
                        Triple(
                            "\"As a working mom, I don't have time to haggle. BID.ai took care of it all – price, visibility, and even reminders\"",
                            "Anjali",
                            "Delhi"
                        )
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(testimonials) { (quote, name, city) ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(130.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = quote,
                                        fontSize = 11.sp,
                                        color = TextDarkGray,
                                        lineHeight = 14.sp,
                                        maxLines = 4
                                    )
                                    Text(
                                        text = "$name, $city",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BidAiBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 13. REFERRAL INVITE BANNER
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
                                text = "Invite Friends",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sign up using my referral code to earn up to 1 lakh Bid Credits!",
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
                                    text = "Invite & Earn ↗",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BidAiBlue
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Invite",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
    }
}

