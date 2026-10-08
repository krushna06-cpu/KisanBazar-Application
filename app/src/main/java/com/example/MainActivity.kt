package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppLanguage
import com.example.data.LocalizationProvider
import com.example.data.UserRole
import com.example.ui.AddCropListingScreen
import com.example.ui.ConsumerOrdersScreen
import com.example.ui.ConsumerTab
import com.example.ui.KisanViewModel
import com.example.ui.LoginScreen
import com.example.ui.MandiIntelligenceScreen
import com.example.ui.MarketFeedScreen
import com.example.ui.SellerBuyerOrdersScreen
import com.example.ui.SellerInventoryScreen
import com.example.ui.SellerTab
import com.example.ui.UserProfileModalSheet
import com.example.ui.theme.KisanBazarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KisanBazarTheme {
                KisanBazarRootApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KisanBazarRootApp(
    viewModel: KisanViewModel = viewModel()
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val loggedInUser by viewModel.loggedInUser.collectAsStateWithLifecycle()
    val sellerTab by viewModel.sellerTab.collectAsStateWithLifecycle()
    val consumerTab by viewModel.consumerTab.collectAsStateWithLifecycle()
    val cropListings by viewModel.cropListings.collectAsStateWithLifecycle()
    val marketRates by viewModel.marketRates.collectAsStateWithLifecycle()
    val directOrders by viewModel.directOrders.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val organicOnly by viewModel.organicFilterOnly.collectAsStateWithLifecycle()
    val aiRecommendation by viewModel.aiRecommendation.collectAsStateWithLifecycle()
    val isCalculatingAi by viewModel.isCalculatingAi.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()

    val strings = LocalizationProvider.get(language)
    val activeUser = loggedInUser

    var showProfileSheet by remember { mutableStateOf(false) }

    // STEP 1: Always start on LoginScreen with 6-digit OTP verification and Auto-GPS location
    if (activeUser == null) {
        LoginScreen(
            language = language,
            strings = strings,
            onToggleLanguage = { viewModel.toggleLanguage() },
            onSelectLanguage = { viewModel.setLanguage(it) },
            onLoginSuccess = { session -> viewModel.loginUser(session) }
        )
    } else {
        val isSeller = activeUser.role == UserRole.FARMER
        BackHandler(
            enabled = (isSeller && sellerTab != SellerTab.MY_CROPS_DASHBOARD) ||
                (!isSeller && consumerTab != ConsumerTab.BUY_DIRECT_MARKET)
        ) {
            if (isSeller) {
                viewModel.selectSellerTab(SellerTab.MY_CROPS_DASHBOARD)
            } else {
                viewModel.selectConsumerTab(ConsumerTab.BUY_DIRECT_MARKET)
            }
        }

        val topBarColor = if (isSeller) Color(0xFF1B5E20) else Color(0xFFBF360C)

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = topBarColor,
                        titleContentColor = Color.White
                    ),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSeller) Icons.Default.Agriculture else Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = strings.appTitle,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isSeller) strings.sellerPortalBadge else strings.consumerPortalBadge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                        }
                    },
                    actions = {
                        // 3-Language Switcher Pill (मराठी • हिंदी • English)
                        Surface(
                            onClick = { viewModel.toggleLanguage() },
                            color = Color.White.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .testTag("topbar_lang_switch")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = language.displayName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        // User Profile Pill (Replaces raw Logout button in top bar)
                        Surface(
                            onClick = { showProfileSheet = true },
                            color = Color(0xFFF9A825),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .testTag("topbar_profile_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFF1F1400),
                                    shape = CircleShape,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = strings.profileLabel,
                                            tint = Color(0xFFFFD54F),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activeUser.fullName.split(" ").firstOrNull() ?: strings.profileLabel,
                                    color = Color(0xFF1F1400),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (isSeller) {
                    // CROP SELLER (FARMER) DEDICATED 4-TAB NAVIGATION BAR (No "Code" tab)
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        NavigationBarItem(
                            selected = sellerTab == SellerTab.MY_CROPS_DASHBOARD,
                            onClick = { viewModel.selectSellerTab(SellerTab.MY_CROPS_DASHBOARD) },
                            icon = { Icon(Icons.Default.Agriculture, contentDescription = strings.sellerNavMyCrops) },
                            label = { Text(strings.sellerNavMyCrops, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("seller_nav_my_crops")
                        )
                        NavigationBarItem(
                            selected = sellerTab == SellerTab.ADD_NEW_CROP,
                            onClick = { viewModel.selectSellerTab(SellerTab.ADD_NEW_CROP) },
                            icon = { Icon(Icons.Default.AddCircle, contentDescription = strings.sellerNavAddCrop) },
                            label = { Text(strings.sellerNavAddCrop, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("seller_nav_add_crop")
                        )
                        NavigationBarItem(
                            selected = sellerTab == SellerTab.AI_AND_MANDI_ADVISOR,
                            onClick = { viewModel.selectSellerTab(SellerTab.AI_AND_MANDI_ADVISOR) },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = strings.sellerNavMandiAi) },
                            label = { Text(strings.sellerNavMandiAi, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("seller_nav_ai_mandi")
                        )
                        NavigationBarItem(
                            selected = sellerTab == SellerTab.BUYER_ORDERS_RECEIVED,
                            onClick = { viewModel.selectSellerTab(SellerTab.BUYER_ORDERS_RECEIVED) },
                            icon = { Icon(Icons.Default.Inventory2, contentDescription = strings.sellerNavOrders) },
                            label = { Text(strings.sellerNavOrders, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("seller_nav_orders")
                        )
                    }
                } else {
                    // CONSUMER (BUYER) DEDICATED 3-TAB NAVIGATION BAR (No "Code" tab)
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        NavigationBarItem(
                            selected = consumerTab == ConsumerTab.BUY_DIRECT_MARKET,
                            onClick = { viewModel.selectConsumerTab(ConsumerTab.BUY_DIRECT_MARKET) },
                            icon = { Icon(Icons.Default.Storefront, contentDescription = strings.consumerNavBuyCrops) },
                            label = { Text(strings.consumerNavBuyCrops, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("consumer_nav_market")
                        )
                        NavigationBarItem(
                            selected = consumerTab == ConsumerTab.MY_DIRECT_ORDERS,
                            onClick = { viewModel.selectConsumerTab(ConsumerTab.MY_DIRECT_ORDERS) },
                            icon = { Icon(Icons.Default.LocalShipping, contentDescription = strings.consumerNavMyOrders) },
                            label = { Text(strings.consumerNavMyOrders, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("consumer_nav_orders")
                        )
                        NavigationBarItem(
                            selected = consumerTab == ConsumerTab.APMC_PRICE_CHECK,
                            onClick = { viewModel.selectConsumerTab(ConsumerTab.APMC_PRICE_CHECK) },
                            icon = { Icon(Icons.Default.TrendingUp, contentDescription = strings.consumerNavMandiRates) },
                            label = { Text(strings.consumerNavMandiRates, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("consumer_nav_mandi")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedVisibility(visible = bannerMessage != null) {
                    bannerMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(topBarColor)
                                .clickable { viewModel.clearBannerMessage() }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "✕",
                                    color = Color(0xFFFFD54F),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (isSeller) {
                        when (sellerTab) {
                            SellerTab.MY_CROPS_DASHBOARD -> {
                                SellerInventoryScreen(
                                    userSession = activeUser,
                                    language = language,
                                    strings = strings,
                                    listings = cropListings,
                                    incomingOrders = directOrders,
                                    onNavigateToAddCrop = { viewModel.selectSellerTab(SellerTab.ADD_NEW_CROP) },
                                    onNavigateToMandiAi = { viewModel.selectSellerTab(SellerTab.AI_AND_MANDI_ADVISOR) },
                                    onNavigateToOrders = { viewModel.selectSellerTab(SellerTab.BUYER_ORDERS_RECEIVED) },
                                    onUpdateListingPhoto = { id, newUri -> viewModel.updateCropListingPhoto(id, newUri) },
                                    onDeleteListing = { id -> viewModel.deleteCropListing(id) }
                                )
                            }

                            SellerTab.ADD_NEW_CROP -> {
                                AddCropListingScreen(
                                    userSession = activeUser,
                                    language = language,
                                    strings = strings,
                                    onPublishListing = { nameEn, nameMr, cat, farmer, phone, loc, qty, ask, modal, grade, moist, org, uri ->
                                        viewModel.addNewCropListing(
                                            cropNameEn = nameEn,
                                            cropNameMr = nameMr,
                                            category = cat,
                                            farmerName = farmer,
                                            farmerPhone = phone,
                                            villageAndMandi = loc,
                                            quantityQuintals = qty,
                                            askingPricePerQuintal = ask,
                                            apmcModalPricePerQuintal = modal,
                                            qualityGrade = grade,
                                            moisturePercent = moist,
                                            isOrganic = org,
                                            imageUri = uri
                                        )
                                    },
                                    onToastMessage = { viewModel.showToastMessage(it) }
                                )
                            }

                            SellerTab.AI_AND_MANDI_ADVISOR -> {
                                MandiIntelligenceScreen(
                                    language = language,
                                    strings = strings,
                                    marketRates = marketRates,
                                    aiRecommendation = aiRecommendation,
                                    isCalculatingAi = isCalculatingAi,
                                    onRefreshRates = { viewModel.refreshLiveApmcRates() },
                                    onRunAiAdvisor = { cropName, modal, grade, moisture, organic, mandi, trend ->
                                        viewModel.runAiPriceEvaluation(
                                            cropName = cropName,
                                            apmcModalPrice = modal,
                                            qualityGrade = grade,
                                            moisturePercent = moisture,
                                            isOrganic = organic,
                                            mandiName = mandi,
                                            arrivalTrendPercent = trend
                                        )
                                    }
                                )
                            }

                            SellerTab.BUYER_ORDERS_RECEIVED -> {
                                SellerBuyerOrdersScreen(
                                    language = language,
                                    strings = strings,
                                    orders = directOrders
                                )
                            }
                        }
                    } else {
                        when (consumerTab) {
                            ConsumerTab.BUY_DIRECT_MARKET -> {
                                MarketFeedScreen(
                                    userSession = activeUser,
                                    language = language,
                                    strings = strings,
                                    userRole = UserRole.CONSUMER,
                                    listings = cropListings,
                                    directOrders = directOrders,
                                    selectedCategory = selectedCategory,
                                    searchQuery = searchQuery,
                                    organicOnly = organicOnly,
                                    onCategoryChange = { viewModel.setCategoryFilter(it) },
                                    onSearchChange = { viewModel.setSearchQuery(it) },
                                    onToggleOrganic = { viewModel.toggleOrganicFilter() },
                                    onNavigateToAddCrop = {},
                                    onNavigateToMandiAi = { viewModel.selectConsumerTab(ConsumerTab.APMC_PRICE_CHECK) },
                                    onPlaceOrder = { listing, buyerName, phone, address, qtyKg ->
                                        viewModel.placeDirectOrder(listing, buyerName, phone, address, qtyKg)
                                    }
                                )
                            }

                            ConsumerTab.MY_DIRECT_ORDERS -> {
                                ConsumerOrdersScreen(
                                    language = language,
                                    strings = strings,
                                    orders = directOrders,
                                    onBrowseMarket = { viewModel.selectConsumerTab(ConsumerTab.BUY_DIRECT_MARKET) }
                                )
                            }

                            ConsumerTab.APMC_PRICE_CHECK -> {
                                MandiIntelligenceScreen(
                                    language = language,
                                    strings = strings,
                                    marketRates = marketRates,
                                    aiRecommendation = aiRecommendation,
                                    isCalculatingAi = isCalculatingAi,
                                    onRefreshRates = { viewModel.refreshLiveApmcRates() },
                                    onRunAiAdvisor = { cropName, modal, grade, moisture, organic, mandi, trend ->
                                        viewModel.runAiPriceEvaluation(
                                            cropName = cropName,
                                            apmcModalPrice = modal,
                                            qualityGrade = grade,
                                            moisturePercent = moisture,
                                            isOrganic = organic,
                                            mandiName = mandi,
                                            arrivalTrendPercent = trend
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // User Profile Modal Sheet (opened when tapping User Profile pill in TopAppBar)
        if (showProfileSheet) {
            UserProfileModalSheet(
                userSession = activeUser,
                language = language,
                strings = strings,
                activeListingsCount = cropListings.size,
                ordersCount = directOrders.size,
                onDismiss = { showProfileSheet = false },
                onUpdateProfile = { newName, newLoc ->
                    viewModel.updateUserProfile(newName, newLoc)
                },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onSelectLanguage = { viewModel.setLanguage(it) },
                onSwitchRole = {
                    viewModel.switchLoggedInUserRole()
                    showProfileSheet = false
                },
                onLogout = {
                    showProfileSheet = false
                    viewModel.logout()
                }
            )
        }
    }
}
