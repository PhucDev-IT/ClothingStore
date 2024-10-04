package vn.clothing.store.interfaces

import vn.clothing.store.common.CoreConstant
import vn.clothing.store.models.Category
import vn.clothing.store.models.Product

interface SearchProductContract {
    interface View{
        fun onShowLoading()
        fun onHiddenLoading()
        fun onShowToast(message:String?, type: CoreConstant.ToastType)
        fun onResultCategories(categories: List<Category>)
        fun onResultProducts(products:List<Product>)
    }

    interface Presenter{
        fun getCategories()
        fun getProductByCategory(categoryId:Int)
    }
}