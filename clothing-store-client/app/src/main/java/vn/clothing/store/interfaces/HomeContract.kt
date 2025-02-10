package vn.clothing.store.interfaces

import vn.clothing.store.common.CoreConstant
import vn.clothing.store.models.Category
import vn.clothing.store.models.Product

interface HomeContract {
    interface View {
        fun onShowLoading()
        fun onHiddenLoading()
        fun onShowToast(message:String?, type:CoreConstant.ToastType)
        fun onShowCategories(categories: List<Category>)
        fun onResultProducts(products:List<Product>)
        fun onCountNotification(count:Int)
    }

    interface Presenter{
        fun loadData()
        fun getCategories()
        fun getProductByCategory(categoryId:Int)
        fun countNotification()
    }
}