package com.shopmanager.viewmodel
import android.app.Application
import androidx.lifecycle.*
import com.shopmanager.data.AppDatabase
import com.shopmanager.data.Repository
import com.shopmanager.data.entities.*
import com.shopmanager.utils.PreferenceHelper
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: Repository
    private val shopId get() = PreferenceHelper.getShopId(getApplication())

    init {
        val db = AppDatabase.getDatabase(application)
        repo = Repository(db.shopDao(), db.productDao(), db.billDao())
    }

    // --- Shops ---
    val allShops = repo.getAllShops()
    fun insertShop(shop: Shop, onDone: (Long) -> Unit) = viewModelScope.launch {
        val id = repo.insertShop(shop)
        onDone(id)
    }

    // --- Products ---
    fun getProducts() = repo.getProductsByShop(shopId)
    fun getProductTypes() = repo.getProductTypes(shopId)
    fun searchProducts(query: String, onResult: (List<Product>) -> Unit) = viewModelScope.launch {
        onResult(repo.searchProducts(shopId, query))
    }
    fun insertProduct(product: Product, onDone: (() -> Unit)? = null) = viewModelScope.launch {
        repo.insertProduct(product)
        onDone?.invoke()
    }
    fun updateProduct(product: Product, onDone: (() -> Unit)? = null) = viewModelScope.launch {
        repo.updateProduct(product)
        onDone?.invoke()
    }
    fun deleteProduct(product: Product, onDone: (() -> Unit)? = null) = viewModelScope.launch {
        repo.deleteProduct(product)
        onDone?.invoke()
    }

    // --- Bills ---
    fun getAllBills() = repo.getBillsByShop(shopId)
    fun getDraftBills() = repo.getBillsByStatus(shopId, "DRAFT")
    fun getSavedBills() = repo.getBillsByStatus(shopId, "SAVED")
    fun getBillsByDateRange(start: Long, end: Long) = repo.getBillsByDateRange(shopId, start, end)

    fun saveBill(bill: Bill, items: List<BillItem>, onDone: (Long) -> Unit) = viewModelScope.launch {
        val billId = repo.insertBill(bill.copy(shopId = shopId))
        val mappedItems = items.map { it.copy(billId = billId.toInt()) }
        repo.insertBillItems(mappedItems)
        onDone(billId)
    }

    fun updateBillToDraft(bill: Bill) = viewModelScope.launch { repo.updateBill(bill.copy(status = "DRAFT")) }
    fun updateBillToSaved(bill: Bill) = viewModelScope.launch { repo.updateBill(bill.copy(status = "SAVED")) }

    fun getBillItems(billId: Int, onResult: (List<BillItem>) -> Unit) = viewModelScope.launch {
        onResult(repo.getBillItems(billId))
    }

    // --- Progress Data ---
    data class ProgressData(val label: String, val sales: Float, val profit: Float)

    fun getWeeklyProgress(onResult: (List<ProgressData>) -> Unit) = viewModelScope.launch {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val weekStart = cal.timeInMillis
        val data = mutableListOf<ProgressData>()
        val days = arrayOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
        for (i in 0..6) {
            val dayStart = weekStart + i * 86400000L
            val dayEnd = dayStart + 86400000L - 1
            val bills = repo.getBillsForPeriod(shopId, dayStart, dayEnd)
            data.add(ProgressData(days[i], bills.sumOf { it.totalAmount }.toFloat(), bills.sumOf { it.totalProfit }.toFloat()))
        }
        onResult(data)
    }

    fun getMonthlyProgress(onResult: (List<ProgressData>) -> Unit) = viewModelScope.launch {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val months = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        val data = mutableListOf<ProgressData>()
        for (m in 0..11) {
            cal.set(year, m, 1, 0, 0, 0); cal.set(Calendar.MILLISECOND, 0)
            val start = cal.timeInMillis
            cal.set(year, m, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            val end = cal.timeInMillis
            val bills = repo.getBillsForPeriod(shopId, start, end)
            data.add(ProgressData(months[m], bills.sumOf { it.totalAmount }.toFloat(), bills.sumOf { it.totalProfit }.toFloat()))
        }
        onResult(data)
    }

    fun getYearlyProgress(onResult: (List<ProgressData>) -> Unit) = viewModelScope.launch {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val data = mutableListOf<ProgressData>()
        for (y in (currentYear - 4)..currentYear) {
            val cal = Calendar.getInstance()
            cal.set(y, 0, 1, 0, 0, 0); cal.set(Calendar.MILLISECOND, 0)
            val start = cal.timeInMillis
            cal.set(y, 11, 31, 23, 59, 59)
            val end = cal.timeInMillis
            val bills = repo.getBillsForPeriod(shopId, start, end)
            data.add(ProgressData(y.toString(), bills.sumOf { it.totalAmount }.toFloat(), bills.sumOf { it.totalProfit }.toFloat()))
        }
        onResult(data)
    }

    fun getCurrentMonthSummary(onResult: (Double, Double, Int) -> Unit) = viewModelScope.launch {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        val end = System.currentTimeMillis()
        val bills = repo.getBillsForPeriod(shopId, start, end)
        onResult(bills.sumOf { it.totalAmount }, bills.sumOf { it.totalProfit }, bills.size)
    }
}
