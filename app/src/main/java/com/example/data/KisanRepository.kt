package com.example.data

import kotlinx.coroutines.flow.Flow

class KisanRepository(private val dao: KisanBazarDao) {

    val userProfileFlow: Flow<UserProfileEntity?> = dao.observeUserProfile()
    val allCropListings: Flow<List<CropListingEntity>> = dao.getAllCropListings()
    val allMarketRates: Flow<List<LiveMarketRateEntity>> = dao.getAllMarketRates()
    val allDirectOrders: Flow<List<DirectOrderEntity>> = dao.getAllDirectOrders()

    suspend fun getSavedUserProfile(): UserProfileEntity? {
        return dao.getUserProfileOnce()
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
    }

    suspend fun clearUserProfile() {
        dao.clearUserProfile()
    }

    suspend fun insertCropListing(listing: CropListingEntity) {
        dao.insertCropListing(listing)
    }

    suspend fun updateCropListingPhoto(id: Int, imageUri: String) {
        dao.updateCropListingPhoto(id, imageUri)
    }

    suspend fun deleteCropListingById(id: Int) {
        dao.deleteCropListingById(id)
    }

    suspend fun insertDirectOrder(order: DirectOrderEntity) {
        dao.insertDirectOrder(order)
    }

    suspend fun refreshApmcRatesWithLiveDelta() {
        val updatedRates = KisanBackendService.computeUpdatedApmcRates()
        dao.insertAllMarketRates(updatedRates)
    }

    /**
     * Loads ONLY real benchmark APMC reference rates for the price calculator via Java backend service.
     * NEVER inserts any dummy or fake crop listings!
     * Only real crops added by a Seller appear in the Seller Inventory and Consumer Marketplace.
     */
    suspend fun ensureSeedDataLoaded() {
        if (dao.getMarketRatesCount() == 0) {
            dao.insertAllMarketRates(KisanBackendService.getInitialApmcRates())
        }
    }
}
