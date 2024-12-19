package vn.clothing.store.presenter

import android.os.Handler
import android.os.Looper
import androidx.core.util.Consumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.clothing.store.adapter.RvMyAddressAdapter
import vn.clothing.store.common.AppManager
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.interfaces.MyAddressContract
import vn.clothing.store.models.DeliveryInformation
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class MyAddressPresenter(private var view: MyAddressContract.View?) : MyAddressContract.Presenter  {
    var adapter: RvMyAddressAdapter?=null
    private var handler = Handler(Looper.getMainLooper())
    private val callback = Consumer<DeliveryInformation> {
        view?.onSelectedAddress(it)
    }

    init {
        adapter = RvMyAddressAdapter(emptyList(),callback)
    }

    fun removeItem(position:Int){
        adapter?.removeItem(position)
    }

    override fun getAllAddress() {
        view?.onShowLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val list = APPDATABASE.addressDao().getAll()
            if(list.isNotEmpty()){
                handler.post{
                    adapter?.setData(list)
                    view?.onHiddenLoading()
                }
            }else{
                try{
                    APISERVICE.getService(AppManager.token).getAllDeliveryByUserId().enqueue(object : BaseCallback<ResponseModel<List<DeliveryInformation>>>() {
                        override fun onSuccess(model: ResponseModel<List<DeliveryInformation>>) {
                            if(model.success && model.data!=null){
                                handler.post{
                                   adapter?.setData( model.data!!)
                               }
                                APPDATABASE.addressDao().upsertAll(model.data!!)
                            }
                        }

                        override fun onError(message: String) {

                        }
                    })
                }finally {
                    handler.post{
                        view?.onHiddenLoading()
                    }
                }
            }
        }
    }

    fun removeAddressToDatabase(position:Int){
        val address = adapter!!.getAddress(position)
        CoroutineScope(Dispatchers.IO).launch {
            APPDATABASE.addressDao().delete(address)
            APISERVICE.getService(AppManager.token).deleteDeliveryAddress(address.id).enqueue(object : BaseCallback<ResponseModel<Boolean>>(){
                override fun onSuccess(model: ResponseModel<Boolean>) {

                }

                override fun onError(message: String) {

                }
            })
        }

    }
}