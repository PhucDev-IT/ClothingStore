package com.example.clothingstoreapp.FragmentLayout

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothingstoreapp.API.ApiGHN
import com.example.clothingstoreapp.Adapter.RvCustomSimpleItemAdapter
import com.example.clothingstoreapp.Interface.ClickObjectInterface
import com.example.clothingstoreapp.Model.District
import com.example.clothingstoreapp.Model.DistrictsJson
import com.example.clothingstoreapp.Model.Province
import com.example.clothingstoreapp.Model.ProvincesJson
import com.example.clothingstoreapp.Model.Ward
import com.example.clothingstoreapp.Model.WardJson
import com.example.clothingstoreapp.ViewModel.NewAddressViewModel
import com.example.clothingstoreapp.databinding.FragmentSelectNewAddressBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SelectNewAddressFragment : Fragment() {
    private lateinit var _binding:FragmentSelectNewAddressBinding
    private val binding get() = _binding
    private val sharedViewModel:NewAddressViewModel by activityViewModels()
    private lateinit var listProvince: List<Province>
    private lateinit var listDistricts: List<District>
    private lateinit var listWard: List<Ward>

    private lateinit var apiService: ApiGHN
    private var mDisposable: Disposable? = null

    private var nextStatus:String = "Tỉnh/Thành phố";

    private lateinit var adapter:RvCustomSimpleItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectNewAddressBinding.inflate(inflater,container,false)

        apiService = ApiGHN.create()

        initView()
        callApiProvinces()
        handleClick()
        return binding.root
    }

    private fun initView() {

        binding.tvCurrentName.text = nextStatus
        sharedViewModel.district.observe(viewLifecycleOwner){
            if(it==null){
                binding.lnDistrict.visibility = View.GONE
                binding.tvDistrict.text = ""
            }else{
                binding.tvDistrict.text = it.districtName
                binding.lnDistrict.visibility = View.VISIBLE
            }
        }
        sharedViewModel.province.observe(viewLifecycleOwner){
            if(it==null){
                binding.lnProvince.visibility = View.GONE
                binding.tvProvince.text = ""
            }else{
                binding.tvProvince.text = it.ProvinceName
                binding.lnProvince.visibility = View.VISIBLE
            }
        }
        sharedViewModel.ward.observe(viewLifecycleOwner){
            if(it==null){
                binding.lnWard.visibility = View.GONE
                binding.tvWard.text = ""
            }else{
                binding.tvWard.text = it.WardName
                binding.lnWard.visibility = View.VISIBLE
            }
        }

        adapter = RvCustomSimpleItemAdapter(emptyList(),object : ClickObjectInterface<String>{
            @SuppressLint("SetTextI18n")
            override fun onClickListener(t: String) {
                when(nextStatus){
                    "Tỉnh/Thành phố" -> {
                        listProvince.find { it.ProvinceName == t }?.let {
                            sharedViewModel.setProvince(it)
                            sharedViewModel.setInformation("$t\n")
                            sharedViewModel.setDistrict(null)
                            sharedViewModel.setWard(null)
                            nextStatus = "Quận/Huyện"
                            binding.tvCurrentName.text = "Quận/Huyện"
                            it.ProvinceID?.let { it1 -> callApiDistrict(it1) }
                        }
                    }

                    "Quận/Huyện" ->{
                        listDistricts.find { it.districtName == t }?.let {
                            sharedViewModel.setDistrict(it)
                            sharedViewModel.insertInformation("$t\n")
                            binding.tvCurrentName.text = "Phường/Xã"
                            nextStatus =  "Phường/Xã"
                            it.districtID?.let { it1 -> callApiWard(it1) }
                        }
                    }

                    "Phường/Xã" ->{
                        listWard.find { it.WardName == t }?.let {
                            sharedViewModel.setWard(it)
                            sharedViewModel.insertInformation(t)
                            requireActivity().onBackPressed()

                        }
                    }

                }
            }
        })

        val linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rvAddress.layoutManager = linearLayoutManager
        binding.rvAddress.adapter = adapter
    }


    //Lấy danh sách thành phố
    private fun callApiProvinces() {
        nextStatus = "Tỉnh/Thành phố"

        apiService.callApiProvinces()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ProvincesJson>{
                override fun onSubscribe(d: Disposable) {
                    mDisposable = d
                }

                override fun onNext(t: ProvincesJson) {
                    t.data?.let { listProvince = it }

                }

                override fun onError(e: Throwable) {
                    Log.e(TAG,"Fail call api province: ${e.message}")
                }

                override fun onComplete() {
                    val names = listProvince.mapNotNull { it.ProvinceName }
                    adapter.setData(names)
                    mDisposable?.dispose()
                }
            })





    }

    //Lấy danh sách quận huyện
    private fun callApiDistrict(code: Int) {
      apiService.callApiDistricts(code)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Observer<DistrictsJson>{
              override fun onSubscribe(d: Disposable) {
                  mDisposable = d
              }

              override fun onNext(t: DistrictsJson) {
                  t.data?.let { listDistricts = it }
              }

              override fun onError(e: Throwable) {
                  Log.e(TAG,"Fail call api province: ${e.message}")
              }

              override fun onComplete() {
                  val names =
                      listDistricts.map { it.districtName!! } // Lấy danh sách tên từ danh sách ProvincesJson

                  adapter.setData(names)
                  mDisposable?.dispose()
              }
          })

    }

    //Lấy tên phường xã
    private fun callApiWard(code: Int) {
      apiService.callApiWard(code)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(object : Observer<WardJson>{
              override fun onSubscribe(d: Disposable) {
                  mDisposable = d
              }

              override fun onNext(t: WardJson) {
                  t.data.let { listWard = it }
              }

              override fun onError(e: Throwable) {
                  Log.e(TAG,"Fail call api province: ${e.message}")
              }

              override fun onComplete() {
                  val names = listWard.map { it.WardName!! } // Lấy danh sách tên từ danh sách ProvincesJson
                  adapter.setData(names)
                  mDisposable?.dispose()
              }
          })
        
    }


    private fun handleClick(){
        binding.tvThietLapLai.setOnClickListener {
            sharedViewModel.setProvince(null)
            sharedViewModel.setDistrict(null)
            sharedViewModel.setWard(null)
            callApiProvinces()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}