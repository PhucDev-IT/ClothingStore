package vn.clothing.store.activities.order

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
import vn.clothing.store.databinding.ActivityPurchaseHistoryBinding
import vn.clothing.store.interfaces.PurchasedHistoryContract
import vn.clothing.store.presenter.PurchaseHistoryPresenter

class PurchaseHistoryActivity : BaseActivity(), PurchasedHistoryContract.View {
    private lateinit var binding:ActivityPurchaseHistoryBinding
    private var presenter:PurchaseHistoryPresenter? = null
    private var currentTab:String  = "PENDING"

    override fun initView() {
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
        presenter?.getOrders(currentTab)
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

    }
}