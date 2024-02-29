package com.example.clothingstoreapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clothingstoreapp.Model.AddressModel
import com.example.clothingstoreapp.Model.District
import com.example.clothingstoreapp.Model.Province
import com.example.clothingstoreapp.Model.Ward

class NewAddressViewModel:ViewModel() {

    private val _province  = MutableLiveData<Province?>()
    val province: LiveData<Province?> = _province

    private val _district  = MutableLiveData<District?>()
    val district: LiveData<District?> = _district

    private val _ward  = MutableLiveData<Ward?>()
    val ward: LiveData<Ward?> = _ward

    private val _informationAddress = MutableLiveData<String?>()
    val informationAddress:LiveData<String?> = _informationAddress


    fun setProvince(province:Province?){
        this._province.value = province
    }

    fun setDistrict(district:District?){
        this._district.value = district
    }

    fun setWard(ward:Ward?){
        this._ward.value = ward
    }

    fun setInformation(value:String?){
        this._informationAddress.value = value
    }

    fun insertInformation(value:String){
        this._informationAddress.value = this._informationAddress.value + value
    }


    fun checkEmpty():Boolean{
        return _province.value!=null && _district.value!=null && _ward.value!=null
    }


}