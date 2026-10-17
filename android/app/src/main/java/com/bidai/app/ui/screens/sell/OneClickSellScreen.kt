package com.bidai.app.ui.screens.sell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bidai.app.ui.components.BidAiBottomNavBar
import com.bidai.app.ui.components.CategoryCircleItem
import com.bidai.app.ui.theme.BidAiBlue
import com.bidai.app.ui.theme.BidAiBlueLight
import com.bidai.app.ui.theme.BorderColor
import com.bidai.app.ui.theme.InputBg
import com.bidai.app.ui.theme.PureWhite
import com.bidai.app.ui.theme.TextBlack
import com.bidai.app.ui.theme.TextDarkGray
import com.bidai.app.ui.theme.TextGray
import com.bidai.app.ui.theme.TextLightGray

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OneClickSellScreen(
    viewModel: SellViewModel,
    onNavigateBack: () -> Unit,
    onListingSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val form by viewModel.formState.collectAsState()
    val scrollState = rememberScrollState()

    var sellAsVendor by remember { mutableStateOf(true) }
    var brandName by remember { mutableStateOf("Bajaj Pulsar") }
    var productType by remember { mutableStateOf("Motorcycle") }
    var location by remember { mutableStateOf("Bengaluru, Karnataka") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.resetState()
                onListingSuccess()
            },
            containerColor = PureWhite,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = BidAiBlue,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Product Listed Successfully!",
                    color = TextBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Your listing is now live on Bid.ai auction feed. Verified buyers can now place bids.",
                    color = TextGray,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetState()
                        onListingSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BidAiBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Go to Home Feed", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sell your Product",
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            BidAiBottomNavBar(
                selectedTab = "sell",
                onTabSelected = { tab ->
                    if (tab == "home") onNavigateBack()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            // 1. SELECT CATEGORIES SECTION
            Text(
                text = "Select Categories",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            val figmaCategories = listOf(
                Pair(1, "Cars" to "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=100&q=80"),
                Pair(2, "Real Estate" to "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=100&q=80"),
                Pair(3, "Mobiles" to "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=100&q=80"),
                Pair(4, "Jobs" to "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=100&q=80"),
                Pair(5, "Bikes" to "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=100&q=80"),
                Pair(6, "Electronics" to "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100&q=80"),
                Pair(7, "Home & Garden" to "https://images.unsplash.com/photo-1580481077195-c3a82145d875?w=100&q=80"),
                Pair(8, "Beauty" to "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=100&q=80")
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                figmaCategories.forEach { (catId, pair) ->
                    val (title, img) = pair
                    CategoryCircleItem(
                        title = title,
                        imageUrl = img,
                        isSelected = form.categoryId == catId,
                        onClick = { viewModel.onCategoryChange(catId) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. IMAGE UPLOAD BOX (Light Blue Box)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BidAiBlueLight)
                    .border(1.dp, BidAiBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Upload Photos",
                        tint = BidAiBlue,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            // Cycle / auto-trigger photo and AI fill
                            viewModel.triggerAiAutoFill()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BidAiBlue),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select or Take Photo", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Upload photos (Min 2, Max 5, Video Max 30s)",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
            }

            // 3. PHOTO THUMBNAILS ROW (Image 5 from Figma)
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val thumbnails = listOf(
                    form.imageUrl,
                    "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=300&q=80"
                )

                for (imgUrl in thumbnails) {
                    Card(
                        modifier = Modifier
                            .size(100.dp, 120.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = BidAiBlue)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BidAiBlue)
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Photos", color = PureWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = PureWhite,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            AsyncImage(
                                model = imgUrl,
                                contentDescription = "Thumb",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. SELL AS (Vendor / Business vs Individual)
            Text(
                text = "Sell as",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Vendor pill
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, if (sellAsVendor) BidAiBlue else BorderColor, RoundedCornerShape(8.dp))
                        .clickable { sellAsVendor = true }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = sellAsVendor,
                        onClick = { sellAsVendor = true },
                        colors = RadioButtonDefaults.colors(selectedColor = BidAiBlue)
                    )
                    Text("vendor / Business", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextBlack)
                }

                // Individual pill
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, if (!sellAsVendor) BidAiBlue else BorderColor, RoundedCornerShape(8.dp))
                        .clickable { sellAsVendor = false }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !sellAsVendor,
                        onClick = { sellAsVendor = false },
                        colors = RadioButtonDefaults.colors(selectedColor = BidAiBlue)
                    )
                    Text("Individual", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextBlack)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. BRANDS
            Text(text = "Brands", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = brandName,
                onValueChange = { brandName = it },
                placeholder = { Text("Enter Brands", color = TextLightGray, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BidAiBlue,
                    unfocusedBorderColor = BorderColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 6. SELECT PRODUCT TYPE
            Text(text = "Select Product Type", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = productType,
                onValueChange = { productType = it },
                placeholder = { Text("Type", color = TextLightGray, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BidAiBlue,
                    unfocusedBorderColor = BorderColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 7. AD TITLE
            Text(text = "Ad Title", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = form.title,
                onValueChange = { viewModel.onTitleChange(it) },
                placeholder = { Text("Enter Title", color = TextLightGray, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BidAiBlue,
                    unfocusedBorderColor = BorderColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 8. DESCRIPTION
            Text(text = "Description", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = form.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                placeholder = { Text("Enter detailed description...", color = TextLightGray, fontSize = 13.sp) },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BidAiBlue,
                    unfocusedBorderColor = BorderColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 9. PRICE
            Text(text = "Price", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = form.startingPrice,
                onValueChange = { viewModel.onPriceChange(it) },
                placeholder = { Text("Enter price", color = TextLightGray, fontSize = 13.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BidAiBlue,
                    unfocusedBorderColor = BorderColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 10. LOCATION
            Text(text = "Location", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("Enter Location", color = TextLightGray, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BidAiBlue,
                    unfocusedBorderColor = BorderColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 11. UPLOAD BUTTON (Blue Pill)
            val isSubmitting = uiState is SellUiState.Submitting

            Button(
                onClick = {
                    if (form.title.isBlank()) {
                        viewModel.onTitleChange("$brandName $productType")
                    }
                    if (form.startingPrice.isBlank()) {
                        viewModel.onPriceChange("56000")
                    }
                    viewModel.submitListing {
                        showSuccessDialog = true
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BidAiBlue)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = PureWhite,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Upload",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
