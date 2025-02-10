package vn.clothing.store.interfaces

import vn.clothing.store.models.DeliveryInformation

interface MyAddressContract {
    interface View{
        fun onShowLoading()
        fun onHiddenLoading()
        fun onSelectedAddress(delivery: DeliveryInformation)
    }

    interface Presenter{
        fun getAllAddress()
    }
}