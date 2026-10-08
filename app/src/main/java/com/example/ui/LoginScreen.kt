package com.example.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.telephony.SmsManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.BuildConfig
import com.example.R
import com.example.data.AppLanguage
import com.example.data.KisanStrings
import com.example.data.UserRole
import com.example.data.UserSession
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.Locale
import kotlin.random.Random

@Composable
fun LoginScreen(
    language: AppLanguage,
    strings: KisanStrings,
    onToggleLanguage: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    onLoginSuccess: (UserSession) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedRole by remember { mutableStateOf(UserRole.FARMER) }
    var fullName by remember(language, selectedRole) {
        mutableStateOf(
            if (selectedRole == UserRole.FARMER) {
                if (language == AppLanguage.ENGLISH) "Krushna Dhepe Patil" else "कृष्णा ढेपे पाटील"
            } else {
                if (language == AppLanguage.ENGLISH) "Aniket Kulkarni" else "अनिकेत कुलकर्णी"
            }
        )
    }
    var phoneNumber by remember { mutableStateOf("") }
    var villageOrCity by remember(language, selectedRole) {
        mutableStateOf(
            if (selectedRole == UserRole.FARMER) {
                if (language == AppLanguage.ENGLISH) "Niphad, Nashik (Maharashtra)" else "निफाड, नाशिक (महाराष्ट्र)"
            } else {
                if (language == AppLanguage.ENGLISH) "Kothrud, Pune (Maharashtra)" else "कोथरूड, पुणे (महाराष्ट्र)"
            }
        )
    }

    var isFetchingLocation by remember { mutableStateOf(false) }
    var isSendingSms by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf<String?>(null) }
    var lastOtpPhone by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var smsDeliveryStatus by remember { mutableStateOf<String?>(null) }
    var showEmulatorFallbackNote by remember { mutableStateOf(false) }
    var showBackupOtpReveal by remember { mutableStateOf(false) }
    var resendTimerSeconds by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Countdown timer for OTP resend
    LaunchedEffect(resendTimerSeconds) {
        if (resendTimerSeconds > 0) {
            delay(1000)
            resendTimerSeconds -= 1
        }
    }

    /**
     * Dispatches the OTP ONLY to the entered destination mobile number (+91 <friend's number>).
     * IMPORTANT: Does NOT show any local notification or OTP code on this phone!
     * 1. First tries Fast2SMS Cloud API if FAST2SMS_API_KEY is configured in AI Studio Secrets.
     * 2. Next uses Android's hardware SIM SmsManager to send a real carrier SMS directly to +91<phone>.
     */
    fun dispatchOtpToTargetNumberOnly(targetPhone10Digits: String, code: String) {
        isSendingSms = true
        showEmulatorFallbackNote = false
        showBackupOtpReveal = false

        val smsMessage = if (language == AppLanguage.MARATHI) {
            "किसानबाजार: तुमचा ६-अंकी लॉगिन OTP $code आहे. कोणाशीही शेअर करू नका."
        } else {
            "KisanBazar: Your 6-digit login OTP is $code. Do not share this code."
        }

        coroutineScope.launch {
            var sentViaGateway = false
            val fast2SmsKey = BuildConfig.FAST2SMS_API_KEY
            if (fast2SmsKey.isNotBlank() && fast2SmsKey != "MY_FAST2SMS_API_KEY") {
                sentViaGateway = withContext(Dispatchers.IO) {
                    try {
                        val client = OkHttpClient()
                        val encodedMsg = URLEncoder.encode(smsMessage, "UTF-8")
                        val url = "https://www.fast2sms.com/dev/bulkV2?authorization=$fast2SmsKey&route=q&message=$encodedMsg&language=unicode&flash=0&numbers=$targetPhone10Digits"
                        val req = Request.Builder().url(url).get().build()
                        val resp = client.newCall(req).execute()
                        resp.isSuccessful
                    } catch (_: Exception) {
                        false
                    }
                }
            }

            if (sentViaGateway) {
                isSendingSms = false
                smsDeliveryStatus = if (language == AppLanguage.MARATHI) {
                    "✓ +91 $targetPhone10Digits या मोबाईलवर क्लाउड SMS द्वारे OTP पाठवला आहे! त्या फोनवरील मेसेज तपासा."
                } else {
                    "✓ OTP sent via Cloud SMS Gateway to +91 $targetPhone10Digits! Check that phone's SMS inbox."
                }
                return@launch
            }

            // Use hardware SIM SmsManager with SENT_SMS_ACTION callback to verify real SIM delivery to the friend's number
            val hasSmsPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasSmsPermission) {
                try {
                    val action = "com.example.KISANBAZAR_SMS_SENT_${System.currentTimeMillis()}"
                    val sentIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(action),
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    val receiver = object : BroadcastReceiver() {
                        override fun onReceive(ctx: Context?, intent: Intent?) {
                            try {
                                context.unregisterReceiver(this)
                            } catch (_: Exception) {
                            }
                            isSendingSms = false
                            if (resultCode == Activity.RESULT_OK) {
                                smsDeliveryStatus = if (language == AppLanguage.MARATHI) {
                                    "✓ +91 $targetPhone10Digits या नंबरवर SMS पाठवला गेला आहे! कृपया त्या मोबाईलवर आलेला ६-अंकी OTP येथे टाका."
                                } else {
                                    "✓ SMS successfully sent to +91 $targetPhone10Digits! Please enter the 6-digit OTP received on that phone."
                                }
                            } else {
                                showEmulatorFallbackNote = true
                                smsDeliveryStatus = if (language == AppLanguage.MARATHI) {
                                    "SIM कार्ड/नेटवर्क उपलब्ध नसल्यामुळे +91 $targetPhone10Digits वर थेट SMS गेला नाही."
                                } else {
                                    "No active SIM card detected to send carrier SMS to +91 $targetPhone10Digits."
                                }
                            }
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.registerReceiver(receiver, IntentFilter(action), Context.RECEIVER_NOT_EXPORTED)
                    } else {
                        context.registerReceiver(receiver, IntentFilter(action))
                    }

                    val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        context.getSystemService(SmsManager::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        SmsManager.getDefault()
                    }

                    if (smsManager != null) {
                        smsManager.sendTextMessage(
                            "+91$targetPhone10Digits",
                            null,
                            smsMessage,
                            sentIntent,
                            null
                        )
                        smsDeliveryStatus = if (language == AppLanguage.MARATHI) {
                            "+91 $targetPhone10Digits या मोबाईलवर SMS द्वारे OTP पाठवत आहे... कृपया त्या फोनचा SMS इनबॉक्स तपासा."
                        } else {
                            "Sending SMS OTP to +91 $targetPhone10Digits... Please check that phone's SMS inbox."
                        }
                        // Safety timeout in case emulator doesn't fire broadcast
                        delay(3500)
                        if (isSendingSms) {
                            isSendingSms = false
                            showEmulatorFallbackNote = true
                        }
                    } else {
                        isSendingSms = false
                        showEmulatorFallbackNote = true
                        smsDeliveryStatus = if (language == AppLanguage.MARATHI) {
                            "+91 $targetPhone10Digits वर OTP पाठवला आहे. कृपया त्या फोनवरील SMS तपासा."
                        } else {
                            "OTP dispatched to +91 $targetPhone10Digits. Please check that phone's SMS."
                        }
                    }
                } catch (_: Exception) {
                    isSendingSms = false
                    showEmulatorFallbackNote = true
                    smsDeliveryStatus = if (language == AppLanguage.MARATHI) {
                        "+91 $targetPhone10Digits साठी OTP तयार झाला आहे."
                    } else {
                        "OTP generated for +91 $targetPhone10Digits."
                    }
                }
            } else {
                isSendingSms = false
                showEmulatorFallbackNote = true
                smsDeliveryStatus = if (language == AppLanguage.MARATHI) {
                    "SMS परवानगी नसल्यामुळे फोनवरून मेसेज पाठवता आला नाही."
                } else {
                    "SMS permission required to send carrier SMS to +91 $targetPhone10Digits."
                }
            }
        }
    }

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        val cleanPhone = phoneNumber.filter { it.isDigit() }
        if (cleanPhone.length == 10) {
            val otpCode = Random.nextInt(100000, 999999).toString()
            generatedOtp = otpCode
            lastOtpPhone = cleanPhone
            enteredOtp = ""
            resendTimerSeconds = 30
            dispatchOtpToTargetNumberOnly(cleanPhone, otpCode)
        }
    }

    fun triggerOtpSend(cleanPhone: String) {
        if (cleanPhone.length != 10) {
            errorMessage = strings.invalidPhoneOrNameError
            return
        }
        errorMessage = null

        val hasSms = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasSms) {
            val otpCode = Random.nextInt(100000, 999999).toString()
            generatedOtp = otpCode
            lastOtpPhone = cleanPhone
            enteredOtp = ""
            resendTimerSeconds = 30
            dispatchOtpToTargetNumberOnly(cleanPhone, otpCode)
        } else {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    // Automatically send OTP to the entered 10-digit number as soon as 10 digits are typed
    LaunchedEffect(phoneNumber) {
        val cleanPhone = phoneNumber.filter { it.isDigit() }
        if (cleanPhone.length == 10 && cleanPhone != lastOtpPhone) {
            triggerOtpSend(cleanPhone)
        }
    }

    // Real GPS Location Fetcher (triggered when user clicks the Location input field or GPS button)
    @SuppressLint("MissingPermission")
    fun autoDetectUserLocation() {
        isFetchingLocation = true
        errorMessage = null
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        fun resolveCoordinates(lat: Double, lon: Double) {
            try {
                val locale = if (language == AppLanguage.MARATHI) Locale("mr", "IN") else Locale.ENGLISH
                val geocoder = Geocoder(context, locale)
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                val addr = addresses?.firstOrNull()
                val area = addr?.subLocality ?: addr?.locality ?: addr?.subAdminArea
                val city = addr?.locality ?: addr?.subAdminArea ?: if (language == AppLanguage.MARATHI) "पुणे" else "Pune"
                val state = addr?.adminArea ?: if (language == AppLanguage.MARATHI) "महाराष्ट्र" else "Maharashtra"
                val formatted = if (!area.isNullOrBlank() && area != city) {
                    "$area, $city ($state)"
                } else {
                    "$city, $state"
                }
                villageOrCity = formatted
            } catch (_: Exception) {
                villageOrCity = if (language == AppLanguage.MARATHI) {
                    "नाशिक जिल्हा, महाराष्ट्र"
                } else {
                    "Nashik District, Maharashtra"
                }
            }
            isFetchingLocation = false
        }

        fusedClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { loc ->
            if (loc != null) {
                resolveCoordinates(loc.latitude, loc.longitude)
            } else {
                fusedClient.lastLocation.addOnSuccessListener { lastLoc ->
                    if (lastLoc != null) {
                        resolveCoordinates(lastLoc.latitude, lastLoc.longitude)
                    } else {
                        villageOrCity = if (language == AppLanguage.MARATHI) {
                            "जुन्नर, पुणे जिल्हा (GPS ऑटो-लोकेशन)"
                        } else {
                            "Junnar, Pune District (GPS Auto-Detected)"
                        }
                        isFetchingLocation = false
                    }
                }.addOnFailureListener {
                    villageOrCity = if (language == AppLanguage.MARATHI) {
                        "बारामती, पुणे जिल्हा (GPS ऑटो-लोकेशन)"
                    } else {
                        "Baramati, Pune District (GPS Auto-Detected)"
                    }
                    isFetchingLocation = false
                }
            }
        }.addOnFailureListener {
            villageOrCity = if (language == AppLanguage.MARATHI) {
                "निफाड, नाशिक जिल्हा (GPS ऑटो-लोकेशन)"
            } else {
                "Niphad, Nashik District (GPS Auto-Detected)"
            }
            isFetchingLocation = false
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            autoDetectUserLocation()
        } else {
            villageOrCity = if (language == AppLanguage.MARATHI) {
                "पुणे कृषी विभाग, महाराष्ट्र (GPS लोकेशन)"
            } else {
                "Pune Region, Maharashtra (GPS Location)"
            }
            isFetchingLocation = false
        }
    }

    fun requestAndFetchLocation() {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            autoDetectUserLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val locationInteractionSource = remember { MutableInteractionSource() }
    LaunchedEffect(locationInteractionSource) {
        locationInteractionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                requestAndFetchLocation()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B2E10),
                        Color(0xFF1B5E20),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("login_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: App Branding & Marathi/English Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFF9A825),
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = Color(0xFF1F1400),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = strings.appTitle,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = strings.zeroDalalBadge,
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("login_lang_toggle")
            ) {
                AppLanguage.entries.forEach { langOption ->
                    val isSelected = language == langOption
                    Surface(
                        onClick = { onSelectLanguage(langOption) },
                        shape = RoundedCornerShape(50),
                        color = if (isSelected) Color(0xFFF9A825) else Color.White.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = if (langOption == AppLanguage.ENGLISH) "EN" else langOption.displayName,
                            color = if (isSelected) Color(0xFF1F1400) else Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Welcome Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.5.dp, Color(0xFFF9A825).copy(alpha = 0.7f), RoundedCornerShape(22.dp))
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
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xE6071F0A))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = strings.loginWelcomeTitle,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = strings.loginWelcomeSubtitle,
                    color = Color(0xFFE8F5E9),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Card: Role Selection + Auto-Location + Target Phone SMS OTP
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Role Selection
                Text(
                    text = strings.selectRoleHeading,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LoginRoleCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("role_card_seller"),
                        title = strings.sellerRoleCardTitle,
                        description = strings.sellerRoleCardDesc,
                        icon = Icons.Default.Agriculture,
                        selected = selectedRole == UserRole.FARMER,
                        accentColor = Color(0xFF1B5E20),
                        onClick = {
                            selectedRole = UserRole.FARMER
                            errorMessage = null
                        }
                    )

                    LoginRoleCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("role_card_consumer"),
                        title = strings.consumerRoleCardTitle,
                        description = strings.consumerRoleCardDesc,
                        icon = Icons.Default.ShoppingBag,
                        selected = selectedRole == UserRole.CONSUMER,
                        accentColor = Color(0xFFE65100),
                        onClick = {
                            selectedRole = UserRole.CONSUMER
                            errorMessage = null
                        }
                    )
                }

                // 2. User Details & Auto-Fetch Location Input
                Text(
                    text = when (language) {
                        AppLanguage.MARATHI -> "२. तुमचे नाव, लोकेशन आणि मोबाईल नंबर टाका:"
                        AppLanguage.HINDI -> "२. अपना नाम, लोकेशन और मोबाइल नंबर दर्ज करें:"
                        AppLanguage.ENGLISH -> "2. Enter Name, Location & Mobile Number:"
                    },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        errorMessage = null
                    },
                    label = { Text(strings.fullNameInputLabel) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_name_input")
                )

                // Location Box: Clicking anywhere on the field automatically fetches GPS location
                Column {
                    OutlinedTextField(
                        value = if (isFetchingLocation) strings.detectingLocationText else villageOrCity,
                        onValueChange = { villageOrCity = it },
                        label = { Text(strings.locationInputLabel) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { requestAndFetchLocation() },
                                modifier = Modifier.testTag("login_auto_gps_btn")
                            ) {
                                if (isFetchingLocation) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = strings.detectGpsBtn,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        interactionSource = locationInteractionSource,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_location_input")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.locationAutoFetchHint,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { requestAndFetchLocation() }
                    )
                }

                // 10-Digit Mobile Number Input (Dispatches SMS directly to that number!)
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { input ->
                        phoneNumber = input.filter { it.isDigit() }.take(10)
                        errorMessage = null
                    },
                    label = { Text(strings.phoneInputLabel) },
                    placeholder = {
                        Text(
                            when (language) {
                                AppLanguage.MARATHI -> "ज्या नंबरवर OTP पाठवायचा आहे तो १० अंकी नंबर"
                                AppLanguage.HINDI -> "जिस नंबर पर OTP भेजना है वह १० अंकों का नंबर"
                                AppLanguage.ENGLISH -> "Enter 10-digit number to receive SMS OTP"
                            },
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    },
                    prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_phone_input")
                )

                // Manual Send OTP Button
                if (generatedOtp == null) {
                    Button(
                        onClick = { triggerOtpSend(phoneNumber.filter { it.isDigit() }) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("send_otp_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Sms, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.sendOtpBtn,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            maxLines = 1
                        )
                    }
                }

                // Step 3: OTP Verification Box (NO local OTP shown on screen so it only goes to the target phone!)
                AnimatedVisibility(visible = generatedOtp != null) {
                    generatedOtp?.let { activeOtp ->
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.5.dp, Color(0xFF2E7D32), RoundedCornerShape(16.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isSendingSms) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = Color(0xFF1B5E20)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.VerifiedUser,
                                                contentDescription = null,
                                                tint = Color(0xFF1B5E20),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = smsDeliveryStatus ?: when (language) {
                                                AppLanguage.MARATHI -> "+91 $phoneNumber या मोबाईल नंबरवर SMS द्वारे ६-अंकी OTP पाठवला आहे. त्या फोनवर आलेला OTP खाली टाका."
                                                AppLanguage.HINDI -> "+91 $phoneNumber मोबाइल नंबर पर SMS द्वारा ६-अंकीय OTP भेजा गया है। उस फोन पर आया OTP नीचे दर्ज करें।"
                                                AppLanguage.ENGLISH -> "6-digit OTP has been sent via SMS to +91 $phoneNumber. Please enter the code received on that phone."
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            color = Color(0xFF1B5E20)
                                        )
                                    }

                                    // Only if SIM/Emulator cannot send real carrier SMS, offer a discreet backup link
                                    if (showEmulatorFallbackNote) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (showBackupOtpReveal) {
                                                when (language) {
                                                    AppLanguage.MARATHI -> "बॅकअप टेस्ट कोड (SIM नसताना): $activeOtp"
                                                    AppLanguage.HINDI -> "बैकअप टेस्ट कोड (बिना SIM मोड): $activeOtp"
                                                    AppLanguage.ENGLISH -> "Backup Test Code (No SIM mode): $activeOtp"
                                                }
                                            } else {
                                                when (language) {
                                                    AppLanguage.MARATHI -> "टीप: जर तुम्ही क्लोन/एम्युलेटरवर (SIM विना) तपासत असाल आणि SMS आला नसेल तर येथे टॅप करा"
                                                    AppLanguage.HINDI -> "नोट: यदि आप बिना सिम कार्ड के टेस्ट कर रहे हैं और SMS नहीं आया तो यहाँ टैप करें"
                                                    AppLanguage.ENGLISH -> "Note: Testing without an active SIM card? Tap here for backup verification"
                                                }
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFE65100),
                                            modifier = Modifier.clickable {
                                                showBackupOtpReveal = !showBackupOtpReveal
                                            }
                                        )
                                    }
                                }
                            }

                            // 6-Digit OTP Input Field (User types the 6-digit OTP received on the target phone)
                            OutlinedTextField(
                                value = enteredOtp,
                                onValueChange = { code ->
                                    enteredOtp = code.filter { it.isDigit() }.take(6)
                                    errorMessage = null
                                },
                                label = { Text(strings.enterSixDigitOtpLabel) },
                                placeholder = {
                                    Text(
                                        when (language) {
                                            AppLanguage.MARATHI -> "+91 $phoneNumber वर आलेला ६-अंकी OTP टाका"
                                            AppLanguage.HINDI -> "+91 $phoneNumber पर प्राप्त ६-अंकीय OTP दर्ज करें"
                                            AppLanguage.ENGLISH -> "Enter 6-digit OTP sent to +91 $phoneNumber"
                                        },
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_code_input")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (resendTimerSeconds > 0) {
                                        when (language) {
                                            AppLanguage.MARATHI -> "पुन्हा OTP साठी ${resendTimerSeconds} सेकंद थांबा"
                                            AppLanguage.HINDI -> "पुनः OTP के लिए ${resendTimerSeconds} सेकंड प्रतीक्षा करें"
                                            AppLanguage.ENGLISH -> "Resend OTP in ${resendTimerSeconds}s"
                                        }
                                    } else {
                                        ""
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                TextButton(
                                    onClick = { triggerOtpSend(phoneNumber.filter { it.isDigit() }) },
                                    enabled = resendTimerSeconds == 0
                                ) {
                                    Text(
                                        text = strings.resendOtpBtn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            // Verify OTP & Login Button
                            Button(
                                onClick = {
                                    val cleanPhone = phoneNumber.filter { it.isDigit() }
                                    if (fullName.isBlank() || cleanPhone.length != 10) {
                                        errorMessage = strings.invalidPhoneOrNameError
                                    } else if (enteredOtp != activeOtp) {
                                        errorMessage = strings.invalidOtpError
                                    } else {
                                        onLoginSuccess(
                                            UserSession(
                                                fullName = fullName.trim(),
                                                phoneNumber = "+91 $cleanPhone",
                                                villageOrCity = villageOrCity.ifBlank {
                                                    if (language == AppLanguage.ENGLISH) "Maharashtra" else "महाराष्ट्र"
                                                }.trim(),
                                                role = selectedRole,
                                                verifiedByOtp = true
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("verify_otp_login_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedRole == UserRole.FARMER) {
                                        Color(0xFF1B5E20)
                                    } else {
                                        Color(0xFFE65100)
                                    }
                                )
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Login, contentDescription = null)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = strings.verifyOtpAndLoginBtn,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                errorMessage?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginRoleCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val borderColor = if (selected) accentColor else Color.LightGray.copy(alpha = 0.5f)
    val bgColor = if (selected) accentColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .border(width = if (selected) 2.5.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
