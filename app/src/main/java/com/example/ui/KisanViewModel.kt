package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiPriceEngine
import com.example.data.AiPriceRecommendationResult
import com.example.data.AppLanguage
import com.example.data.CropListingEntity
import com.example.data.DirectOrderEntity
import com.example.data.KisanBazarDatabase
import com.example.data.KisanRepository
import com.example.data.LiveMarketRateEntity
import com.example.data.UserProfileEntity
import com.example.data.UserRole
import com.example.data.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Dedicated tabs for Crop Seller (शेतकरी / विक्रेता)
enum class SellerTab {
    MY_CROPS_DASHBOARD,
    ADD_NEW_CROP,
    AI_AND_MANDI_ADVISOR,
    BUYER_ORDERS_RECEIVED
}

// Dedicated tabs for Consumer / Buyer (ग्राहक / खरेदीदार)
enum class ConsumerTab {
    BUY_DIRECT_MARKET,
    MY_DIRECT_ORDERS,
    APMC_PRICE_CHECK
}

class KisanViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("kisan_bazar_account_prefs", Context.MODE_PRIVATE)
    private val repository: KisanRepository

    val cropListings: StateFlow<List<CropListingEntity>>
    val marketRates: StateFlow<List<LiveMarketRateEntity>>
    val directOrders: StateFlow<List<DirectOrderEntity>>

    private val _language = MutableStateFlow(loadSavedLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Synchronously initialized from SharedPreferences so the user's Main Dashboard & Profile open immediately on app launch!
    private val _loggedInUser = MutableStateFlow<UserSession?>(loadSavedSessionFromPrefs())
    val loggedInUser: StateFlow<UserSession?> = _loggedInUser.asStateFlow()

    private val _sellerTab = MutableStateFlow(SellerTab.MY_CROPS_DASHBOARD)
    val sellerTab: StateFlow<SellerTab> = _sellerTab.asStateFlow()

    private val _consumerTab = MutableStateFlow(ConsumerTab.BUY_DIRECT_MARKET)
    val consumerTab: StateFlow<ConsumerTab> = _consumerTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _organicFilterOnly = MutableStateFlow(false)
    val organicFilterOnly: StateFlow<Boolean> = _organicFilterOnly.asStateFlow()

    private val _aiRecommendation = MutableStateFlow<AiPriceRecommendationResult?>(null)
    val aiRecommendation: StateFlow<AiPriceRecommendationResult?> = _aiRecommendation.asStateFlow()

    private val _isCalculatingAi = MutableStateFlow(false)
    val isCalculatingAi: StateFlow<Boolean> = _isCalculatingAi.asStateFlow()

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    init {
        val dao = KisanBazarDatabase.getDatabase(application).dao()
        repository = KisanRepository(dao)

        cropListings = repository.allCropListings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )
        marketRates = repository.allMarketRates.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )
        directOrders = repository.allDirectOrders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.ensureSeedDataLoaded()

            // Also sync with Room user_profile table if present
            val roomProfile = repository.getSavedUserProfile()
            if (_loggedInUser.value == null && roomProfile != null) {
                val restored = UserSession(
                    fullName = roomProfile.fullName,
                    phoneNumber = roomProfile.phoneNumber,
                    villageOrCity = roomProfile.villageOrCity,
                    role = if (roomProfile.role == "CONSUMER") UserRole.CONSUMER else UserRole.FARMER,
                    verifiedByOtp = roomProfile.verifiedByOtp
                )
                _loggedInUser.value = restored
                saveSessionToPrefs(restored, _language.value)
            }

            runAiPriceEvaluation(
                cropName = "लाल कांदा (लासलगाव सुपर)",
                apmcModalPrice = 2380,
                qualityGrade = "Grade A+ Export",
                moisturePercent = 10.5,
                isOrganic = true,
                mandiName = "लासलगाव कृषी बाजार समिती, नाशिक",
                arrivalTrendPercent = 6.4
            )
        }
    }

    private fun loadSavedLanguage(): AppLanguage {
        return com.example.data.UserSessionBackendJava.loadSavedLanguage(prefs)
    }

    private fun loadSavedSessionFromPrefs(): UserSession? {
        return com.example.data.UserSessionBackendJava.loadSavedSessionFromPrefs(prefs)
    }

    private fun saveSessionToPrefs(session: UserSession, lang: AppLanguage) {
        com.example.data.UserSessionBackendJava.saveSessionToPrefs(prefs, session, lang)
    }

    private suspend fun persistSessionEverywhere(session: UserSession, lang: AppLanguage) {
        saveSessionToPrefs(session, lang)
        repository.saveUserProfile(
            com.example.data.UserSessionBackendJava.toUserProfileEntity(session, lang)
        )
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        com.example.data.UserSessionBackendJava.savePreferredLanguage(prefs, lang)
        _loggedInUser.value?.let { currentSession ->
            viewModelScope.launch {
                persistSessionEverywhere(currentSession, lang)
            }
        }
    }

    fun toggleLanguage() {
        val nextLang = when (_language.value) {
            AppLanguage.MARATHI -> AppLanguage.HINDI
            AppLanguage.HINDI -> AppLanguage.ENGLISH
            AppLanguage.ENGLISH -> AppLanguage.MARATHI
        }
        setLanguage(nextLang)
    }

    fun loginUser(session: UserSession) {
        _loggedInUser.value = session
        if (session.role == UserRole.FARMER) {
            _sellerTab.value = SellerTab.MY_CROPS_DASHBOARD
        } else {
            _consumerTab.value = ConsumerTab.BUY_DIRECT_MARKET
        }
        viewModelScope.launch {
            persistSessionEverywhere(session, _language.value)
        }
        _bannerMessage.value = when (_language.value) {
            AppLanguage.MARATHI -> if (session.role == UserRole.FARMER) {
                "तुमचे खाते सेव्ह झाले आहे! शेतकरी पॅनेलमध्ये स्वागत आहे, ${session.fullName}!"
            } else {
                "तुमचे खाते सेव्ह झाले आहे! ग्राहक पॅनेलमध्ये स्वागत आहे, ${session.fullName}!"
            }
            AppLanguage.HINDI -> if (session.role == UserRole.FARMER) {
                "आपका खाता सेव हो गया है! किसान पैनल में स्वागत है, ${session.fullName}!"
            } else {
                "आपका खाता सेव हो गया है! ग्राहक पैनल में स्वागत है, ${session.fullName}!"
            }
            AppLanguage.ENGLISH -> if (session.role == UserRole.FARMER) {
                "Account saved! Welcome to Crop Seller Portal, ${session.fullName}"
            } else {
                "Account saved! Welcome to Consumer Buyer Portal, ${session.fullName}"
            }
        }
    }

    fun updateUserProfile(newName: String, newLocation: String) {
        val current = _loggedInUser.value ?: return
        val updated = current.copy(
            fullName = newName.ifBlank { current.fullName },
            villageOrCity = newLocation.ifBlank { current.villageOrCity }
        )
        _loggedInUser.value = updated
        viewModelScope.launch {
            persistSessionEverywhere(updated, _language.value)
        }
        _bannerMessage.value = when (_language.value) {
            AppLanguage.MARATHI -> "प्रोफाईल माहिती कायमस्वरूपी सेव्ह केली!"
            AppLanguage.HINDI -> "प्रोफ़ाइल जानकारी स्थायी रूप से सेव हो गई!"
            AppLanguage.ENGLISH -> "Profile saved permanently!"
        }
    }

    fun switchLoggedInUserRole() {
        val current = _loggedInUser.value ?: return
        val nextRole = if (current.role == UserRole.FARMER) UserRole.CONSUMER else UserRole.FARMER
        val updated = current.copy(role = nextRole)
        _loggedInUser.value = updated
        if (nextRole == UserRole.FARMER) {
            _sellerTab.value = SellerTab.MY_CROPS_DASHBOARD
        } else {
            _consumerTab.value = ConsumerTab.BUY_DIRECT_MARKET
        }
        viewModelScope.launch {
            persistSessionEverywhere(updated, _language.value)
        }
        _bannerMessage.value = when (_language.value) {
            AppLanguage.MARATHI -> if (nextRole == UserRole.FARMER) "शेतकरी विक्रेता मोड सेव्ह केला" else "ग्राहक खरेदी मोड सेव्ह केला"
            AppLanguage.HINDI -> if (nextRole == UserRole.FARMER) "किसान विक्रेता मोड सेव किया गया" else "ग्राहक खरीद मोड सेव किया गया"
            AppLanguage.ENGLISH -> if (nextRole == UserRole.FARMER) "Switched & saved Crop Seller Portal" else "Switched & saved Consumer Buyer Portal"
        }
    }

    fun logout() {
        _loggedInUser.value = null
        _bannerMessage.value = null
        com.example.data.UserSessionBackendJava.clearSessionOnLogout(prefs, _language.value)
        viewModelScope.launch {
            repository.clearUserProfile()
        }
    }

    fun selectSellerTab(tab: SellerTab) {
        _sellerTab.value = tab
    }

    fun selectConsumerTab(tab: ConsumerTab) {
        _consumerTab.value = tab
    }

    fun setCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleOrganicFilter() {
        _organicFilterOnly.value = !_organicFilterOnly.value
    }

    fun clearBannerMessage() {
        _bannerMessage.value = null
    }

    fun showToastMessage(msg: String) {
        _bannerMessage.value = msg
    }

    fun updateCropListingPhoto(id: Int, permanentImageUri: String) {
        viewModelScope.launch {
            repository.updateCropListingPhoto(id, permanentImageUri)
            _bannerMessage.value = when (_language.value) {
                AppLanguage.MARATHI -> "शेतमालाचा फोटो कायमस्वरूपी सेव्ह झाला!"
                AppLanguage.HINDI -> "फसल का फोटो स्थायी रूप से सेव हो गया!"
                AppLanguage.ENGLISH -> "Crop photo saved permanently!"
            }
        }
    }

    fun deleteCropListing(id: Int) {
        viewModelScope.launch {
            val existing = cropListings.value.firstOrNull { it.id == id }
            com.example.data.ImageStorageHelper.deleteStoredImageIfInternal(existing?.imageUri)
            repository.deleteCropListingById(id)
            _bannerMessage.value = when (_language.value) {
                AppLanguage.MARATHI -> "पीक जाहिरात काढून टाकली."
                AppLanguage.HINDI -> "फसल का विज्ञापन हटा दिया गया।"
                AppLanguage.ENGLISH -> "Crop listing removed from marketplace."
            }
        }
    }

    fun refreshLiveApmcRates() {
        viewModelScope.launch {
            repository.refreshApmcRatesWithLiveDelta()
            _bannerMessage.value = when (_language.value) {
                AppLanguage.MARATHI -> "APMC बाजार समितीचे ताजे भाव अपडेट झाले!"
                AppLanguage.HINDI -> "APMC मंडी के ताज़ा भाव अपडेट हो गए!"
                AppLanguage.ENGLISH -> "Live APMC Mandi rates synced with market board!"
            }
        }
    }

    fun runAiPriceEvaluation(
        cropName: String,
        apmcModalPrice: Int,
        qualityGrade: String,
        moisturePercent: Double,
        isOrganic: Boolean,
        mandiName: String,
        arrivalTrendPercent: Double = 4.5
    ) {
        viewModelScope.launch {
            _isCalculatingAi.value = true
            val baseCalc = AiPriceEngine.calculatePriceRecommendation(
                cropName = cropName,
                apmcModalPrice = apmcModalPrice,
                qualityGrade = qualityGrade,
                moisturePercent = moisturePercent,
                isOrganic = isOrganic,
                arrivalTrendPercent = arrivalTrendPercent
            )
            _aiRecommendation.value = baseCalc

            val geminiText = AiPriceEngine.fetchGeminiMarketAdvisory(
                cropName = cropName,
                mandiName = mandiName,
                recommendedPrice = baseCalc.recommendedDirectPrice,
                language = _language.value
            )
            _aiRecommendation.value = baseCalc.copy(geminiLiveAdvisory = geminiText)
            _isCalculatingAi.value = false
        }
    }

    fun addNewCropListing(
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
    ) {
        viewModelScope.launch {
            val aiEval = AiPriceEngine.calculatePriceRecommendation(
                cropName = cropNameEn,
                apmcModalPrice = apmcModalPricePerQuintal,
                qualityGrade = qualityGrade,
                moisturePercent = moisturePercent,
                isOrganic = isOrganic,
                arrivalTrendPercent = 4.2
            )

            val newEntity = CropListingEntity(
                cropNameEn = cropNameEn,
                cropNameMr = cropNameMr.ifBlank { cropNameEn },
                category = category,
                farmerNameEn = farmerName,
                farmerNameMr = farmerName,
                farmerPhone = farmerPhone,
                villageAndMandiEn = villageAndMandi,
                villageAndMandiMr = villageAndMandi,
                quantityQuintals = quantityQuintals,
                askingPricePerQuintal = askingPricePerQuintal,
                aiRecommendedPricePerQuintal = aiEval.recommendedDirectPrice,
                apmcModalPricePerQuintal = apmcModalPricePerQuintal,
                qualityGrade = qualityGrade,
                moisturePercent = moisturePercent,
                isOrganic = isOrganic,
                imageUri = imageUri,
                harvestDateLabel = "Listed Just Now",
                aiReasoningEn = aiEval.explanationEn,
                aiReasoningMr = aiEval.explanationMr
            )

            repository.insertCropListing(newEntity)
            _sellerTab.value = SellerTab.MY_CROPS_DASHBOARD
            _bannerMessage.value = when (_language.value) {
                AppLanguage.MARATHI -> "तुमचा शेतमाल किसानबाजारवर थेट विक्रीसाठी प्रसिद्ध झाला!"
                AppLanguage.HINDI -> "आपकी फसल किसानबाज़ार पर सीधी बिक्री के लिए प्रकाशित हो गई!"
                AppLanguage.ENGLISH -> "Harvest listed live on KisanBazar! Zero middlemen."
            }
        }
    }

    fun placeDirectOrder(
        listing: CropListingEntity,
        buyerName: String,
        buyerPhone: String,
        deliveryAddress: String,
        quantityKg: Int
    ) {
        viewModelScope.launch {
            val order = com.example.data.KisanBackendService.createDirectOrderEntity(
                listing,
                buyerName,
                buyerPhone,
                deliveryAddress,
                quantityKg
            )
            val savedFeeInr = order.savedMiddlemanFeeInr
            repository.insertDirectOrder(order)
            _consumerTab.value = ConsumerTab.MY_DIRECT_ORDERS
            _bannerMessage.value = when (_language.value) {
                AppLanguage.MARATHI -> "ऑर्डर यशस्वी! ₹$savedFeeInr दलाली वाचली. शेतकरी लवकरच संपर्क करतील."
                AppLanguage.HINDI -> "ऑर्डर सफल! ₹$savedFeeInr दलाली की बचत। किसान जल्द ही संपर्क करेंगे।"
                AppLanguage.ENGLISH -> "Direct Order Confirmed! You saved ₹$savedFeeInr in middleman fees."
            }
        }
    }
}
