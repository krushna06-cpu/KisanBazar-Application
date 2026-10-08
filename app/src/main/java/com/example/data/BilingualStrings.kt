package com.example.data

enum class AppLanguage(val code: String, val displayName: String) {
    MARATHI("mr", "मराठी"),
    HINDI("hi", "हिंदी"),
    ENGLISH("en", "English")
}

enum class UserRole {
    FARMER,   // Crop Seller (शेतकरी / विक्रेता)
    CONSUMER  // Direct Buyer / Consumer (ग्राहक / खरेदीदार)
}

data class UserSession(
    val fullName: String,
    val phoneNumber: String,
    val villageOrCity: String,
    val role: UserRole,
    val verifiedByOtp: Boolean = true
)

data class KisanStrings(
    val appTitle: String,
    val appSubtitle: String,
    val farmerRole: String,
    val consumerRole: String,
    val switchRoleTip: String,
    val navMarket: String,
    val navMandiAi: String,
    val navAddCrop: String,
    val directFarmFeed: String,
    val zeroDalalBadge: String,
    val aiRecommendedTag: String,
    val apmcModalLabel: String,
    val farmerAskPrice: String,
    val farmerEarningsGain: String,
    val consumerSavings: String,
    val buyDirectBtn: String,
    val callFarmerBtn: String,
    val addListingTitle: String,
    val cropNameLabel: String,
    val categoryLabel: String,
    val quantityQuintalLabel: String,
    val qualityGradeLabel: String,
    val moisturePercentLabel: String,
    val districtMandiLabel: String,
    val detectGpsBtn: String,
    val pickCropPhotoBtn: String,
    val runAiPriceAdvisorBtn: String,
    val publishListingBtn: String,
    val aiAdvisorTitle: String,
    val liveApmcHeader: String,
    val searchCropHint: String,
    val allCategories: String,
    val organicOnly: String,
    val orderPlacedSuccess: String,
    val listingPublishedSuccess: String,
    // Login, OTP Security & Auto-Location Strings
    val loginWelcomeTitle: String,
    val loginWelcomeSubtitle: String,
    val selectRoleHeading: String,
    val sellerRoleCardTitle: String,
    val sellerRoleCardDesc: String,
    val consumerRoleCardTitle: String,
    val consumerRoleCardDesc: String,
    val fullNameInputLabel: String,
    val phoneInputLabel: String,
    val locationInputLabel: String,
    val locationAutoFetchHint: String,
    val detectingLocationText: String,
    val sendOtpBtn: String,
    val resendOtpBtn: String,
    val enterSixDigitOtpLabel: String,
    val verifyOtpAndLoginBtn: String,
    val otpSentBannerTitle: String,
    val invalidOtpError: String,
    val invalidPhoneOrNameError: String,
    // Profile Screen & Sheet
    val profileLabel: String,
    val profileTitle: String,
    val verifiedMobileBadge: String,
    val editProfileBtn: String,
    val saveProfileBtn: String,
    val switchRoleBtn: String,
    val logoutBtn: String,
    // Seller Portal Specific
    val sellerPortalBadge: String,
    val sellerNavMyCrops: String,
    val sellerNavAddCrop: String,
    val sellerNavMandiAi: String,
    val sellerNavOrders: String,
    val sellerDashboardTitle: String,
    val sellerTotalListingsLabel: String,
    val sellerBuyerOrdersLabel: String,
    val sellerEstRevenueLabel: String,
    val deleteListingBtn: String,
    val noOrdersYetSeller: String,
    val liveForBuyersBadge: String,
    val aiFairLabel: String,
    val apmcShortLabel: String,
    val qtlUnit: String,
    val kgUnit: String,
    // Consumer Portal Specific
    val consumerPortalBadge: String,
    val consumerNavBuyCrops: String,
    val consumerNavMyOrders: String,
    val consumerNavMandiRates: String,
    val consumerDashboardTitle: String,
    val noOrdersYetConsumer: String,
    val directGainText: String,
    val confirmedOrderStatus: String
)

