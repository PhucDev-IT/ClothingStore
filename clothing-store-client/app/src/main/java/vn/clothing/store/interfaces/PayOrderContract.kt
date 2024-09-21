package vn.clothing.store.interfaces

import vn.clothing.store.common.PopupDialog
import vn.clothing.store.database.models.AddressSchema

interface PayOrderContract {
    interface View{
        fun onShowLoading()
        fun onHideLoading()
        fun onShowPopup(message:String, type:PopupDialog.PopupType)
        fun onResultAddress(addresses:List<AddressSchema>?)
    }
    interface Presenter{
        fun getDefaultAddress()
        fun onDestroy()
    }
}