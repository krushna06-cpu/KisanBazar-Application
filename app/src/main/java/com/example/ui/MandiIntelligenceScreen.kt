package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AiPriceRecommendationResult
import com.example.data.AppLanguage
import com.example.data.KisanStrings
import com.example.data.LiveMarketRateEntity
import kotlin.math.abs
import kotlin.math.roundToInt

private data class SearchedMandiResult(
    val cropDisplayName: String,
    val marketDisplayName: String,
    val minPriceQtl: Int,
    val modalPriceQtl: Int,
    val maxPriceQtl: Int,
    val aiDirectPriceQtl: Int,
    val extraFarmerProfitQtl: Int
)

@Composable
fun MandiIntelligenceScreen(
    language: AppLanguage,
    strings: KisanStrings,
    marketRates: List<LiveMarketRateEntity>,
    aiRecommendation: AiPriceRecommendationResult?,
    isCalculatingAi: Boolean,
    onRefreshRates: () -> Unit,
    onRunAiAdvisor: (String, Int, String, Double, Boolean, String, Double) -> Unit
) {
    // Popular quick-select chips for Crops and Markets (user can also type ANY crop or village/market!)
    val cropPresets = remember(language) {
        when (language) {
            AppLanguage.MARATHI -> listOf("कांदा", "सोयाबीन", "तूर डाळ", "गहू", "डाळिंब", "टोमॅटो", "तांदूळ", "आंबा", "कापूस", "हरभरा")
            AppLanguage.HINDI -> listOf("प्याज", "सोयाबीन", "तूर दाल", "गेहूं", "अनार", "टमाटर", "चावल", "आम", "कपास", "चना")
            AppLanguage.ENGLISH -> listOf("Onion", "Soybean", "Tur Dal", "Wheat", "Pomegranate", "Tomato", "Rice", "Mango", "Cotton", "Chana")
        }
    }

    val marketPresets = remember(language) {
        when (language) {
            AppLanguage.MARATHI -> listOf("लासलगाव", "पुणे", "नाशिक", "लातूर", "अकोला", "सोलापूर", "कोल्हापूर", "नागपूर", "छ. संभाजीनगर", "अमरावती", "सांगली", "बारामती")
            AppLanguage.HINDI -> listOf("लासलगांव", "पुणे", "नाशिक", "लातूर", "अकोला", "सोलापुर", "कोल्हापुर", "नागपुर", "छ. संभाजीनगर", "अमरावती", "सांगली", "बारामती")
            AppLanguage.ENGLISH -> listOf("Lasalgaon", "Pune", "Nashik", "Latur", "Akola", "Solapur", "Kolhapur", "Nagpur", "Chh. Sambhajinagar", "Amravati", "Sangli", "Baramati")
        }
    }

    var cropInput by remember { mutableStateOf("") }
    var marketInput by remember { mutableStateOf("") }
    var isGoodQuality by remember { mutableStateOf(true) }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    // Holds the single searched result; NULL initially so all villages' prices are NOT dumped on screen!
    var searchedResult by remember { mutableStateOf<com.example.data.MandiRateSearchBackendJava.SearchedMandiResult?>(null) }

    fun computeRateForCropAndMarket(cropQuery: String, marketQuery: String, bestQuality: Boolean) {
        val result = com.example.data.MandiRateSearchBackendJava.searchMandiRate(
            cropQuery,
            marketQuery,
            bestQuality,
            marketRates,
            language
        )

        if (result == null) {
            validationMessage = when (language) {
                AppLanguage.MARATHI -> "कृपया पिकाचे नाव आणि गाव / मार्केटचे नाव निवडा किंवा टाइप करा."
                AppLanguage.HINDI -> "कृपया फसल का नाम और गाँव / मंडी का नाम चुनें या टाइप करें।"
                AppLanguage.ENGLISH -> "Please enter or select both the Crop Name and Village / Market Name."
            }
            return
        }
        validationMessage = null
        searchedResult = result

        onRunAiAdvisor(
            result.cropDisplayName,
            result.modalPriceQtl,
            if (bestQuality) "Grade A+ Export" else "Grade A",
            10.5,
            bestQuality,
            result.marketDisplayName,
            4.5
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .testTag("mandi_intelligence_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. SEARCH INPUT CARD: Ask User Which Crop & Which Village/Market They Want
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFF57F17),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "ताजे बाजारभाव (मंडी भाव) शोधा"
                                AppLanguage.HINDI -> "ताज़ा मंडी भाव (बाज़ार भाव) खोजें"
                                AppLanguage.ENGLISH -> "Check Live Market (Mandi) Rates"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = when (language) {
                                AppLanguage.MARATHI -> "तुम्हाला कोणत्या पिकाचा आणि कोणत्या गावाच्या मार्केटचा भाव हवा आहे ते निवडा"
                                AppLanguage.HINDI -> "जिस फसल और गाँव/मंडी का भाव जानना है उसे चुनें या टाइप करें"
                                AppLanguage.ENGLISH -> "Enter or select the crop and village/city market you want to check"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // STEP 1: CROP INPUT + QUICK CHIPS
                Text(
                    text = when (language) {
                        AppLanguage.MARATHI -> "१. कशाचा भाव हवा आहे? (पिकाचे नाव):"
                        AppLanguage.HINDI -> "१. किस फसल का भाव चाहिए? (फसल का नाम):"
                        AppLanguage.ENGLISH -> "1. Which Crop Rate Do You Want?"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = cropInput,
                    onValueChange = {
                        cropInput = it
                        validationMessage = null
                    },
                    placeholder = {
                        Text(
                            when (language) {
                                AppLanguage.MARATHI -> "उदा. कांदा, सोयाबीन, तूर डाळ, कापूस..."
                                AppLanguage.HINDI -> "उदा. प्याज, सोयाबीन, तूर दाल, कपास..."
                                AppLanguage.ENGLISH -> "e.g. Onion, Soybean, Tur Dal, Wheat..."
                            },
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mandi_crop_input")
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cropPresets.forEach { crop ->
                        val isSelected = cropInput.equals(crop, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                cropInput = crop
                                validationMessage = null
                                if (marketInput.isNotBlank()) {
                                    computeRateForCropAndMarket(crop, marketInput, isGoodQuality)
                                }
                            },
                            label = {
                                Text(
                                    text = crop,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                // STEP 2: VILLAGE / MARKET INPUT + QUICK CHIPS
                Text(
                    text = when (language) {
                        AppLanguage.MARATHI -> "२. कोणत्या गावाच्या / शहराच्या मार्केटचा भाव हवा आहे?"
                        AppLanguage.HINDI -> "२. किस गाँव / शहर की मंडी का भाव चाहिए?"
                        AppLanguage.ENGLISH -> "2. Which Village / City Market?"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = marketInput,
                    onValueChange = {
                        marketInput = it
                        validationMessage = null
                    },
                    placeholder = {
                        Text(
                            when (language) {
                                AppLanguage.MARATHI -> "उदा. लासलगाव, पुणे, नाशिक, लातूर, अकोला..."
                                AppLanguage.HINDI -> "उदा. लासलगांव, पुणे, नाशिक, लातूर, अकोला..."
                                AppLanguage.ENGLISH -> "e.g. Lasalgaon, Pune, Nashik, Latur, Akola..."
                            },
                            fontSize = 13.sp,
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
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mandi_location_input")
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    marketPresets.forEach { mkt ->
                        val isSelected = marketInput.equals(mkt, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                marketInput = mkt
                                validationMessage = null
                                if (cropInput.isNotBlank()) {
                                    computeRateForCropAndMarket(cropInput, mkt, isGoodQuality)
                                }
                            },
                            label = {
                                Text(
                                    text = mkt,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                // STEP 3: Simple 2-Choice Quality Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = isGoodQuality,
                        onClick = {
                            isGoodQuality = true
                            if (cropInput.isNotBlank() && marketInput.isNotBlank()) {
                                computeRateForCropAndMarket(cropInput, marketInput, true)
                            }
                        },
                        leadingIcon = if (isGoodQuality) {
                            { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        label = {
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "उत्तम प्रत (चांगला माल)"
                                    AppLanguage.HINDI -> "उत्तम ग्रेड (अच्छी उपज)"
                                    AppLanguage.ENGLISH -> "Best Quality"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = !isGoodQuality,
                        onClick = {
                            isGoodQuality = false
                            if (cropInput.isNotBlank() && marketInput.isNotBlank()) {
                                computeRateForCropAndMarket(cropInput, marketInput, false)
                            }
                        },
                        leadingIcon = if (!isGoodQuality) {
                            { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        label = {
                            Text(
                                text = when (language) {
                                    AppLanguage.MARATHI -> "साधारण प्रत"
                                    AppLanguage.HINDI -> "सामान्य ग्रेड"
                                    AppLanguage.ENGLISH -> "Standard Quality"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                validationMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // SEARCH RATE BUTTON
                Button(
                    onClick = {
                        computeRateForCropAndMarket(cropInput, marketInput, isGoodQuality)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("check_mandi_rate_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (language) {
                            AppLanguage.MARATHI -> "ताजा बाजारभाव पहा"
                            AppLanguage.HINDI -> "ताज़ा मंडी भाव देखें"
                            AppLanguage.ENGLISH -> "Show Live Market Rate"
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                }
            }
        }

        // 2. RESULT AREA: ONLY shows the selected Crop + selected Village/Market rate!
        AnimatedVisibility(visible = searchedResult != null) {
            searchedResult?.let { res ->
                val modalPerKg = (res.modalPriceQtl / 100.0).roundToInt()
                val aiPerKg = (res.aiDirectPriceQtl / 100.0).roundToInt()

                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("searched_mandi_result_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF0E3A14), Color(0xFF1B5E20))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Selected Crop & Selected Market Title
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = Color(0xFFF9A825),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = when (language) {
                                                AppLanguage.MARATHI -> "थेट बाजारभाव निकाल"
                                                AppLanguage.HINDI -> "ताज़ा मंडी भाव परिणाम"
                                                AppLanguage.ENGLISH -> "LIVE MARKET RESULT"
                                            },
                                            color = Color(0xFF1F1400),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = res.cropDisplayName,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD54F),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = res.marketDisplayName,
                                            color = Color(0xFFE8F5E9),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            // Min / Average (Modal) / Max Rate Strip for the Selected Market
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                RateStatColumn(
                                    label = when (language) {
                                        AppLanguage.MARATHI -> "किमान भाव"
                                        AppLanguage.HINDI -> "न्यूनतम भाव"
                                        AppLanguage.ENGLISH -> "Min Rate"
                                    },
                                    value = "₹${res.minPriceQtl}"
                                )
                                RateStatColumn(
                                    label = when (language) {
                                        AppLanguage.MARATHI -> "सरासरी मंडी भाव"
                                        AppLanguage.HINDI -> "औसत मंडी भाव"
                                        AppLanguage.ENGLISH -> "Mandi Modal"
                                    },
                                    value = "₹${res.modalPriceQtl}",
                                    subValue = if (language == AppLanguage.ENGLISH) "(₹$modalPerKg/kg)" else "(₹$modalPerKg/किलो)",
                                    highlight = true
                                )
                                RateStatColumn(
                                    label = when (language) {
                                        AppLanguage.MARATHI -> "कमाल भाव"
                                        AppLanguage.HINDI -> "अधिकतम भाव"
                                        AppLanguage.ENGLISH -> "Max Rate"
                                    },
                                    value = "₹${res.maxPriceQtl}"
                                )
                            }

                            // Clean Direct Selling Rate Box (without AI buzzwords)
                            Surface(
                                color = Color(0xFFFFF8E1),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.5.dp, Color(0xFFF9A825), RoundedCornerShape(14.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (language) {
                                            AppLanguage.MARATHI -> "थेट ग्राहक विक्रीसाठी योग्य दर (०% दलाली)"
                                            AppLanguage.HINDI -> "सीधे ग्राहक बिक्री के लिए उचित दर (०% दलाली)"
                                            AppLanguage.ENGLISH -> "Recommended Direct Selling Rate (0% Dalal)"
                                        },
                                        color = Color(0xFF5D4037),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (language == AppLanguage.ENGLISH) {
                                            "₹${res.aiDirectPriceQtl} / Quintal  (₹$aiPerKg / kg)"
                                        } else {
                                            "₹${res.aiDirectPriceQtl} / क्विंटल  (₹$aiPerKg / किलो)"
                                        },
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 19.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Empty State Prompt when user hasn't searched yet
        if (searchedResult == null) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (language) {
                            AppLanguage.MARATHI -> "वर तुमचे पीक आणि गाव/मार्केट निवडून 'ताजा बाजारभाव पहा' बटनावर क्लिक करा."
                            AppLanguage.HINDI -> "ऊपर अपनी फसल और गाँव/मंडी चुनकर 'ताज़ा मंडी भाव देखें' बटन पर क्लिक करें।"
                            AppLanguage.ENGLISH -> "Select your Crop and Village/Market above and tap 'Show Live Market Rate'."
                        },
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RateStatColumn(
    label: String,
    value: String,
    subValue: String? = null,
    highlight: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color(0xFFC8E6C9),
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = if (highlight) Color(0xFFFFD54F) else Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (highlight) 18.sp else 15.sp
        )
        if (subValue != null) {
            Text(
                text = subValue,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
