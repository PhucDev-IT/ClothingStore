package vn.clothing.store.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import vn.clothing.store.R
import vn.clothing.store.activities.common.BaseActivity
import vn.clothing.store.adapter.RvProductHomeAdapter
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.IntentData
import vn.clothing.store.databinding.ActivitySearchProductBinding
import vn.clothing.store.interfaces.SearchProductContract
import vn.clothing.store.models.Category
import vn.clothing.store.models.Product
import vn.clothing.store.presenter.SearchProductPresenter

class SearchProductActivity : BaseActivity() , SearchProductContract.View{
   private lateinit var binding:ActivitySearchProductBinding
   private lateinit var presenter:SearchProductPresenter
    private var adapterProduct:RvProductHomeAdapter?=null

    override fun initView() {
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        presenter = SearchProductPresenter(this)
        adapterProduct = RvProductHomeAdapter(emptyList()) { prod ->
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra(IntentData.KEY_PRODUCT,prod)
            startActivity(intent)
        }
        binding.rvProducts.adapter = adapterProduct
    }

    override fun populateData() {
        presenter.getCategories()
    }

    override fun setListener() {
        binding.btnBack.setOnClickListener { finish() }
    }

    override val layoutView: View
        get() {
            binding = ActivitySearchProductBinding.inflate(layoutInflater)
            return  binding.root
        }


    private fun initCategory(categories:List<Category>){
        var index = 0
        val inflater = LayoutInflater.from(this)
        val radioButtonLayout = R.layout.viewholder_category
        categories.forEach { value ->
            val radioButton =
                inflater.inflate(radioButtonLayout, binding.rdoGroupCategory, false) as RadioButton

            radioButton.text = value.name
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    selectedCategory( categories.find { it.name == buttonView.text.toString() }?.id)
                }
            }
            binding.rdoGroupCategory.addView(radioButton)
            if (index == 0) {
                radioButton.isChecked = true
            }
            index++
        }
    }


    private fun selectedCategory(categoryId:Int?){
        if(categoryId!=null){
            presenter?.getProductByCategory(categoryId)
        }else{
            CoreConstant.showToast(this,getString(R.string.has_error_please_retry),
                CoreConstant.ToastType.ERROR)
        }
    }


    override fun onResultCategories(categories: List<Category>) {
        initCategory(categories)
    }

    override fun onShowLoading() {
        binding.progressLoading.visibility = View.VISIBLE
    }

    override fun onHiddenLoading() {
        binding.progressLoading.visibility = View.GONE
    }

    override fun onShowToast(message: String?, type: CoreConstant.ToastType) {
        CoreConstant.showToast(this,message?:getString(R.string.has_error_please_retry),type)
    }

    override fun onResultProducts(products: List<Product>) {
        adapterProduct?.setData(products)
    }
}