object LocalizationProvider {
    val english = KisanStrings(
        appTitle = "KisanBazar",
        appSubtitle = "Zero-Middleman Farm Marketplace & Live APMC Rates",
        farmerRole = "Crop Seller (Farmer)",
        consumerRole = "Consumer (Buyer)",
        switchRoleTip = "Open profile to manage account or logout",
        navMarket = "Bazar Feed",
        navMandiAi = "Mandi Rates",
        navAddCrop = "Sell Crop",
        directFarmFeed = "Fresh Direct-from-Farm Harvest",
        zeroDalalBadge = "0% Dalal Commission",
        aiRecommendedTag = "Fair Rate",
        apmcModalLabel = "APMC Mandi Rate",
        farmerAskPrice = "Direct Price",
        farmerEarningsGain = "Farmer +18% vs Dalal",
        consumerSavings = "Buyer saves 14% vs Retail",
        buyDirectBtn = "Buy Direct Now",
        callFarmerBtn = "Call Farmer",
        addListingTitle = "List New Crop for Direct Sale",
        cropNameLabel = "Select or Enter Crop Name",
        categoryLabel = "Crop Category",
        quantityQuintalLabel = "Available Quantity (Quintals)",
        qualityGradeLabel = "Quality Grade",
        moisturePercentLabel = "Moisture Content (%)",
        districtMandiLabel = "Farm Village / Nearest APMC Mandi (Tap GPS to Auto-Detect)",
        detectGpsBtn = "Auto-Detect GPS",
        pickCropPhotoBtn = "Upload Harvest Photo",
        runAiPriceAdvisorBtn = "Check Market Rate",
        publishListingBtn = "Publish Crop to Buyer Market",
        aiAdvisorTitle = "Live Market Rate Check",
        liveApmcHeader = "Real-Time Maharashtra APMC Mandi Board",
        searchCropHint = "Search Onion, Soybean, Tur Dal, Pomegranate...",
        allCategories = "All Crops",
        organicOnly = "Organic Certified",
        orderPlacedSuccess = "Direct order placed! Zero middleman commission.",
        listingPublishedSuccess = "Crop published to Seller Inventory & Buyer Feed!",
        loginWelcomeTitle = "Create Your KisanBazar Account",
        loginWelcomeSubtitle = "One-time registration • Next time your Main Page & Profile open directly!",
        selectRoleHeading = "1. Select Your Role:",
        sellerRoleCardTitle = "Crop Seller (Farmer)",
        sellerRoleCardDesc = "Sell crops directly, check live APMC market rates & receive buyer orders",
        consumerRoleCardTitle = "Consumer (Buyer)",
        consumerRoleCardDesc = "Buy fresh farm produce directly from farmers with 0% middleman commission",
        fullNameInputLabel = "Your Full Name",
        phoneInputLabel = "10-Digit Mobile Number",
        locationInputLabel = "Your Location (Tap here to Auto-Fetch GPS)",
        locationAutoFetchHint = "Tap location box or GPS icon to auto-detect your current village/city",
        detectingLocationText = "Detecting live GPS location...",
        sendOtpBtn = "Send 6-Digit Security OTP",
        resendOtpBtn = "Resend OTP",
        enterSixDigitOtpLabel = "Enter 6-Digit Security OTP",
        verifyOtpAndLoginBtn = "Verify OTP & Login Securely",
        otpSentBannerTitle = "Security OTP Sent to +91",
        invalidOtpError = "Invalid OTP! Please enter the exact 6-digit OTP sent to your number.",
        invalidPhoneOrNameError = "Please enter your full name and a valid 10-digit mobile number.",
        profileLabel = "Profile",
        profileTitle = "My KisanBazar Profile",
        verifiedMobileBadge = "OTP Verified Mobile",
        editProfileBtn = "Edit Profile Details",
        saveProfileBtn = "Save Profile Changes",
        switchRoleBtn = "Switch Role (Seller / Consumer)",
        logoutBtn = "Logout from Account",
        sellerPortalBadge = "CROP SELLER PORTAL",
        sellerNavMyCrops = "My Crops",
        sellerNavAddCrop = "Add Crop",
        sellerNavMandiAi = "Mandi Rates",
        sellerNavOrders = "Buyer Orders",
        sellerDashboardTitle = "Farmer Seller Dashboard",
        sellerTotalListingsLabel = "Active Crops",
        sellerBuyerOrdersLabel = "Orders Received",
        sellerEstRevenueLabel = "Stock Value",
        deleteListingBtn = "Remove Listing",
        noOrdersYetSeller = "No buyer orders yet. Customers ordering from Consumer Mode will appear here.",
        liveForBuyersBadge = "LIVE FOR BUYERS",
        aiFairLabel = "Fair Rate",
        apmcShortLabel = "Mandi Rate",
        qtlUnit = "qtl",
        kgUnit = "kg",
        consumerPortalBadge = "CONSUMER BUYER PORTAL",
        consumerNavBuyCrops = "Buy Crops",
        consumerNavMyOrders = "My Orders",
        consumerNavMandiRates = "Mandi Rates",
        consumerDashboardTitle = "Direct Farm-to-Home Marketplace",
        noOrdersYetConsumer = "You haven't placed any direct farm orders yet. Browse crops to order at 0% dalal fee!",
        directGainText = "Direct Gain",
        confirmedOrderStatus = "CONFIRMED DIRECT"
    )

