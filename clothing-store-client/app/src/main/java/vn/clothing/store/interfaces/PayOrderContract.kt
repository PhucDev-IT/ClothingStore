package vn.clothing.store.interfaces

import vn.clothing.store.common.PopupDialog
import vn.clothing.store.models.DeliveryInformation
import vn.clothing.store.networks.request.OrderRequestModel


interface PayOrderContract {
    interface View{
        fun onShowLoading()
        fun onHideLoading()
        fun onShowPopup(message:String, type:PopupDialog.PopupType)
        fun onResultAddress(addresses:List<DeliveryInformation>?)
        fun onPaymentSuccess(orderId:String)
    }
    interface Presenter{
        fun getDefaultAddress()
        fun payment(orderRequestModel: OrderRequestModel, cartIds:List<Int>)
        fun onDestroy()
    }
}