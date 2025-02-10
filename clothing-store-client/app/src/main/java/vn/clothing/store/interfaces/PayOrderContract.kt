package vn.clothing.store.interfaces

import vn.clothing.store.common.PopupDialog
import vn.clothing.store.models.DeliveryInformation
import vn.clothing.store.models.VoucherModel
import vn.clothing.store.networks.request.OrderRequestModel


interface PayOrderContract {
    interface View{
        fun onShowLoading()
        fun onHideLoading()
        fun onShowPopup(message:String, type:PopupDialog.PopupType)
        fun onResultAddress(addresses:List<DeliveryInformation>?)
        fun onPaymentSuccess(orderId:String)
        fun onResultFindVoucher(voucher:VoucherModel)
    }
    interface Presenter{
        fun getDefaultAddress()
        fun findVoucher(id:String)
        fun payment(orderRequestModel: OrderRequestModel, cartIds:List<Int>)
        fun onDestroy()
    }
}