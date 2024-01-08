package com.example.clothingstoreapp.FragmentLayout

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.clothingstoreapp.Activity.ProductDetailsScreen
import com.example.clothingstoreapp.Activity.SearchScreen
import com.example.clothingstoreapp.Adapter.RvProductHomeAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.PaginationScrollListener
import com.example.clothingstoreapp.Model.Product
import com.example.clothingstoreapp.R
import com.example.clothingstoreapp.Service.ProductService
import com.example.clothingstoreapp.databinding.FragmentHomeBinding
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale


class HomeFragment : Fragment(), View.OnClickListener {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private val categories: List<String> =
        listOf("All", "Mới nhất", "Phổ biến", "Nam", "Nữ", "Đặc biệt")
    private var countExit = 0
    private lateinit var productAdapter: RvProductHomeAdapter
    private var isLoading: Boolean = false
    private var isLastPage = false
    private lateinit var productService: ProductService
    private val db = Firebase.firestore
    private val PERMISSION_ID = 1000
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        productService = ProductService(db)
        flashSale()
        initSlider()
        initViewProduct()
        initCategory()
        handleClick()
        return binding.root
    }


    private fun initCategory() {

        var index = 0
        val inflater = LayoutInflater.from(context)
        val radioButtonLayout = R.layout.viewholder_category_two
        categories.forEach { value ->
            val radioButton =
                inflater.inflate(radioButtonLayout, binding.radioGroup, false) as RadioButton

            radioButton.text = value
            radioButton.setOnCheckedChangeListener { buttonView, ischecked ->
                if (ischecked) {
                    selectedCategory(buttonView.text.toString())

                }
            }
            binding.radioGroup.addView(radioButton)
            if (index == 0) {
                radioButton.isChecked = true
            }
            index++
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

    private fun initViewProduct() {
        productAdapter = RvProductHomeAdapter(emptyList(), object : ClickObjectInterface<Product> {
            override fun onClickListener(t: Product) {
                val intent = Intent(context, ProductDetailsScreen::class.java)
                intent.putExtra("product", t)
                startActivity(intent)
            }
        })

        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 2)
        binding.rvProducts.adapter = productAdapter
        binding.rvProducts.layoutManager = linearLayoutManager

        binding.rvProducts.addOnScrollListener(object :
            PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItem() {

            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }
        })
    }

    private fun selectedCategory(name: String) {
        isLoading = true
        binding.progressCircular.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.GONE

        when (name) {
            "All" -> {
                productService.selectAllFirstPage { list ->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false
                    binding.swipRefresh.isRefreshing = false
                }
            }

            "Mới nhất" -> {
                productService.selectNewest { list ->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false
                }
            }

            "Phổ biến" -> {
                productService.selectPopular { list ->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false
                }
            }

            "Nam" -> {
                productService.selectByTags("NAM") { list ->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false

                }
            }

            "Nữ" -> {
                productService.selectByTags("NỮ") { list ->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false

                }
            }

            else -> {
                productService.selectAllFirstPage { list ->
                    productAdapter.setData(list)

                    binding.progressCircular.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    isLoading = false
                }
            }
        }

    }

    private fun handleClick() {
        binding.tvSearch.setOnClickListener(this)
        binding.tvSeAllCategory.setOnClickListener(this)
        binding.lnLocation.setOnClickListener(this)
        binding.swipRefresh.setOnRefreshListener {
            selectedCategory("All")
        }


    }


    override fun onClick(v: View?) {
        when (v) {
            binding.tvSeAllCategory, binding.tvSearch -> {
                val intent = Intent(context, SearchScreen::class.java)
                startActivity(intent)
            }

            binding.lnLocation -> {
                requestLocation()
            }
        }
    }

    //LẤY VỊ TRÍ HIỆN TẠI
    fun requestLocation() {
        // Kiểm tra xem ứng dụng đã có quyền truy cập vị trí chưa
        if (!isLocationPermissionGranted()) {
            // Nếu chưa có quyền, yêu cầu quyền truy cập vị trí từ người dùng
            requestPermission()
        } else {
            // Nếu đã có quyền, tiến hành lấy vị trí
            getLocation()
        }

    }

    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context,"Gọi yêu cầu",Toast.LENGTH_SHORT).show()
            requestPermission()
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Đã lấy được vị trí
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Gọi hàm để xử lý thông tin vị trí ở đây
                    handleLocationInfo(latitude, longitude)
                }
            }
            .addOnFailureListener { e ->
               Toast.makeText(context,"Lỗi: ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleLocationInfo(latitude: Double, longitude: Double) {
        // Thực hiện xử lý vị trí như lấy tên thành phố, quận huyện, phường xã từ vị trí
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

        if (addresses?.isNotEmpty() == true) {
            val address: Address = addresses[0]
            val cityName = address.locality // Tên thành phố
            val district = address.subAdminArea // Tên quận/huyện
            val ward = address.subLocality // Tên phường/xã
            Toast.makeText(context,"$cityName/$district",Toast.LENGTH_SHORT).show()
            // Sử dụng thông tin địa lý ở đây (cityName, district, ward)
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_ID
        )
    }

}