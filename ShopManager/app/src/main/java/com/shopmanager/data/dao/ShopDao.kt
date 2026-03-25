package com.shopmanager.data.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.shopmanager.data.entities.Shop
@Dao
interface ShopDao {
    @Query("SELECT * FROM shops ORDER BY name ASC")
    fun getAllShops(): LiveData<List<Shop>>
    @Query("SELECT * FROM shops ORDER BY name ASC")
    suspend fun getAllShopsSync(): List<Shop>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: Shop): Long
    @Query("SELECT * FROM shops WHERE id = :id")
    suspend fun getShopById(id: Int): Shop?
}