    val marathi = KisanStrings(
        appTitle = "किसानबाजार",
        appSubtitle = "दलालमुक्त थेट शेतकरी ते ग्राहक बाजारपेठ आणि मंडी भाव",
        farmerRole = "शेतकरी / विक्रेता",
        consumerRole = "ग्राहक / खरेदीदार",
        switchRoleTip = "खाते माहिती पाहण्यासाठी किंवा लॉगआउटसाठी प्रोफाईलवर टॅप करा",
        navMarket = "थेट बाजार",
        navMandiAi = "मंडी भाव",
        navAddCrop = "पीक विका",
        directFarmFeed = "शेतातून थेट ताजा शेतमाल खरेदी करा",
        zeroDalalBadge = "०% दलाली - थेट व्यवहार",
        aiRecommendedTag = "योग्य दर",
        apmcModalLabel = "APMC मंडी भाव",
        farmerAskPrice = "थेट विक्री दर",
        farmerEarningsGain = "शेतकऱ्याला +१८% अधिक नफा",
        consumerSavings = "ग्राहकाची १४% थेट बचत",
        buyDirectBtn = "थेट खरेदी करा",
        callFarmerBtn = "शेतकऱ्याला कॉल करा",
        addListingTitle = "नवीन शेतमाल विक्रीसाठी जोडा",
        cropNameLabel = "पिकाचे नाव निवडा",
        categoryLabel = "पिकाचा प्रकार",
        quantityQuintalLabel = "उपलब्ध प्रमाण (क्विंटल)",
        qualityGradeLabel = "प्रतवारी (ग्रेड)",
        moisturePercentLabel = "ओलावा / आर्द्रता (%)",
        districtMandiLabel = "गाव / जवळची बाजार समिती (GPS साठी टॅप करा)",
        detectGpsBtn = "GPS लोकेशन घ्या",
        pickCropPhotoBtn = "पिकाचा फोटो जोडा",
        runAiPriceAdvisorBtn = "बाजारभाव तपासा",
        publishListingBtn = "शेतमाल बाजारात प्रसिद्ध करा",
        aiAdvisorTitle = "ताजे बाजारभाव तपासा",
        liveApmcHeader = "महाराष्ट्र राज्य कृषी बाजार समिती (APMC) थेट भाव",
        searchCropHint = "कांदा, सोयाबीन, तूर डाळ, डाळिंब शोधा...",
        allCategories = "सर्व पिके",
        organicOnly = "सेंद्रिय (ओरगॅनिक)",
        orderPlacedSuccess = "तुमची थेट ऑर्डर शेतकऱ्याकडे पाठवली गेली आहे!",
        listingPublishedSuccess = "तुमचा शेतमाल विक्रीसाठी यशस्वीरित्या जोडला गेला!",
        loginWelcomeTitle = "तुमचे किसानबाजार खाते तयार करा",
        loginWelcomeSubtitle = "फक्त पहिल्यांदाच खाते तयार करा • पुढच्या वेळी थेट तुमचे मुख्य पेज आणि प्रोफाईल उघडेल!",
        selectRoleHeading = "१. तुमची भूमिका निवडा:",
        sellerRoleCardTitle = "शेतकरी / विक्रेता",
        sellerRoleCardDesc = "शेतमाल विक्रीस टाका, ताजे बाजारभाव पहा आणि ग्राहकांच्या थेट ऑर्डर्स मिळवा",
        consumerRoleCardTitle = "ग्राहक / खरेदीदार",
        consumerRoleCardDesc = "थेट शेतकऱ्यांकडून ताजा शेतमाल, फळे व धान्य ०% दलालीत खरेदी करा",
        fullNameInputLabel = "तुमचे पूर्ण नाव",
        phoneInputLabel = "१० अंकी मोबाईल नंबर",
        locationInputLabel = "तुमचे लोकेशन / गाव (ऑटो-लोकेशनसाठी येथे क्लिक करा)",
        locationAutoFetchHint = "तुमचे चालू गाव/शहर आपोआप घेण्यासाठी लोकेशन बॉक्सवर क्लिक करा",
        detectingLocationText = "तुमचे GPS लोकेशन शोधत आहे...",
        sendOtpBtn = "६-अंकी सुरक्षित OTP पाठवा",
        resendOtpBtn = "पुन्हा OTP पाठवा",
        enterSixDigitOtpLabel = "६-अंकी सुरक्षा OTP टाका",
        verifyOtpAndLoginBtn = "OTP तपासा आणि लॉगिन करा",
        otpSentBannerTitle = "तुमच्या नंबरवर सुरक्षा OTP पाठवला: +91",
        invalidOtpError = "चुकीचा OTP! कृपया मोबाईलवर आलेला अचूक ६-अंकी OTP टाका.",
        invalidPhoneOrNameError = "कृपया तुमचे पूर्ण नाव आणि वैध १० अंकी मोबाईल नंबर टाका.",
        profileLabel = "प्रोफाईल",
        profileTitle = "माझे किसानबाजार प्रोफाईल",
        verifiedMobileBadge = "OTP द्वारे प्रमाणित नंबर",
        editProfileBtn = "प्रोफाईल माहिती बदला",
        saveProfileBtn = "बदल सेव्ह करा",
        switchRoleBtn = "भूमिका बदला (शेतकरी ⇄ ग्राहक)",
        logoutBtn = "खात्यातून लॉगआउट करा",
        sellerPortalBadge = "शेतकरी विक्रेता पॅनेल",
        sellerNavMyCrops = "माझा शेतमाल",
        sellerNavAddCrop = "पीक जोडा",
        sellerNavMandiAi = "मंडी भाव",
        sellerNavOrders = "ग्राहक ऑर्डर्स",
        sellerDashboardTitle = "शेतकरी विक्री व्यवस्थापन पॅनेल",
        sellerTotalListingsLabel = "विक्रीवरील पिके",
        sellerBuyerOrdersLabel = "आलेल्या ऑर्डर्स",
        sellerEstRevenueLabel = "एकूण माल किंमत",
        deleteListingBtn = "जाहिरात काढा",
        noOrdersYetSeller = "अद्याप कोणतीही ग्राहक ऑर्डर आलेली नाही. ग्राहकांनी ऑर्डर दिल्यावर येथे दिसेल.",
        liveForBuyersBadge = "खरेदीसाठी उपलब्ध",
        aiFairLabel = "हमीभाव",
        apmcShortLabel = "मंडी भाव",
        qtlUnit = "क्विंटल",
        kgUnit = "किलो",
        consumerPortalBadge = "ग्राहक खरेदी पॅनेल",
        consumerNavBuyCrops = "शेतमाल खरेदी",
        consumerNavMyOrders = "माझ्या ऑर्डर्स",
        consumerNavMandiRates = "मंडी बाजारभाव",
        consumerDashboardTitle = "थेट शेतकरी ते ग्राहक बाजारपेठ",
        noOrdersYetConsumer = "तुम्ही अद्याप कोणतीही ऑर्डर दिलेली नाही. ताजा शेतमाल निवडून थेट ऑर्डर करा!",
        directGainText = "अधिक नफा",
        confirmedOrderStatus = "थेट ऑर्डर निश्चित"
    )

