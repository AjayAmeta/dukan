package com.shopmanager.data
import androidx.lifecycle.LiveData
import com.shopmanager.data.dao.BillDao
import com.shopmanager.data.dao.ProductDao
import com.shopmanager.data.dao.ShopDao
import com.shopmanager.data.entities.*

class Repository(private val shopDao: ShopDao, private val productDao: ProductDao, private val billDao: BillDao) {

    fun getAllShops(): LiveData<List<Shop>> = shopDao.getAllShops()
    suspend fun getAllShopsSync() = shopDao.getAllShopsSync()
    suspend fun insertShop(shop: Shop) = shopDao.insertShop(shop)
    suspend fun getShopById(id: Int) = shopDao.getShopById(id)

    fun getProductsByShop(shopId: Int) = productDao.getProductsByShop(shopId)
    fun getProductTypes(shopId: Int) = productDao.getProductTypes(shopId)
    suspend fun searchProducts(shopId: Int, query: String) = productDao.searchProducts(shopId, query)
    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    suspend fun getProductsByShopSync(shopId: Int) = productDao.getProductsByShopSync(shopId)

    fun getBillsByShop(shopId: Int) = billDao.getBillsByShop(shopId)
    fun getBillsByStatus(shopId: Int, status: String) = billDao.getBillsByStatus(shopId, status)
    fun getBillsByDateRange(shopId: Int, start: Long, end: Long) = billDao.getBillsByDateRange(shopId, start, end)
    suspend fun getBillsForPeriod(shopId: Int, start: Long, end: Long) = billDao.getBillsForPeriod(shopId, start, end)
    suspend fun insertBill(bill: Bill): Long = billDao.insertBill(bill)
    suspend fun updateBill(bill: Bill) = billDao.updateBill(bill)
    suspend fun insertBillItems(items: List<BillItem>) = billDao.insertBillItems(items)
    suspend fun getBillItems(billId: Int) = billDao.getBillItems(billId)
    suspend fun deleteBillItems(billId: Int) = billDao.deleteBillItems(billId)
    suspend fun getBillById(billId: Int) = billDao.getBillById(billId)
}
