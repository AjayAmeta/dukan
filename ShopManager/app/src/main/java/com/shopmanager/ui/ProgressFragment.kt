package com.shopmanager.ui
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.shopmanager.R
import com.shopmanager.databinding.FragmentProgressBinding
import com.shopmanager.viewmodel.MainViewModel

class ProgressFragment : Fragment() {
    private var _b: FragmentProgressBinding? = null
    private val b get() = _b!!
    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentProgressBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        loadSummary()
        loadWeekly()
        b.tabPeriod.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) { 0 -> loadWeekly(); 1 -> loadMonthly(); 2 -> loadYearly() }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun loadSummary() {
        vm.getCurrentMonthSummary { sales, profit, count ->
            b.tvMonthSales.text = "₹${String.format("%.2f", sales)}"
            b.tvMonthProfit.text = "₹${String.format("%.2f", profit)}"
            b.tvMonthBills.text = "$count Bills"
        }
    }

    private fun setupChart(data: List<MainViewModel.ProgressData>) {
        val labels = data.map { it.label }
        val salesEntries = data.mapIndexed { i, d -> BarEntry(i.toFloat(), d.sales) }
        val profitEntries = data.mapIndexed { i, d -> BarEntry(i.toFloat(), d.profit) }

        val salesSet = BarDataSet(salesEntries, "Sales").apply {
            color = Color.rgb(63, 81, 181); valueTextColor = Color.BLACK; valueTextSize = 10f
        }
        val profitSet = BarDataSet(profitEntries, "Profit").apply {
            color = Color.rgb(76, 175, 80); valueTextColor = Color.BLACK; valueTextSize = 10f
        }

        val groupSpace = 0.3f; val barSpace = 0.05f; val barWidth = 0.3f
        val barData = BarData(salesSet, profitSet).apply { this.barWidth = barWidth }
        
        fun configChart(chart: com.github.mikephil.charting.charts.BarChart) {
            chart.data = barData
            chart.groupBars(-0.5f, groupSpace, barSpace)
            chart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textSize = 10f
            }
            chart.axisLeft.apply { axisMinimum = 0f; textSize = 10f }
            chart.axisRight.isEnabled = false
            chart.description.isEnabled = false
            chart.legend.isEnabled = true
            chart.setFitBars(true)
            chart.animateY(800)
            chart.invalidate()
        }
        configChart(b.chartSales)
        configChart(b.chartProfit)
    }

    private fun loadWeekly() { vm.getWeeklyProgress { setupChart(it) } }
    private fun loadMonthly() { vm.getMonthlyProgress { setupChart(it) } }
    private fun loadYearly() { vm.getYearlyProgress { setupChart(it) } }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