    val hindi = KisanStrings(
        appTitle = "किसानबाजार",
        appSubtitle = "बिचौलिया-मुक्त सीधा किसान से ग्राहक बाज़ार और मंडी भाव",
        farmerRole = "किसान / विक्रेता",
        consumerRole = "ग्राहक / खरीदार",
        switchRoleTip = "खाता जानकारी या लॉगआउट के लिए प्रोफ़ाइल पर टैप करें",
        navMarket = "सीधा बाज़ार",
        navMandiAi = "मंडी भाव",
        navAddCrop = "फसल बेचें",
        directFarmFeed = "खेत से सीधा ताज़ा कृषि उत्पाद खरीदें",
        zeroDalalBadge = "०% दलाली - सीधा व्यापार",
        aiRecommendedTag = "उचित दर",
        apmcModalLabel = "APMC मंडी भाव",
        farmerAskPrice = "सीधी बिक्री दर",
        farmerEarningsGain = "किसान को +१८% अधिक मुनाफा",
        consumerSavings = "ग्राहक की १४% सीधी बचत",
        buyDirectBtn = "सीधे खरीदें",
        callFarmerBtn = "किसान को कॉल करें",
        addListingTitle = "बिक्री के लिए नई फसल जोड़ें",
        cropNameLabel = "फसल का नाम चुनें या लिखें",
        categoryLabel = "फसल की श्रेणी",
        quantityQuintalLabel = "उपलब्ध मात्रा (क्विंटल)",
        qualityGradeLabel = "गुणवत्ता (ग्रेड)",
        moisturePercentLabel = "नमी / आर्द्रता (%)",
        districtMandiLabel = "गाँव / नज़दीकी मंडी (GPS के लिए टैप करें)",
        detectGpsBtn = "GPS लोकेशन लें",
        pickCropPhotoBtn = "फसल का फोटो जोड़ें",
        runAiPriceAdvisorBtn = "मंडी भाव देखें",
        publishListingBtn = "फसल बाज़ार में प्रकाशित करें",
        aiAdvisorTitle = "ताज़ा मंडी भाव जांचें",
        liveApmcHeader = "महाराष्ट्र कृषि उपज मंडी समिति (APMC) लाइव भाव",
        searchCropHint = "प्याज, सोयाबीन, तूर दाल, अनार खोजें...",
        allCategories = "सभी फसलें",
        organicOnly = "जैविक (ऑर्गेनिक)",
        orderPlacedSuccess = "आपका सीधा ऑर्डर किसान को भेज दिया गया है!",
        listingPublishedSuccess = "आपकी फसल बिक्री के लिए सफलतापूर्वक प्रकाशित हो गई है!",
        loginWelcomeTitle = "अपना किसानबाजार खाता बनाएं",
        loginWelcomeSubtitle = "केवल पहली बार खाता बनाएं • अगली बार सीधे आपका मुख्य पेज और प्रोफ़ाइल खुलेगा!",
        selectRoleHeading = "१. अपनी भूमिका चुनें:",
        sellerRoleCardTitle = "किसान / विक्रेता",
        sellerRoleCardDesc = "अपनी फसल सीधे बेचें, ताज़ा मंडी भाव देखें और ग्राहकों से सीधे ऑर्डर पाएं",
        consumerRoleCardTitle = "ग्राहक / खरीदार",
        consumerRoleCardDesc = "सीधे किसानों से ताज़ा सब्जियां, फल और अनाज ०% दलाली में खरीदें",
        fullNameInputLabel = "आपका पूरा नाम",
        phoneInputLabel = "१० अंकों का मोबाइल नंबर",
        locationInputLabel = "आपका गाँव / शहर (ऑटो-लोकेशन के लिए यहाँ टैप करें)",
        locationAutoFetchHint = "अपना वर्तमान गाँव/शहर अपने-आप भरने के लिए लोकेशन बॉक्स पर टैप करें",
        detectingLocationText = "आपकी GPS लोकेशन खोजी जा रही है...",
        sendOtpBtn = "६-अंकों का सुरक्षा OTP भेजें",
        resendOtpBtn = "OTP पुनः भेजें",
        enterSixDigitOtpLabel = "६-अंकों का सुरक्षा OTP दर्ज करें",
        verifyOtpAndLoginBtn = "OTP सत्यापित करें और लॉगिन करें",
        otpSentBannerTitle = "आपके नंबर पर सुरक्षा OTP भेजा गया: +91",
        invalidOtpError = "गलत OTP! कृपया मोबाइल पर प्राप्त सही ६-अंकों का OTP दर्ज करें।",
        invalidPhoneOrNameError = "कृपया अपना पूरा नाम और १० अंकों का सही मोबाइल नंबर दर्ज करें।",
        profileLabel = "प्रोफ़ाइल",
        profileTitle = "मेरा किसानबाजार प्रोफ़ाइल",
        verifiedMobileBadge = "OTP सत्यापित नंबर",
        editProfileBtn = "प्रोफ़ाइल जानकारी बदलें",
        saveProfileBtn = "बदलाव सेव करें",
        switchRoleBtn = "भूमिका बदलें (किसान ⇄ ग्राहक)",
        logoutBtn = "खाते से लॉगआउट करें",
        sellerPortalBadge = "किसान विक्रेता पैनल",
        sellerNavMyCrops = "मेरी फसल",
        sellerNavAddCrop = "फसल जोड़ें",
        sellerNavMandiAi = "मंडी भाव",
        sellerNavOrders = "ग्राहक ऑर्डर",
        sellerDashboardTitle = "किसान बिक्री प्रबंधन पैनल",
        sellerTotalListingsLabel = "बिक्री पर फसलें",
        sellerBuyerOrdersLabel = "प्राप्त ऑर्डर",
        sellerEstRevenueLabel = "कुल माल मूल्य",
        deleteListingBtn = "विज्ञापन हटाएं",
        noOrdersYetSeller = "अभी तक कोई ग्राहक ऑर्डर नहीं आया है। ग्राहकों द्वारा ऑर्डर करने पर यहाँ दिखेगा।",
        liveForBuyersBadge = "खरीद के लिए उपलब्ध",
        aiFairLabel = "उचित भाव",
        apmcShortLabel = "मंडी भाव",
        qtlUnit = "क्विंटल",
        kgUnit = "किलो",
        consumerPortalBadge = "ग्राहक खरीद पैनल",
        consumerNavBuyCrops = "फसल खरीदें",
        consumerNavMyOrders = "मेरे ऑर्डर",
        consumerNavMandiRates = "मंडी बाज़ारभाव",
        consumerDashboardTitle = "सीधा किसान से ग्राहक बाज़ार",
        noOrdersYetConsumer = "आपने अभी तक कोई सीधा ऑर्डर नहीं दिया है। ताज़ा फसल चुनकर ०% दलाली पर ऑर्डर करें!",
        directGainText = "अधिक लाभ",
        confirmedOrderStatus = "सीधा ऑर्डर कन्फर्म"
    )

