package com.shopmanager.data.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.shopmanager.data.entities.Bill
import com.shopmanager.data.entities.BillItem
@Dao
interface BillDao {
    @Query("SELECT * FROM bills WHERE shopId = :shopId ORDER BY createdAt DESC")
    fun getBillsByShop(shopId: Int): LiveData<List<Bill>>
    @Query("SELECT * FROM bills WHERE shopId = :shopId AND status = :status ORDER BY createdAt DESC")
    fun getBillsByStatus(shopId: Int, status: String): LiveData<List<Bill>>
    @Query("SELECT * FROM bills WHERE shopId = :shopId AND createdAt BETWEEN :start AND :end ORDER BY createdAt DESC")
    fun getBillsByDateRange(shopId: Int, start: Long, end: Long): LiveData<List<Bill>>
    @Query("SELECT * FROM bills WHERE shopId = :shopId AND status = 'SAVED' AND createdAt BETWEEN :start AND :end")
    suspend fun getBillsForPeriod(shopId: Int, start: Long, end: Long): List<Bill>
    @Insert
    suspend fun insertBill(bill: Bill): Long
    @Update
    suspend fun updateBill(bill: Bill)
    @Delete
    suspend fun deleteBill(bill: Bill)
    @Insert
    suspend fun insertBillItems(items: List<BillItem>)
    @Query("SELECT * FROM bill_items WHERE billId = :billId")
    suspend fun getBillItems(billId: Int): List<BillItem>
    @Query("DELETE FROM bill_items WHERE billId = :billId")
    suspend fun deleteBillItems(billId: Int)
    @Query("SELECT * FROM bills WHERE id = :billId")
    suspend fun getBillById(billId: Int): Bill?
}
