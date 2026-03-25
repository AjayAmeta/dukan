package com.shopmanager.data.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.shopmanager.data.entities.Product
@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE shopId = :shopId ORDER BY type ASC, name ASC")
    fun getProductsByShop(shopId: Int): LiveData<List<Product>>
    @Query("SELECT * FROM products WHERE shopId = :shopId AND (name LIKE '%' || :query || '%' OR type LIKE '%' || :query || '%') ORDER BY type ASC, name ASC")
    suspend fun searchProducts(shopId: Int, query: String): List<Product>
    @Query("SELECT DISTINCT type FROM products WHERE shopId = :shopId ORDER BY type ASC")
    fun getProductTypes(shopId: Int): LiveData<List<String>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long
    @Update
    suspend fun updateProduct(product: Product)
    @Delete
    suspend fun deleteProduct(product: Product)
    @Query("SELECT * FROM products WHERE shopId = :shopId ORDER BY type ASC, name ASC")
    suspend fun getProductsByShopSync(shopId: Int): List<Product>
}
