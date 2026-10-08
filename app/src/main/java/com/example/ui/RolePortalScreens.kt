package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AppLanguage
import com.example.data.CropListingEntity
import com.example.data.DirectOrderEntity
import com.example.data.KisanStrings
import com.example.data.LocalizationProvider
import com.example.data.UserRole
import com.example.data.UserSession
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileModalSheet(
    userSession: UserSession,
    language: AppLanguage,
    strings: KisanStrings,
    activeListingsCount: Int,
    ordersCount: Int,
    onDismiss: () -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onToggleLanguage: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember(userSession.fullName) { mutableStateOf(userSession.fullName) }
    var editedLocation by remember(userSession.villageOrCity) { mutableStateOf(userSession.villageOrCity) }

    val isSeller = userSession.role == UserRole.FARMER
    val badgeColor = if (isSeller) Color(0xFF1B5E20) else Color(0xFFE65100)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 10.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Header Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = badgeColor,
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = userSession.fullName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.profileTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = userSession.fullName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = badgeColor.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (isSeller) strings.farmerRole else strings.consumerRole,
                                color = badgeColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = Color(0xFF1B5E20),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.verifiedMobileBadge,
                                    color = Color(0xFF1B5E20),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Verified Contact & Location Details (or Edit Form)
            if (isEditing) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text(strings.fullNameInputLabel) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = editedLocation,
                    onValueChange = { editedLocation = it },
                    label = { Text(strings.locationInputLabel) },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        onUpdateProfile(editedName.trim(), editedLocation.trim())
                        isEditing = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(strings.saveProfileBtn, fontWeight = FontWeight.Bold)
                }
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = userSession.phoneNumber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = userSession.villageOrCity,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 3-Language Selector Strip (मराठी • हिंदी • English)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = when (language) {
                        AppLanguage.MARATHI -> "अ‍ॅपची भाषा निवडा (Language):"
                        AppLanguage.HINDI -> "ऐप की भाषा चुनें (Language):"
                        AppLanguage.ENGLISH -> "Select App Language:"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { langOption ->
                        val isSelected = language == langOption
                        Surface(
                            onClick = { onSelectLanguage(langOption) },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("profile_lang_${langOption.code}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = langOption.displayName,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Profile Action Buttons
            if (!isEditing) {
                OutlinedButton(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.editProfileBtn, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedButton(
                onClick = onSwitchRole,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.switchRoleBtn, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("profile_sheet_logout_btn"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.logoutBtn, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }

            // Developer Identity Footer below Logout
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("built_by_kruti_labs_badge")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Built by KRUTI Labs",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun SellerInventoryScreen(
    userSession: UserSession,
    language: AppLanguage,
    strings: KisanStrings,
    listings: List<CropListingEntity>,
    incomingOrders: List<DirectOrderEntity>,
    onNavigateToAddCrop: () -> Unit,
    onNavigateToMandiAi: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onUpdateListingPhoto: (Int, String) -> Unit = { _, _ -> },
    onDeleteListing: (Int) -> Unit
) {
    val totalValueInr = listings.sumOf { (it.quantityQuintals * it.askingPricePerQuintal).roundToInt() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("seller_inventory_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Seller Executive Summary Banner
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0B2E10),
                                    Color(0xFF1B5E20),
                                    Color(0xFF2E7D32)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = Color(0xFFF9A825),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = strings.sellerPortalBadge,
                                        color = Color(0xFF1F1400),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = when (language) {
                                        AppLanguage.MARATHI -> "नमस्कार, ${userSession.fullName}"
                                        AppLanguage.HINDI -> "नमस्ते, ${userSession.fullName}"
                                        AppLanguage.ENGLISH -> "Welcome, ${userSession.fullName}"
                                    },
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "${userSession.villageOrCity} • ${userSession.phoneNumber}",
                                    color = Color(0xFFC8E6C9),
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }

                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = CircleShape,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Agriculture,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD54F),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }

                        // 3 KPI Boxes for Crop Seller
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SellerKpiTile(
                                modifier = Modifier.weight(1f),
                                label = strings.sellerTotalListingsLabel,
                                value = "${listings.size}"
                            )
                            SellerKpiTile(
                                modifier = Modifier.weight(1f),
                                label = strings.sellerBuyerOrdersLabel,
                                value = "${incomingOrders.size}"
                            )
                            SellerKpiTile(
                                modifier = Modifier.weight(1.2f),
                                label = strings.sellerEstRevenueLabel,
                                value = if (totalValueInr >= 1000) "₹${totalValueInr / 1000}K" else "₹$totalValueInr"
                            )
                        }

                        // Quick Seller Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onNavigateToAddCrop,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF9A825),
                                    contentColor = Color(0xFF1F1400)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("seller_add_crop_cta"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strings.sellerNavAddCrop,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }

                            OutlinedButton(
                                onClick = onNavigateToMandiAi,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strings.sellerNavMandiAi,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Section Heading
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (language) {
                        AppLanguage.MARATHI -> "तुमचा विक्रीसाठी ठेवलेला शेतमाल (${listings.size})"
                        AppLanguage.HINDI -> "आपकी बिक्री के लिए उपलब्ध फसलें (${listings.size})"
                        AppLanguage.ENGLISH -> "Your Active Crop Listings (${listings.size})"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )
                if (incomingOrders.isNotEmpty()) {
                    Surface(
                        onClick = onNavigateToOrders,
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.border(1.dp, Color(0xFFF9A825), RoundedCornerShape(50))
                    ) {
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "${incomingOrders.size} ऑर्डर्स पहा →"
                                AppLanguage.HINDI -> "${incomingOrders.size} ऑर्डर देखें →"
                                AppLanguage.ENGLISH -> "View ${incomingOrders.size} Orders →"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // 3. Seller Crop Inventory Cards (or Empty State if no real crops added yet)
        if (listings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "तुम्ही अद्याप कोणताही शेतमाल विक्रीसाठी जोडलेला नाही."
                                AppLanguage.HINDI -> "आपने अभी तक बिक्री के लिए कोई फसल नहीं जोड़ी है।"
                                AppLanguage.ENGLISH -> "You haven't added any crop listings for sale yet."
                            },
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "'पीक जोडा' बटनावर क्लिक करून तुमचा खरा शेतमाल जोडल्यावर तो येथे आणि ग्राहकांना खरेदीसाठी दिसेल."
                                AppLanguage.HINDI -> "'फसल जोड़ें' बटन पर क्लिक करके अपनी फसल जोड़ें, यह तुरंत यहाँ और ग्राहक बाज़ार में दिखेगी।"
                                AppLanguage.ENGLISH -> "Tap 'Add Crop' to publish your real harvest. It will immediately appear here and in the Consumer Marketplace."
                            },
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(onClick = onNavigateToAddCrop) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.sellerNavAddCrop, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(listings, key = { it.id }) { item ->
                SellerCropCard(
                    listing = item,
                    language = language,
                    strings = strings,
                    onUpdatePhoto = { newPermanentUri ->
                        onUpdateListingPhoto(item.id, newPermanentUri)
                    },
                    onDelete = { onDeleteListing(item.id) }
                )
            }
        }
    }
}

@Composable
private fun SellerKpiTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.14f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Color(0xFFFFD54F),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                maxLines = 1
            )
            Text(
                text = label,
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SellerCropCard(
    listing: CropListingEntity,
    language: AppLanguage,
    strings: KisanStrings,
    onUpdatePhoto: (String) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val cropTitle = if (language == AppLanguage.ENGLISH) listing.cropNameEn else listing.cropNameMr
    val locationName = if (language == AppLanguage.ENGLISH) listing.villageAndMandiEn else listing.villageAndMandiMr
    val harvestLabel = LocalizationProvider.localizeHarvestTime(listing.harvestDateLabel, language)
    val shortGrade = when (language) {
        AppLanguage.MARATHI -> "अ+ दर्जा"
        AppLanguage.HINDI -> "ए+ ग्रेड"
        AppLanguage.ENGLISH -> "Grade A+"
    }
    val estStockValue = (listing.quantityQuintals * listing.askingPricePerQuintal).roundToInt()

    val hasValidPhoto = remember(listing.imageUri) {
        com.example.data.ImageStorageHelper.isImageUriReadable(context, listing.imageUri)
    }

    val cardPhotoLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedUri = com.example.data.ImageStorageHelper.copyUriToPermanentStorage(context, uri)
            if (savedUri != null) {
                onUpdatePhoto(savedUri)
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Top Header: Crop Info (Left) & Prices (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = strings.liveForBuyersBadge,
                                color = Color(0xFF1B5E20),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        if (listing.isOrganic) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = when (language) {
                                        AppLanguage.MARATHI -> " सेंद्रिय"
                                        AppLanguage.HINDI -> " जैविक"
                                        AppLanguage.ENGLISH -> " Organic"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }

                    Text(
                        text = cropTitle,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$locationName • $harvestLabel",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${listing.askingPricePerQuintal}/${strings.qtlUnit}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${strings.apmcShortLabel}: ₹${listing.apmcModalPricePerQuintal}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. Crop Photo Banner (Permanent image from internal storage, or compact 1-line button to attach/change)
            if (hasValidPhoto && !listing.imageUri.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    AsyncImage(
                        model = com.example.data.ImageStorageHelper.resolveImageModel(listing.imageUri),
                        contentDescription = cropTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        onClick = {
                            cardPhotoLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        color = Color(0xCC1B5E20),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "फोटो बदला"
                                    AppLanguage.HINDI -> "फ़ोटो बदलें"
                                    AppLanguage.ENGLISH -> "Change Photo"
                                },
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = {
                        cardPhotoLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (language) {
                            AppLanguage.MARATHI -> "पिकाचा फोटो जोडा / अपडेट करा"
                            AppLanguage.HINDI -> "फसल की फ़ोटो जोड़ें / बदलें"
                            AppLanguage.ENGLISH -> "Add / Update Harvest Photo"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            // 3. Equal-Width 3-Column Inventory Metrics Strip (No squishing or empty vertical space!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.1f)) {
                    Text(
                        text = when (language) {
                            AppLanguage.MARATHI -> "उपलब्ध स्टॉक"
                            AppLanguage.HINDI -> "उपलब्ध स्टॉक"
                            AppLanguage.ENGLISH -> "Stock Available"
                        },
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Text(
                        text = "${listing.quantityQuintals.toInt()} ${strings.qtlUnit} (${(listing.quantityQuintals * 100).toInt()} ${strings.kgUnit})",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                Column(modifier = Modifier.weight(1.1f)) {
                    Text(
                        text = when (language) {
                            AppLanguage.MARATHI -> "प्रतवारी व ओलावा"
                            AppLanguage.HINDI -> "ग्रेड व नमी"
                            AppLanguage.ENGLISH -> "Grade & Moisture"
                        },
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "$shortGrade (${listing.moisturePercent}%)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(0.9f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = when (language) {
                            AppLanguage.MARATHI -> "एकूण किंमत"
                            AppLanguage.HINDI -> "कुल मूल्य"
                            AppLanguage.ENGLISH -> "Total Value"
                        },
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Text(
                        text = "₹$estStockValue",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFF1B5E20),
                        maxLines = 1
                    )
                }
            }

            // 4. Clean Bottom Remove Listing Button (No extra complicated AI hints)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = strings.deleteListingBtn,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.deleteListingBtn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun SellerBuyerOrdersScreen(
    language: AppLanguage,
    strings: KisanStrings,
    orders: List<DirectOrderEntity>
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("seller_orders_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = when (language) {
                    AppLanguage.MARATHI -> "ग्राहकांकडून आलेल्या थेट ऑर्डर्स (${orders.size})"
                    AppLanguage.HINDI -> "ग्राहकों से प्राप्त सीधे ऑर्डर (${orders.size})"
                    AppLanguage.ENGLISH -> "Direct Buyer Orders Received (${orders.size})"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = when (language) {
                    AppLanguage.MARATHI -> "०% दलाली • ग्राहकांशी थेट संपर्क करून शेतमाल पाठवा"
                    AppLanguage.HINDI -> "०% दलाली • ग्राहकों से सीधे संपर्क करके फसल भेजें"
                    AppLanguage.ENGLISH -> "0% Middleman Commission • Contact buyers directly to dispatch harvest"
                },
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (orders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = strings.noOrdersYetSeller,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(orders, key = { it.id }) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.ENGLISH) order.cropNameEn else order.cropNameMr,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }

                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = when (language) {
                                        AppLanguage.MARATHI -> "${order.quantityKg} किलो ऑर्डर"
                                        AppLanguage.HINDI -> "${order.quantityKg} किलो ऑर्डर"
                                        AppLanguage.ENGLISH -> "${order.quantityKg} KG ORDER"
                                    },
                                    color = Color(0xFF1B5E20),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "ग्राहक: ${order.buyerName} (${order.buyerPhone})"
                                AppLanguage.HINDI -> "ग्राहक: ${order.buyerName} (${order.buyerPhone})"
                                AppLanguage.ENGLISH -> "Buyer: ${order.buyerName} (${order.buyerPhone})"
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "डिलिव्हरी पत्ता: ${order.deliveryAddress}"
                                AppLanguage.HINDI -> "डिलीवरी पता: ${order.deliveryAddress}"
                                AppLanguage.ENGLISH -> "Delivery Address: ${order.deliveryAddress}"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Payout Summary Box
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF2E7D32).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = when (language) {
                                            AppLanguage.MARATHI -> "तुम्हाला मिळणारी थेट रक्कम"
                                            AppLanguage.HINDI -> "आपको मिलने वाली सीधी राशि"
                                            AppLanguage.ENGLISH -> "Direct Farmer Payout"
                                        },
                                        fontSize = 11.sp,
                                        color = Color(0xFF1B5E20),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "₹${order.totalAmountInr}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1B5E20)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = when (language) {
                                            AppLanguage.MARATHI -> "वाचलेली दलाली"
                                            AppLanguage.HINDI -> "बची हुई दलाली"
                                            AppLanguage.ENGLISH -> "Dalal Fee Saved"
                                        },
                                        fontSize = 11.sp,
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "₹${order.savedMiddlemanFeeInr}",
                                        fontSize = 16.sp,
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Full-Width Clear Call Buyer Button
                        Button(
                            onClick = {
                                val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.buyerPhone}"))
                                context.startActivity(dial)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("call_buyer_order_btn_${order.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B5E20),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "ग्राहकाला कॉल करा (${order.buyerPhone})"
                                    AppLanguage.HINDI -> "ग्राहक को कॉल करें (${order.buyerPhone})"
                                    AppLanguage.ENGLISH -> "Call Buyer (${order.buyerPhone})"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsumerOrdersScreen(
    language: AppLanguage,
    strings: KisanStrings,
    orders: List<DirectOrderEntity>,
    onBrowseMarket: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("consumer_orders_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = when (language) {
                    AppLanguage.MARATHI -> "माझ्या थेट शेतकरी ऑर्डर्स (${orders.size})"
                    AppLanguage.HINDI -> "मेरे सीधे किसान ऑर्डर (${orders.size})"
                    AppLanguage.ENGLISH -> "My Direct Farm Orders (${orders.size})"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = when (language) {
                    AppLanguage.MARATHI -> "तुम्ही थेट शेतकऱ्यांकडून ०% दलालीत खरेदी केलेला ताजा शेतमाल"
                    AppLanguage.HINDI -> "आपने सीधे किसानों से ०% दलाली में खरीदी गई ताज़ा उपज"
                    AppLanguage.ENGLISH -> "Fresh produce ordered directly from verified farmers with 0% middleman markup"
                },
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (orders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = strings.noOrdersYetConsumer,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(onClick = onBrowseMarket) {
                            Text(strings.consumerNavBuyCrops, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(orders, key = { it.id }) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == AppLanguage.ENGLISH) order.cropNameEn else order.cropNameMr,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp
                            )
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = strings.confirmedOrderStatus,
                                    color = Color(0xFF1B5E20),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "शेतकरी: ${order.farmerNameEn} • प्रमाण: ${order.quantityKg} किलो"
                                AppLanguage.HINDI -> "किसान: ${order.farmerNameEn} • मात्रा: ${order.quantityKg} किलो"
                                AppLanguage.ENGLISH -> "Farmer: ${order.farmerNameEn} • Quantity: ${order.quantityKg} kg"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "डिलिव्हरी पत्ता: ${order.deliveryAddress}"
                                AppLanguage.HINDI -> "डिलीवरी पता: ${order.deliveryAddress}"
                                AppLanguage.ENGLISH -> "Delivery: ${order.deliveryAddress}"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (language) {
                                        AppLanguage.MARATHI -> "एकूण रक्कम: ₹${order.totalAmountInr}"
                                        AppLanguage.HINDI -> "कुल राशि: ₹${order.totalAmountInr}"
                                        AppLanguage.ENGLISH -> "Total Paid: ₹${order.totalAmountInr}"
                                    },
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = when (language) {
                                        AppLanguage.MARATHI -> "तुमची बचत: ₹${order.savedMiddlemanFeeInr}"
                                        AppLanguage.HINDI -> "आपकी बचत: ₹${order.savedMiddlemanFeeInr}"
                                        AppLanguage.ENGLISH -> "You Saved: ₹${order.savedMiddlemanFeeInr}"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
