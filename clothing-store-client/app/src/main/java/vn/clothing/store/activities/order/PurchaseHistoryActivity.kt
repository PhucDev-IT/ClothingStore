package vn.clothing.store.activities.order

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.common.IntentData
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.databinding.ActivityPurchaseHistoryBinding
import vn.clothing.store.interfaces.PurchasedHistoryContract
import vn.clothing.store.networks.response.OrderResponseModel
import vn.clothing.store.presenter.PurchaseHistoryPresenter

class PurchaseHistoryActivity : BaseActivity(), PurchasedHistoryContract.View {
    private lateinit var binding:ActivityPurchaseHistoryBinding
    private var presenter:PurchaseHistoryPresenter? = null
    private var currentTab:String  = "PENDING"
    private var indexTab:Int = 0

    override fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        presenter = PurchaseHistoryPresenter(this)
        binding.rvOrders.adapter = presenter?.adapter
        binding.rvOrders.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        val dividerItemDecoration = DividerItemDecoration(binding.rvOrders.context, DividerItemDecoration.VERTICAL)
        binding.rvOrders.addItemDecoration(dividerItemDecoration)
    }

    override fun populateData() {
        setUpUi()
        presenter?.getFirstOrder(currentTab)
    }

    override fun setListener() {
        binding.header.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override val layoutView: View
        get() {
            binding = ActivityPurchaseHistoryBinding.inflate(layoutInflater)
            return binding.root
        }


    private fun setUpUi(){

        //--------------------- SET UP TAB LAYOUT ---------------------------------
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    indexTab = it.position
                    currentTab = when (it.position) {
                        0 ->  "PENDING"
                        1 -> "PACKING"
                        2 -> "DELIVERED"
                        3-> "CANCELLED"
                        else -> "PENDING"
                    }
                    presenter?.getFirstOrder(currentTab)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Mario Velasco's code
        // Mario Velasco's code
        binding.tabLayout.post {
            val tabLayoutWidth: Int = binding.tabLayout.width
            val metrics = DisplayMetrics()
            this@PurchaseHistoryActivity.windowManager.getDefaultDisplay().getMetrics(metrics)
            val deviceWidth = metrics.widthPixels
            if (tabLayoutWidth < deviceWidth) {
                binding.tabLayout.tabMode = TabLayout.MODE_FIXED
                binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            } else {
                binding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            }
        }

        binding.tabLayout.getTabAt(0)?.select()
    }


    override fun onShowLoading() {
        binding.shimmerLayout.startLayoutAnimation()
        binding.shimmerLayout.visibility = View.VISIBLE
    }

    override fun onHideLoading() {
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
    }

    override fun onShowError(message: String) {
        PopupDialog.showDialog(this,PopupDialog.PopupType.NOTIFICATION,null,message){}
    }

    override fun onNotFoundItem() {
        binding.icNotFound.visibility = View.VISIBLE
    }

    override fun onRequestSeenDetail(order: OrderResponseModel) {
        val intent = Intent(this,TrackOrderActivity::class.java)
        intent.putExtra(IntentData.KEY_ORDER,order)
        startActivity(intent)
    }
}