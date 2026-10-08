package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.AppLanguage
import com.example.data.CropListingEntity
import com.example.data.DirectOrderEntity
import com.example.data.KisanStrings
import com.example.data.LocalizationProvider
import com.example.data.UserRole
import com.example.data.UserSession
import kotlin.math.roundToInt

@Composable
fun MarketFeedScreen(
    userSession: UserSession,
    language: AppLanguage,
    strings: KisanStrings,
    userRole: UserRole,
    listings: List<CropListingEntity>,
    directOrders: List<DirectOrderEntity>,
    selectedCategory: String,
    searchQuery: String,
    organicOnly: Boolean,
    onCategoryChange: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleOrganic: () -> Unit,
    onNavigateToAddCrop: () -> Unit,
    onNavigateToMandiAi: () -> Unit,
    onPlaceOrder: (CropListingEntity, String, String, String, Int) -> Unit
) {
    val context = LocalContext.current
    var selectedListingForOrder by remember { mutableStateOf<CropListingEntity?>(null) }
    var selectedVillageFilter by remember { mutableStateOf("") }

    // Extract unique real seller villages from active crop listings via Java backend service
    val activeSellerVillages = remember(listings, language) {
        com.example.data.KisanBackendService.extractUniqueSellerVillages(listings, language)
    }

    val filteredListings = remember(listings, selectedCategory, searchQuery, organicOnly, selectedVillageFilter) {
        listings.filter { item ->
            val matchesCat = selectedCategory == "ALL" || item.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                item.cropNameEn.contains(searchQuery, ignoreCase = true) ||
                item.cropNameMr.contains(searchQuery, ignoreCase = true) ||
                item.farmerNameEn.contains(searchQuery, ignoreCase = true) ||
                item.farmerNameMr.contains(searchQuery, ignoreCase = true) ||
                item.villageAndMandiEn.contains(searchQuery, ignoreCase = true) ||
                item.villageAndMandiMr.contains(searchQuery, ignoreCase = true)
            val matchesOrganic = !organicOnly || item.isOrganic
            val matchesVillage = matchesSellerVillage(item, selectedVillageFilter)
            matchesCat && matchesSearch && matchesOrganic && matchesVillage
        }
    }

    val categories = listOf(
        "ALL" to strings.allCategories,
        "Vegetables" to when (language) {
            AppLanguage.MARATHI -> "भाजीपाला"
            AppLanguage.HINDI -> "सब्जियां"
            AppLanguage.ENGLISH -> "Vegetables"
        },
        "Grains & Pulses" to when (language) {
            AppLanguage.MARATHI -> "धान्य व डाळी"
            AppLanguage.HINDI -> "अनाज व दालें"
            AppLanguage.ENGLISH -> "Grains & Pulses"
        },
        "Fruits" to when (language) {
            AppLanguage.MARATHI -> "फळे"
            AppLanguage.HINDI -> "फल"
            AppLanguage.ENGLISH -> "Fruits"
        }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("market_feed_list"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Hero Impact Banner
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(162.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner_1791200033171),
                        contentDescription = strings.appTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xE60B2E10),
                                        Color(0xBF1B5E20),
                                        Color(0x4D000000)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFF9A825),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = strings.zeroDalalBadge,
                                    color = Color(0xFF1F1400),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = if (userRole == UserRole.FARMER) strings.farmerEarningsGain else strings.consumerSavings,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = strings.directFarmFeed,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp,
                                maxLines = 1
                            )
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "थेट शेतकरी ते ग्राहक • ०% दलाली आणि ताजे बाजारभाव"
                                    AppLanguage.HINDI -> "सीधे किसान से ग्राहक • ०% दलाली और ताज़ा मंडी भाव"
                                    AppLanguage.ENGLISH -> "Verified Maharashtra Farmers • 0% Middleman & Live Mandi Rates"
                                },
                                color = Color(0xFFE8F5E9),
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (userRole == UserRole.FARMER) {
                                Button(
                                    onClick = onNavigateToAddCrop,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF9A825),
                                        contentColor = Color(0xFF1F1400)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("hero_add_crop_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = strings.navAddCrop, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                }
                            }

                            OutlinedButton(
                                onClick = onNavigateToMandiAi,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("hero_mandi_ai_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = strings.consumerNavMandiRates, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        // 2. CONSUMER-ONLY FEATURE: Filter Sellers by Village / City (कोणत्या गावाचे शेतकरी हवे आहेत)
        if (userRole == UserRole.CONSUMER) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            RoundedCornerShape(18.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (language) {
                                        AppLanguage.MARATHI -> "कोणत्या गावाचे शेतकरी (Seller) हवे आहेत?"
                                        AppLanguage.HINDI -> "किस गाँव के किसान (Seller) चाहिए?"
                                        AppLanguage.ENGLISH -> "Select Village / City to View Sellers:"
                                    },
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }

                            if (selectedVillageFilter.isNotBlank()) {
                                Surface(
                                    onClick = { selectedVillageFilter = "" },
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.testTag("clear_village_filter_btn")
                                ) {
                                    Text(
                                        text = when (language) {
                                            AppLanguage.MARATHI -> "✕ सर्व गावे"
                                            AppLanguage.HINDI -> "✕ सभी गाँव"
                                            AppLanguage.ENGLISH -> "✕ All Villages"
                                        },
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Input box to type any village/city name
                        OutlinedTextField(
                            value = selectedVillageFilter,
                            onValueChange = { selectedVillageFilter = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("consumer_village_filter_input"),
                            placeholder = {
                                Text(
                                    text = when (language) {
                                        AppLanguage.MARATHI -> "येथे गावाचे नाव टाका (उदा. काठोरा, नाशिक, पुणे, अमरावती...)"
                                        AppLanguage.HINDI -> "यहाँ गाँव का नाम लिखें (उदा. काठोरा, नाशिक, पुणे, अमरावती...)"
                                        AppLanguage.ENGLISH -> "Type village or city name (e.g. Kathora, Nashik, Pune...)"
                                    },
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )

                        // Quick-Select Village Chips (All Villages + Consumer's Village + Active Sellers' Villages)
                        val consumerOwnVillage = remember(userSession.villageOrCity) {
                            userSession.villageOrCity.substringBefore(",").substringBefore("(").trim()
                        }
                        val quickVillages = remember(activeSellerVillages, consumerOwnVillage) {
                            buildList {
                                if (consumerOwnVillage.isNotBlank()) add(consumerOwnVillage)
                                activeSellerVillages.forEach { v ->
                                    if (none { it.equals(v, ignoreCase = true) }) add(v)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = selectedVillageFilter.isBlank(),
                                onClick = { selectedVillageFilter = "" },
                                label = {
                                    Text(
                                        text = when (language) {
                                            AppLanguage.MARATHI -> "सर्व गावे (${listings.size})"
                                            AppLanguage.HINDI -> "सभी गाँव (${listings.size})"
                                            AppLanguage.ENGLISH -> "All Villages (${listings.size})"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )

                            quickVillages.forEach { villageName ->
                                val isSelected = selectedVillageFilter.equals(villageName, ignoreCase = true)
                                val countInVillage = listings.count { matchesSellerVillage(it, villageName) }
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedVillageFilter = if (isSelected) "" else villageName
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "$villageName ($countInVillage)",
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Search & Category Filter Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_crop_input"),
                    placeholder = { Text(strings.searchCropHint, fontSize = 13.sp, maxLines = 1) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    categories.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedCategory == key,
                            onClick = { onCategoryChange(key) },
                            label = { Text(label, fontWeight = FontWeight.Medium, fontSize = 12.sp) }
                        )
                    }
                    FilterChip(
                        selected = organicOnly,
                        onClick = onToggleOrganic,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        },
                        label = { Text(strings.organicOnly, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                // Active Village Filter Status Strip
                if (selectedVillageFilter.isNotBlank()) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "📍 '${selectedVillageFilter}' गावातील शेतकरी: ${filteredListings.size} शेतमाल उपलब्ध"
                                    AppLanguage.HINDI -> "📍 '${selectedVillageFilter}' गाँव के किसान: ${filteredListings.size} फसलें उपलब्ध"
                                    AppLanguage.ENGLISH -> "📍 Sellers in '${selectedVillageFilter}': ${filteredListings.size} listings found"
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = Color(0xFF1B5E20),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 4. Recent Direct Orders Banner
        if (directOrders.isNotEmpty()) {
            item {
                val latest = directOrders.first()
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "ताजी थेट ऑर्डर: ${latest.cropNameMr} (${latest.quantityKg} किलो)"
                                    AppLanguage.HINDI -> "ताज़ा सीधा ऑर्डर: ${latest.cropNameMr} (${latest.quantityKg} किलो)"
                                    AppLanguage.ENGLISH -> "Recent Direct Order: ${latest.cropNameEn} (${latest.quantityKg} kg)"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "एकूण: ₹${latest.totalAmountInr} • दलाली बचत: ₹${latest.savedMiddlemanFeeInr}"
                                    AppLanguage.HINDI -> "कुल: ₹${latest.totalAmountInr} • दलाली बचत: ₹${latest.savedMiddlemanFeeInr}"
                                    AppLanguage.ENGLISH -> "Total: ₹${latest.totalAmountInr} • Middleman Commission Saved: ₹${latest.savedMiddlemanFeeInr}"
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // 5. Crop Listing Cards (Only real seller-added crops matching the selected village!)
        if (filteredListings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedVillageFilter.isNotBlank()) {
                                when (language) {
                                    AppLanguage.MARATHI -> "'${selectedVillageFilter}' गावातील कोणत्याही शेतकऱ्याचा शेतमाल सध्या उपलब्ध नाही."
                                    AppLanguage.HINDI -> "'${selectedVillageFilter}' गाँव के किसी भी किसान की फसल अभी उपलब्ध नहीं है।"
                                    AppLanguage.ENGLISH -> "No sellers found in '${selectedVillageFilter}' right now."
                                }
                            } else {
                                when (language) {
                                    AppLanguage.MARATHI -> "सध्या खरेदीसाठी कोणताही शेतमाल उपलब्ध नाही."
                                    AppLanguage.HINDI -> "अभी खरीदारी के लिए कोई फसल उपलब्ध नहीं है।"
                                    AppLanguage.ENGLISH -> "No crop listings are currently available."
                                }
                            },
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (selectedVillageFilter.isNotBlank()) {
                                when (language) {
                                    AppLanguage.MARATHI -> "दुसरे गाव निवडा किंवा सर्व गावांचे शेतकरी पाहण्यासाठी खालील बटनावर क्लिक करा."
                                    AppLanguage.HINDI -> "दूसरा गाँव चुनें या सभी गाँवों के किसान देखने के लिए नीचे दिए बटन पर क्लिक करें।"
                                    AppLanguage.ENGLISH -> "Try another village or tap below to view sellers from all villages."
                                }
                            } else {
                                when (language) {
                                    AppLanguage.MARATHI -> "जेव्हा शेतकरी (Seller) नवीन पीक विक्रीसाठी जोडतील, तेव्हा ते थेट येथे दिसेल."
                                    AppLanguage.HINDI -> "जब किसान (Seller) बिक्री के लिए नई फसल जोड़ेंगे, तो वह सीधे यहाँ दिखाई देगी।"
                                    AppLanguage.ENGLISH -> "When a Crop Seller adds a harvest for sale, it will appear here in real time."
                                }
                            },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (selectedVillageFilter.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { selectedVillageFilter = "" }) {
                                Text(
                                    text = when (language) {
                                        AppLanguage.MARATHI -> "सर्व गावांचे शेतकरी पहा"
                                        AppLanguage.HINDI -> "सभी गाँवों के किसान देखें"
                                        AppLanguage.ENGLISH -> "Show All Villages"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            items(filteredListings, key = { it.id }) { listing ->
                CropListingCard(
                    listing = listing,
                    language = language,
                    strings = strings,
                    onBuyClick = { selectedListingForOrder = listing },
                    onCallFarmer = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${listing.farmerPhone}"))
                        context.startActivity(dialIntent)
                    }
                )
            }
        }
    }

    // Direct Order Checkout Modal pre-populated with logged-in user's details
    selectedListingForOrder?.let { listing ->
        DirectOrderDialog(
            userSession = userSession,
            listing = listing,
            language = language,
            onDismiss = { selectedListingForOrder = null },
            onConfirm = { buyerName, buyerPhone, address, qtyKg ->
                onPlaceOrder(listing, buyerName, buyerPhone, address, qtyKg)
                selectedListingForOrder = null
            }
        )
    }
}

@Composable
private fun CropListingCard(
    listing: CropListingEntity,
    language: AppLanguage,
    strings: KisanStrings,
    onBuyClick: () -> Unit,
    onCallFarmer: () -> Unit
) {
    val context = LocalContext.current

    val cropTitle = if (language == AppLanguage.ENGLISH) listing.cropNameEn else listing.cropNameMr
    val farmerName = if (language == AppLanguage.ENGLISH) listing.farmerNameEn else listing.farmerNameMr
    val locationName = if (language == AppLanguage.ENGLISH) listing.villageAndMandiEn else listing.villageAndMandiMr
    val shortGrade = when (language) {
        AppLanguage.MARATHI -> "अ+ दर्जा"
        AppLanguage.HINDI -> "ए+ ग्रेड"
        AppLanguage.ENGLISH -> "Grade A+"
    }
    val pricePerKg = (listing.askingPricePerQuintal / 100.0).roundToInt()

    val hasValidPhoto = remember(listing.imageUri) {
        com.example.data.ImageStorageHelper.isImageUriReadable(context, listing.imageUri)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("crop_card_${listing.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = cropTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$farmerName • $locationName",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                if (listing.isOrganic) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(50))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = Color(0xFF1B5E20),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "सेंद्रिय"
                                    AppLanguage.HINDI -> "जैविक"
                                    AppLanguage.ENGLISH -> "Organic"
                                },
                                color = Color(0xFF1B5E20),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (hasValidPhoto && !listing.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = com.example.data.ImageStorageHelper.resolveImageModel(listing.imageUri),
                    contentDescription = cropTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            }

            // Quality specs strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpecPill(text = shortGrade)
                SpecPill(
                    text = when (language) {
                        AppLanguage.MARATHI -> "ओलावा ${listing.moisturePercent}%"
                        AppLanguage.HINDI -> "नमी ${listing.moisturePercent}%"
                        AppLanguage.ENGLISH -> "Moisture ${listing.moisturePercent}%"
                    },
                    icon = Icons.Default.WaterDrop
                )
                SpecPill(
                    text = when (language) {
                        AppLanguage.MARATHI -> "${listing.quantityQuintals.toInt()} क्विंटल उपलब्ध"
                        AppLanguage.HINDI -> "${listing.quantityQuintals.toInt()} क्विंटल उपलब्ध"
                        AppLanguage.ENGLISH -> "${listing.quantityQuintals.toInt()} Qtl Available"
                    }
                )
            }

            // Clean 2-Column Direct Price vs Mandi Rate Box (No complicated AI hints)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.farmerAskPrice,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Text(
                        text = "₹${listing.askingPricePerQuintal}/${strings.qtlUnit} (≈ ₹$pricePerKg/${strings.kgUnit})",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = strings.apmcModalLabel,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Text(
                        text = "₹${listing.apmcModalPricePerQuintal}/${strings.qtlUnit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCallFarmer,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("call_farmer_btn_${listing.id}"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.callFarmerBtn,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }

                Button(
                    onClick = onBuyClick,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp)
                        .testTag("buy_direct_btn_${listing.id}"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.buyDirectBtn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecPill(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DirectOrderDialog(
    userSession: UserSession,
    listing: CropListingEntity,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int) -> Unit
) {
    var buyerName by remember(userSession.fullName) { mutableStateOf(userSession.fullName) }
    var buyerPhone by remember(userSession.phoneNumber) { mutableStateOf(userSession.phoneNumber) }
    var deliveryAddress by remember(userSession.villageOrCity) { mutableStateOf(userSession.villageOrCity) }
    var quantityText by remember { mutableStateOf("") }
    var quantityError by remember { mutableStateOf(false) }

    val availableKg = (listing.quantityQuintals * 100.0).roundToInt().coerceAtLeast(1)
    val enteredQtyKg = quantityText.trim().toIntOrNull() ?: 0
    val pricePerKg = (listing.askingPricePerQuintal / 100.0).coerceAtLeast(1.0)
    val totalInr = (pricePerKg * enteredQtyKg).roundToInt()
    val savedMiddlemanFee = (totalInr * 0.16).roundToInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (language) {
                    AppLanguage.MARATHI -> "थेट शेतकरी खरेदी: ${listing.cropNameMr}"
                    AppLanguage.HINDI -> "सीधी किसान खरीद: ${listing.cropNameMr}"
                    AppLanguage.ENGLISH -> "Direct Farm Order: ${listing.cropNameEn}"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = when (language) {
                        AppLanguage.MARATHI -> "शेतकरी: ${listing.farmerNameMr} (${listing.villageAndMandiMr})"
                        AppLanguage.HINDI -> "किसान: ${listing.farmerNameMr} (${listing.villageAndMandiMr})"
                        AppLanguage.ENGLISH -> "Farmer: ${listing.farmerNameEn} (${listing.villageAndMandiEn})"
                    },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { input ->
                        val digitsOnly = input.filter { it.isDigit() }
                        quantityText = digitsOnly
                        quantityError = false
                    },
                    label = {
                        Text(
                            when (language) {
                                AppLanguage.MARATHI -> "तुम्हाला किती किलो शेतमाल हवा आहे? (किलो)"
                                AppLanguage.HINDI -> "आपको कितने किलो उपज चाहिए? (किलो)"
                                AppLanguage.ENGLISH -> "Enter Quantity Needed (in Kg)"
                            }
                        )
                    },
                    placeholder = {
                        Text(
                            when (language) {
                                AppLanguage.MARATHI -> "उदा. 10, 25, 50, 120 किलो... (उपलब्ध: $availableKg किलो)"
                                AppLanguage.HINDI -> "उदा. 10, 25, 50, 120 किलो... (उपलब्ध: $availableKg किलो)"
                                AppLanguage.ENGLISH -> "e.g. 10, 25, 50, 120 kg... (Available: $availableKg kg)"
                            },
                            fontSize = 12.sp
                        )
                    },
                    supportingText = if (quantityError) {
                        {
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "कृपया १ किलो किंवा त्यापेक्षा जास्त प्रमाण टाका."
                                    AppLanguage.HINDI -> "कृपया १ किलो या उससे अधिक मात्रा दर्ज करें।"
                                    AppLanguage.ENGLISH -> "Please enter a valid quantity (at least 1 kg)."
                                },
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp
                            )
                        }
                    } else null,
                    isError = quantityError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("order_quantity_kg_input")
                )

                OutlinedTextField(
                    value = buyerName,
                    onValueChange = { buyerName = it },
                    label = {
                        Text(
                            when (language) {
                                AppLanguage.MARATHI -> "ग्राहकाचे नाव"
                                AppLanguage.HINDI -> "ग्राहक का नाम"
                                AppLanguage.ENGLISH -> "Your Full Name"
                            }
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = buyerPhone,
                    onValueChange = { buyerPhone = it },
                    label = {
                        Text(
                            when (language) {
                                AppLanguage.MARATHI -> "मोबाईल नंबर"
                                AppLanguage.HINDI -> "मोबाइल नंबर"
                                AppLanguage.ENGLISH -> "Phone Number"
                            }
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = {
                        Text(
                            when (language) {
                                AppLanguage.MARATHI -> "डिलिव्हरी पत्ता"
                                AppLanguage.HINDI -> "डिलीवरी का पता"
                                AppLanguage.ENGLISH -> "Delivery Address"
                            }
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "एकूण रक्कम: ₹$totalInr (दर: ₹${pricePerKg.roundToInt()}/किलो)"
                                AppLanguage.HINDI -> "कुल राशि: ₹$totalInr (दर: ₹${pricePerKg.roundToInt()}/किलो)"
                                AppLanguage.ENGLISH -> "Direct Total: ₹$totalInr (Rate: ₹${pricePerKg.roundToInt()}/kg)"
                            },
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "०% दलाली • तुमची बचत: ₹$savedMiddlemanFee"
                                AppLanguage.HINDI -> "०% दलाली • आपकी बचत: ₹$savedMiddlemanFee"
                                AppLanguage.ENGLISH -> "0% Dalal Fee • Estimated Savings: ₹$savedMiddlemanFee"
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF1B5E20),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (enteredQtyKg <= 0) {
                        quantityError = true
                    } else if (buyerName.isNotBlank() && deliveryAddress.isNotBlank()) {
                        onConfirm(buyerName, buyerPhone, deliveryAddress, enteredQtyKg)
                    }
                },
                modifier = Modifier.testTag("confirm_direct_order_btn")
            ) {
                Text(
                    when (language) {
                        AppLanguage.MARATHI -> "ऑर्डर निश्चित करा"
                        AppLanguage.HINDI -> "ऑर्डर कन्फर्म करें"
                        AppLanguage.ENGLISH -> "Confirm Direct Order"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    when (language) {
                        AppLanguage.MARATHI -> "रद्द करा"
                        AppLanguage.HINDI -> "रद्द करें"
                        AppLanguage.ENGLISH -> "Cancel"
                    }
                )
            }
        }
    )
}

/**
 * Matches a seller's crop listing against the consumer's selected/typed village or city name
 * across both Marathi and English spellings.
 */
private fun matchesSellerVillage(item: CropListingEntity, villageQuery: String): Boolean {
    return com.example.data.KisanBackendService.matchesSellerVillage(item, villageQuery)
}

