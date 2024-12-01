package vn.mobile.clothing.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import vn.mobile.clothing.R
import vn.mobile.clothing.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {
    private lateinit var _binding:FragmentDashboardBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater,container,false)
        setUp()
        return binding.root
    }

    private fun setUp(){
        val barEntries = mutableListOf<BarEntry>()
        barEntries.add(BarEntry(1f, 10f)) // (x, y) = (1, 10)
        barEntries.add(BarEntry(2f, 20f))
        barEntries.add(BarEntry(3f, 30f))
        barEntries.add(BarEntry(4f, 40f))

        val barDataSet = BarDataSet(barEntries, "Sales")
        barDataSet.color = resources.getColor(R.color.color_primary_dark) // Màu cho thanh
        barDataSet.valueTextColor = resources.getColor(R.color.black) // Màu giá trị
        barDataSet.valueTextSize = 12f // Kích thước chữ

        val barData = BarData(barDataSet)
        binding.barChart.data = barData
        binding.barChart.description.text = "Sales by Quarter" // Thêm mô tả biểu đồ
        binding.barChart.animateY(1000) // Hiệu ứng chuyển động theo trục Y

        // Tùy chỉnh Legend (chú thích)
        val legend = binding.barChart.legend
        legend.isEnabled = true
        legend.textColor = resources.getColor(R.color.black)
        legend.textSize = 14f
        legend.form = Legend.LegendForm.CIRCLE
        val xAxis = binding.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f // Khoảng cách giữa các giá trị
        xAxis.setDrawGridLines(false) // Ẩn đường lưới

        // Tùy chỉnh nhãn
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Q1", "Q2", "Q3", "Q4"))


        val yAxis = binding.barChart.axisLeft
        yAxis.axisMinimum = 0f // Giá trị tối thiểu
        yAxis.setDrawGridLines(true) // Hiện đường lưới

        // Ẩn trục Y bên phải nếu không cần
        binding.barChart.axisRight.isEnabled = false

    }
}