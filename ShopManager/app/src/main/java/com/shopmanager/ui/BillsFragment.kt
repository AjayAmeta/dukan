package com.shopmanager.ui
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.shopmanager.R
import com.shopmanager.adapter.BillAdapter
import com.shopmanager.data.entities.Bill
import com.shopmanager.databinding.FragmentBillsBinding
import com.shopmanager.utils.PdfGenerator
import com.shopmanager.utils.PreferenceHelper
import com.shopmanager.viewmodel.MainViewModel
import java.util.Calendar

class BillsFragment : Fragment() {
    private var _b: FragmentBillsBinding? = null
    private val b get() = _b!!
    private val vm: MainViewModel by activityViewModels()
    private lateinit var billAdapter: BillAdapter
    private var currentTab = 0

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentBillsBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        billAdapter = BillAdapter { bill -> showBillDetail(bill) }
        b.recyclerBills.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerBills.adapter = billAdapter

        b.tabBills.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { currentTab = tab?.position ?: 0; loadCurrentTab() }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        b.btnFilterDate.setOnClickListener { showDateFilter() }
        b.btnFilterMonth.setOnClickListener { showMonthFilter() }
        b.btnClearFilter.setOnClickListener { clearFilter() }
        loadCurrentTab()
    }

    private fun loadCurrentTab() {
        val shopId = PreferenceHelper.getShopId(requireContext())
        when (currentTab) {
            0 -> { // This Month
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
                val start = cal.timeInMillis
                vm.getBillsByDateRange(start, System.currentTimeMillis()).observe(viewLifecycleOwner) { updateList(it) }
            }
            1 -> vm.getSavedBills().observe(viewLifecycleOwner) { updateList(it) }
            2 -> vm.getDraftBills().observe(viewLifecycleOwner) { updateList(it) }
            3 -> vm.getAllBills().observe(viewLifecycleOwner) { updateList(it) }
        }
    }

    private fun updateList(bills: List<Bill>) {
        billAdapter.submitList(bills)
        b.tvNoBills.visibility = if (bills.isEmpty()) View.VISIBLE else View.GONE
        b.tvTotalCount.text = "${bills.size} bills | Total: ₹${String.format("%.2f", bills.sumOf { it.totalAmount })}"
    }

    private fun showBillDetail(bill: Bill) {
        vm.getBillItems(bill.id) { items ->
            val msg = StringBuilder("Bill #${bill.id}\nCustomer: ${bill.customerName}\nStatus: ${bill.status}\n\nItems:\n")
            items.forEach { msg.append("• ${it.productName} x${it.quantity} = ₹${String.format("%.2f", it.quantity * it.sellPrice)}\n") }
            msg.append("\nTotal: ₹${String.format("%.2f", bill.totalAmount)}")
            val dlg = AlertDialog.Builder(requireContext())
                .setTitle("Bill Details")
                .setMessage(msg.toString())
                .setNegativeButton("Close", null)
            if (bill.status == "DRAFT") {
                dlg.setPositiveButton("Save to Bills") { _, _ ->
                    vm.updateBillToSaved(bill)
                    Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
                }
            }
            dlg.setNeutralButton("Download PDF") { _, _ ->
                val file = PdfGenerator.generateBillPdf(requireContext(), bill, items,
                    PreferenceHelper.getShopName(requireContext()), PreferenceHelper.getShopType(requireContext()))
                val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
                val intent = Intent(Intent.ACTION_VIEW).apply { setDataAndType(uri, "application/pdf"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                try { startActivity(intent) } catch (e: Exception) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply { type = "application/pdf"; putExtra(Intent.EXTRA_STREAM, uri); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                    startActivity(Intent.createChooser(shareIntent, "Open PDF"))
                }
            }
            dlg.show()
        }
    }

    private fun showDateFilter() {
        val dlgView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_date_filter, null)
        val dpFrom = dlgView.findViewById<DatePicker>(R.id.datePickerFrom)
        val dpTo = dlgView.findViewById<DatePicker>(R.id.datePickerTo)
        AlertDialog.Builder(requireContext()).setTitle("Filter by Date Range").setView(dlgView)
            .setPositiveButton("Apply") { _, _ ->
                val calFrom = Calendar.getInstance().apply { set(dpFrom.year, dpFrom.month, dpFrom.dayOfMonth, 0, 0, 0) }
                val calTo = Calendar.getInstance().apply { set(dpTo.year, dpTo.month, dpTo.dayOfMonth, 23, 59, 59) }
                vm.getBillsByDateRange(calFrom.timeInMillis, calTo.timeInMillis).observe(viewLifecycleOwner) { updateList(it) }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun showMonthFilter() {
        val months = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        AlertDialog.Builder(requireContext()).setTitle("Select Month").setItems(months) { _, which ->
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val start = Calendar.getInstance().apply { set(year, which, 1, 0, 0, 0) }.timeInMillis
            val end = Calendar.getInstance().apply { set(year, which, getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59) }.timeInMillis
            vm.getBillsByDateRange(start, end).observe(viewLifecycleOwner) { updateList(it) }
        }.show()
    }

    private fun clearFilter() { loadCurrentTab() }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
