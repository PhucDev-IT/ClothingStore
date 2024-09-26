package vn.clothing.store.interfaces

interface NewDeliveryInfoContract {
    interface View{
        fun showError(error:String)
        fun onShowLoading()
        fun onHideLoading()
        fun onSelectedProvinces(name:String)
        fun onSelectedDistrict(name:String)
        fun onSelectedWard(name:String)
        fun onShowDialogLoading()
        fun onAddSuccess()
    }

    interface Presenter{
        fun resetData()
        fun addNewDeliveryInfo(name:String,phone:String)
        fun getProvinces()
    }
}