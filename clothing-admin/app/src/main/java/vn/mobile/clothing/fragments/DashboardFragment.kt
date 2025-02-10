package vn.mobile.clothing.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import vn.mobile.clothing.R
import vn.mobile.clothing.adapters.RvTopProductAdapter
import vn.mobile.clothing.common.AppManager
import vn.mobile.clothing.databinding.FragmentDashboardBinding
import vn.mobile.clothing.models.EOrderStatus
import vn.mobile.clothing.network.ApiService
import vn.mobile.clothing.network.response.ResponseModel
import vn.mobile.clothing.network.response.StatisticalRevenueYear
import vn.mobile.clothing.network.response.StatisticalStatusOrderResModel
import vn.mobile.clothing.network.response.TopProductResponseModel
import vn.mobile.clothing.network.rest.BaseCallback
import vn.mobile.clothing.viewmodel.DashboardViewModel
import java.util.Calendar


class DashboardFragment : Fragment() {
    private lateinit var _binding:FragmentDashboardBinding
    private val binding get() = _binding
    private lateinit var adapterTopProduct : RvTopProductAdapter
    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater,container,false)

        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]


        adapterTopProduct = RvTopProductAdapter(emptyList())
        binding.rvTopproduct.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTopproduct.adapter = adapterTopProduct
        val dividerItemDecoration = DividerItemDecoration(binding.rvTopproduct.context, DividerItemDecoration.VERTICAL)
        binding.rvTopproduct.addItemDecoration(dividerItemDecoration)
        observe()
        return binding.root
    }


    private fun observe(){
        viewModel.topProducts.observe(viewLifecycleOwner){
            adapterTopProduct.setData(it)
        }
        viewModel.statisticalOrders.observe(viewLifecycleOwner){
            setUp(it)
        }

        viewModel.statisticalRevenue.observe(viewLifecycleOwner){
            statisticalRevenueOfYear(it)
        }
    }


    private fun setUp(list: List<StatisticalStatusOrderResModel>) {
        val barEntries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        val mapOrders = mutableMapOf<String, Int>()
        mapOrders[EOrderStatus.PENDING.name] = 0
        mapOrders[EOrderStatus.PACKING.name] = 0
        mapOrders[EOrderStatus.SHIPPING.name] = 0
        mapOrders[EOrderStatus.DELIVERED.name] = 0

        list.map {
            if (mapOrders.containsKey(it.status)) {
                mapOrders[it.status] = it.count
            }
        }

        var i = 0
        mapOrders.map {
            barEntries.add(BarEntry(i.toFloat(), it.value.toFloat()))
            labels.add(EOrderStatus.findName(it.key, requireContext()))
            i++
        }

        val colors = listOf(
            resources.getColor(R.color.info),
            resources.getColor(R.color.warning),
            resources.getColor(R.color.blue),
            resources.getColor(R.color.success)
        )

        val barDataSet = BarDataSet(barEntries, "Thống kê đơn hàng")
        barDataSet.colors = colors
        barDataSet.valueTextColor = resources.getColor(R.color.black)
        barDataSet.valueTextSize = 12f

        // Hiển thị số nguyên trên các thanh
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val barData = BarData(barDataSet)
        binding.barChart.data = barData
        binding.barChart.description.isEnabled = false
        binding.barChart.animateY(1000)

        val legend = binding.barChart.legend
        legend.isEnabled = true
        legend.textColor = resources.getColor(R.color.black)
        legend.textSize = 14f
        legend.form = Legend.LegendForm.CIRCLE

        val xAxis = binding.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val yAxis = binding.barChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.setDrawGridLines(true)
        yAxis.granularity = 1f // Bước nhảy là 1
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        binding.barChart.axisRight.isEnabled = false
    }


    //Biểu đồ tăng trưởng
    private fun statisticalRevenueOfYear(list:List<StatisticalRevenueYear>) {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Tháng hiện tại (0-11 nên cần +1)
        val currentYear = calendar.get(Calendar.YEAR)

        // Dữ liệu mẫu
        val entries = ArrayList<Entry>()
        for(i in 1..currentMonth){
            val revenue = list.find { it.month == i }?.sale ?: 0.0f
            entries.add(Entry(i.toFloat(), revenue))
        }

        val lineDataSet = LineDataSet(entries, "Doanh số bán hàng năm $currentYear")
        lineDataSet.fillAlpha = 110
        lineDataSet.color = resources.getColor(R.color.colorPrimary)
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.setCircleColor(Color.RED)
        val lineData = LineData(lineDataSet)
        binding.lineChart.data = lineData

        // Tùy chỉnh trục X để chỉ hiển thị giá trị nguyên
        val xAxis = binding.lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

       xAxis.granularity = 1f // Đảm bảo mỗi bước là 1
        xAxis.position = XAxis.XAxisPosition.BOTTOM // Đặt trục X ở dưới

        binding.lineChart.invalidate() // Refresh biểu đồ
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }


}