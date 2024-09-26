package vn.clothing.store.presenter

import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import vn.clothing.store.common.AppManager
import vn.clothing.store.common.PopupDialog
import vn.clothing.store.database.AppDatabase
import vn.clothing.store.database.AppDatabase.Companion.APPDATABASE
import vn.clothing.store.interfaces.PayOrderContract
import vn.clothing.store.models.DeliveryInformation
import vn.clothing.store.models.Order
import vn.clothing.store.networks.ApiService.Companion.APISERVICE
import vn.clothing.store.networks.request.DeleteCartRequest
import vn.clothing.store.networks.request.OrderRequestModel
import vn.mobile.banking.network.response.ResponseModel
import vn.mobile.banking.network.rest.BaseCallback

class PayOrderPresenter(private var view: PayOrderContract.View?) : PayOrderContract.Presenter {
    private val handler = Handler(Looper.getMainLooper())

    companion object{
        private val TAG = PayOrderPresenter::class.java.name
    }

    override fun getDefaultAddress() {
        view?.onShowLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val list = APPDATABASE.addressDao().getAll()
            if (list.isNotEmpty()) {
                handler.post {
                    view?.onResultAddress(list)
                    view?.onHideLoading()
                }
            } else {
                try {
                    APISERVICE.getService(AppManager.token).getAllDeliveryByUserId()
                        .enqueue(object : BaseCallback<ResponseModel<List<DeliveryInformation>>>() {
                            override fun onSuccess(model: ResponseModel<List<DeliveryInformation>>) {
                                if (model.success && model.data != null) {
                                    APPDATABASE.addressDao().upsertAll(model.data!!)
                                }
                                handler.post {
                                    view?.onResultAddress(model.data)
                                }
                            }

                            override fun onError(message: String) {
                                handler.post {
                                    view?.onResultAddress(null)
                                }
                            }
                        })
                } finally {
                    handler.post {
                        view?.onHideLoading()
                    }
                }
            }
        }
    }

    override fun payment(orderRequestModel: OrderRequestModel, cartIds:List<Int>) {
        view?.onShowLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val addOrderResult = addOrder(orderRequestModel)
                if(addOrderResult.success && addOrderResult.data!=null){
                    val removeCartResult = removeCartItems(cartIds)
                    handler.post {
                        view?.onHideLoading()
                        view?.onPaymentSuccess(addOrderResult.data!!.id!!)
                    }
                }else{
                    handler.post{
                        view?.onHideLoading()
                        view?.onShowPopup(
                            addOrderResult.error?.message ?: "",
                            PopupDialog.PopupType.NOTIFICATION
                        )
                    }
                }
            }catch (e:Exception){
                Log.e(TAG,"Fail: ${e.message}")
            }
        }

    }


    private suspend fun addOrder(orderRequestModel: OrderRequestModel):ResponseModel<Order>{
       return   APISERVICE.getService(AppManager.token).createOrder(orderRequestModel).await()
    }

    //After payment success, we need to remove item cart
    private suspend fun removeCartItems(list:List<Int>):ResponseModel<Boolean>{
        val request = DeleteCartRequest(list)
        return APISERVICE.getService(AppManager.token).deleteCart(request).await()
    }


    override fun onDestroy() {
        view = null
    }
}