package com.shopmanager.ui
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shopmanager.R
import com.shopmanager.adapter.BillItemAdapter
import com.shopmanager.adapter.ProductSearchAdapter
import com.shopmanager.data.entities.Bill
import com.shopmanager.data.entities.BillItem
import com.shopmanager.databinding.FragmentCreateBillBinding
import com.shopmanager.utils.PdfGenerator
import com.shopmanager.utils.PreferenceHelper
import com.shopmanager.viewmodel.MainViewModel
import java.io.File

class CreateBillFragment : Fragment() {
    private var _binding: FragmentCreateBillBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()
    private lateinit var billItemAdapter: BillItemAdapter
    private var generatedPdfFile: File? = null
    private var currentBillId: Int = -1

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentCreateBillBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billItemAdapter = BillItemAdapter { pos ->
            billItemAdapter.removeItem(pos)
            updateTotal()
        }
        binding.recyclerBillItems.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBillItems.adapter = billItemAdapter
        binding.btnAddItem.setOnClickListener { showProductSearchDialog() }
        binding.btnSaveDraft.setOnClickListener { saveBill("DRAFT") }
        binding.btnSaveBill.setOnClickListener { saveBill("SAVED") }
        binding.btnShareWhatsapp.setOnClickListener { shareViaWhatsApp() }
        binding.btnShareMessage.setOnClickListener { shareViaMessage() }
        binding.btnClearBill.setOnClickListener { clearBill() }
    }

    private fun showProductSearchDialog() {
        val dlgView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_product_search, null)
        val etSearch = dlgView.findViewById<EditText>(R.id.etProductSearch)
        val recycler = dlgView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerProductSearch)
        val tvEmpty = dlgView.findViewById<TextView>(R.id.tvSearchEmpty)
        val adapter = ProductSearchAdapter { product ->
            val existing = billItemAdapter.getItems().firstOrNull { it.productId == product.id }
            if (existing != null) {
                Toast.makeText(requireContext(), "${product.name} already added", Toast.LENGTH_SHORT).show()
                return@ProductSearchAdapter
            }
            billItemAdapter.addItem(BillItem(billId = 0, productId = product.id, productName = product.name,
                productType = product.type, quantity = 1, buyPrice = product.buyPrice, sellPrice = product.sellPrice))
            updateTotal()
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        fun doSearch(q: String) {
            vm.searchProducts(q) { results ->
                adapter.submitList(results)
                tvEmpty.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        doSearch("")
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { doSearch(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })
        AlertDialog.Builder(requireContext())
            .setTitle("Search Products")
            .setView(dlgView)
            .setNegativeButton("Done", null)
            .show()
    }

    private fun updateTotal() {
        val total = billItemAdapter.getItems().sumOf { it.quantity * it.sellPrice }
        binding.tvTotalAmount.text = "Total: ₹${String.format("%.2f", total)}"
        binding.btnSaveDraft.isEnabled = billItemAdapter.getItems().isNotEmpty()
        binding.btnSaveBill.isEnabled = billItemAdapter.getItems().isNotEmpty()
    }

    private fun getBillFromForm(): Bill? {
        val customer = binding.etCustomerName.text.toString().trim()
        if (customer.isEmpty()) { Toast.makeText(requireContext(), "Enter customer name", Toast.LENGTH_SHORT).show(); return null }
        if (billItemAdapter.getItems().isEmpty()) { Toast.makeText(requireContext(), "Add at least one item", Toast.LENGTH_SHORT).show(); return null }
        val items = billItemAdapter.getItems()
        val total = items.sumOf { it.quantity * it.sellPrice }
        val profit = items.sumOf { it.quantity * (it.sellPrice - it.buyPrice) }
        return Bill(shopId = 0, customerName = customer, status = "SAVED", totalAmount = total, totalProfit = profit)
    }

    private fun saveBill(status: String) {
        val bill = getBillFromForm() ?: return
        val items = billItemAdapter.getItems()
        vm.saveBill(bill.copy(status = status), items) { billId ->
            currentBillId = billId.toInt()
            val savedBill = bill.copy(id = billId.toInt(), status = status)
            generatedPdfFile = PdfGenerator.generateBillPdf(requireContext(), savedBill, items,
                PreferenceHelper.getShopName(requireContext()), PreferenceHelper.getShopType(requireContext()))
            Toast.makeText(requireContext(), "Bill ${if (status=="DRAFT") "saved as Draft" else "saved!"}", Toast.LENGTH_LONG).show()
            binding.layoutShareActions.visibility = View.VISIBLE
        }
    }

    private fun shareViaWhatsApp() {
        val file = generatedPdfFile ?: run { Toast.makeText(requireContext(),"Save bill first",Toast.LENGTH_SHORT).show(); return }
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.whatsapp")
        }
        try { startActivity(intent) } catch (e: Exception) {
            Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareViaMessage() {
        val file = generatedPdfFile ?: run { Toast.makeText(requireContext(),"Save bill first",Toast.LENGTH_SHORT).show(); return }
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Bill via"))
    }

    private fun clearBill() {
        binding.etCustomerName.text?.clear()
        billItemAdapter.clear()
        updateTotal()
        binding.layoutShareActions.visibility = View.GONE
        generatedPdfFile = null
        currentBillId = -1
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
