package com.bidai.app.ui.screens.sell

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import com.bidai.app.R
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.TextButton
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
    var brandName by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            val urls = uris.map { it.toString() }
            viewModel.onImagesSelected(urls)
            // Trigger AI Autofill based on first image as placeholder feature
            viewModel.triggerAiAutoFill()
        }
    }

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
                    text = "Congratulations",
                    color = TextBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Your product get Approved and its live Now",
                    color = TextGray,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetState()
                        onListingSuccess()
                    }
                ) {
                    Text("Close", color = TextGray)
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
                Pair(1, "Cars" to R.drawable.cars),
                Pair(2, "Real Estate" to R.drawable.realestate),
                Pair(3, "Mobiles" to R.drawable.mobile),
                Pair(4, "Jobs" to R.drawable.jobs),
                Pair(5, "Bikes" to R.drawable.bikes),
                Pair(6, "Electronics" to R.drawable.electronics),
                Pair(7, "Home & Garden" to R.drawable.homegarden),
                Pair(8, "Beauty" to R.drawable.beautyproducts),
                Pair(9, "Clothing" to R.drawable.clothing),
                Pair(10, "Books" to R.drawable.books),
                Pair(11, "Arts and Crafts" to R.drawable.artcraft),
                Pair(12, "Services" to R.drawable.services),
                Pair(13, "General Service" to R.drawable.generalscience),
                Pair(14, "Yodha" to R.drawable.yodha),
                Pair(15, "Agriculture" to R.drawable.agriculture)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                figmaCategories.forEach { (catId, pair) ->
                    val (title, imgRes) = pair
                    CategoryCircleItem(
                        title = title,
                        imageRes = imgRes,
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
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BidAiBlue),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Text(
                            text = "Select or Take Photo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PureWhite
                        )
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
            if (form.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    form.imageUrls.forEachIndexed { index, imgUrl ->
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
                                    Text("Photo", color = PureWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = PureWhite,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { viewModel.onRemoveImage(index) }
                                    )
                                }
                                AsyncImage(
                                    model = android.net.Uri.parse(imgUrl),
                                    contentDescription = "Thumb",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (form.imageUrls.isNotEmpty()) {
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

                Spacer(modifier = Modifier.height(16.dp))

                // 5. BRAND
                Text(text = "Brand", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
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
                        viewModel.submitListing {
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BidAiBlue),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Upload", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