    fun get(lang: AppLanguage): KisanStrings = when (lang) {
        AppLanguage.ENGLISH -> english
        AppLanguage.MARATHI -> marathi
        AppLanguage.HINDI -> hindi
    }

    fun localizeGrade(grade: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.ENGLISH -> grade
            AppLanguage.HINDI -> when {
                grade.contains("A+", ignoreCase = true) -> "ए+ ग्रेड (एक्सपोर्ट क्वालिटी)"
                grade.contains("Grade A", ignoreCase = true) -> "ए ग्रेड (उत्तम क्वालिटी)"
                else -> "बी ग्रेड (सामान्य)"
            }
            AppLanguage.MARATHI -> when {
                grade.contains("A+", ignoreCase = true) -> "अ+ दर्जा (एक्सपोर्ट प्रत)"
                grade.contains("Grade A", ignoreCase = true) -> "अ दर्जा (उत्तम प्रत)"
                else -> "ब दर्जा (साधारण प्रत)"
            }
        }
    }

    fun localizeHarvestTime(label: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.ENGLISH -> label
            AppLanguage.HINDI -> when (label) {
                "Harvested 3 days ago" -> "३ दिन पहले कटी फसल"
                "Freshly Milled" -> "ताज़ा तैयार माल"
                "Picked Today Morning" -> "आज सुबह तुड़ाई किया ताज़ा माल"
                "Cleaned & Graded" -> "साफ़ और ग्रेडिंग किया हुआ"
                "Farm Mill Processed" -> "खेत पर तैयार"
                "Listed Just Now" -> "अभी जोड़ा गया ताज़ा माल"
                else -> label
            }
            AppLanguage.MARATHI -> when (label) {
                "Harvested 3 days ago" -> "३ दिवसांपूर्वी काढलेला माल"
                "Freshly Milled" -> "ताजा भरडलेला माल"
                "Picked Today Morning" -> "आज सकाळी तोडलेला ताजा माल"
                "Cleaned & Graded" -> "स्वच्छ आणि प्रतवारी केलेला"
                "Farm Mill Processed" -> "शेतातील गिरणीत तयार"
                "Listed Just Now" -> "आत्ताच जोडलेला ताजा माल"
                else -> label
            }
        }
    }

    fun localizeMandiTime(label: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.ENGLISH -> label
            AppLanguage.HINDI -> label
                .replace("Live • Just Updated", "लाइव • अभी अपडेट हुआ")
                .replace("Live •", "लाइव •")
                .replace("mins ago", "मिनट पहले")
            AppLanguage.MARATHI -> label
                .replace("Live • Just Updated", "थेट • आत्ताच अपडेट झाले")
                .replace("Live •", "थेट •")
                .replace("mins ago", "मिनिटांपूर्वी")
        }
    }
}
