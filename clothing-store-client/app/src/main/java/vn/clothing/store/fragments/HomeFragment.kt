package vn.clothing.store.fragments

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import vn.clothing.store.R
import vn.clothing.store.activities.NotificationActivity
import vn.clothing.store.activities.ProductDetailsActivity
import vn.clothing.store.activities.SearchProductActivity
import vn.clothing.store.adapter.RvProductHomeAdapter
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.common.IntentData
import vn.clothing.store.databinding.FragmentHomeBinding
import vn.clothing.store.interfaces.HomeContract
import vn.clothing.store.models.Category
import vn.clothing.store.models.Product
import vn.clothing.store.presenter.HomePresenter

class HomeFragment : Fragment(), HomeContract.View {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private var presenter: HomeContract.Presenter? = null
    private var adapterProduct:RvProductHomeAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        presenter = HomePresenter(this)
        initSlider()
        flashSale()
        initUI()
        return binding.root
    }

    private fun initUI(){
        presenter?.getCategories()
        adapterProduct = RvProductHomeAdapter(emptyList()) { prod ->
            val intent = Intent(requireActivity(), ProductDetailsActivity::class.java)
            intent.putExtra(IntentData.KEY_PRODUCT,prod)
            startActivity(intent)
        }
        setListener()
        binding.rvProducts.adapter = adapterProduct
    }

    private fun setListener(){
        binding.swipRefresh.setOnRefreshListener {
            Handler().postDelayed({
                if (binding.radioGroup.childCount > 0) {
                    val firstRadioButton = binding.radioGroup.getChildAt(0) as RadioButton
                    firstRadioButton.isChecked = true
                }
                binding.swipRefresh.isRefreshing = false
            },1500)
        }

        binding.tvSearch.setOnClickListener{
            val intent = Intent(requireActivity(), SearchProductActivity::class.java)
            startActivity(intent)
        }

        binding.llContainerNotification.setOnClickListener {
            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        }
    }


    private fun initSlider() {
        val imageList = ArrayList<SlideModel>() // Create image list

        imageList.add(SlideModel(R.drawable.slider1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.slider2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.slider3, ScaleTypes.CENTER_CROP))
        binding.imageSlider.setImageList(imageList)
    }

    private fun flashSale() {
        object : CountDownTimer(3 * 60 * 60 * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (60 * 60 * 1000) // tính giờ
                val minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000) // tính phút
                val seconds = (millisUntilFinished % (60 * 1000)) / 1000 // tính giây

                binding.tvHours.text = hours.toString()
                binding.tvMinute.text = minutes.toString()
                binding.tvSecond.text = seconds.toString()
            }

            override fun onFinish() {
                binding.lnTimeSale.visibility = View.GONE
            }
        }.start()

    }

    private fun initCategory(categories:List<Category>){
        var index = 0
        val inflater = LayoutInflater.from(context)
        val radioButtonLayout = R.layout.viewholder_category
        categories.forEach { value ->
            val radioButton =
                inflater.inflate(radioButtonLayout, binding.radioGroup, false) as RadioButton

            radioButton.text = value.name
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    selectedCategory( categories.find { it.name == buttonView.text.toString() }?.id)
                }
            }
            binding.radioGroup.addView(radioButton)
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
            CoreConstant.showToast(requireActivity(),getString(R.string.has_error_please_retry),CoreConstant.ToastType.ERROR)
        }
    }


    //===================================================
    //    region HomeContract.View
    //==================================================

    override fun onShowLoading() {
        binding.progressCircular.visibility = View.VISIBLE
    }

    override fun onHiddenLoading() {
        binding.progressCircular.visibility = View.GONE
    }

    override fun onShowCategories(categories: List<Category>) {
        initCategory(categories)
    }

    override fun onShowToast(message: String?, type: CoreConstant.ToastType) {
        CoreConstant.showToast(requireActivity(),message?:getString(R.string.has_error_please_retry),type)
    }

    override fun onResultProducts(products: List<Product>) {
       adapterProduct?.setData(products)
    }

    //================================================
    //  endregion HomeContract.View
    //===============================================
}