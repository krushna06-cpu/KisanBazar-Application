package com.example.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.data.AiPriceEngine
import com.example.data.AppLanguage
import com.example.data.KisanStrings
import com.example.data.UserSession
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale
import kotlin.math.roundToInt

data class PresetCropOption(
    val nameEn: String,
    val nameMr: String,
    val nameHi: String,
    val category: String,
    val defaultMandiEn: String,
    val defaultMandiMr: String,
    val apmcModalQtl: Int
)

@Composable
fun AddCropListingScreen(
    userSession: UserSession,
    language: AppLanguage,
    strings: KisanStrings,
    onPublishListing: (
        cropNameEn: String,
        cropNameMr: String,
        category: String,
        farmerName: String,
        farmerPhone: String,
        villageAndMandi: String,
        quantityQuintals: Double,
        askingPricePerQuintal: Int,
        apmcModalPricePerQuintal: Int,
        qualityGrade: String,
        moisturePercent: Double,
        isOrganic: Boolean,
        imageUri: String?
    ) -> Unit,
    onToastMessage: (String) -> Unit
) {
    val context = LocalContext.current

    val presetCrops = remember {
        listOf(
            PresetCropOption(
                "Lasalgaon Red Onion",
                "लासलगाव लाल कांदा",
                "लासलगांव लाल प्याज",
                "Vegetables",
                "Niphad, Nashik (Lasalgaon APMC)",
                "निफाड, नाशिक (लासलगाव बाजार समिती)",
                2380
            ),
            PresetCropOption(
                "Yellow Soybean",
                "पिवळा सोयाबीन",
                "पीला सोयाबीन",
                "Grains & Pulses",
                "Ausa, Latur APMC",
                "औसा, लातूर बाजार समिती",
                4740
            ),
            PresetCropOption(
                "Unpolished Tur Dal",
                "गावरान तूर डाळ",
                "देसी तूर दाल",
                "Grains & Pulses",
                "Murtizapur, Akola APMC",
                "मुर्तिजापूर, अकोला बाजार समिती",
                9850
            ),
            PresetCropOption(
                "Bhagwa Pomegranate",
                "भगवा डाळिंब",
                "भगवा अनार",
                "Fruits",
                "Sangola, Solapur APMC",
                "सांगोला, सोलापूर बाजार समिती",
                9400
            ),
            PresetCropOption(
                "Lokwan Golden Wheat",
                "लोकवन प्रीमियम गहू",
                "लोकवन प्रीमियम गेहूं",
                "Grains & Pulses",
                "Baramati, Pune APMC",
                "बारामती, पुणे बाजार समिती",
                2940
            )
        )
    }

    var selectedPresetIdx by remember { mutableIntStateOf(0) }
    var cropNameInput by remember(language, selectedPresetIdx) {
        mutableStateOf(
            when (language) {
                AppLanguage.MARATHI -> presetCrops[selectedPresetIdx].nameMr
                AppLanguage.HINDI -> presetCrops[selectedPresetIdx].nameHi
                AppLanguage.ENGLISH -> presetCrops[selectedPresetIdx].nameEn
            }
        )
    }
    var category by remember { mutableStateOf(presetCrops[0].category) }
    var locationText by remember(userSession.villageOrCity) { mutableStateOf(userSession.villageOrCity) }
    var quantityQtlText by remember { mutableStateOf("25") }
    var apmcModalPrice by remember { mutableIntStateOf(presetCrops[0].apmcModalQtl) }
    var isOrganic by remember { mutableStateOf(true) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val aiPreview = remember(cropNameInput, apmcModalPrice, isOrganic) {
        AiPriceEngine.calculatePriceRecommendation(
            cropName = cropNameInput,
            apmcModalPrice = apmcModalPrice,
            qualityGrade = "Grade A+ Export",
            moisturePercent = 10.5,
            isOrganic = isOrganic,
            arrivalTrendPercent = 5.0
        )
    }

    var askingPriceText by remember(aiPreview.recommendedDirectPrice) {
        mutableStateOf(aiPreview.recommendedDirectPrice.toString())
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val permanentUri = com.example.data.ImageStorageHelper.copyUriToPermanentStorage(context, uri)
            selectedImageUri = permanentUri ?: uri.toString()
            onToastMessage(
                when (language) {
                    AppLanguage.MARATHI -> "पिकाचा फोटो कायमस्वरूपी जोडला!"
                    AppLanguage.HINDI -> "फसल की फ़ोटो स्थायी रूप से सेव हो गई!"
                    AppLanguage.ENGLISH -> "Harvest photo saved permanently!"
                }
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchGpsLocation() {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        fusedClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { loc ->
            if (loc != null) {
                try {
                    val locale = when (language) {
                        AppLanguage.MARATHI -> Locale("mr", "IN")
                        AppLanguage.HINDI -> Locale("hi", "IN")
                        AppLanguage.ENGLISH -> Locale.ENGLISH
                    }
                    val geocoder = Geocoder(context, locale)
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                    val addr = addresses?.firstOrNull()
                    val locality = addr?.locality ?: addr?.subAdminArea ?: if (language == AppLanguage.ENGLISH) "Nashik" else "नाशिक"
                    val state = addr?.adminArea ?: if (language == AppLanguage.ENGLISH) "Maharashtra" else "महाराष्ट्र"
                    locationText = "$locality, $state"
                    onToastMessage(
                        when (language) {
                            AppLanguage.MARATHI -> "GPS लोकेशन जोडले: $locality"
                            AppLanguage.HINDI -> "GPS लोकेशन जोड़ा गया: $locality"
                            AppLanguage.ENGLISH -> "GPS Location detected: $locality"
                        }
                    )
                } catch (_: Exception) {
                    locationText = if (language == AppLanguage.ENGLISH) "Pune District, Maharashtra" else "पुणे जिल्हा, महाराष्ट्र"
                }
            } else {
                locationText = if (language == AppLanguage.ENGLISH) "Junnar, Pune District (GPS)" else "जुन्नर, पुणे जिल्हा (GPS)"
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchGpsLocation()
        } else {
            locationText = if (language == AppLanguage.ENGLISH) "Baramati, Pune District" else "बारामती, पुणे जिल्हा"
        }
    }

    fun triggerLocationFetch() {
        val hasPerm = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPerm) {
            fetchGpsLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    val locationFieldInteraction = remember { MutableInteractionSource() }
    LaunchedEffect(locationFieldInteraction) {
        locationFieldInteraction.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                triggerLocationFetch()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .testTag("add_crop_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = strings.addListingTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        // 1. Quick Crop Selection Chips
        Text(
            text = when (language) {
                AppLanguage.MARATHI -> "१. पीक निवडा:"
                AppLanguage.HINDI -> "१. फसल चुनें:"
                AppLanguage.ENGLISH -> "1. Select Crop:"
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presetCrops.forEachIndexed { idx, preset ->
                val label = when (language) {
                    AppLanguage.MARATHI -> preset.nameMr
                    AppLanguage.HINDI -> preset.nameHi
                    AppLanguage.ENGLISH -> preset.nameEn
                }
                FilterChip(
                    selected = selectedPresetIdx == idx,
                    onClick = {
                        selectedPresetIdx = idx
                        cropNameInput = label
                        category = preset.category
                        locationText = if (language == AppLanguage.ENGLISH) preset.defaultMandiEn else preset.defaultMandiMr
                        apmcModalPrice = preset.apmcModalQtl
                    },
                    label = { Text(label, fontSize = 12.sp) }
                )
            }
        }

        OutlinedTextField(
            value = cropNameInput,
            onValueChange = { cropNameInput = it },
            label = { Text(strings.cropNameLabel) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_crop_name")
        )

        // 2. Simple Quantity & Organic Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = quantityQtlText,
                onValueChange = { quantityQtlText = it },
                label = { Text(strings.quantityQuintalLabel) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_quantity")
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(strings.organicOnly, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Spacer(modifier = Modifier.width(6.dp))
                Switch(checked = isOrganic, onCheckedChange = { isOrganic = it })
            }
        }

        // 3. Auto-GPS Location Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = locationText,
                onValueChange = { locationText = it },
                label = { Text(strings.districtMandiLabel) },
                interactionSource = locationFieldInteraction,
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_location")
            )
            OutlinedButton(
                onClick = { triggerLocationFetch() },
                modifier = Modifier
                    .height(54.dp)
                    .testTag("detect_gps_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = strings.detectGpsBtn,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("GPS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // 4. Photo Upload Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                .testTag("pick_crop_photo_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            )
        ) {
            if (!selectedImageUri.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    AsyncImage(
                        model = com.example.data.ImageStorageHelper.resolveImageModel(selectedImageUri!!),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = Color(0xCC1B5E20),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "फोटो बदलण्यासाठी टॅप करा"
                                    AppLanguage.HINDI -> "फ़ोटो बदलने के लिए टैप करें"
                                    AppLanguage.ENGLISH -> "Tap to Change Photo"
                                },
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.pickCropPhotoBtn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        OutlinedTextField(
            value = askingPriceText,
            onValueChange = { askingPriceText = it },
            label = {
                Text(
                    when (language) {
                        AppLanguage.MARATHI -> "तुमचा थेट विक्री भाव (₹ प्रति क्विंटल)"
                        AppLanguage.HINDI -> "आपका सीधा बिक्री भाव (₹ प्रति क्विंटल)"
                        AppLanguage.ENGLISH -> "Your Selling Price (₹ per Quintal)"
                    }
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_asking_price")
        )

        Button(
            onClick = {
                val qty = quantityQtlText.toDoubleOrNull() ?: 20.0
                val askPrice = askingPriceText.toIntOrNull() ?: aiPreview.recommendedDirectPrice
                val preset = presetCrops.getOrNull(selectedPresetIdx)
                val finalEn = if (language == AppLanguage.ENGLISH) cropNameInput else (preset?.nameEn ?: cropNameInput)
                val finalMr = if (language != AppLanguage.ENGLISH) cropNameInput else (preset?.nameMr ?: cropNameInput)

                onPublishListing(
                    finalEn.ifBlank { "Fresh Farm Produce" },
                    finalMr.ifBlank { "ताजा शेतमाल" },
                    category,
                    userSession.fullName,
                    userSession.phoneNumber,
                    locationText.ifBlank { userSession.villageOrCity },
                    qty,
                    askPrice,
                    apmcModalPrice,
                    "Grade A+ Export",
                    10.5,
                    isOrganic,
                    selectedImageUri
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("publish_crop_listing_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(imageVector = Icons.Default.Publish, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = strings.publishListingBtn,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
