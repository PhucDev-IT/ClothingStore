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
import com.github.mikephil.charting.formatter.ValueFormatter
import vn.mobile.clothing.R
import vn.mobile.clothing.common.AppManager
import vn.mobile.clothing.databinding.FragmentDashboardBinding
import vn.mobile.clothing.models.EOrderStatus
import vn.mobile.clothing.network.ApiService
import vn.mobile.clothing.network.response.ResponseModel
import vn.mobile.clothing.network.response.StatisticalStatusOrderResModel
import vn.mobile.clothing.network.rest.BaseCallback


class DashboardFragment : Fragment() {
    private lateinit var _binding:FragmentDashboardBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater,container,false)
        loadData()
        return binding.root
    }


    private fun loadData(){
        ApiService.APISERVICE.getService(AppManager.token).getStatistical().enqueue(object : BaseCallback<ResponseModel<List<StatisticalStatusOrderResModel>>>(){
            override fun onSuccess(model: ResponseModel<List<StatisticalStatusOrderResModel>>) {
                if(model.success && model.data!=null){
                    setUp(model.data!!)
                }
            }

            override fun onError(message: String) {

            }
        })
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

}