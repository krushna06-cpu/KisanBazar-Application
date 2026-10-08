package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Singleton row for the logged-in user on this device
    val fullName: String,
    val phoneNumber: String,
    val villageOrCity: String,
    val role: String, // "FARMER" or "CONSUMER"
    val preferredLanguage: String = "mr", // "mr" or "en"
    val verifiedByOtp: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "crop_listings")
data class CropListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cropNameEn: String,
    val cropNameMr: String,
    val category: String, // Vegetables, Grains & Pulses, Fruits, Spices
    val farmerNameEn: String,
    val farmerNameMr: String,
    val farmerPhone: String,
    val villageAndMandiEn: String,
    val villageAndMandiMr: String,
    val quantityQuintals: Double,
    val askingPricePerQuintal: Int,
    val aiRecommendedPricePerQuintal: Int,
    val apmcModalPricePerQuintal: Int,
    val qualityGrade: String, // Grade A+ Export, Grade A, Standard B
    val moisturePercent: Double,
    val isOrganic: Boolean,
    val imageUri: String? = null,
    val harvestDateLabel: String,
    val aiReasoningEn: String,
    val aiReasoningMr: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "live_market_rates")
data class LiveMarketRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cropNameEn: String,
    val cropNameMr: String,
    val mandiNameEn: String,
    val mandiNameMr: String,
    val district: String,
    val minPrice: Int,
    val maxPrice: Int,
    val modalPrice: Int,
    val arrivalTonnes: Int,
    val priceTrendPercent: Double,
    val aiForecastNext7DaysPrice: Int,
    val updatedTimeLabel: String
)

@Entity(tableName = "direct_orders")
data class DirectOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val cropNameEn: String,
    val cropNameMr: String,
    val farmerNameEn: String,
    val buyerName: String,
    val buyerPhone: String,
    val deliveryAddress: String,
    val quantityKg: Int,
    val totalAmountInr: Int,
    val savedMiddlemanFeeInr: Int,
    val status: String = "CONFIRMED_DIRECT",
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface KisanBazarDao {
    // Persistent User Account Queries
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun observeUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()

    // Crop Listings Queries
    @Query("SELECT * FROM crop_listings ORDER BY timestamp DESC")
    fun getAllCropListings(): Flow<List<CropListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCropListing(listing: CropListingEntity)

    @Query("UPDATE crop_listings SET imageUri = :imageUri WHERE id = :id")
    suspend fun updateCropListingPhoto(id: Int, imageUri: String)

    @Query("DELETE FROM crop_listings WHERE id = :id")
    suspend fun deleteCropListingById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCropListings(listings: List<CropListingEntity>)

    @Query("SELECT COUNT(*) FROM crop_listings")
    suspend fun getCropListingsCount(): Int

    // Market Rates Queries
    @Query("SELECT * FROM live_market_rates ORDER BY priceTrendPercent DESC")
    fun getAllMarketRates(): Flow<List<LiveMarketRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMarketRates(rates: List<LiveMarketRateEntity>)

    @Query("SELECT COUNT(*) FROM live_market_rates")
    suspend fun getMarketRatesCount(): Int

    // Direct Orders Queries
    @Query("SELECT * FROM direct_orders ORDER BY timestamp DESC")
    fun getAllDirectOrders(): Flow<List<DirectOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirectOrder(order: DirectOrderEntity)
}

@Database(
    entities = [
        UserProfileEntity::class,
        CropListingEntity::class,
        LiveMarketRateEntity::class,
        DirectOrderEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class KisanBazarDatabase : RoomDatabase() {
    abstract fun dao(): KisanBazarDao

    companion object {
        @Volatile
        private var INSTANCE: KisanBazarDatabase? = null

        fun getDatabase(context: Context): KisanBazarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KisanBazarDatabase::class.java,
                    "kisan_bazar_ai_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
