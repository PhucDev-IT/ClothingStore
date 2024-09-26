package vn.clothing.store.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.clothing.store.adapter.RvRowNameAddressAdapter
import vn.clothing.store.common.AppManager
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.interfaces.NewDeliveryInfoContract
import vn.clothing.store.models.DeliveryInformation
import vn.clothing.store.models.DistrictModel
import vn.clothing.store.models.ProvinceModel
import vn.clothing.store.models.WardModel
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class NewDeliveryInfoPresenter(private var view: NewDeliveryInfoContract.View?) :
    NewDeliveryInfoContract.Presenter {
    private var delivery = DeliveryInformation()
    private var provinces: List<ProvinceModel> = listOf()
    private var mapFinish: MutableMap<String, Boolean> =
        mutableMapOf("provinces" to false, "districts" to false, "wards" to false)

    var adapter: RvRowNameAddressAdapter

    init {
        adapter = RvRowNameAddressAdapter(null) {
            if (mapFinish["provinces"] == false) {
                view?.onSelectedProvinces(it.second)
                mapFinish["provinces"] = true
                delivery.provinceId = it.first
                delivery.details += it.second + ", "
                getDistricts(it.first)
            } else if (mapFinish["districts"] == false) {
                view?.onSelectedDistrict(it.second)
                mapFinish["districts"] = true
                getWards(it.first)
                delivery.details += it.second + ", "
                delivery.districtId = it.first
            } else  if (mapFinish["wards"] == false){
                view?.onSelectedWard(it.second)
                mapFinish["wards"] = true
                delivery.wardId = it.first
                delivery.details += it.second
            }
        }
    }

    override fun resetData() {
        delivery = DeliveryInformation()
        mapFinish["provinces"] = false
        mapFinish["districts"] = false
        mapFinish["wards"] = false
        getProvinces()
    }

    override fun addNewDeliveryInfo(name: String, phone: String) {
        if (mapFinish["provinces"] == false || mapFinish["districts"] == false || mapFinish["wards"] == false) {
            view?.onHideLoading()
            view?.showError("Vui lòng chọn địa chỉ")
            return
        }
        delivery.fullName = name
        delivery.numberPhone = phone
        addAddress(delivery)
    }

    override fun getProvinces() {
        if (provinces.isNotEmpty()) {
            val provincePairList: List<Pair<String, String>> = provinces.map { province ->
                Pair(province.code!!, province.nameWithType!!)
            }
            adapter.setData(provincePairList)
            return
        }
        view?.onShowLoading()

        APISERVICE.getService().getAllProvince()
            .enqueue(object : BaseCallback<ResponseModel<List<ProvinceModel>>>() {
                override fun onSuccess(model: ResponseModel<List<ProvinceModel>>) {
                    view?.onHideLoading()
                    if (model.success && model.data != null) {
                        val provincePairList: List<Pair<String, String>> =
                            model.data!!.map { province ->
                                Pair(province.code!!, province.nameWithType!!)
                            }
                        provinces = model.data!!
                        adapter.setData(provincePairList)
                    }
                }

                override fun onError(message: String) {
                    view?.onHideLoading()
                }
            })
    }

    private fun getDistricts(id: String) {
        view?.onShowLoading()

        APISERVICE.getService().getDistrictByProvinceId(id)
            .enqueue(object : BaseCallback<ResponseModel<List<DistrictModel>>>() {
                override fun onSuccess(model: ResponseModel<List<DistrictModel>>) {
                    view?.onHideLoading()
                    if (model.success && model.data != null) {
                        val provincePairList: List<Pair<String, String>> =
                            model.data!!.map { province ->
                                Pair(province.code!!, province.nameWithType!!)
                            }
                        adapter.setData(provincePairList)
                    }
                }

                override fun onError(message: String) {
                    view?.onHideLoading()
                }
            })
    }

    private fun getWards(id: String) {
        view?.onShowLoading()
        APISERVICE.getService().getWardByDistrictId(id)
            .enqueue(object : BaseCallback<ResponseModel<List<WardModel>>>() {
                override fun onSuccess(model: ResponseModel<List<WardModel>>) {
                    view?.onHideLoading()
                    if (model.success && model.data != null) {
                        val provincePairList: List<Pair<String, String>> =
                            model.data!!.map { province ->
                                Pair(province.code!!, province.nameWithType!!)
                            }
                        adapter.setData(provincePairList)
                    }
                }

                override fun onError(message: String) {
                    view?.onHideLoading()
                }
            })
    }

    private fun addAddress(delivery: DeliveryInformation) {
        view?.onShowDialogLoading()
        APISERVICE.getService(AppManager.token).addDeliveryAddress(delivery)
            .enqueue(object : BaseCallback<ResponseModel<DeliveryInformation>>() {
                override fun onSuccess(model: ResponseModel<DeliveryInformation>) {
                    CoroutineScope(Dispatchers.IO).launch {
                        APPDATABASE.addressDao().insert(model.data!!)
                        withContext(Dispatchers.Main){
                            view?.onHideLoading()
                            view?.onAddSuccess()
                        }
                    }
                }

                override fun onError(message: String) {
                    view?.showError(message)
                }
            })
    }
}