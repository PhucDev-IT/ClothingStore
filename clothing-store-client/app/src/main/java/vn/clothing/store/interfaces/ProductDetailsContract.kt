package vn.clothing.store.interfaces

import android.content.Context
import vn.clothing.store.common.CoreConstant
import vn.clothing.store.models.Image
import vn.clothing.store.models.Product
import vn.clothing.store.models.ProductDetails
import vn.clothing.store.models.ProductFavorite
import vn.clothing.store.networks.request.CartRequestModel

interface ProductDetailsContract {
    interface View{
        fun onShowLoading()
        fun onHiddenLoading()
        fun onShowError(message: String?)
        fun onShowToast(message:String?, type: CoreConstant.ToastType)
        fun onResultProduct(product: Product)
        fun onResultImages(images:List<Image>)
        fun getContext():Context
        fun isProductIsFavorite(isFavorite:Boolean)
    }

    interface Presenter{
        fun loadInformationProduct(productId:String)
        fun loadImages(id:String)
        fun addToCart(model: CartRequestModel)
        fun upsertProductFavorite(productFavorite: ProductFavorite)
        fun checkFavorite(productId:String)
    }
}